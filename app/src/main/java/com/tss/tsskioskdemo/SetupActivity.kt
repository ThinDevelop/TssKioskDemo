package com.tss.tsskioskdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setup.*

class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        btn_setup.setOnClickListener {
            val ip = edt_ip.text.toString()
            val intent = Intent(this@SetupActivity, MainActivity::class.java)
            intent.putExtra("ip", ip)
            startActivity(intent)
        }

    }
}