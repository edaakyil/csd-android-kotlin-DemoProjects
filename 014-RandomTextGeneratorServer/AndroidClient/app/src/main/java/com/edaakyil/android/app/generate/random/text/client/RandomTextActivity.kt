package com.edaakyil.android.app.generate.random.text.client

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.edaakyil.android.app.generate.random.text.client.constant.SERVER_INFO_KEY
import com.edaakyil.android.app.generate.random.text.client.databinding.ActivityRandomTextBinding
import com.edaakyil.android.app.generate.random.text.client.viewmodel.ServerInfo
import com.edaakyil.android.app.generate.random.text.client.viewmodel.ServerParam

class RandomTextActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRandomTextBinding
    private lateinit var mServerInfo: ServerInfo

    private fun serverInfo() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
        intent.getSerializableExtra(SERVER_INFO_KEY, ServerInfo::class.java) as ServerInfo
    else
        intent.getSerializableExtra(SERVER_INFO_KEY) as ServerInfo

    private fun initServerInfo() {
        mServerInfo = serverInfo()
    }

    private fun initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_random_text)
        mBinding.activity = this
        mBinding.serverParam = ServerParam("10", "10", "20")
        mBinding.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
    }

    private fun initialize() {
        enableEdgeToEdge()
        initBinding()
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.randomTextActivityMainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initServerInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun onGetButtonClicked() {
        TODO("Not yet implemented!...")
    }
}