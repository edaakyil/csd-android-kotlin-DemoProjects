package com.edaakyil.kotlin.app.generate.random.text.client.component

import com.karandev.util.net.TcpUtil
import org.csystem.kotlin.util.console.readInt
import org.csystem.kotlin.util.console.readLong
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
            // Server'ın socket'inin timeout'dan kurtulmak için önce Client'dan count, min, max değerleri alınacak sonra socket açılacak
            val count = readLong("Input count: ", "Invalid value!...")
            val min = readInt("Input min: ", "Invalid value!...")
            val max = readInt("Input max: ", "Invalid value!...")

            Socket(mServerHost, mServerPort).use { s ->
                TcpUtil.sendLong(s, count) // Server'a count bilgisini gönderilecek
                TcpUtil.sendInt(s, min)
                TcpUtil.sendInt(s, max)

                // Şimdi success kodu veya unsuccess elde edeceğiz:
                //mLogger.info("Result: {}", TcpUtil.receiveInt(s))
                val statusCode = TcpUtil.receiveInt(s)
                mLogger.info("Result: {}", statusCode)

                if (statusCode == 0)
                    // Şimdi Server'dan gelecek olan text'leri okumaya başlayacağız:
                    generateSequence(0) { it + 1 }.takeWhile { it < count }.forEach { _ -> println(TcpUtil.receiveStringViaLength(s)) }

            }
        } catch (ex: Exception) {
            mLogger.error("Error occurred: {}", ex.message)
        }
    }
}