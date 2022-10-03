package com.example.blinddating.auth

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.MainActivity
import com.example.blinddating.R
import com.example.blinddating.utils.AppUtil
import com.example.blinddating.utils.FirebaseRef
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "JoinActivity"

    private var email = ""
    private var password = ""
    private var passwordCheck = ""

    // 데이터베이스에 저장할 정보 (닉네임, 성별, 지역, 나이, UID)
    private var uid = ""
    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = "" // 문자로 받아올 예정

    lateinit var profileImageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        // Initialize Firebase Auth
        auth = Firebase.auth

        profileImageView = findViewById(R.id.imageArea)

        val profileContents = findViewById<TextView>(R.id.imageTextArea)

        // 핸드폰에 있는 이미지 가져오기
        val getImageUri = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { uri ->
            profileImageView.setImageURI(uri)
            profileContents.visibility = View.GONE
        })


        // 이미지 클릭 시 Firebase storage에 저장
        profileImageView.setOnClickListener {
            // 결과를 위한 활동 실행
            getImageUri.launch("image/*")
        }


        val btnJoin = findViewById<Button>(R.id.btn_join_ok)
        btnJoin.setOnClickListener {
            email = findViewById<TextInputEditText>(R.id.inputEmail).text.toString().trim()
            password = findViewById<TextInputEditText>(R.id.inputPassword).text.toString().trim()
            passwordCheck = findViewById<TextInputEditText>(R.id.inputPasswordCheck).text.toString().trim()

            nickname = findViewById<TextInputEditText>(R.id.inputNickName).text.toString()
            gender = findViewById<TextInputEditText>(R.id.inputGender).text.toString()
            city = findViewById<TextInputEditText>(R.id.inputCity).text.toString()
            age = findViewById<TextInputEditText>(R.id.inputAge).text.toString()

            // null 체크
            if (checkValidation()) {
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

                            // 사용자 정보 저장 및 이미지 업로드
                            val userDataModel = UserDataModel(nickname, gender, city, age, uid)
                            FirebaseRef.userInfoRef.child(uid).setValue(userDataModel)
                            uploadImage(uid)

                            // 사용자 프로필 이미지 업로드


                            // 계정 생성 성공 시 바로 로그인 할 것인지
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("회원가입 성공")
                                .setMessage("로그인 하시겠습니까?")
                                .setPositiveButton(
                                    "확인",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        // 회원가입 성공 후 로그인시 바로 메인 Activity로 이동
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                        Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    })
                                .setNegativeButton("취소",
                                    DialogInterface.OnClickListener { dialog, i ->
                                        finish()
                                    })
                            builder.show()
                        } else {
                            Log.w(TAG, "createUserWithEmail: 계정 생성 실패", task.exception)
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(this, "Email 형식에 맞지 않습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "이미 존재하는 Email 입니다.", Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                Toast.makeText(this, "비밀번호를 6자리 이상 입력해주세요", Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e: Exception) {
                                Toast.makeText(this, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }


        }

    }

    private fun checkValidation(): Boolean {
        // 이미지 선택 안했을 때 toast 나중에 작성

        if (email?.isEmpty()!!) {
            AppUtil.showToast(this, "이메일을 입력해주세요.")
            return false
        } else if (password?.isEmpty()!! || passwordCheck?.isEmpty()!!) {
            AppUtil.showToast(this, "비밀번호를 입력해주세요.")
            return false
        } else if (password != passwordCheck) {
            AppUtil.showToast(this, "비밀번호가 일치하지 않습니다.")
            return false
        } else if (nickname?.isEmpty()!!) {
            AppUtil.showToast(this, "닉네임을 입력해주세요.")
            return false
        } else if (gender?.isEmpty()!!) {
            AppUtil.showToast(this, "성별을 입력해주세요.")
            return false
        } else if (city?.isEmpty()!!) {
            AppUtil.showToast(this, "지역을 입력해주세요.")
            return false
        } else if (age?.isEmpty()!!) {
            AppUtil.showToast(this, "나이를 입력해주세요.")
            return false
        }
        return true
    }

    private fun uploadImage(uid : String) {
        // 이미지 저장 주소 설정 Cloud Storage 설정
        val storage = Firebase.storage
        val storageRef = storage.reference.child("$uid.png")


        // Get the data from an ImageView as bytes
        profileImageView.isDrawingCacheEnabled = true
        profileImageView.buildDrawingCache()
        val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }


}
