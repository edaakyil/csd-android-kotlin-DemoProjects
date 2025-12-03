package com.edaakyil.kotlin.app.random.text.server.component

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService

@Component
class Server(private val mThreadPool: ExecutorService) {
    private val mLogger = LoggerFactory.getLogger(Server::class.java)

    @Value($$"${app.port}")  //@Value("\${app.port}")
    private var mPort = 0

    @Value($$"${app.text.length.max}")
    private var mTextMaxLength = 0

    private fun handleClient(socket: Socket) {
        socket.use {
            mLogger.info("Client connected: {}:{}", socket.remoteSocketAddress, socket.port)
        }
    }

    fun start() {
        try {
            mLogger.info("Starting server on port: $mPort")

            ServerSocket(mPort).use {
                while (true) {
                    val clientSocket = it.accept()

                    mThreadPool.execute { handleClient(clientSocket) }
                }
            }
        } catch (ex: Exception) {
            mLogger.error("Error occurred: {}", ex.message)
        }
    }
}