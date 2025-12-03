package com.edaakyil.kotlin.app.random.text.client.component

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService

@Component
class ClientRunner(private val mThreadPool: ExecutorService, private val mClient: Client) : CommandLineRunner {
    override fun run(vararg args: String) {
        mThreadPool.execute { mClient.start() }
        mThreadPool.shutdown()
    }
}


/*
* Aşağıdaki kodda `class ServerRunner(threadPool: ExecutorService)` ile ctor injection yaptık:

@Component
class ServerRunner(threadPool: ExecutorService) : CommandLineRunner {
    private val mThreadPool: ExecutorService = threadPool

    override fun run(vararg args: String) {
        TODO("Not yet implemented")
    }
}
*
*
* Aşağıdaki kodda `class ServerRunner(val threadPool: ExecutorService)` ile ctor injection yaptık:

@Component
class ServerRunner(private val mThreadPool: ExecutorService) : CommandLineRunner {
    override fun run(vararg args: String) {
        TODO("Not yet implemented")
    }
}
 */