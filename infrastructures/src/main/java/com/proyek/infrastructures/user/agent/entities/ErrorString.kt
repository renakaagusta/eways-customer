package com.proyek.infrastructures.user.agent.entities

import com.google.gson.annotations.SerializedName

data class ErrorString(
    @SerializedName("message")
    val message: String
)