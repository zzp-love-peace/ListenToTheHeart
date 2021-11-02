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
    fun getFriends(@Body getFriendsBody: GetFriendsBody) : Call<GetFriendsResult>

    @POST("user/selectId")
    fun selectId(@Body selectIdBody: SelectIdBody) : Call<SelectIdResult>

    @POST("friend/isFriend")
    fun isFriend(@Body isFriendBody: IsFriendBody) : Call<IsFriendResult>
}