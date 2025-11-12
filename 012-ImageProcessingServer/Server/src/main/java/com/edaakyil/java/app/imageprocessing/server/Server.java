package com.edaakyil.java.app.imageprocessing.server;

import lombok.extern.slf4j.Slf4j;
import org.csystem.image.OpenCVUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@Component
@Slf4j
public class Server {
    private final ExecutorService m_executorService;
    private final DateTimeFormatter m_dateTimeFormatter;

    @Value("${app.server.port}")
    private int m_port;

    @Value("${app.image.directory}")
    private String m_imagesPath;

    @Value("${app.image.transmission.bufsize}")
    private int m_bufferSize;

    @Value("${app.image.transmission.maxbufcount}")
    private int m_maxBufferCount;

    public Server(ExecutorService executorService, DateTimeFormatter dateTimeFormatter)
    {
        m_executorService = executorService;
        m_dateTimeFormatter = dateTimeFormatter;
    }

    private int readInt(InputStream is) throws IOException
    {
        byte[] bytes = new byte[Integer.BYTES];

        if (is.read(bytes) != Integer.BYTES)
            throw new IOException("Invalid data length");

        // wrap metodu ile bytes dizisini sarmalıyoruz
        return ByteBuffer.wrap(bytes).getInt();
    }

    private int readImageDataCallback(Socket socket, byte[] buffer)
    {
        try {
            // Okuma yapıyoruz
            var len = socket.getInputStream().read(buffer);
            log.info("Len: {}", len);

            return len;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void saveImageDataCallback(FileOutputStream fos, byte[] buffer, int len)
    {
        try {
            // Yazma yapıyoruz
            fos.write(buffer, 0, len);  // 0'dan len'e kadar okuyacak

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void readAndSaveImage(Socket socket, byte[] buffer) throws IOException
    {
        var br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // get filename from Client (dosya ismini okuma)
        var filename = br.readLine();
        // dosya isminde noktadan itibaren sonuna kadar ver yani filename'deki uzantı kısmını alma
        var extension = filename.substring(filename.lastIndexOf('.') + 1); // noktayı bulduğumuz yer -1 olduğu için +1 yazdık
        // Path belirleme
        var path = "%s/%s-%s-%s.%s".formatted(
                m_imagesPath, filename, socket.getInetAddress().getHostAddress(), m_dateTimeFormatter.format(LocalDateTime.now()), extension
        );

        // Dosyaya kaydetme
        try (var fos = new FileOutputStream(path)) {
            // Her adımda readImageCallback ile image dosyasının içeriğini okuyup kaydediceğiz
            IntStream.generate(() -> readImageDataCallback(socket, buffer))
                    .takeWhile(len -> len != -1)
                    .limit(m_maxBufferCount)
                    .forEach(len -> saveImageDataCallback(fos, buffer, len)
            );
        }

        // Aldığımız görüntüyü işleme
        OpenCVUtil.grayScale(path, path + "gs.jpeg");
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

            var os = socket.getOutputStream();
            var bufSizeData = ByteBuffer.allocate(Integer.BYTES).putInt(m_bufferSize).array();

            os.write(bufSizeData);

            readAndSaveImage(socket, new byte[m_bufferSize]);

        } catch (IOException ex) {
            log.error("IO Problem occurred while client connected: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Problem occurred while client connected: {}", ex.getMessage());
        }
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
