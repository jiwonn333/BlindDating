package com.example.blinddating.utils

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthUtils {
    companion object {
        private lateinit var auth: FirebaseAuth

        fun getUid(): String {
            auth = FirebaseAuth.getInstance()
            return auth.currentUser?.uid.toString()
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