package com.example.blinddating

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.auth.IntroActivity
import com.example.blinddating.auth.UserDataModel
import com.example.blinddating.slider.CardStackAdapter
import com.example.blinddating.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter

    // RecycelrView 만들 때 LayoutManager(LinearLayout, GridLayout)과 같은
    lateinit var manager: CardStackLayoutManager

    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingIcon = findViewById<ImageView>(R.id.logoutIcon)
        settingIcon.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                    val auth = Firebase.auth
                    auth.signOut()

                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent)
                    finish()

                    Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, i ->

                    })
            builder.show()

        }

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })

        // 문자열 임시 생성 (items 임시 생성)
        val testList = mutableListOf<String>()
        testList.add("a")
        testList.add("b")
        testList.add("c")

        cardStackAdapter = CardStackAdapter(baseContext, testList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

        getUserDataList()
    }

    private fun getUserDataList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val post = dataSnapshot.getValue<Post>()
//                Log.d(TAG, dataSnapshot.toString()) // 전체 불러옴

                for (dataModel in dataSnapshot.children) {
                    val userInfoList = dataModel.getValue(UserDataModel::class.java)
                    if (userInfoList != null) {
                        Log.d(TAG, userInfoList.nickname.toString())
                    }
                }
                
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        // 데이터 저장 경로
        // addValueEventListener - 경로의 전체 내용에 대한 변경 사항 읽고 수신 대기
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }
}