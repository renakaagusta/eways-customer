package com.eways.customer.order.viewdto

import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType
import com.eways.customer.utils.date.SLDate

data class OrderBasicViewDTO(
    val id: String,
    override val orderType: OrderType,
    val orderDescription: String,
    val orderTime: SLDate,
    override val orderStatus: OrderStatus
):IOrderViewDTO