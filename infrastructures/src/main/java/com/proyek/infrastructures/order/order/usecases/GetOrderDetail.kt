package com.proyek.infrastructures.order.order.usecases

import android.content.Context
import android.graphics.Color
import android.util.Log
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


class GetOrderDetail: ViewModel() {
    private val services = OrderApiServices::class.java
    public val result = ArrayList<Order>()

    fun set(ID:String, context: Context) {

        MyRetrofit
            .createService(services)
            .getOrderDetail(ID)
            .enqueue(object : Callback<OrderResponse?> {
                override fun onFailure(call: Call<OrderResponse?>?, t: Throwable?) {
                    Log.d("errorresponse", "oi "+t?.message)
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<OrderResponse?>?, response: Response<OrderResponse?>?) {
                    Log.d("response", response?.body().toString())
                        if (response!!.isSuccessful) {
                            result.addAll(response.body()!!.data)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    fun get(): java.util.ArrayList<Order> {
        return result
    }
}