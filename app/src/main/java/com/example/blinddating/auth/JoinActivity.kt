package com.example.blinddating.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.MainActivity
import com.example.blinddating.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "JoinActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        // Initialize Firebase Auth
        auth = Firebase.auth


        val btnJoin = findViewById<Button>(R.id.btn_join_ok)
        btnJoin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val passwordCheck = inputPasswordCheck.text.toString().trim()
            val nickname = inputName.text.toString().trim()
            val gender = inputGender.text.toString().trim()
            val area = inputArea.text.toString().trim()
            val age = inputAge.text.toString().trim()


            // 신규 사용자 가입
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")

                        // 신규 사용자 유효성 검사사
                       val user = auth.currentUser
                        Log.d(TAG, user?.uid.toString())

                        // 회원가입 성공시 메인 Activity로 이동
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    }
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)


                }
        }

    }


}
