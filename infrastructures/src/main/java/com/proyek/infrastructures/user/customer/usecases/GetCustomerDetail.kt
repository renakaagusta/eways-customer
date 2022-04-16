package com.proyek.infrastructures.user.customer.usecases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.customer.network.CustomerApiServices
import com.proyek.infrastructures.user.customer.network.CustomerSingleResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetCustomerDetail: ViewModel() {
    private val services = CustomerApiServices::class.java
    private val result = MutableLiveData<CustomerSingleResponse>()

    fun set(customerId:String) {
        MyRetrofit
            .createService(services)
            .getCustomerDetail(customerId)
            .enqueue(object : Callback<CustomerSingleResponse?> {
                override fun onFailure(call: Call<CustomerSingleResponse?>?, t: Throwable?) {
                    Log.d("error", t?.message)
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<CustomerSingleResponse?>?, response: Response<CustomerSingleResponse?>?) {
                    Log.d("response", response?.body().toString())
                    if (response!!.isSuccessful) {
                        val results = response.body()!!
                        result.postValue(results)
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<CustomerSingleResponse> {
        return result
    }
}