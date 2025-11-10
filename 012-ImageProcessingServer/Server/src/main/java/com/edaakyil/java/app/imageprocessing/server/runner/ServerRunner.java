package com.edaakyil.java.app.imageprocessing.server.runner;

import com.edaakyil.java.app.imageprocessing.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

@Component
public class ServerRunner implements ApplicationRunner {
    private final Server m_server;
    private final ExecutorService m_executorService;

    @Value("${app.image.directory}")
    private String m_imagesPath;

    public ServerRunner(Server server, ExecutorService executorService)
    {
        m_server = server;
        m_executorService = executorService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        // Uygulama başlar başlamaz directory yaratılacak
        Files.createDirectories(Path.of(m_imagesPath));

        m_executorService.execute(m_server::start);
    }
}
