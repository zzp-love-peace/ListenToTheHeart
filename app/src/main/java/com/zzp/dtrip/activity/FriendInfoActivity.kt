package com.zzp.dtrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.zzp.dtrip.R
import com.zzp.dtrip.data.FriendToFriendBody
import com.zzp.dtrip.util.NetWork
import com.zzp.dtrip.util.UserInformation
import com.zzp.dtrip.util.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class FriendInfoActivity : AppCompatActivity() {

    private lateinit var headImage: CircleImageView
    private lateinit var usernameText: TextView
    private lateinit var sexText: TextView
    private lateinit var sendButton: MaterialButton
    private lateinit var deleteButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_friend_info)
        findViewById()
        usernameText.text = intent.getStringExtra("username")
        sexText.text = intent.getStringExtra("sex")
        val id = intent.getIntExtra("id", -1)
        sendButton.setOnClickListener {

        }
        deleteButton.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("你确定要删除该好友吗？")
                setPositiveButton("确定") { _, _ ->
                    GlobalScope.launch(Dispatchers.Main) {
                        deleteFriend(UserInformation.id, id)
                        "删除成功".showToast()
                        finish()
                    }
                }
                setNegativeButton("取消") { _,_ -> }
                show()
            }
        }
    }

    private fun findViewById() {
        headImage = findViewById(R.id.friend_head_image)
        usernameText = findViewById(R.id.friend_username_text)
        sexText = findViewById(R.id.friend_sex_text)
        sendButton = findViewById(R.id.send_button)
        deleteButton = findViewById(R.id.delete_button)
    }

    private suspend fun deleteFriend(fromId: Int, toId: Int) {
        try {
            val response = NetWork.deleteFriend(FriendToFriendBody(fromId, toId))
            if (response.errorCode == 0) {
                "删除成功".showToast()
            } else {
                response.errorMsg.showToast()
            }
        } catch (e: Exception) {
            "出现异常啦".showToast()
        }
    }
}