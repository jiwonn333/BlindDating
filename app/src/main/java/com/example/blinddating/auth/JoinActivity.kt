package com.example.blinddating.auth

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        // 현재 인증상태 확인
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
                        Log.d(TAG, "createUserWithEmail: 계정 생성 성공")

                        // 신규 사용자 유효성 검사사
                       val user = auth.currentUser
                        Log.d(TAG, user?.uid.toString())
                        Toast.makeText(this, "계정 생성 성공", Toast.LENGTH_SHORT).show()

                        // 계정 생성 성공 시 바로 로그인 할 것인지
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("회원가입 성공")
                            .setMessage("로그인 하시겠습니까?")
                            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                                // 회원가입 성공 후 로그인시 바로 메인 Activity로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                            })
                            .setNegativeButton("취소",
                                DialogInterface.OnClickListener { dialog, i ->
                                })
                        builder.show()
                        

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail: 계정 생성 실패", task.exception)
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }


}
