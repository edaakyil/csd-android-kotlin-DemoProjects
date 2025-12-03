package com.edaakyil.kotlin.app.random.text.client.component

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.Socket
import java.util.concurrent.ExecutorService

@Component
class Client(private val mThreadPool: ExecutorService) {
    private val mLogger = LoggerFactory.getLogger(Client::class.java)

    @Value($$"${app.server.port}")
    private val mServerPort = 0

    @Value($$"${app.server.host}")
    private val mServerHost = ""

    fun start() {
        try {
            Socket(mServerHost, mServerPort).use {

            }
        } catch (ex: Exception) {
            mLogger.error("Error occurred: {}", ex.message)
        }
    }
}