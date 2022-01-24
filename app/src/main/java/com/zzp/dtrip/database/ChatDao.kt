package com.zzp.dtrip.database

import androidx.room.*

@Dao
interface ChatDao {

    @Insert
    fun insertChat(chat: Chat): Long

    @Delete
    fun deleteChat(chat: Chat)

    @Update
    fun updateChat(chat: Chat): Int

    @Query("select * from Chat")
    fun loadAllChat(): List<Chat>

    @Query("select * from Chat where fromUserId = :fromId and toUserId = :toId")
    fun loadChat(fromId: String, toId: String): Chat

    @Query("SELECT COUNT(*) from Chat")
    fun chatsCount() : Int

}