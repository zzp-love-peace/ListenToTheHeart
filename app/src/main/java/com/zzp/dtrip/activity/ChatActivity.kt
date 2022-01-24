package com.zzp.dtrip.activity

import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zzp.dtrip.R
import com.zzp.dtrip.adapter.ChatMsgAdapter
import com.zzp.dtrip.data.ChatMsg
import com.zzp.dtrip.database.Chat
import com.zzp.dtrip.database.ChatDao
import com.zzp.dtrip.database.ChatDatabase
import com.zzp.dtrip.service.ChatService
import com.zzp.dtrip.util.UserInformation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

@RequiresApi(Build.VERSION_CODES.O)
class ChatActivity : AppCompatActivity() {

    private lateinit var binder: ChatService.ClientBinder
    private lateinit var chatService: ChatService

    private lateinit var sendBtn: Button
    private lateinit var msgEdit: EditText
    private lateinit var msgRecyclerView: RecyclerView

    private lateinit var msgList: ArrayList<ChatMsg>
    private lateinit var chatMsgReceiver: ChatMsgReceiver
    private var adapter: ChatMsgAdapter? = null

    private lateinit var chatDao: ChatDao
    private lateinit var chatDatabase: ChatDatabase
    private var chat: Chat? = null

    private lateinit var toUserId: String

    companion object {
        private const val TAG = "ChatActivity"
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

    inner class ChatMsgReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("message") ?: ""
            Log.d(TAG, "onReceive: $message")

            val gson = Gson()
            val chatMessage = gson.fromJson(message, ChatMsg::class.java)
            Log.d(TAG, "onReceive: ${chatMessage.context} ${chatMessage.fromUserId} ${chatMessage.toUserId} ${chatMessage.time}")
            msgList.add(chatMessage)
            adapter?.notifyItemInserted(msgList.size - 1)
            msgRecyclerView.scrollToPosition(msgList.size - 1)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toUserId = intent.getStringExtra("toUserId") ?: ""

//        //启动服务
//        startChatService()
        //绑定服务
        bindService()
        //注册广播
        doRegisterReceiver()
        //检测通知是否开启
        checkNotification()

        findViewById()
        initView()

        initChatDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(chatMsgReceiver)
        unbindService(serviceConnection)
//        val intent = Intent(this, ChatService::class.java)
//        stopService(intent)

        // 存储聊天记录
        thread {
            if (chat == null) {
                chatDao.insertChat(Chat(UserInformation.id.toString(), toUserId, msgList))
            } else {
                chatDao.updateChat(chat!!)
            }
            Log.d(TAG, "onDestroy: ${chatDao.chatsCount()}")
            Log.d(TAG, "onDestroy: ${chatDao.loadChat(UserInformation.id.toString(), toUserId).chatList.size}")
        }
    }

    /**
     * 启动服务(WebSocket客户端服务)
     */
    private fun startChatService() {
        val intent = Intent(this, ChatService::class.java)
        startService(intent)
        Log.d(TAG, "startChatService: 启动chatService")
    }

    /**
     * 绑定服务
     */
    private fun bindService() {
        Log.d(TAG, "bindService: 开始绑定")
        val intent = Intent(this, ChatService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)    // 绑定Service
    }

    /**
     * 动态注册广播
     */
    private fun doRegisterReceiver() {
        chatMsgReceiver = ChatMsgReceiver()
        val filter = IntentFilter("com.tangt.learnheart.receivemsg")
        registerReceiver(chatMsgReceiver, filter)
    }

    private fun findViewById() {
        sendBtn = findViewById(R.id.btn_send)
        msgEdit = findViewById(R.id.msg_edit)
        msgRecyclerView = findViewById(R.id.recycler_chat)
    }

