package com.eways.customer.utils.firebase.messaging

import android.util.Log
import com.eways.customer.utils.firebase.firestore.Firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FirebaseCloudMessaging {
    fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Firestore.token = it.token
        }.addOnFailureListener {
            Log.d("fcm", it.message)
        }
    }
}
