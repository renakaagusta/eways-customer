package com.proyek.infrastructures.notification.network

import com.proyek.infrastructures.notification.entities.Notification


data class NotificationResponse(
    val errors: Error? = null,
    val message: String? = null,
    val data: List<Notification>? = null
)