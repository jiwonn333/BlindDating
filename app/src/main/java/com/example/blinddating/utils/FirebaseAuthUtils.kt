package com.example.blinddating.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseAuthUtils {
    companion object {
        private lateinit var auth: FirebaseAuth
        private lateinit var storage: FirebaseStorage

        // 현재 로그인한 사용자 가져오기, 없으면 null 반환, 인증객체가 초기화 되지 않은 경우에도 null 반환 가능
        fun getUid(): String {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()
        }

        fun getStorage(): StorageReference {
            storage = FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage.reference
            return storageRef.child(getUid()+".png")
        }


    }
    // 자바
//    public static final int MAX_AGE = 500; --> Person 클래스
//    public static void main() {
//     System.out.println(Person.MAX_AGE);
//     }

//    // kotlin 으로
//    class Person {
//        companion object {
//            const val MAX_AGE: Int = 500
//        }
//    }
//    fun main() {
//        print(Person.MAX_AGE)
//    }
}