package com.proyek.infrastructures.kabarcluster.post.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proyek.infrastructures.kabarcluster.post.network.PostApiServices
import com.proyek.infrastructures.kabarcluster.entities.Comment
import com.proyek.infrastructures.kabarcluster.entities.Post
import com.proyek.infrastructures.kabarcluster.post.network.PostResponse
import com.proyek.infrastructures.user.user.entities.User
import com.proyek.infrastructures.user.cluster.entities.Cluster
import com.proyek.infrastructures.utils.NetworkErrorHandler
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetPostDetail: ViewModel() {
    private val services = PostApiServices::class.java
    private val result = MutableLiveData<PostResponse>()

    fun set(postId: String) {
        MyRetrofit
            .createService(services)
            .getPostDetail(postId)
            .enqueue(object : Callback<PostResponse?> {
                override fun onFailure(call: Call<PostResponse?>?, t: Throwable?) {
                    NetworkErrorHandler.checkFailure(t!!)
                }

                override fun onResponse(call: Call<PostResponse?>?, response: Response<PostResponse?>?) {
                    if (response!!.isSuccessful) {
                        val results: PostResponse = response.body()!!
                        result.postValue(results)
                    } else {
                        NetworkErrorHandler.checkResponse(response.code())
                    }
                }
            })
    }
    fun get(): LiveData<PostResponse> {
        return result
    }
}