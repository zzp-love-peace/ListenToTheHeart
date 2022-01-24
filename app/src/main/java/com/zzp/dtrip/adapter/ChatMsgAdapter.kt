package com.zzp.dtrip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.application.MyApplication
import com.zzp.dtrip.data.ChatMsg
import com.zzp.dtrip.util.UserInformation
import java.lang.IllegalArgumentException

class ChatMsgAdapter(private val msgList: List<ChatMsg>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class LeftViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leftTime: TextView = view.findViewById(R.id.left_time_text)
        val leftMsg: TextView = view.findViewById(R.id.left_msg)
    }

    private inner class RightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rightTime: TextView = view.findViewById(R.id.right_time_text)
        val rightMsg: TextView = view.findViewById(R.id.right_msg)
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.fromUserId.toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == UserInformation.id) {
        val view = LayoutInflater.from(MyApplication.context).inflate(R.layout.msg_right_item, parent, false)
        RightViewHolder(view)
    } else {
        val view = LayoutInflater.from(MyApplication.context).inflate(R.layout.msg_left_item, parent, false)
        LeftViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> {
                holder.leftTime.text = msg.time
                holder.leftMsg.text = msg.context
            }
            is RightViewHolder -> {
                holder.rightTime.text = msg.time
                holder.rightMsg.text = msg.context
            }
            else -> throw  IllegalArgumentException()
        }
    }

    override fun getItemCount() = msgList.size
}