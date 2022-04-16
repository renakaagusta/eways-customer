package com.proyek.infrastructures.user.agent.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.user.agent.entities.Agent
import com.proyek.infrastructures.user.agent.entities.ModalWallet
import com.proyek.infrastructures.user.agent.network.AgentApiServices
import com.proyek.infrastructures.user.agent.network.AgentResponse

import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.proyek.infrastructures.user.cluster.entities.Cluster


class CreateAgent: ViewModel() {
    private val services = AgentApiServices::class.java
    private val result = MutableLiveData<AgentResponse>()

    fun set(username: String, fullname: String, phoneNumber: String, email: String, password: String, address: String, NIK: String) {
        MyRetrofit
            .createService(services)
            .createAgent(username, fullname, phoneNumber, email, password, address, NIK)
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