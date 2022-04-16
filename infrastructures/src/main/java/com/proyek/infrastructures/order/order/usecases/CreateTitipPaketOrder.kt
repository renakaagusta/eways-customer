package com.proyek.infrastructures.order.order.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.order.order.`interface`.IBuyer
import com.proyek.infrastructures.order.order.`interface`.ICourier
import com.proyek.infrastructures.order.order.`interface`.IOrderable
import com.proyek.infrastructures.order.order.entities.Order
import com.proyek.infrastructures.order.order.network.OrderApiServices
import com.proyek.infrastructures.order.order.network.OrderResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateTitipPaketOrder: ViewModel() {
    private val services = OrderApiServices::class.java
    private val result = MutableLiveData<OrderResponse>()

    fun set(senderName: String, senderPhoneNumber: String, senderAddress: String,
            receiverName: String, receiverPhoneNumber: String, receiverAddress: String,
            itemName: String, itemDescription: String,
            serviceId: String, customerId: String, agentId: String,
            notificationTitle: String, notificationDescription: String) {
        MyRetrofit
            .createService(services)
            .createTitipPaketOrder(
                senderName, senderPhoneNumber, senderAddress,
                receiverName, receiverPhoneNumber, receiverAddress,
                itemName, itemDescription, serviceId, customerId, agentId,
                notificationTitle, notificationDescription
            )
            .enqueue(object : Callback<OrderResponse?> {
                override fun onFailure(call: Call<OrderResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<OrderResponse?>?, response: Response<OrderResponse?>?) {
                    if (response!!.isSuccessful) {
                        result.postValue(response.body())
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<OrderResponse> {
        return result
    }
}