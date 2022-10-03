package com.example.blinddating.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.MainActivity
import com.example.blinddating.R

class IntroActivity : AppCompatActivity() {
    private val TAG = "IntroActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // 1. gradle에  id 'kotlin-android-extensions' 추가하면
        // btn_join.setOnClickListener() 로 바로 사용 가능

        // val btnJoin : Button = findViewById(R.id.btn_join)
        // 회원가입 버튼 클릭 시 페이지 이동
        val btnJoin = findViewById<Button>(R.id.btn_join)
        btnJoin.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 클릭")
            val intentJoin = Intent(this, JoinActivity::class.java)
            startActivity(intentJoin)
        }

        // 로그인 버튼 클릭 시 페이지 이동
        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            Log.d(TAG, "로그인버튼 클릭")
            val intentLogin = Intent(this, LoginActivity::class.java)
            startForResult.launch(intentLogin)
            //startActivity(intentLogin)

        }
    }
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}