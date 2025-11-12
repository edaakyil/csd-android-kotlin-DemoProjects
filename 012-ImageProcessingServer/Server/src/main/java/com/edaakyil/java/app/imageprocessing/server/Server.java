package com.edaakyil.java.app.imageprocessing.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class Server {
    private final ExecutorService m_executorService;

    @Value("${app.server.port}")
    private int m_port;

    @Value("${app.image.transmission.bufsize}")
    private int m_bufferSize;

    @Value("${app.image.transmission.maxbufcount}")
    private int m_maxBufferCount;

    private int readInt(InputStream is) throws IOException
    {
        byte[] bytes = new byte[Integer.BYTES];

        if (is.read(bytes) != Integer.BYTES)
            throw new IOException("Invalid data length");

        // wrap metodu ile bytes dizisini sarmalıyoruz
        return ByteBuffer.wrap(bytes).getInt();
    }

    private void readAndSaveImage(int bufferCount)
    {
        log.info("Buffer count: {}", bufferCount);
    }

    /**
     * Client ile ilgili işlemler bu akış içerisinde karşılanacak
     * @param socket
     */
    private void handleClient(Socket socket)
    {
        try (socket) {
            // Client'ın bağlantı bilgilerini yazdırma
            log.info("Client connected from: {}:{}", socket.getInetAddress(), socket.getPort());
            //log.info("Client received from: {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());

            var is = socket.getInputStream();
            var os = socket.getOutputStream();

            byte[] bytes = new byte[Integer.BYTES];

            var bufSizeData = ByteBuffer.allocate(Integer.BYTES).putInt(m_bufferSize).array();
            var bufCountData = ByteBuffer.allocate(Integer.BYTES).putInt(m_maxBufferCount).array();

            os.write(bufSizeData);
            os.write(bufCountData);

            if (is.read(bytes) != Integer.BYTES) {
                os.write(ByteBuffer.allocate(Integer.BYTES).putInt(-1).array()); // -1 is our error code: You sent incorrect data.
                return;
            }

            var bufCount = ByteBuffer.wrap(bytes).getInt();

            if (bufCount > m_maxBufferCount) {
                os.write(ByteBuffer.allocate(Integer.BYTES).putInt(0).array()); // -1 is our error code: You sent more data than expected.
                return;
            }

            readAndSaveImage(bufCount);

        } catch (IOException ex) {
            log.error("IO Problem occurred while client connected: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Problem occurred while client connected: {}", ex.getMessage());
        }
    }

    public Server(ExecutorService executorService)
    {
        m_executorService = executorService;
    }

    /**
     * Starts the server
     */
    public void start()
    {
        log.info("Starting server on port: {}", m_port);

        try (var serverSocket = new ServerSocket(m_port)) {
            while (true) {
                // ServerSocket sınıfının accept metodu client'ı karşılar,
                // accept metodundan dönen Socket ile de doğrudan konuşmayı (veri-alışverişini) gerçekleştiririz.
                // Server'ın sürekli olarak client geldikçe beklemesi lazım
                var socket = serverSocket.accept();

                m_executorService.execute(() -> handleClient(socket));
            }
        } catch (IOException ex) {
            log.error("IO Problem occurred while server is waiting: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Problem occurred while server is waiting: {}", ex.getMessage());
        }
    }
}
