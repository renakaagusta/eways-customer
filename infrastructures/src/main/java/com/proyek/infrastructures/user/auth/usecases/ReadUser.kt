package com.proyek.infrastructures.user.auth.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.auth.entities.User
import com.proyek.infrastructures.user.auth.network.AuthApiServices
import com.proyek.infrastructures.user.auth.network.UserResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReadUser: ViewModel() {
    private val services = AuthApiServices::class.java
    private val result = MutableLiveData<ArrayList<User>>()

    internal fun set(ID:String) {
        MyRetrofit
            .createService(services)
            .readUser(ID)
            .enqueue(object : Callback<UserResponse?> {
                override fun onFailure(call: Call<UserResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<UserResponse?>?, response: Response<UserResponse?>?) {
                        if (response!!.isSuccessful) {
                            val results: ArrayList<User> = ArrayList()
                            results.addAll(response.body()!!.data)
                            result.postValue(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    internal fun get(): LiveData<java.util.ArrayList<User>> {
        return result
    }
}