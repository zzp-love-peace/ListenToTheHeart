package com.zzp.dtrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.zzp.dtrip.R
import com.zzp.dtrip.adapter.FriendAdapter
import com.zzp.dtrip.data.*
import com.zzp.dtrip.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class AddFriendActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var searchButton: MaterialButton
    private lateinit var friendRecycler: RecyclerView
    private lateinit var notFindText: TextView
    private lateinit var progressBar: ProgressBar
    private var addFriendAdapter: FriendAdapter? = null

    private var username = ""

    companion object {
        private const val TAG = "AddFriendActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        findViewById()
        initRecyclerView(ArrayList())
        searchView.requestFocusFromTouch()
        searchButton.setOnClickListener {
            doSearch()
            hideSoftKeyboard(this)
        }
        val edit = searchView.findViewById<EditText>(R.id.search_src_text)
        edit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch()
                hideSoftKeyboard(this)
                true
            } else false
        }
    }

    private fun findViewById() {
        searchView = findViewById(R.id.search_view)
        searchButton = findViewById(R.id.search_button1)
        friendRecycler = findViewById(R.id.add_friend_recycler)
        notFindText = findViewById(R.id.not_find_text)
        progressBar = findViewById(R.id.add_friend_progressbar)
    }

    private fun initRecyclerView(list: ArrayList<FriendWithType>) {
        val layoutManager = LinearLayoutManager(this)
        friendRecycler.layoutManager = layoutManager
        addFriendAdapter = FriendAdapter(this, list)
        friendRecycler.adapter = addFriendAdapter
        friendRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun doSearch() {
        ArrayList<FriendWithType>().let {
            username = searchView.query.toString()
            if (username.trim().isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                addFriendAdapter?.setList(ArrayList(), FriendWithType.ADD_FRIEND)
                addFriendAdapter?.notifyDataSetChanged()
                GlobalScope.launch(Dispatchers.Main) {
                    val list = selectId(username)
                    for (id in list) {
                        val friend = NetWork.searchFriendFromId(id.toInt())
                        if (friend.id != -1) {
                            val flag = isFriend(UserInformation.id, friend.id)
                            if (flag) it.add(FriendWithType(friend, FriendWithType.MY_FRIEND))
                            else it.add(FriendWithType(friend, FriendWithType.ADD_FRIEND))
                        }
                    }
                    addFriendAdapter?.setList(it)
                    addFriendAdapter?.notifyDataSetChanged()
                    notFindText.visibility = if (it.isNotEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                    progressBar.visibility = View.GONE
                }
            } else {
                "不能为空哦".showToast()
            }
        }

    }

    private suspend fun selectId(username: String): List<String> {
        return try {
            val response = NetWork.searchIdFromUsername(SelectIdBody(username))
            if (response.errorCode == 0) {
                response.list
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            "出现异常啦".showToast()
            emptyList()
        }
    }

    private suspend fun isFriend(fromId: Int, toId: Int) =
        try {
            val response = NetWork.isFriend(FriendToFriendBody(fromId, toId))
            if (response.errorCode == 0) {
                response.message
            } else {
                false
            }
        } catch (e: Exception) {
            "出现异常啦".showToast()
            false
        }
}