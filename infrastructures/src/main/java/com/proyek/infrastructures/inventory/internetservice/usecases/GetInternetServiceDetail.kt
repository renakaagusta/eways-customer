package com.proyek.infrastructures.inventory.internetservice.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.inventory.category.network.CategoryResponse
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.inventory.internetservice.network.InternetServiceApiServices
import com.proyek.infrastructures.inventory.internetservice.network.InternetServiceResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetInternetServiceDetail: ViewModel() {
    private val internetservices = InternetServiceApiServices::class.java
    private val result = MutableLiveData<InternetServiceResponse>()

    fun set(id:String) {
        MyRetrofit
            .createService(internetservices)
            .getServiceDetail(id)
            .enqueue(object : Callback<InternetServiceResponse?> {
                override fun onFailure(call: Call<InternetServiceResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<InternetServiceResponse?>?, response: Response<InternetServiceResponse?>?) {
                    if (response!!.isSuccessful) {
                        val results: InternetServiceResponse = response.body()!!
                        result.postValue(results)
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<InternetServiceResponse> {
        return result
    }
}