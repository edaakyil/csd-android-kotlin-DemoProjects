package com.edaakyil.java.app.imageprocessing.server;

import lombok.extern.slf4j.Slf4j;
//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
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

    @Value("${app.image.transmission.buffersize}")
    private int m_bufferSize;

    /*
    private void doGrayScale(String srcPath, String destPath)
    {
        var srcMat = Imgcodecs.imread(srcPath);
        var destMat = new Mat();

        Imgproc.cvtColor(srcMat, destMat, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(destPath, destMat);
    }
    */

    /**
     * Client ile ilgili işlemler bu akış içerisinde karşılanacak
     * @param socket
     */
    private void handleClient(Socket socket)
    {
        try (socket) {
            // Client'ın bağlantı bilgilerini yazdırma
            log.info("Client connected from: {}:{}", socket.getInetAddress(), socket.getPort());

            var is = socket.getInputStream();
            var os = socket.getOutputStream();

            // int'i BigEndian olarak byte dizisine çeviriyor
            var bufferSizeData = ByteBuffer.allocate(Integer.BYTES).putInt(m_bufferSize).array();

            // bufferSizeData'yı yolluyoruz
            os.write(bufferSizeData);

            log.info("Client received from: {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());

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
