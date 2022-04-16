package com.proyek.infrastructures.user.cluster.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.cluster.entities.Cluster

import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.proyek.infrastructures.user.cluster.network.ClusterApiServices
import com.proyek.infrastructures.user.cluster.network.ClusterResponse


class CreateCluster: ViewModel() {
    private val services = ClusterApiServices::class.java
    private val result = MutableLiveData<ClusterResponse>()

    fun set(name: String, areas: ArrayList<String>) {
        MyRetrofit
            .createService(services)
            .createCluster(name, areas)
            .enqueue(object : Callback<ClusterResponse?> {
                override fun onFailure(call: Call<ClusterResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<ClusterResponse?>?, response: Response<ClusterResponse?>?) {
                    if (response!!.isSuccessful) {
                        val results: ClusterResponse = response.body()!!
                        result.postValue(results)
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<ClusterResponse> {
        return result
    }
}