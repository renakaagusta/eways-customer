package com.proyek.infrastructures.user.user.network

import com.proyek.infrastructures.user.user.entities.User
import java.lang.Error

data class UserResponse(
    val errors: Error,
    val message: String,
    val data: List<User>
)