package com.example.blinddating.slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinddating.R
import com.example.blinddating.auth.UserDataModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CardStackAdapter(val context: Context, val items: List<UserDataModel>) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) {
        holder.binding(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.profileImageArea)
        var nickname = itemView.findViewById<TextView>(R.id.name)
        var age = itemView.findViewById<TextView>(R.id.age)
        var city = itemView.findViewById<TextView>(R.id.city)

        fun binding(data: UserDataModel) {
            val storageImgRef = Firebase.storage.reference.child(data.uid + ".png")
            storageImgRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Glide.with(context).load(downloadUri).into(image)
                } else {
                    // else일때...
                }
            })

            nickname.text = data.nickname
            age.text = data.age
            city.text = data.city

        }
    }


}