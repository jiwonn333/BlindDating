package com.example.blinddating.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.R

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // 1. gradle에  id 'kotlin-android-extensions' 추가하면
        // btn_join.setOnClickListener() 로 바로 사용 가능

        // val btnJoin : Button = findViewById(R.id.btn_join)
        val btnJoin = findViewById<Button>(R.id.btn_join)
        btnJoin.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}