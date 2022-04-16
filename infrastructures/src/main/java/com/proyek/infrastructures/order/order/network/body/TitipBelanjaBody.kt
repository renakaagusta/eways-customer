package com.proyek.infrastructures.order.order.network.body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.inventory.item.entities.Grocery
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TitipBelanjaBody(
    @SerializedName("groceries")
    val groceries: ArrayList<Grocery>,

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