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

    public void run()
    {
        try (var socket = new Socket(m_host, m_port)) {
            log.info("Connected to {}:{}", m_host, m_port);

            var is = socket.getInputStream();
            var os = socket.getOutputStream();

            // Client bufferSize bilgisini alıyor
            var bytes = is.readAllBytes();

            if (bytes.length != Integer.BYTES)
                throw new IOException("Invalid data length");

            // wrap metodu ile bytes dizisini sarmalıyoruz
            var bufferSize = ByteBuffer.wrap(bytes).getInt();

            log.info("Buffer size: {}", bufferSize);

        } catch (IOException ex) {
            log.error("IO Problem occurred: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Problem occurred: {}", ex.getMessage());
        }
    }
}
