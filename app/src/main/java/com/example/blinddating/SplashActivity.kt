package com.example.blinddating

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.auth.IntroActivity
import com.example.blinddating.utils.FirebaseAuthUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val uid = FirebaseAuthUtils.getUid()

        // 현재 UID 값
        Log.d(TAG, uid)

        if (uid == "null") {
            //Handler 구현 : 특정 시간동안 SplashActivity 화면 보여주기 위함
            //        Handler().postDelayed({
            //            val intent = Intent(this, IntroActivity::class.java)
            //            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            //            startActivity(intent)
            //            finish()
            //        }, 3000)

            // Handler 경고 --> Looper 사용
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            // Handler 경고 --> Looper 사용
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }, 2000)
        }



    }
}