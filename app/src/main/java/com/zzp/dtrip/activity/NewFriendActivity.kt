package com.zzp.dtrip.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.zzp.dtrip.R
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
import kotlin.concurrent.thread

class NewFriendActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var requestRecycler: RecyclerView
    private lateinit var noRequestText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var requestAdapter: FriendAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_friend)
        findViewById()
        setSupportActionBar(toolbar)
        initRecyclerView(ArrayList())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getRequestToRecycler()
        swipeRefreshLayout.setColorSchemeResources(R.color.blue)
        swipeRefreshLayout.setOnRefreshListener { refreshRequest(requestAdapter!!) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_friend_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.add_friend -> {
                val intent = Intent(this, AddFriendActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun findViewById() {
        toolbar = findViewById(R.id.new_friend_toolbar)
        requestRecycler = findViewById(R.id.new_friend_recycler)
        noRequestText = findViewById(R.id.no_request_text)
        progressBar = findViewById(R.id.new_friend_progressbar)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
    }

    private fun initRecyclerView(list: ArrayList<FriendWithType>) {
        val layoutManager = LinearLayoutManager(this)
        requestRecycler.layoutManager = layoutManager
        requestAdapter = FriendAdapter(this, list)
        requestRecycler.adapter = requestAdapter
    }

    private suspend fun getRequestList(myId: Int) : List<Info> {
        return try {
            val response = NetWork.getRequestList(IdBody(myId))
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

    private fun getRequestToRecycler() {
        GlobalScope.launch(Dispatchers.Main) {
            val list = NetWork.getFriendListFromInfoList(NetWork.getFriendId(UserInformation.id, ::getRequestList))
            requestAdapter?.setList(list, FriendWithType.NEW_FRIEND)
            requestAdapter?.notifyDataSetChanged()
            noRequestText.visibility = if (list.isNotEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun refreshRequest(adapter: FriendAdapter) {
        GlobalScope.launch(Dispatchers.Main) {
            val list = NetWork.getFriendListFromInfoList(NetWork.getFriendId(UserInformation.id, ::getRequestList))
            requestAdapter?.setList(list, FriendWithType.NEW_FRIEND)
            requestAdapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}