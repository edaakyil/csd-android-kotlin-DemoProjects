package com.edaakyil.kotlin.app.random.text.server.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ThreadPoolConfig {
    @Bean
    fun threadPool(): ExecutorService = Executors.newCachedThreadPool()
}