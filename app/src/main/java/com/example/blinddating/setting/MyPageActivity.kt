package com.example.blinddating.setting

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blinddating.MainActivity
import com.example.blinddating.R
import com.example.blinddating.auth.IntroActivity
import com.example.blinddating.auth.UserDataModel
import com.example.blinddating.utils.AppUtil
import com.example.blinddating.utils.FirebaseAuthUtils
import com.example.blinddating.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MyPageActivity : AppCompatActivity() {

    private val TAG = "MyPageActivity"

    private lateinit var auth: FirebaseAuth
    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        getMyData()

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        val btnLeave = findViewById<Button>(R.id.btnLeave)
        btnLeave.setOnClickListener {
            showLeaveDialog()

        }

        val btnEdit = findViewById<Button>(R.id.btnEdit)
        btnEdit.setOnClickListener {
            val intentEditPage = Intent(baseContext, MyEditPageActivity::class.java)
            startActivity(intentEditPage)
           // getMyData()
        }
    }

    private fun showLogoutDialog() {
        // 로그아웃 dialog 창
        val builder = AlertDialog.Builder(this)
        builder.setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                val auth = Firebase.auth
                auth.signOut()

                val intent = Intent(this, MainActivity::class.java)
                setResult(RESULT_OK, intent)
                finish()

                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, i ->

                })
        builder.show()
    }

    private fun showLeaveDialog() {
        // 로그아웃 dialog 창
        val builder = AlertDialog.Builder(this)
        builder.setTitle("탈퇴")
            .setMessage("탈퇴 하시겠습니까?")
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                val user = Firebase.auth.currentUser!!
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseRef.userInfoRef.removeValue()
                        FirebaseAuthUtils.getStorage().delete().addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    AppUtil.showToast(this, "이미지 삭제 성공ㅇㅇ")
                                }
                            })
                        Toast.makeText(this, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        setResult(10, intent)
                        finish()

                    }
                }




            })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, i ->

                })
        builder.show()
    }

    private fun getMyData() {
        val myImage = findViewById<ImageView>(R.id.myImage)
        val myNickname = findViewById<TextView>(R.id.myNickname)
        val myAge = findViewById<TextView>(R.id.myAge)
        val myGender = findViewById<TextView>(R.id.myGender)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "dataSnapshot.string : " + dataSnapshot.getValue(UserDataModel::class.java))
                val data = dataSnapshot.getValue(UserDataModel::class.java)
                    if (data == null) {
                        Log.e(TAG, "data is null~~~")
                    }

                    myNickname.text = data?.nickname
                    myAge.text = data?.age
                    myGender.text = data?.gender

                val storageRef = Firebase.storage.reference.child(data?.uid + ".png")
                storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Glide.with(baseContext).load(task.result).into(myImage)
                    }
                })

            }

            override fun onCancelled(databaseErrorf: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseErrorf.toException())
            }

        }

        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }
}