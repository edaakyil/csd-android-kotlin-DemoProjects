package com.edaakyil.kotlin.app.generate.random.text.server.component

import com.edaakyil.kotlin.app.generate.random.text.server.constant.COUNT_NOT_POSITIVE_ERROR
import com.edaakyil.kotlin.app.generate.random.text.server.constant.MAX_LENGTH_ERROR
import com.edaakyil.kotlin.app.generate.random.text.server.constant.MAX_MIN_ERROR
import com.edaakyil.kotlin.app.generate.random.text.server.constant.SOCKET_TIMEOUT
import com.edaakyil.kotlin.app.generate.random.text.server.constant.SUCCESS
import com.karandev.util.net.TcpUtil
import org.csystem.kotlin.util.string.randomTextEN
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import kotlin.random.Random

@Component
class Server(private val mThreadPool: ExecutorService) {
    private val mLogger = LoggerFactory.getLogger(Server::class.java)

    @Value($$"${app.port}")  //@Value("\${app.port}")
    private var mPort = 0

    @Value($$"${app.text.length.max}")
    private var mTextMaxLength = 0

    private fun handleClient(socket: Socket) {
        socket.use { s ->
            try {
                mLogger.info("Client connected: {}:{}", socket.inetAddress.hostAddress  , socket.port)

                s.soTimeout = SOCKET_TIMEOUT

                val count = TcpUtil.receiveLong(s) // Önce client'ın kaç tane istediği bilgisi gelecek
                val min = TcpUtil.receiveInt(s)  // Client'dan sonra min'i alacak
                val max = TcpUtil.receiveInt(s)  // Client'dan sonra max'ı alacak

                if (max - min > mTextMaxLength) {
                    TcpUtil.sendInt(s, MAX_LENGTH_ERROR)  // karşı tarafa (client'a) unsuccess kodu olarak 1 gönderiyoruz
                    return
                }

                if (max < min) {
                    TcpUtil.sendInt(s, MAX_MIN_ERROR)
                    return
                }

                if (count <= 0) {
                    TcpUtil.sendInt(s, COUNT_NOT_POSITIVE_ERROR)
                    return
                }

                TcpUtil.sendInt(s, SUCCESS) // success kodu gönderiyoruz

                // 0'den başlayacak ve (it < count) olduğu sürece dönen bir döngü oluşturduk:
                generateSequence(0) { it + 1 }.takeWhile { it < count }.forEach { _ ->
                    // Her adımda karşı tarafa text gönderiyoruz
                    TcpUtil.sendStringViaLength(s, Random.randomTextEN(Random.nextInt(min, max + 1)))
                }

            } catch (ex: Exception) {
                mLogger.error("Client disconnected: {}", ex.message)

                // Burada connection kesildiği için burada artık elimizde socket yok bu yüzden burada unsuccess kodu göndermemizin bir manası yok:
                //TcpUtil.sendInt(s, -1)
            }
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