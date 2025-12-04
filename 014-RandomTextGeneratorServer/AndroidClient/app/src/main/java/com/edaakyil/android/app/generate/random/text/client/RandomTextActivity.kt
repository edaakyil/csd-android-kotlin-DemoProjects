package com.edaakyil.android.app.generate.random.text.client

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.edaakyil.android.app.generate.random.text.client.constant.SERVER_INFO_KEY
import com.edaakyil.android.app.generate.random.text.client.databinding.ActivityRandomTextBinding
import com.edaakyil.android.app.generate.random.text.client.viewmodel.ServerInfo
import com.edaakyil.android.app.generate.random.text.client.viewmodel.ServerParam
import com.karandev.util.net.TcpUtil
import java.net.Socket
import kotlin.concurrent.thread

class RandomTextActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRandomTextBinding
    private lateinit var mServerInfo: ServerInfo

    private fun getTextsForEachCallback(socket: Socket, index: Int) {
        // Server'dan gelecek olan text'leri okumayı yapıyoruz. Bunun için thread içinde olmamız gerekiyor.
        // Bu yüzden bu kısmı runOnUiThread'in dışında yapıyoruz ve şu an bu fonksiyon thread'de çağırılıyor.
        // Eğer bu kısmı runOnUiThread içerisinde yaparsak ui thread'de yapmış oluruz ve ana thread'de diğerinin işini yapmış oluruz.
        // Bunun exception kısmı, bu fonksiyonun çağrıldığı yerde exception handling yapıldığı için burada yapmıyoruz.
        val text = "${index + 1}. " + TcpUtil.receiveStringViaLength(socket)

        // adapter'a ekleme yapabilmek için main thread içinde olmamız lazım
        runOnUiThread { mBinding.adapter?.add(text) }
    }

    private fun getTextsCallback(count: Long, minLength: Int, maxLength: Int) {
        try {
            // Socket ile Server'a bağlanıyoruz:
            Socket(mServerInfo.host, mServerInfo.port.toInt()).use { s ->
                // Akış buraya gelirse Socket'e bağlanmışdır. Artık şimdi karşılıklı olarak bilgileri göndereceğiz:
                TcpUtil.sendLong(s, count)  // Karşı tarafa (Server'a) count bilgisini gönderiyoruz
                TcpUtil.sendInt(s, minLength)
                TcpUtil.sendInt(s, maxLength)

                // Bu noktada bize Server'dan success veya unsuccess bilgisi gelecek biz de bunu kontrol ediyoruz:
                val statusCode = TcpUtil.receiveInt(s)
                Log.i("Status Code", "$statusCode")
                if (statusCode != 0) {
                    runOnUiThread {
                        Toast.makeText(this, "Invalid values", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                // Artık karşı tarafdan (Server'dan) text'leri alacağız:
                generateSequence(0) { it + 1 }.takeWhile { it < count }.forEach { it -> getTextsForEachCallback(s, it) }
                //generateSequence(0) { it + 1 }.takeWhile { it < count }.forEach { getTextsForEachCallback(s, it) }
            }
        } catch (ex: Exception) {
            // main thread içinde olmadığımız için main thread'de işlem yapmak için runOnUiThread kullandık:
            runOnUiThread {
                Toast.makeText(this, "Exception occurred: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        mBinding.serverParam = ServerParam()
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
        try {
            mBinding.adapter!!.clear()

            val count = mBinding.serverParam!!.count.toLong()
            val minLength = mBinding.serverParam!!.minLength.toInt()
            val maxLength = mBinding.serverParam!!.maxLength.toInt()

            // İnternet erişimi için thread lazım. Bu yüzden burada bir thread açacağız:
            // Thread'i ayrıca sonlandırmamıza gerek yok çünkü zaten bu, sonlanan bir thread.
            thread { getTextsCallback(count, minLength, maxLength) }

        } catch (ex: NumberFormatException) {
            Log.e("number-format-exception", "${ex.message}")
            //Log.e("number-format-exception", ex.message.toString())

            Toast.makeText(this, getString(R.string.not_number_message, mBinding.serverParam!!.count), Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, resources.getString(R.string.not_number_message, mBinding.serverParam!!.count), Toast.LENGTH_SHORT).show()
        }
    }
}