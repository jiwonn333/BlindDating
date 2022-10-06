package com.example.blinddating.setting

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blinddating.R
import com.example.blinddating.auth.UserDataModel
import com.example.blinddating.utils.AppUtil
import com.example.blinddating.utils.FirebaseAuthUtils
import com.example.blinddating.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MyEditPageActivity : AppCompatActivity() {

    private val TAG = "MyEditPageActivity"

    private val uid = FirebaseAuthUtils.getUid()
    lateinit var profileUpdateImage : ImageView
    private val updateData = mutableListOf<UserDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_page)

        getMyData()

        // 핸드폰에 있는 이미지 가져오기
        profileUpdateImage = findViewById(R.id.etMyImage)
        val getImageUri = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { uri ->
            profileUpdateImage.setImageURI(uri)
        })
        // 이미지 클릭 시 Firebase storage에 저장
        profileUpdateImage.setOnClickListener {
            // 결과를 위한 활동 실행
            getImageUri.launch("image/*")
        }

        val btnUpdate = findViewById<Button>(R.id.btnUpdate)

        btnUpdate.setOnClickListener {
            val uid = findViewById<TextView>(R.id.etMyUid).text.toString()
            val email = findViewById<EditText>(R.id.tvMyEmail).text.toString()
            val nickName = findViewById<EditText>(R.id.etMyNickname).text.toString()
            val age = findViewById<EditText>(R.id.etMyAge).text.toString()
            val city = findViewById<EditText>(R.id.etMyCity).text.toString()
            val gender = findViewById<EditText>(R.id.etMyGender).text.toString()

            updateData(uid, email, nickName, age, city, gender)



        }
    }

    private fun updateData(uid: String, email: String, nickName: String, age: String, city: String, gender: String) {
        if (FirebaseAuthUtils.getUid() == uid) {
            updateData.set(0, UserDataModel(email))
            Log.d(TAG, "usersDataList : " + updateData)

            // FirebaseRef.userInfoRef.setValue(updateData)

            // 이미지 저장 주소 설정 Cloud Storage 설정
            val storage = Firebase.storage
            val storageRef = storage.reference.child("$uid.png")

            // Get the data from an ImageView as bytes
            profileUpdateImage.isDrawingCacheEnabled = true
            profileUpdateImage.buildDrawingCache()
            val bitmap = (profileUpdateImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener {

            }.addOnSuccessListener { taskSnapshot ->

            }

            AppUtil.showToast(this, "수정되었습니다.")
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
            finish()
        } else {

        }


    }

    private fun getMyData() {
        val myEmail = findViewById<EditText>(R.id.tvMyEmail)
        val myImage = findViewById<ImageView>(R.id.etMyImage)
        val myUid = findViewById<TextView>(R.id.etMyUid)
        val myNickname = findViewById<EditText>(R.id.etMyNickname)
        val myAge = findViewById<EditText>(R.id.etMyAge)
        val myCity = findViewById<EditText>(R.id.etMyCity)
        val myGender = findViewById<EditText>(R.id.etMyGender)


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG,"dataSnapshot : " + dataSnapshot.getValue(UserDataModel::class.java))
                val data = dataSnapshot.getValue(UserDataModel::class.java)

                myUid.append(data?.uid)
                myEmail.append(data?.email)
                myNickname.append(data?.nickname)
                myAge.append(data?.age)
                myCity.append(data?.city)
                myGender.append(data?.gender)

                val storageRef = Firebase.storage.reference.child(data!!.uid + ".png")
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