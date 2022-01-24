package com.zzp.dtrip.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.zzp.dtrip.R
import com.zzp.dtrip.adapter.ChatAdapter
import com.zzp.dtrip.application.MyApplication
import com.zzp.dtrip.data.FriendOfMsg
import com.zzp.dtrip.data.IdBody
import com.zzp.dtrip.data.Info
import com.zzp.dtrip.util.NetWork
import com.zzp.dtrip.util.UserInformation
import com.zzp.dtrip.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
        private const val TAG = "ChatFragment"
    }

    private var adapter: ChatAdapter? = null
    private lateinit var chatRecycler: RecyclerView

    private lateinit var chatList: ArrayList<FriendOfMsg>

    private val KEY = "data_chatFriendOf${UserInformation.id}"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)

        findViewById(view)

        initChatList()

        initChatRecycler()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        saveChatFriend(requireContext(), KEY, chatList)
    }

    private fun findViewById(view: View) {
        chatRecycler = view.findViewById(R.id.chat_recycler)
    }

    private fun initChatRecycler() {
        val layoutManager = LinearLayoutManager(MyApplication.context)
        chatRecycler.layoutManager = layoutManager
        adapter = ChatAdapter(requireContext(), chatList)
        chatRecycler.adapter = adapter
        chatRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initChatList() {
        chatList = getChatFriend(requireContext(), KEY, FriendOfMsg::class.java) as ArrayList<FriendOfMsg>
        Log.d(TAG, "initChatList: ${chatList.isEmpty()}")
        if (chatList.isEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                val list = NetWork.getFriendListFromInfoList(NetWork.getFriendId(UserInformation.id, ::getFriendList))
                for (friend in list) {
                    chatList.add(FriendOfMsg(friend.id.toString(), friend.username))
                }
                adapter?.notifyDataSetChanged()
            }
        }
//        Log.d(TAG, "initChatList: ${chatList[chatList.lastIndex].friendId} ${chatList[chatList.lastIndex].friendName}")
    }

    private suspend fun getFriendList(myId: Int) : List<Info> {
        return try {
            val response = NetWork.getFriendList(IdBody(myId))
            if (response.errorCode == 0) {
                response.list
            } else {
                "出现异常啦".showToast()
                emptyList()
            }
        } catch (e: Exception) {
            "出现异常啦".showToast()
            emptyList()
        }
    }

    private fun <T> saveChatFriend(context: Context, key: String, dataList: List<T>) {
        val editor = requireContext().getSharedPreferences("data_chatFriend", Context.MODE_PRIVATE).edit()
        val gson = Gson()
        //转换成json数据,再保存
        val json = gson.toJson(dataList)
        editor.putString(key, json)
        editor.apply()
    }

    private fun <T> getChatFriend(context: Context, key: String, cls: Class<T>): List<T> {
        val sp = requireContext().getSharedPreferences("data_chatFriend", Context.MODE_PRIVATE)
        val dataList = ArrayList<T>()
        val json = sp.getString(key, "")
        if (json == "") {
            return ArrayList<T>()
        }
        val gson = Gson()

        // 使用泛型解析数据会报错,返回的数据类型是LinkedTreeMap
//        return gson.fromJson<ArrayList<T>>(json, object : TypeToken<List<T>>() {}.type)

        // 这样写太死
//        return gson.fromJson(json, object : TypeToken<List<T>>() {}.type)

        val array = JsonParser.parseString(json).asJsonArray
        for (jsonElement in array) {
            dataList.add(gson.fromJson(jsonElement, cls))
        }

        return dataList
    }

}