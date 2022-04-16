package com.proyek.infrastructures.order.order.network.body

import android.location.Address
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.proyek.infrastructures.inventory.invoice.entities.Invoice
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

@Parcelize
class SOPPBody (
    @SerializedName("invoice")
    val invoice: Invoice,

    @SerializedName("name")
    val name: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("description")
    val description: String,

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
