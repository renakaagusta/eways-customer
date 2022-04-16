package com.proyek.infrastructures.user.auth.network

import com.proyek.infrastructures.user.auth.entities.User

data class UserResponse(
    val errors: ArrayList<String>,
    val message: String,
    val data: List<User>
)