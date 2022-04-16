package com.eways.customer.order.viewdto

import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType

interface IOrderViewDTO {
    val orderType : OrderType
    val orderStatus: OrderStatus
}