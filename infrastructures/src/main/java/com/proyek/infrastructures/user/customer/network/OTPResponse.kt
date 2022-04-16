package com.proyek.infrastructures.user.customer.network

import com.google.gson.annotations.SerializedName
import com.proyek.infrastructures.user.customer.entities.Customer
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.agent.entities.Error

data class OTPResponse(
    @SerializedName("error")
    val error: Int,
    @SerializedName("sms_id")
    val sms_id: Int,
    @SerializedName("message")
    val message: String
)