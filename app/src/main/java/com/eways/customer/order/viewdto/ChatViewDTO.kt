package com.eways.customer.order.viewdto

import com.eways.customer.utils.date.SLDate

data class ChatViewDTO(
    val isBelongToCurrentUser : Boolean,
    val text: String,
    val time: SLDate
)