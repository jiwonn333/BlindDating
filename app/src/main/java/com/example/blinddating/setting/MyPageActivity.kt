package com.example.blinddating.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blinddating.MainActivity
import com.example.blinddating.R
import com.example.blinddating.auth.UserDataModel
import com.example.blinddating.message.ListViewAdapter
import com.example.blinddating.utils.AppUtil
import com.example.blinddating.utils.FirebaseAuthUtils
import com.example.blinddating.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MyPageActivity : AppCompatActivity() {

    private val TAG = "MyPageActivity"

    private val uid = FirebaseAuthUtils.getUid()

    private val userLikeList = mutableListOf<UserDataModel>()
    private val userLikeListUid = mutableListOf<String>()

    lateinit var listViewAdapter: ListViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        val userListView = findViewById<ListView>(R.id.userLikeListView)
        listViewAdapter = ListViewAdapter(this, userLikeList)
        userListView.adapter = listViewAdapter

        getMyData()

        userListView.setOnItemClickListener { parent, view, position, id ->
            Log.d(TAG, "position uid : " + userLikeList[position].uid.toString())
            checkMatching(userLikeList[position].uid.toString())
        }

        // 내가 좋아요 한 사람의 리스트 받아오기
        getUserLikeList()

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        val btnLeave = findViewById<Button>(R.id.btnLeave)
        btnLeave.setOnClickListener {
            showLeaveDialog()
        }

        val btnUpdate = findViewById<Button>(R.id.btnEdit)
        btnUpdate.setOnClickListener {
            val intentEditPage = Intent(baseContext, MyEditPageActivity::class.java)
            startActivity(intentEditPage)
            // getMyData()
        }


    }

    private fun checkMatching(otherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d(TAG, otherUid)
                Log.d(TAG, dataSnapshot.toString())
                Log.d(TAG, "value: " + dataSnapshot.value.toString())
                Log.d(TAG, "children count: " + dataSnapshot.children.count())
                Log.d(TAG, "childrenCount: " + dataSnapshot.childrenCount)

                // 아무도 선택하지 않아서 count가 0일때
                if (dataSnapshot.children.count() == 0) {
                    AppUtil.showToast(baseContext, "날 선택 안했어요ㅜ.ㅜ")
                } else {

                    for (dataModel in dataSnapshot.children) {

                        val likeUserKey = dataModel.key.toString()
                        if (likeUserKey.equals(uid)) {
                            AppUtil.showToast(baseContext, "매칭되었어요!")
                        } else {
                            AppUtil.showToast(baseContext, "매칭이 되지 않았습니다.")

                        }
                    }
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        // addValueEventListener - 데이터 저장 경로의 전체 내용에 대한 변경 사항 읽고 수신 대기
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
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

                AppUtil.showToast(this, "로그아웃 되었습니다.")
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

                val storageRef = FirebaseAuthUtils.getStorageRef().child("images/"+ uid + ".png").name
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseRef.userInfoRef.child(uid).removeValue().addOnCompleteListener {
                            if (task.isSuccessful) {
                                AppUtil.showToast(this, "사용자 정보 데이터베이스 삭제 성공")
                            } else {
                                AppUtil.showToast(this, "사용자 정보 데이터베이스 삭제 실패")
                            }
                        }
                        FirebaseRef.userLikeRef.child(uid).removeValue().addOnCompleteListener {
                            if (task.isSuccessful) {
                                AppUtil.showToast(this, "사용자가 좋아요 누른 데이터베이스 삭제 성공")
                            } else {
                                AppUtil.showToast(this, "사용가 좋아요 누른 데이터베이스 삭제 실패")
                            }
                        }

                        FirebaseAuthUtils.getStorageRef().child(storageRef).delete()
                            .addOnSuccessListener {
                                if (task.isSuccessful) {
                                    AppUtil.showToast(this, "이미지 삭제 성공")
                                    Log.d(TAG, "이미지 삭제 성공")
                                } else {
                                    AppUtil.showToast(this, "이미지 삭제 실패")
                                    Log.e(TAG, "이미지 Ref : " + storageRef)
                                    Log.e(TAG, "이미지 삭제 실패 : " + task.exception.toString())
                                }
                            }



                        AppUtil.showToast(this, "탈퇴 되었습니다.")
                        val intent = Intent(this, MainActivity::class.java)
                        setResult(10, intent)
                        finish()

                    } else {
                        AppUtil.showToast(this, "탈퇴 오류가 있습니다.")
                        Log.e(TAG, task.exception.toString())
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
        val myMatchingList = findViewById<TextView>(R.id.myLikeList)

        val postListener = object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation", "SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserDataModel::class.java)
                if (data == null) {
                    Log.e(TAG, "data is null~~~")
                }

                myNickname.text = data?.nickname
                myAge.text = data?.age
                myGender.text = data?.gender
                myMatchingList.text = (data?.nickname + "님의 좋아요 리스트")

                FirebaseAuthUtils.getStorageRef()
                    .child(data?.uid + ".png").downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
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

    private fun getUserDataList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    var user = dataModel.getValue(UserDataModel::class.java)

                    if (userLikeListUid.contains(user?.uid)) {
                        // 내가 좋아요 한 사람들의 정보 가져오기
                        userLikeList.add(user!!)
                    }
                }
                // 데이터를 받아온 후 adapter 연결
                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }


    private fun getUserLikeList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    // 내가 좋아요 한 사람의 uid가 userLikeList에 들어있음
                    userLikeListUid.add(dataModel.key.toString())
                    Log.e(TAG, "내가 좋아요 한 사람 : " + userLikeListUid.toString())
                }
                getUserDataList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)
    }
}