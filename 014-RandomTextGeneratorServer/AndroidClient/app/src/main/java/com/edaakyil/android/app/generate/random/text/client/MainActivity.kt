package com.edaakyil.android.app.generate.random.text.client

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.edaakyil.android.app.generate.random.text.client.constant.DEFAULT_HOST
import com.edaakyil.android.app.generate.random.text.client.constant.DEFAULT_PORT
import com.edaakyil.android.app.generate.random.text.client.constant.SERVER_INFO_KEY
import com.edaakyil.android.app.generate.random.text.client.databinding.ActivityMainBinding
import com.edaakyil.android.app.generate.random.text.client.viewmodel.ServerInfo

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.activity = this
        mBinding.serverInfo = ServerInfo(DEFAULT_HOST, DEFAULT_PORT)
    }

    private fun initialize() {
        enableEdgeToEdge()
        initBinding()
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.mainActivityMainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun onStartButtonClicked() {
        Intent(this, RandomTextActivity::class.java).apply {
            putExtra(SERVER_INFO_KEY, mBinding.serverInfo)
            startActivity(this)
        }
    }
}