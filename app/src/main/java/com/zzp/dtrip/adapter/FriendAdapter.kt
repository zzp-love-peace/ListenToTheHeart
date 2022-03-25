package com.zzp.dtrip.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.FriendInfoActivity
import com.zzp.dtrip.data.Friend
import com.zzp.dtrip.data.FriendToFriendBody
import com.zzp.dtrip.data.FriendWithType
import com.zzp.dtrip.util.NetWork
import com.zzp.dtrip.util.UserInformation
import com.zzp.dtrip.util.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.util.Pair as UtilPair

class FriendAdapter(
    private var activity: Activity,
    private var friendsList: ArrayList<FriendWithType>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val AGREE = 0
        private const val REJECT = 1
    }

    inner class MyFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val headImage: CircleImageView = view.findViewById(R.id.friend_head_image)
        val friendUsername: TextView = view.findViewById(R.id.friend_username)
    }

    inner class AddFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headImage: CircleImageView = view.findViewById(R.id.friend_head_image)
        val friendUsername: TextView = view.findViewById(R.id.friend_username)
        val addButton: MaterialButton = view.findViewById(R.id.add_button)
    }

    inner class NewFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headImage: CircleImageView = view.findViewById(R.id.friend_head_image)
        val friendUsername: TextView = view.findViewById(R.id.friend_username)
        val agreeButton: MaterialButton = view.findViewById(R.id.agree_button)
        val rejectButton: MaterialButton = view.findViewById(R.id.reject_button)
    }

    override fun getItemViewType(position: Int): Int {
        val friendWithType = friendsList[position]
        return friendWithType.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        FriendWithType.MY_FRIEND -> {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
            MyFriendViewHolder(view)
        }
        FriendWithType.ADD_FRIEND -> {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.add_friend_item, parent, false)
            AddFriendViewHolder(view)
        }
        FriendWithType.NEW_FRIEND -> {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.new_friend_item, parent, false)
            NewFriendViewHolder(view)
        }
        else -> throw IllegalArgumentException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val friendWithType = friendsList[position]
        val friend = friendWithType.friend
        when (holder) {
            is MyFriendViewHolder -> {
                holder.friendUsername.text = friend.username
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, FriendInfoActivity::class.java)
                    intent.putExtra("username", friend.username)
                    intent.putExtra("sex", friend.sex)
                    intent.putExtra("id", friend.id)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        UtilPair.create(holder.headImage, "friend_head_image"),
                        UtilPair.create(holder.friendUsername, "friend_username_text")
                    )
                    activity.startActivity(intent, options.toBundle())
                }
            }

            is AddFriendViewHolder -> {
                holder.friendUsername.text = friend.username
                holder.addButton.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        addFriend(UserInformation.id, friend.id)
                    }
                }
            }
            is NewFriendViewHolder -> {
                holder.friendUsername.text = friend.username
                holder.agreeButton.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        doRequest(friend.id, UserInformation.id, AGREE)
                        removeRequest(position)
                    }
                }
                holder.rejectButton.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        doRequest(friend.id, UserInformation.id, REJECT)
                        removeRequest(position)
                    }
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = friendsList.size

    fun setList(list: List<Friend>, type: Int) {
        ArrayList<FriendWithType>().apply {
            for (friend in list) {
                add(FriendWithType(friend, type))
            }
            friendsList = this
        }
    }

    fun setList(list: ArrayList<FriendWithType>) {
        friendsList = list
    }

    private suspend fun addFriend(fromId: Int, toId: Int) {
        try {
            val response = NetWork.addFriend(FriendToFriendBody(fromId, toId))
            if (response.errorCode == 0) {
                "已发出申请，请等待对方同意".showToast()
            } else {
                response.errorMsg.showToast()
            }
        } catch (e: Exception) {
            "出现异常啦".showToast()
        }
    }

    private fun removeRequest(position: Int) {
        friendsList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    private suspend fun doRequest(fromId: Int, toId: Int, type: Int) {
        try {
            val response = if (type == AGREE)
                NetWork.agreeRequest(FriendToFriendBody(fromId, toId))
            else NetWork.rejectRequest(FriendToFriendBody(fromId, toId))
            if (response.errorCode == 0 && type == AGREE) {
                "已同意好友申请".showToast()
            } else if (response.errorCode == 0 && type == REJECT) {
                "已拒绝好友申请".showToast()
            } else {
                response.errorMsg.showToast()
            }
        } catch (e: java.lang.Exception) {
            "出现异常啦".showToast()
        }
    }
}