package com.tss.tsskioskdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var url = ""
    var amount = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ip = intent.getStringExtra("ip")
        url = "http://"+ ip +":9000/transaction"
        btn_confirm.setOnClickListener {
            val amount = edt_amount.text.toString()
            val id = edt_id.text.toString()
            sendData(amount, id)
            DialogFactory.showLoadingDialog(this@MainActivity, "", "waiting...")
        }
    }

    fun sendData(amount: String, id: String) {
        AndroidNetworking.post(url)
            .addStringBody("{'amount':"+amount+",'id': "+id+"}")
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.e("panya", "onResponse : $response")
                    val status = response.getString("status")
                    val msg = response.getString("msg")
                    if (status == "200") {
                        DialogFactory.showMessage(this@MainActivity, "สำเร็จ!!", "ชำระเงินเรียบร้อย")
                    } else {
                        DialogFactory.showMessage(this@MainActivity, "ไม่สำเร็จ!!", "การชำระเงินผิดพลาด")
                    }
                    clearFormData()
                }

                override fun onError(error: ANError) {
                    clearFormData()
                    Log.e("panya", "onError : " + error.message)
                }
            })
    }

    fun clearFormData() {
        edt_amount.setText("")
        edt_id.setText("")
    }
}
