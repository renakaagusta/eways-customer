package com.eways.customer.utils.firebase.messaging

import com.google.gson.annotations.SerializedName

data class Body (
    @SerializedName("type")
    val type: Int,
    @SerializedName("content")
    val content: String
)