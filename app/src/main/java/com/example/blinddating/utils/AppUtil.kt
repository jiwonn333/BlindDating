package com.example.blinddating.utils

import android.content.Context
import android.widget.Toast

class AppUtil {
    companion object {
        fun showToast(context: Context, msg: String) {
            return Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}