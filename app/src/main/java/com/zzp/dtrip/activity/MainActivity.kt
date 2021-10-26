package com.zzp.dtrip.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.huawei.hms.mlsdk.common.MLApplication
import com.zzp.dtrip.R
import com.zzp.dtrip.fragment.ChatFragment
import com.zzp.dtrip.fragment.FriendFragment

class MainActivity : AppCompatActivity() {


    private val messageFragment = ChatFragment.newInstance()
    private val friendFragment = FriendFragment.newInstance()

    private lateinit var navView: BottomNavigationView

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MLApplication.getInstance().apiKey =
            "CgF6e3x9L8tbJ7yLqpxTYQQhmiVvF4tdvG5CEqxrxMnm5EHxq2uBjzork9ye1W6tllgzBiZPHx1NxDQlD+B5fy3J"
        navView = findViewById(R.id.nav_view)
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, messageFragment)
        transaction.add(R.id.nav_host_fragment, friendFragment)
        transaction.hide(friendFragment)
        transaction.commit()
        navView.setOnNavigationItemSelectedListener {
            it.isChecked = true
            val transaction2 = fragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.navigation_message -> {
                    transaction2.hide(friendFragment)
                    transaction2.show(messageFragment)
                }
                R.id.navigation_friend -> {
                    transaction2.hide(messageFragment)
                    transaction2.show(friendFragment)
                }
            }
            transaction2.commit()
            false
        }
    }
}