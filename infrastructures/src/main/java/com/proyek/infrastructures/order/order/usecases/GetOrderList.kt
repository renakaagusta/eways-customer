package com.proyek.infrastructures.order.order.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.order.order.entities.Order
import com.proyek.infrastructures.order.order.network.OrderApiServices
import com.proyek.infrastructures.order.order.network.OrderResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetOrderList: ViewModel() {
    private val services = OrderApiServices::class.java
    private val result = MutableLiveData<ArrayList<Order>>()

    fun set() {
        MyRetrofit
            .createService(services)
            .getOrderList()
            .enqueue(object : Callback<OrderResponse?> {
                override fun onFailure(call: Call<OrderResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<OrderResponse?>?, response: Response<OrderResponse?>?) {
                        if (response!!.isSuccessful) {
                            val results: ArrayList<Order> = ArrayList()
                            results.addAll(response.body()!!.data)
                            result.postValue(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    fun get(): LiveData<java.util.ArrayList<Order>> {
        return result
    }
}