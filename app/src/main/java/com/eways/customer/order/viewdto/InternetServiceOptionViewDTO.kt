package com.eways.customer.order.viewdto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InternetServiceOptionViewDTO (
    val id: String,
    val name:String,
    val description:String
): Parcelable