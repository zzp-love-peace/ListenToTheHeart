package com.zzp.dtrip.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.google.gson.Gson
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.ChatActivity
import com.zzp.dtrip.data.ChatMsg
import com.zzp.dtrip.util.UserInformation
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import kotlin.concurrent.thread

class ChatService : Service() {

    private lateinit var client: Client

    private val mBinder = ClientBinder()

    companion object {
        private const val TAG = "ChatService"
        private const val HEART_BEAT_RATE: Long = 10000 // 每隔10秒进行一次对长连接的心跳检测
    }

    // 将继承WebSocket的类写成子类
    open inner class Client(uri: URI) : WebSocketClient(uri) {
        override fun onOpen(handshakedata: ServerHandshake) {
            Log.d("JWebSocketClient", "onOpen()")

            Log.d("ChatService", "WebSocket连接成功")
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onMessage(message: String) {
            Log.d("JWebSocketClient", "onMessage()")

            Log.d(TAG, "收到的消息: $message")
//                val gson = Gson()
//                val chatMessage = gson.fromJson(message, ChatMessage::class.java)
            val intent = Intent("com.tangt.learnheart.receivemsg")
                .putExtra("message", message)
            intent.setPackage(packageName)
            sendBroadcast(intent)

            sendNotification(message)
        }

        override fun onClose(code: Int, reason: String, remote: Boolean) {
            Log.d("JWebSocketClient", "onClose()")
        }

        override fun onError(ex: Exception) {
            Log.d("JWebSocketClient", "onError()")
        }
    }

    // 用于Activity和Service通讯
    inner class ClientBinder : Binder() {
        fun getService() = this@ChatService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 初始化webSocket
        initSocketClient()
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        closeConnect()
        mHandler.removeCallbacks(heartBeatRunnable)
        super.onDestroy()
    }

    /**
     * 初始化WebSocket连接
     */
    private fun initSocketClient() {
        val uri = URI.create("ws://101.34.85.209:5250/websocket/${UserInformation.id}")
        client = Client(uri)
        connect()
    }

    /**
     * 连接WebSocket
     */
    private fun connect() {
        thread {
            try {
                //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
//                client.connectBlocking()
                Log.d(TAG, "connect: ${client.connectBlocking()}")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 断开连接
     */
    private fun closeConnect() {
        try {
            client.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送消息
     *
     * @param json
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(json: String) {
//        Log.d(TAG, "发送的消息：$message")

        Log.d(TAG, json)
        client.send(json)
    }

    fun clientIsOpen() = client.isOpen


//    -----------------------------------消息通知--------------------------------------------------------

    /**
     * 发送通知
     *
     * @param content
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(content: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "message",
                "MessageNotification",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, ChatActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val gson = Gson()
        val chatMessage = gson.fromJson(content, ChatMsg::class.java)
        val notification = NotificationCompat.Builder(this, "message")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.heart_listening)
            .setContentTitle(chatMessage.fromUserId)
            .setContentText(chatMessage.context)
            .setVisibility(VISIBILITY_PUBLIC)
            .setWhen(System.currentTimeMillis())
            // 向通知添加声音、闪灯和振动效果
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_ALL or Notification.DEFAULT_SOUND)
            .setContentIntent(pi)
            .build()
        manager.notify(1, notification) // id要保证唯一
    }


//    -------------------------------------WebSocket心跳检测------------------------------------------------

    private val mHandler = Handler(Looper.getMainLooper())
    private val heartBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "心跳检测WebSocket连接状态 ${client.isOpen}")
            if (client.isClosed) {
                reconnectWs()
            }
            //定时对长连接进行心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE)
        }
    }

    /**
     * 开启重连
     */
    private fun reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable)
        thread {
            try {
                Log.d(TAG, "开启重连")
                client.reconnectBlocking()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}