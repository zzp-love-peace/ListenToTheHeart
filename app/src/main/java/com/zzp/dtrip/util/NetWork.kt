package com.zzp.dtrip.util

import com.zzp.dtrip.data.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KSuspendFunction1

object NetWork {
    private val apiService = RetrofitManager.create<ApiService>()

    suspend fun addFriend(addFriendBody: FriendToFriendBody) = apiService.addFriend(addFriendBody).await()
    suspend fun deleteFriend(deleteFriendBody: FriendToFriendBody) = apiService.deleteFriend(deleteFriendBody).await()
    suspend fun agreeRequest(agreeRequestBody: FriendToFriendBody) = apiService.agreeRequest(agreeRequestBody).await()
    suspend fun rejectRequest(rejectRequestBody: FriendToFriendBody) = apiService.rejectRequest(rejectRequestBody).await()
    suspend fun isFriend(isFriendBody: FriendToFriendBody) = apiService.isFriend(isFriendBody).await()
    suspend fun getFriendList(getFriendBody: IdBody) = apiService.getFriendList(getFriendBody).await()
    suspend fun getRequestList(getRequestBody: IdBody) = apiService.getRequestList(getRequestBody).await()
    suspend fun searchIdFromUsername(selectIdBody: SelectIdBody) = apiService.searchIdFromUsername(selectIdBody).await()
    private suspend fun searchFriendFromId(getFriendBody: IdBody) = apiService.searchFriendFromId(getFriendBody).await()

    suspend fun searchFriendFromId(id: Int) : Friend {
        return try {
            val response = searchFriendFromId(IdBody(id))
            if (response.errorCode == 0) {
                response.user
            } else {
                Friend(-1,"","")
            }
        } catch (e: Exception) {
            "出现异常啦".showToast()
            Friend(-1,"","")
        }
    }
    suspend fun getFriendListFromInfoList(idList: List<Int>) =
        ArrayList<Friend>().let {
            for (friendId in idList) {
                val friend = searchFriendFromId(friendId)
                if (friend.id != -1) it.add(friend)
            }
            it
        }

//    通过高阶函数来获取好友或者新朋友的id
    suspend fun getFriendId(myId: Int, getList: KSuspendFunction1<Int, List<Info>>) : List<Int> {
        val result = ArrayList<Int>()
        val list = getList(myId)
        for (info in list) {
            if (info.toId == myId) {
                result.add(info.fromId)
            } else {
                result.add(info.toId)
            }
        }
        return result
    }

    //  给网络请求方法的返回值增加扩展函数await，简化回调
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}