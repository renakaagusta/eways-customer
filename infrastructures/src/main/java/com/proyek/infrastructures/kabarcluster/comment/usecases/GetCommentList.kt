package com.proyek.infrastructures.kabarcluster.comment.usecases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.kabarcluster.comment.network.CommentApiServices

import com.proyek.infrastructures.kabarcluster.comment.network.CommentResponse
import com.proyek.infrastructures.kabarcluster.entities.Comment
import com.proyek.infrastructures.user.user.entities.User
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetCommentList: ViewModel() {
    private val services = CommentApiServices::class.java
    private var result = CommentResponse()

    fun set(postId: String) {
        MyRetrofit
            .createService(services)
            .getCommentList(postId)
            .enqueue(object : Callback<CommentResponse?> {
                override fun onFailure(call: Call<CommentResponse?>?, t: Throwable?) {
                    Log.d("error", t.toString())
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<CommentResponse?>?, response: Response<CommentResponse?>?) {
                    if (response!!.isSuccessful) {
                        result=response.body()!!
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): CommentResponse {
        return result
    }
}