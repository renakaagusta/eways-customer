package com.proyek.infrastructures.user.customer.usecases

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.customer.entities.Customer

import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.proyek.infrastructures.user.customer.network.CustomerApiServices
import com.proyek.infrastructures.user.customer.network.CustomerResponse


class LoginCustomer: ViewModel() {
    private val services = CustomerApiServices::class.java
    private val result = MutableLiveData<CustomerResponse>()

    fun set(phoneNumber: String, firebaseToken: String, otpStatus: Int, context: Context) {

        MyRetrofit
            .createService(services)
            .loginCustomer("+62"+phoneNumber, firebaseToken, otpStatus)
            .enqueue(object : Callback<CustomerResponse?> {
                override fun onFailure(call: Call<CustomerResponse?>?, t: Throwable?) {
                    val message = ArrayList<String>()
                    message.add(t?.message!!)
                    Log.d("errorku", t?.message)
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<CustomerResponse?>?, response: Response<CustomerResponse?>?) {
                    if (response!!.isSuccessful) {
                        result.postValue(response.body())
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<CustomerResponse> {
        return result
    }
}