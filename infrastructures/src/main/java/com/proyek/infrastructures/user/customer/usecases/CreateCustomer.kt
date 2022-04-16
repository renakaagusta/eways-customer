package com.proyek.infrastructures.user.customer.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.proyek.infrastructures.user.cluster.entities.Cluster
import com.proyek.infrastructures.user.customer.entities.Customer
import com.proyek.infrastructures.user.customer.network.CustomerApiServices
import com.proyek.infrastructures.user.customer.network.CustomerSingleResponse


class CreateCustomer: ViewModel() {
    private val services = CustomerApiServices::class.java
    private val result = MutableLiveData<CustomerSingleResponse>()

    fun set(username: String, fullname: String, phoneNumber: String, email: String, password: String, address: String, addressLat: Float, addressLng: Float) {
        MyRetrofit
            .createService(services)
            .createCustomer(username, fullname, phoneNumber, email, password,  address, addressLat, addressLng)
            .enqueue(object : Callback<CustomerSingleResponse?> {
                override fun onFailure(call: Call<CustomerSingleResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<CustomerSingleResponse?>?, response: Response<CustomerSingleResponse?>?) {
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