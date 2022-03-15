package com.zzp.dtrip.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.ChatActivity
import com.zzp.dtrip.data.FriendOfMsg
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context, private val chatList: List<FriendOfMsg>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgItem: View = view.findViewById(R.id.chat_message_item)
        val msgImage: CircleImageView = view.findViewById(R.id.circleImageView)
        val msgName: TextView = view.findViewById(R.id.msg_name_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.chat_son_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = chatList[position].friendId
        holder.msgItem.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("toUserId", id)
            context.startActivity(intent)
        }
        holder.msgName.text = chatList[position].friendName
    }


    override fun getItemCount() = chatList.size

}