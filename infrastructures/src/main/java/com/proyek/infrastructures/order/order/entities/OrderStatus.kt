package com.proyek.infrastructures.order.order.entities

import com.google.gson.annotations.SerializedName

data class OrderStatus(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null
)