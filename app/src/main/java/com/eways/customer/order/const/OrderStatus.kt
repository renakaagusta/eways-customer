package com.eways.customer.order.const

enum class OrderStatus(val value: String) {
    Created("Belum Dilayani"),
    OnProgress("On Progress"),
    CustomerFinished("Selesai")
}