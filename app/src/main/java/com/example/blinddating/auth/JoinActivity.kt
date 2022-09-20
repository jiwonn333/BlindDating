package com.example.blinddating.auth

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.R
import com.example.blinddating.utils.FirebaseRef
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "JoinActivity"

    // 데이터베이스에 저장할 정보 (닉네임, 성별, 지역, 나이, UID)
    private var uid = ""
    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = "" // 문자로 받아올 예정

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

            nickname = findViewById<TextInputEditText>(R.id.inputNickName).text.toString()
            gender = findViewById<TextInputEditText>(R.id.inputGender).text.toString()
            city = findViewById<TextInputEditText>(R.id.inputCity).text.toString()
            age = findViewById<TextInputEditText>(R.id.inputAge).text.toString()
            Log.d(TAG, "nickName : " + nickname  + " gender : " + gender + ", city: " + city + ", age :" + age)


            // 신규 사용자 가입
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // 회원가입 성공 시
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail: 계정 생성 성공")
                        Toast.makeText(this, "계정 생성 성공", Toast.LENGTH_SHORT).show()

                        // 신규 사용자 유효성 검사사 (UID)
                        val user = auth.currentUser
                        uid = user?.uid.toString()

                        val userDataModel = UserDataModel(nickname, gender, city, age, uid)
                        FirebaseRef.userInfoRef.child(uid).setValue(userDataModel)
                        Log.d(TAG, "nickName : " + userDataModel.nickname  + " gender : " + userDataModel.gender + ", city: " + userDataModel.city + ", age :" + userDataModel.age)

                        // 계정 생성 성공 시 바로 로그인 할 것인지
//                        val builder = AlertDialog.Builder(this)
//                        builder.setTitle("회원가입 성공")
//                            .setMessage("로그인 하시겠습니까?")
//                            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
//                                // 회원가입 성공 후 로그인시 바로 메인 Activity로 이동
//                                val intent = Intent(this, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
//
//                                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
//                            })
//                            .setNegativeButton("취소",
//                                DialogInterface.OnClickListener { dialog, i ->
//                                })
//                        builder.show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail: 계정 생성 실패", task.exception)
                        Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }


}
