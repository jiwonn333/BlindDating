package com.example.blinddating

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddating.auth.IntroActivity
import com.example.blinddating.auth.UserDataModel
import com.example.blinddating.setting.MyPageActivity
import com.example.blinddating.slider.CardStackAdapter
import com.example.blinddating.utils.AppUtil
import com.example.blinddating.utils.FirebaseAuthUtils
import com.example.blinddating.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"


    // RecycelrView 만들 때 LayoutManager(LinearLayout, GridLayout)과 같은
    lateinit var manager: CardStackLayoutManager
    lateinit var cardStackAdapter: CardStackAdapter

    private val uid = FirebaseAuthUtils.getUid()
    private val usersDataList = mutableListOf<UserDataModel>()
    private var swipeCount = 0
    private lateinit var currentUserGender: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 설정아이콘 클릭 시
        val settingIcon = findViewById<ImageView>(R.id.settingIcon)
        settingIcon.setOnClickListener {
            // MyPageActivity 로 이동
            val myPageIntent = Intent(this, MyPageActivity::class.java)
            startForResult.launch(myPageIntent)
        }

        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                // 오른쪽, 왼쪽으로 스와이프 했을때 토스트메시지
                if (direction == Direction.Right) {
                    AppUtil.showToast(baseContext, "오른쪽")
                }

                if (direction == Direction.Left) {
                    AppUtil.showToast(baseContext, "왼쪽")
                }
                swipeCount += 1
                if (swipeCount == usersDataList.count()) {
                    // 유저정보 다시 불러오기
                    getUserDataList(currentUserGender)
                    AppUtil.showToast(baseContext, "사용자들을 새롭게 다시 불러옴")
                }


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

        cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardStackAdapter

//        getUserDataList(currentUserGender)
        getMyData()


    }

    // 현재 사용자 성별 받아오기
    private fun getMyData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserDataModel::class.java)
                currentUserGender = data?.gender.toString()
                getUserDataList(currentUserGender)
            }

            override fun onCancelled(databaseErrorf: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseErrorf.toException())
            }

        }

        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(currentGender: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    var user = dataModel.getValue(UserDataModel::class.java)
                    if (currentGender == user?.gender.toString()) {

                    } else {
                        usersDataList.add(user!!)
                    }

                }
                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        // addValueEventListener - 데이터 저장 경로의 전체 내용에 대한 변경 사항 읽고 수신 대기
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            } else if (result.resultCode == 10) {
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


}