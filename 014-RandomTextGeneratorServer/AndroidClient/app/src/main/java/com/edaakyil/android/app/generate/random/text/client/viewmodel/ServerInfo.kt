package com.edaakyil.android.app.generate.random.text.client.viewmodel

import com.edaakyil.android.app.generate.random.text.client.constant.DEFAULT_HOST
import com.edaakyil.android.app.generate.random.text.client.constant.DEFAULT_PORT
import java.io.Serializable

data class ServerInfo(var host: String = DEFAULT_HOST, var port: String = DEFAULT_PORT) : Serializable {
    override fun toString() = "$host:$port"
}