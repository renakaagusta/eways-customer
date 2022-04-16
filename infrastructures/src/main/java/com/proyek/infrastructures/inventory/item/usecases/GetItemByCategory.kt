package com.proyek.infrastructures.inventory.item.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.inventory.category.entities.Category
import com.proyek.infrastructures.inventory.item.network.ItemApiServices
import com.proyek.infrastructures.inventory.item.entities.Item
import com.proyek.infrastructures.inventory.item.network.ItemResponse
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetItemByCategory: ViewModel() {
    private val services = ItemApiServices::class.java
    private val result = MutableLiveData<ItemResponse>()

    fun set(categoryId: String) {
        MyRetrofit
            .createService(services)
            .getItemByCategory(categoryId)
            .enqueue(object : Callback<ItemResponse?> {
                override fun onFailure(call: Call<ItemResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<ItemResponse?>?, response: Response<ItemResponse?>?) {
                    if (response!!.isSuccessful) {
                        val results: ItemResponse = response.body()!!

                        result.postValue(results)
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<ItemResponse> {
        return result
    }
}