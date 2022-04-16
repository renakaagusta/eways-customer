package com.proyek.infrastructures.utils

import android.accounts.NetworkErrorException
import android.util.Log
import java.io.IOException

object NetworkErrorHandler {
    fun checkResponse(code: Int) {
      if(code in 300..400) {

      }  else if(code in 400..500) {

      } else if(code > 500) {

      }
    }
    fun checkFailure(t: Throwable){
        if(t is IOException){
            Log.d("NetworkHandler", "IOException")
            Log.d("message", t.message)
        } else if( t is NetworkErrorException) {
            Log.d("NetworkHandler", "NetworkErrorException")
            Log.d("message", t.message)
        } else {
            Log.d("NetworkHandler", "UnkownError")
            Log.d("message", t.message)
        }
    }
}