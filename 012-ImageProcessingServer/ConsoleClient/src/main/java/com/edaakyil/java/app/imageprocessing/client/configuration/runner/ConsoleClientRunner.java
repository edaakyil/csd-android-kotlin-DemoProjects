package com.edaakyil.java.app.imageprocessing.client.configuration.runner;

import com.edaakyil.java.app.imageprocessing.client.Client;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsoleClientRunner implements ApplicationRunner {
    private final Client m_client;

    public ConsoleClientRunner(Client client)
    {
        m_client = client;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        new Thread(m_client::run).start();
    }
}
