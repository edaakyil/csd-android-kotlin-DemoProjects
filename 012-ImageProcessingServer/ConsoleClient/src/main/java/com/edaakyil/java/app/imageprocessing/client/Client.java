package com.edaakyil.java.app.imageprocessing.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

@Component
@Slf4j
public class Client {
    @Value("${server.imageprocessing.host}")
    private String m_host;

    @Value("${server.imageprocessing.port}")
    private int m_port;

    private int readInt(InputStream is) throws IOException
    {
        byte[] bytes = new byte[Integer.BYTES];

        if (is.read(bytes) != Integer.BYTES)
            throw new IOException("Invalid data length");

        // wrap metodu ile bytes dizisini sarmalıyoruz
        //return ByteBuffer.wrap(bytes).getInt(0);
        return ByteBuffer.wrap(bytes).getInt();
    }

    private void sendImage(Socket socket, int bufferSize) throws IOException
    {
        var os = socket.getOutputStream();

        // Karşı tarafa dosya ismini gönderme
        var path = "images/red-kit.jpeg";
        var bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write("red-kit.jpeg\r\n");
        bw.flush();

        // Dosyadan okuyup karşı tarafa gönderme
        byte[] buffer = new byte[bufferSize];

        try (var fis = new FileInputStream(path)) { // doğrudan path'den okumayı yapıyoruz
            int len;
            int total = 0;

            while ((len = fis.read(buffer)) != -1) {
                log.info("Len: {}", len);

                total += len;
                os.write(buffer, 0, len);
            }

            os.flush();

            log.info("Len: {}", len);
            log.info("Total: {}", total);
        }

    }

    public void run()
    {
        try (var socket = new Socket(m_host, m_port)) {
            log.info("Connected to {}:{}", m_host, m_port);

            var is = socket.getInputStream();
            var bufSize = readInt(is); // Client bufferSize bilgisini alıyor

            log.info("Buffer size: {}", bufSize);

            sendImage(socket, bufSize);

        } catch (IOException ex) {
            log.error("IO Problem occurred: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Problem occurred: {}", ex.getMessage());
        }
    }
}
