package com.zzp.dtrip.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.NewFriendActivity
import com.zzp.dtrip.adapter.FriendAdapter
import com.zzp.dtrip.data.FriendWithType
import com.zzp.dtrip.data.IdBody
import com.zzp.dtrip.data.Info
import com.zzp.dtrip.util.NetWork
import com.zzp.dtrip.util.UserInformation
import com.zzp.dtrip.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FriendFragment()
        private const val TAG = "FriendFragment"
    }

    private lateinit var newFriendLayout: FrameLayout
    private lateinit var newGroupLayout: FrameLayout
    private lateinit var friendRecycler: RecyclerView
    private lateinit var noFriendText: TextView

    private var friendAdapter: FriendAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewById(view)
        initRecyclerView(ArrayList())
        Log.d(TAG, "onViewCreated: ")
        newFriendLayout.setOnClickListener {
            startActivity(Intent(requireContext(), NewFriendActivity::class.java))
        }
        newGroupLayout.setOnClickListener {

        }
    }

//    第一次启动和回到该活动（main）时回调
    override fun onStart() {
        super.onStart()
        getFriendToRecycler()
    }

    private fun findViewById(root: View?) {
        root?.apply {
            newFriendLayout = findViewById(R.id.new_friend_layout)
            newGroupLayout = findViewById(R.id.new_group_layout)
            friendRecycler = findViewById(R.id.friend_recycler)
            noFriendText = findViewById(R.id.no_friend_text)
        }
    }

    private fun initRecyclerView(list: ArrayList<FriendWithType>) {
        val layoutManager = LinearLayoutManager(requireContext())
        friendRecycler.layoutManager = layoutManager
        friendAdapter = FriendAdapter(requireActivity(), list)
        friendRecycler.adapter = friendAdapter
    }

    private fun getFriendToRecycler() {
        GlobalScope.launch(Dispatchers.Main) {
            val list = NetWork.getFriendListFromInfoList(NetWork.getFriendId(UserInformation.id, ::getFriendList))
            friendAdapter?.setList(list, FriendWithType.MY_FRIEND)
            friendAdapter?.notifyDataSetChanged()
            noFriendText.visibility = if (list.isNotEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
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


}