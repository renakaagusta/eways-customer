package com.proyek.infrastructures.user.user.usecases

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.user.entities.User
import com.proyek.infrastructures.user.user.network.AuthApiServices
import com.proyek.infrastructures.user.user.network.UserResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetUserDetail: ViewModel() {
    private val services = AuthApiServices::class.java
    private var result = ArrayList<User>()

    fun set(ID:String, context: Context) {

        MyRetrofit
            .createService(services)
            .readUser(ID)
            .enqueue(object : Callback<UserResponse?> {
                override fun onFailure(call: Call<UserResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                    Log.e("eror t", t?.message!!)
                }

                override fun onResponse(call: Call<UserResponse?>?, response: Response<UserResponse?>?) {
                        if (response!!.isSuccessful) {
                            val results: ArrayList<User> = ArrayList()
                            results.addAll(response.body()!!.data)
                            result.clear()
                            result.addAll(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    fun get(): ArrayList<User> {
        return result
    }
}