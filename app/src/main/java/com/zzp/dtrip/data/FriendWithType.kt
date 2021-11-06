package com.zzp.dtrip.data

data class FriendWithType(val friend: Friend, val type: Int) {
    companion object {
        const val ADD_FRIEND = 0
        const val NEW_FRIEND = 1
        const val MY_FRIEND = 2
    }
}