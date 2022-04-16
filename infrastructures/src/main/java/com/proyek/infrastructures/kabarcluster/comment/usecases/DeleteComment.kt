package com.proyek.infrastructures.kabarcluster.comment.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.kabarcluster.comment.network.CommentApiServices
import com.proyek.infrastructures.kabarcluster.comment.network.CommentResponse
import com.proyek.infrastructures.kabarcluster.entities.Comment


import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.proyek.infrastructures.user.user.entities.User


class DeleteComment: ViewModel() {
    private val services = CommentApiServices::class.java
    private val result = MutableLiveData<CommentResponse>()

    fun set(commentId: String) {
        MyRetrofit
            .createService(services)
            .deleteComment(commentId)
            .enqueue(object : Callback<CommentResponse?> {
                override fun onFailure(call: Call<CommentResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<CommentResponse?>?, response: Response<CommentResponse?>?) {
                    if (response!!.isSuccessful) {
                        result.postValue(response.body())
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<CommentResponse> {
        return result
    }
}