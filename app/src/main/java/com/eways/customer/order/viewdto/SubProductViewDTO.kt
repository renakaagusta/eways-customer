package com.eways.customer.order.viewdto

data class SubProductViewDTO (
    val id: Int?,
    val itemId: String?,
    val subproductImage : String?,
    val subproductName :String,
    val subproductDescription : String,
    val subproductPrice: Int
){
    var subproductAmount: Int = 0
}