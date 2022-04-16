package com.proyek.infrastructures.user.user.usecases

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


class LoginUser: ViewModel() {
    /*private val services = AgentApiServices::class.java
    private val result = MutableLiveData<ArrayList<Agent>>()

    internal fun set(username: String, password: String, firebaseToken: String, otp: Int) {
        MyRetrofit
            .createService(services)
            .loginAgent(username, password, firebaseToken, otp)
            .enqueue(object : Callback<AgentResponses?> {
                override fun onFailure(call: Call<AgentResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<AgentResponse?>?, response: Response<AgentResponse?>?) {
                        if (response!!.isSuccessful) {
                            val results = ArrayList<Agent>()
                            response.body()?.data?.let { results.addAll(it) }
                            result.postValue(results)
                        } else {
                            NetworkErrorHandler.checkResponse(response.code())
                        }
                }
            })
    }
    internal fun get(): LiveData<java.util.ArrayList<Agent>> {
        return result
    }*/
}