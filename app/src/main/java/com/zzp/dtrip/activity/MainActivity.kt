package com.zzp.dtrip.activity


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.huawei.hms.mlsdk.common.MLApplication
import com.zzp.dtrip.R
import com.zzp.dtrip.fragment.ChatFragment
import com.zzp.dtrip.fragment.FriendFragment
import com.zzp.dtrip.service.ChatService
import com.zzp.dtrip.util.ActivityCollector
import com.zzp.dtrip.util.UserInformation
import com.zzp.dtrip.util.showToast
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private val messageFragment = ChatFragment.newInstance()
    private val friendFragment = FriendFragment.newInstance()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var usernameText: TextView

    private lateinit var binder: ChatService.ClientBinder
    private lateinit var chatService: ChatService

    companion object {
        private const val TAG = "MainActivity"
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "服务与活动成功绑定")
            binder = service as ChatService.ClientBinder
            chatService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "服务与活动成功断开")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCollector.addActivity(this)
        MLApplication.getInstance().apiKey =
            "CgF6e3x9L8tbJ7yLqpxTYQQhmiVvF4tdvG5CEqxrxMnm5EHxq2uBjzork9ye1W6tllgzBiZPHx1NxDQlD+B5fy3J"
        findViewById()

        startService()
//        bindService()

        initToolbar()
        initBottomFragment()
        doNavigationView()
    }

    private fun startService() {
        val intent = Intent(this, ChatService::class.java)
        startService(intent)
    }

//    private fun bindService() {
//        val intent = Intent(this, ChatService::class.java)
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)    // 绑定Service
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.add_friend -> {
                val intent = Intent(this, AddFriendActivity::class.java)
                startActivity(intent)
            }
            R.id.add_group -> "加群".showToast()
        }
        return true
    }

    private fun findViewById() {
        toolbar = findViewById(R.id.main_toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        usernameText = navView.getHeaderView(0).findViewById(R.id.username_text)
    }

    private fun initToolbar() {
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_add)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun initBottomFragment() {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, messageFragment)
        transaction.add(R.id.nav_host_fragment, friendFragment)
        transaction.hide(friendFragment)
        transaction.commit()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            it.isChecked = true
            val transaction2 = fragmentManager.beginTransaction()
            toolbar.title = when (it.itemId) {
                R.id.navigation_message -> {
                    transaction2.hide(friendFragment)
                    transaction2.show(messageFragment)
                    "消息"
                }
                R.id.navigation_friend -> {
                    transaction2.hide(messageFragment)
                    transaction2.show(friendFragment)
                    "联系人"
                }
                else -> ""
            }
            transaction2.commit()
            false
        }
    }

    private fun doNavigationView() {
        usernameText.text = UserInformation.username
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_personal_info -> {
                    val intent = Intent(this, InformationActivity::class.java)
                    startActivity(intent)
                }

                R.id.action_personal_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_gesture_analyze -> {
                    val intent = Intent(this, LiveHandGestureAnalyseActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_speak_gesture -> {
                    val intent = Intent(this, GestureShowActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_eyes_saying -> {
                    val intent = Intent(this, SocialActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_sound_alarm -> {
                    val intent = Intent(this, SoundActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}