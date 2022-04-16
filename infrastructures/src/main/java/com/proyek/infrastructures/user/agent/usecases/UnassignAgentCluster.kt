package com.proyek.infrastructures.user.agent.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.agent.entities.Agent
import com.proyek.infrastructures.user.agent.network.AgentApiServices
import com.proyek.infrastructures.user.agent.network.AgentResponse

import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class UnassignAgentCluster: ViewModel() {
    private val services = AgentApiServices::class.java
    private val result = MutableLiveData<AgentResponse>()

    fun set(id: String) {
        MyRetrofit
            .createService(services)
            .unassignAgent(id)
            .enqueue(object : Callback<AgentResponse?> {
                override fun onFailure(call: Call<AgentResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<AgentResponse?>?, response: Response<AgentResponse?>?) {
                        if (response!!.isSuccessful) {
                            val results: AgentResponse = response.body()!!
                            result.postValue(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    fun get(): LiveData<AgentResponse> {
        return result
    }
}