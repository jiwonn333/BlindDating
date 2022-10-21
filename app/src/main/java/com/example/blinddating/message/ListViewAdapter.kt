package com.example.blinddating.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.blinddating.R
import com.example.blinddating.auth.UserDataModel

class ListViewAdapter(val context: Context, val items: MutableList<UserDataModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // view 연결 부분
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.list_view_item, parent, false)
        }
        val userLikeName = convertView!!.findViewById<TextView>(R.id.otherName)
        userLikeName.text = items[position].nickname

        return convertView!!
    }
}