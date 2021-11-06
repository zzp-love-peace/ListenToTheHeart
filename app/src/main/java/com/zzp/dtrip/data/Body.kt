package com.zzp.dtrip.data

data class AddDataBody(val mileage: String, val type: String,
                    val id: Int, val location: String)

data class CmpFaceBody(val bytes: String)
data class DeleteFaceBody(val id: Int)
data class FaceBody(val bytes: String, val id: Int)
data class LoginBody(val username: String, val password: String)
data class PasswordBody(val username: String, val password: String,
                        val new_Psw: String)

data class RegisterBody(val username: String, val password: String,
                        val sex: String)

data class SexBody(val username: String, val password: String,
                   val new_Sex: String)

data class UsernameBody(val username: String, val password: String,
                        val new_Usn: String)

data class IdBody(val toId: Int)

data class SelectIdBody(val username: String)

data class FriendToFriendBody(val fromId: Int, val toId: Int)
