package com.zzp.dtrip.data

data class GetFriendsResult(
    val errorCode: Int,
    val isError: Boolean,
    val list: List<Info>
)

data class Info(
    val fromId: Int,
    val message: String,
    val toId: Int
)

data class FaceResult(
    val code: Int,
    val `data`: String,
    val errorMsg: String,
    val isError: Boolean
)

data class LoginResult(
    val errorCode: Int,
    val isError: Boolean,
    val errorMsg: String,
    val user: User
)

data class NormalResult(
    val errorCode: Int,
    val errorMsg: String,
    val isError: Boolean
)

data class User(
    val id: Int,
    val sex: String,
    val username: String,
    val password: String
)

data class SelectIdResult(
    val errorCode: Int,
    val errorMsg: String,
    val isError: Boolean,
    val list: List<String>
)

data class IsFriendResult(
    val errorCode: Int,
    val errorMsg: String,
    val isError: Boolean,
    val message: Boolean
)

data class SelectFriendResult(
    val errorCode: Int,
    val errorMsg: String,
    val isError: Boolean, val user: Friend
)

data class Friend(val id: Int, val username: String, val sex: String)