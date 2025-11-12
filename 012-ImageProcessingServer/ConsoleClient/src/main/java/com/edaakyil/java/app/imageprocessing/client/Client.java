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

    public void run()
    {
        try (var socket = new Socket(m_host, m_port)) {
            log.info("Connected to {}:{}", m_host, m_port);

            var is = socket.getInputStream();
            var os = socket.getOutputStream();

            var bufSize = readInt(is); // Client bufferSize bilgisini alıyor
            var maxBufCount = readInt(is); // Client bufferCount bilgisini alıyor

            log.info("Buffer size: {} Max buffer count: {}", bufSize, maxBufCount);

        } catch (IOException ex) {
            log.error("IO Problem occurred: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Problem occurred: {}", ex.getMessage());
        }
    }
}
