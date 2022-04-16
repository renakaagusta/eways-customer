package com.proyek.infrastructures.inventory.service.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.inventory.service.entities.Service
import com.proyek.infrastructures.inventory.service.network.ServiceApiServices
import com.proyek.infrastructures.inventory.service.network.ServiceResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateService: ViewModel() {
    private val services = ServiceApiServices::class.java
    private val result = MutableLiveData<ArrayList<Service>>()

    internal fun set(name: String, description: String, price: Int) {
        MyRetrofit
            .createService(services)
            .createService(name, description, price)
            .enqueue(object : Callback<ServiceResponse?> {
                override fun onFailure(call: Call<ServiceResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<ServiceResponse?>?, response: Response<ServiceResponse?>?) {
                    if (response!!.isSuccessful) {
                        val results = ArrayList<Service>()
                        response.body()?.data?.let { results.addAll(it) }
                        result.postValue(results)
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    internal fun get(): LiveData<java.util.ArrayList<Service>> {
        return result
    }
}