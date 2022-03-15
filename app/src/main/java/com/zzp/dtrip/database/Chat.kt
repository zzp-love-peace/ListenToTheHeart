package com.zzp.dtrip.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zzp.dtrip.data.ChatMsg
import com.zzp.dtrip.data.ChatMsgConverter

@Entity
@TypeConverters(ChatMsgConverter::class)
data class Chat(var fromUserId: String, var toUserId: String, var chatList: List<ChatMsg>) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0


}