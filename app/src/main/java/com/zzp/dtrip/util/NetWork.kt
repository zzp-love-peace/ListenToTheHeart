package com.zzp.dtrip.util

import com.zzp.dtrip.data.GetFriendsBody
import com.zzp.dtrip.data.SelectIdBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetWork {
    private val apiService = RetrofitManager.create<ApiService>()

    suspend fun getFriends(getFriendsBody: GetFriendsBody) = apiService.getFriends(getFriendsBody).await()
    suspend fun selectId(selectIdBody: SelectIdBody) = apiService.selectId(selectIdBody).await()


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