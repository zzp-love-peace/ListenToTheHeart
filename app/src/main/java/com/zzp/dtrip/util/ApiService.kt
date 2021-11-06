package com.zzp.dtrip.util

import com.zzp.dtrip.data.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("/user/login")
    fun postLogin(@Body loginBody: LoginBody) : Call<LoginResult>

    @POST("/user/register")
    fun postRegister(@Body registerBody: RegisterBody) : Call<NormalResult>

    @POST("/user/updateUsn")
    fun postUsername(@Body usernameBody: UsernameBody) : Call<NormalResult>

    @POST("/user/updateSex")
    fun postSex(@Body sexBody: SexBody) : Call<NormalResult>

    @POST("/user/updatePsw")
    fun postPassword(@Body passwordBody: PasswordBody) : Call<NormalResult>

    @POST("/data/addData")
    fun postData(@Body addDataBody: AddDataBody) : Call<NormalResult>

    @POST("/face/addFace")
    fun postFaceData(@Body faceBody: FaceBody) : Call<FaceResult>

    @POST("/face/compareFace")
    fun compareFace(@Body cmpFaceBody: CmpFaceBody) : Call<LoginResult>

    @POST("/face/deleteFace")
    fun deleteFace(@Body deleteFaceBody: DeleteFaceBody) : Call<NormalResult>

    @POST("/friend/friendList")
    fun getFriendList(@Body idBody: IdBody) : Call<GetFriendsResult>

    @POST("user/selectId")
    fun searchIdFromUsername(@Body selectIdBody: SelectIdBody) : Call<SelectIdResult>

    @POST("friend/selectFriend")
    fun searchFriendFromId(@Body idBody: IdBody) : Call<SelectFriendResult>

    @POST("friend/isFriend")
    fun isFriend(@Body isFriendBody: FriendToFriendBody) : Call<IsFriendResult>

    @POST("friend/requestList")
    fun getRequestList(@Body getFriendBody: IdBody) : Call<GetFriendsResult>

    @POST("friend/request")
    fun addFriend(@Body addFriendBody: FriendToFriendBody) : Call<NormalResult>

    @POST("friend/agree")
    fun agreeRequest(@Body agreeRequestBody: FriendToFriendBody) : Call<NormalResult>

    @POST("friend/reject")
    fun rejectRequest(@Body rejectRequestBody: FriendToFriendBody) : Call<NormalResult>

    @POST("friend/delete")
    fun deleteFriend(@Body deleteFriendBody: FriendToFriendBody) : Call<NormalResult>
}