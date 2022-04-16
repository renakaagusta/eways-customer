package com.proyek.infrastructures.user.agent.usecases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.agent.network.AgentApiServices
import com.proyek.infrastructures.user.agent.network.AgentDetailResponse
import com.proyek.infrastructures.user.user.network.AuthApiServices
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetAgentDetail: ViewModel() {
    private val services = AgentApiServices::class.java
    private val result = MutableLiveData<AgentDetailResponse>()

    fun set(ID:String) {
        MyRetrofit
            .createService(services)
            .getAgentDetail(ID)
            .enqueue(object : Callback<AgentDetailResponse?> {
                override fun onFailure(call: Call<AgentDetailResponse?>?, t: Throwable?) {
                    Log.d("error", t?.message)
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<AgentDetailResponse?>?, response: Response<AgentDetailResponse?>?) {
                        Log.d("responseorder", response?.body().toString())
                        if (response!!.isSuccessful) {
                            val results: AgentDetailResponse = response.body()!!
                            result.postValue(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    fun get(): LiveData<AgentDetailResponse> {
        return result
    }
}