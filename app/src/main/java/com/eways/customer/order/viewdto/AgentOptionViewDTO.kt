package com.eways.customer.order.viewdto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AgentOptionViewDTO (
    val id: String,
    val name:String
): Parcelable