package com.proyek.infrastructures.order.chat.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.proyek.infrastructures.order.chat.`interface`.IChatter
import kotlinx.android.parcel.Parcelize
import java.sql.Time
import java.sql.Timestamp

@Parcelize
data class Chat(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("senderId")
    var sender_id: String? = null,

    @SerializedName("receiverId")
    var receiver_id: String? = null,

    @SerializedName("content")
    var content: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("createdAt")
    var created_at: String? = null,

    @SerializedName("updatedAt")
    var updated_at: String? = null
) : Parcelable