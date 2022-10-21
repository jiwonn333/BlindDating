package com.example.blinddating

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private val CHANNEL_ID = "Test_Channel"


    // RecycelrView 만들 때 LayoutManager(LinearLayout, GridLayout)과 같은
    lateinit var manager: CardStackLayoutManager
    lateinit var cardStackAdapter: CardStackAdapter

    private val uid = FirebaseAuthUtils.getUid()
    private val usersDataList = mutableListOf<UserDataModel>()
    private var swipeCount = 0
    private lateinit var currentUserGender: String
    private lateinit var otherUid: String


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
                if (direction == Direction.Right) {
                    otherUid = usersDataList[swipeCount].uid.toString()
                    userLikeOtherUser(uid, otherUid)

                    // 내가 좋아요 한 사람의 좋아요 리스트 불러오기
                    getOtherUserLikeList(otherUid)
                }

                if (direction == Direction.Left) {

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
    
    // 사용자가 좋아요 한 사람의 좋아요 리스트 받아오기
    private fun getOtherUserLikeList(otherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val likeUserKey = dataModel.key.toString()

                    if (likeUserKey == uid) {
                        AppUtil.showToast(baseContext, "매칭 완료")
                        createNotificationChannel()
                        showNotification()
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

    private fun userLikeOtherUser(myUid: String, otherUid: String) {
        // Firebase에 저장
        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("true")
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
                        // 사용자와 다른 성별의 user 불러옴
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


    // notification 채널 등록
    private fun createNotificationChannel() {
        // NotificationChannel 생성하되 api26 이상에서 사용, 라이브러리가 아닌 새 클래스임
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            var descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // 시스템에 채널 등록
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification() {
        var notifyId = 123
        val intent = Intent(this, MyPageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_dating_icon)
            .setContentTitle("매칭완료!")
            .setContentText("내가 선택한 사람이 나를 좋아해요")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(notifyId, builder.build())
        }
    }

}