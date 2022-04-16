package com.eways.customer.order.viewdto

import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType
import com.eways.customer.utils.date.SLDate

class OrderPacketViewDTO (
    val id: String,
    override val orderType: OrderType,
    val orderSenderName: String,
    val orderSenderAddress: String,
    val orderReceiverName: String,
    val orderReceiverAddress: String,
    val orderTime: SLDate,
    override val orderStatus: OrderStatus
):IOrderViewDTO