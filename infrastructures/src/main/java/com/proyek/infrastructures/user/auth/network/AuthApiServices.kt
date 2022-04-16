package com.proyek.infrastructures.user.auth.network

import retrofit2.Call
import retrofit2.http.*

interface AuthApiServices {
    @FormUrlEncoded
    @POST("user/user/active")
    fun activeUser(@Field("id") ID: String): Call<UserResponse>

    @FormUrlEncoded
    @POST("user/user/deactive")
    fun deactiveUser(@Field("id") ID: String): Call<UserResponse>

    @FormUrlEncoded
    @POST("user/user/verif")
    fun verifUser(@Field("id") ID: String): Call<UserResponse>

    @FormUrlEncoded
    @POST("user/user/unverif")
    fun unverifUser(@Field("id") ID: String): Call<UserResponse>

    @FormUrlEncoded
    @POST("user/user/update")
    fun updateUser(@Field("id") id:String, @Field("userName") username: String, @Field("fullName") fullname: String, @Field("phoneNumber") phoneNumber: String, @Field("email") email: String, @Field("password") password: String, @Field("imagePath") imagePath: String, @Field("role") role: Int, @Field("activeStatus") activeStatus: Boolean, @Field("verifStatus") verifStatus: Boolean): Call<UserResponse>

    @FormUrlEncoded
    @DELETE("user/user/data")
    fun deleteUser(@Field("id") id: String): Call<UserResponse>

    @FormUrlEncoded
    @POST("user/user/login")
    fun Login(@Field("userName") username: String, @Field("password") password: String): Call<UserResponse>

    @GET("user/user/create")
    fun createUser(@Query("userName") username: String, @Query("fullName") fullname: String, @Query("phoneNumber") phoneNumber: String, @Query("email") email: String, @Query("password") password: String, @Query("imagePath") imagePath: String, @Query("role") role: Int, @Query("activeStatus") activeStatus: Boolean, @Query("verifStatus") verifStatus: Boolean): Call<UserResponse>

    @GET("user/user/data")
    fun readUser(@Query("id") id: String): Call<UserResponse>

}