    private fun initView() {
        //监听输入框变化
        msgEdit.addTextChangedListener {
            if (msgEdit.text.isNotEmpty()) {

            }
        }
        msgEdit.setOnClickListener {
            thread {
                Thread.sleep(100)
                runOnUiThread {
                    msgRecyclerView.scrollToPosition(msgList.size - 1)
                }
            }
        }
        msgRecyclerView.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {
                Log.d(TAG, "initView: ")
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    msgEdit.windowToken,
                    0
                )
                msgEdit.clearFocus()
            }
            return@setOnTouchListener false
        }
        sendBtn.setOnClickListener {
            if (msgEdit.text.isEmpty()) {
                Toast.makeText(this, "消息不能为空哦", Toast.LENGTH_SHORT).show()
            } else {
                val content = msgEdit.text.toString()
                if (chatService.clientIsOpen()) {
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val time = current.format(formatter)
                    val chatMsg = ChatMsg(content, UserInformation.id.toString(), toUserId, time)
                    val json = Gson().toJson(chatMsg)
                    chatService.sendMessage(json)

                    //暂时将发送的消息加入消息列表，实际以发送成功为准（也就是服务器返回你发的消息时）
//                    val chatMsg = ChatMsg(content, UserInformation.id.toString(), toUserId, )
                    msgList.add(chatMsg)
//                    adapter?.notifyDataSetChanged()
                    adapter?.notifyItemInserted(msgList.size - 1)
                    msgRecyclerView.scrollToPosition(msgList.size - 1)
                    msgEdit.text.clear()
                } else {
                    Toast.makeText(this, "连接已断开, 请稍等或重启App", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun initChatMsgRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        msgRecyclerView.layoutManager = layoutManager
        adapter = ChatMsgAdapter(msgList)
        msgRecyclerView.adapter = adapter
        msgRecyclerView.scrollToPosition(msgList.size - 1)
    }

    /**
     * 加载聊天记录
     */
    private fun initChatDatabase() {
        chatDatabase = ChatDatabase.getDatabase(this)
        chatDao = chatDatabase.chatDao()
        thread {
            Log.d(TAG, "initChatDatabase: ${chatDao.chatsCount()}")
//            if (chatDao.chatsCount() > 0) {
//                msgList = chatDao.loadChat(UserInformation.id.toString(),
//                    toUserId).chatList as ArrayList<ChatMsg>
//                Log.d(TAG, "initChatDatabase: ${msgList.size}")
//            }
            chat = chatDao.loadChat("${UserInformation.id}", toUserId)
            msgList = if (chat != null) {
                chat?.chatList as ArrayList<ChatMsg>
            } else {
                ArrayList()
            }
            Log.d(TAG, "initChatDatabase: $chat")
//            runOnUiThread {
//                Log.d(TAG, "initChatDatabase: $msgList")
//                adapter?.notifyItemInserted(msgList.size - 1)
//                adapter?.notifyDataSetChanged()
//                msgRecyclerView.scrollToPosition(msgList.size - 1)
//            }
            runOnUiThread {
                initChatMsgRecyclerView()
            }
        }
//        adapter?.notifyItemInserted(msgList.size - 1)
    }

//    /**
//     * 子线程更新UI
//     */
//    val updateUI = 1
//    private val handler = object : Handler(Looper.getMainLooper()) {
//        override fun handleMessage(msg: Message) {
//            // 在这里可以进行UI操作
//            when (msg.what) {
//                updateUI -> {
//                    Log.d(TAG, "handleMessage: ${msgList.size} $adapter")
////                    adapter?.notifyItemInserted(msgList.size - 1)
//                    adapter?.notifyDataSetChanged()
//                    msgRecyclerView.scrollToPosition(msgList.size - 1)
//                }
//            }
//        }
//    }

    /**
     * 检测是否开启通知
     */
    private fun checkNotification() {
        if (!isNotificationEnabled()) {
            AlertDialog.Builder(this).apply {
                setTitle("温馨提示")
                setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                setPositiveButton("确定") { _, _ ->
                    setNotification()
                }
                setNegativeButton("取消") { _, _ ->
                    DialogInterface.OnCancelListener { }
                }
                show()
            }
        }
    }

    /**
     * 如果没有开启通知,跳转值设置界面
     */
    private fun setNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                .putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
            startActivity(intent)
        }
    }

    /**
     * 获取通知权限,检测是否开启了系统通知
     */
    private fun isNotificationEnabled() =
        NotificationManagerCompat.from(this).areNotificationsEnabled()

}