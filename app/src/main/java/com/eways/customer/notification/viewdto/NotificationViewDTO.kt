
package com.eways.customer.notification.viewdto

import com.eways.customer.utils.date.SLDate

data class NotificationViewDTO (
    var notificationType: NotificationType,
    var date : SLDate,
    var orderType : String,
    var price : Int?,
    val type_id: String?
){
    enum class NotificationType(val text: String){
        created("Anda membuat pesanan baru"),
        onprogress("Pesanan sedang dilayani"),
        finished("Pesanan anda selesai dilayani"),
        billing("Tagihan")
    }
}