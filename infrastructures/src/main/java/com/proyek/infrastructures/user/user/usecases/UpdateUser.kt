package com.proyek.infrastructures.user.user.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.user.network.AuthApiServices
import com.proyek.infrastructures.user.user.network.UserResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.proyek.infrastructures.user.user.entities.User


class UpdateUser: ViewModel() {
    private val services = AuthApiServices::class.java
    private val result = MutableLiveData<ArrayList<User>>()

    fun set(id: String, username: String, fullname: String, phoneNumber: String, email: String, password: String, address: String) {
        MyRetrofit
            .createService(services)
            .updateUser(id, username, fullname, phoneNumber, email, password, address)
            .enqueue(object : Callback<UserResponse?> {
                override fun onFailure(call: Call<UserResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<UserResponse?>?, response: Response<UserResponse?>?) {
                        if (response!!.isSuccessful) {
                            val results = ArrayList<User>()
                            response.body()?.data?.let { results.addAll(it) }
                            result.postValue(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    fun get(): LiveData<java.util.ArrayList<User>> {
        return result
    }
}