package com.zzp.dtrip.util

object UserInformation {
    var username = ""
    var password = ""
    var sex = ""
    var id = -1
    var isLogin = false

    fun setDataNull() {
        username = ""
        password = ""
        sex = ""
        id = -1
        isLogin = false
    }
}