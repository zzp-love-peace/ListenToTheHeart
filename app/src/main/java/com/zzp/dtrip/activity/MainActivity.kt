package com.zzp.dtrip.activity


import android.content.Intent
import android.os.Bundle
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
import com.zzp.dtrip.util.ActivityCollector
import com.zzp.dtrip.util.UserInformation
import com.zzp.dtrip.util.showToast


class MainActivity : AppCompatActivity() {
    private val messageFragment = ChatFragment.newInstance()
    private val friendFragment = FriendFragment.newInstance()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var usernameText: TextView

    companion object {
        private const val TAG = "MainActivity"
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
        initToolbar()
        initBottomFragment()
        doNavigationView()

//        try {
//            val leftDraggerField: Field = drawerLayout.javaClass
//                .getDeclaredField("mLeftDragger") //通过反射获得DrawerLayout类中mLeftDragger字段
//            leftDraggerField.isAccessible = true //私有属性要允许修改
//            val vdh =
//                leftDraggerField[drawerLayout] as ViewDragHelper //获取ViewDragHelper的实例, 通过ViewDragHelper实例获取mEdgeSize字段
//            val edgeSizeField =
//                vdh.javaClass.getDeclaredField("mEdgeSize") //依旧是通过反射获取ViewDragHelper类中mEdgeSize字段, 这个字段控制边缘触发范围
//            edgeSizeField.isAccessible = true //依然是私有属性要允许修改
//            val edgeSize = edgeSizeField.getInt(vdh) //这里获得mEdgeSize真实值
//            Log.d(TAG, "mEdgeSize: $edgeSize") //这里可以打印一下看看值是多少
//
//            //Start 获取手机屏幕宽度，单位px
//            val point = Point()
//            windowManager.defaultDisplay.getRealSize(point)
//            //End 获取手机屏幕
//            Log.d(TAG, "point: " + point.x) //依然可以打印一下看看值是多少
//            edgeSizeField.setInt(
//                vdh,
//                edgeSize.coerceAtLeast(point.x))
//            Log.d(TAG, edgeSizeField.getInt(vdh).toString())
        //这里设置mEdgeSize的值！！！，Math.max函数取得是最大值，也可以自己指定想要触发的范围值，如: 500,注意单位是px
            //写到这里已经实现了，但是你会发现，如果长按触发范围，侧边栏也会弹出来，而且速度不太同步，稳定

//            //Start 解决长按会触发侧边栏
//            //长按会触发侧边栏是DrawerLayout类的私有类ViewDragCallback中的mPeekRunnable实现导致，我们通过反射把它置空
//            val leftCallbackField: Field = drawerLayout.javaClass
//                .getDeclaredField("mLeftCallback") //通过反射拿到私有类ViewDragCallback字段
//            leftCallbackField.isAccessible = true //私有字段设置允许修改
//            val vdhCallback =
//                leftCallbackField[drawerLayout] as ViewDragHelper.Callback //ViewDragCallback类是私有类，我们返回类型定义成他的父类
//            val peekRunnableField =
//                vdhCallback.javaClass.getDeclaredField("mPeekRunnable") //我们依然是通过反射拿到关键字段，mPeekRunnable
//            peekRunnableField.isAccessible = true //不解释了
//            //定义一个空的实现
//            val nullRunnable = Runnable { }
//            peekRunnableField[vdhCallback] = nullRunnable //给mPeekRunnable字段置空
//            //End 解决长按触发侧边栏
//        } catch (e: NoSuchFieldException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }

    }

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