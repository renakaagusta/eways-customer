package com.proyek.infrastructures.order.order.network.body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LaporanKerusakanBody(
    @SerializedName("internetService")
    val internetService: InternetService,

    @SerializedName("damageDescription")
    val damageDescription: String,

    @SerializedName("serviceId")
    val serviceId: String,

    @SerializedName("customerId")
    val customerId: String,

    @SerializedName("agentId")
    val agentId: String,

    @SerializedName("notificationTitle")
    val notificationTitle: String,

    @SerializedName("notificationDescription")
    val notificationDescription: String
) : Parcelable