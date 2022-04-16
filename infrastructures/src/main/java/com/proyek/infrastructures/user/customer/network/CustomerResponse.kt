package com.proyek.infrastructures.user.customer.network

import com.proyek.infrastructures.user.customer.entities.Customer
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.agent.entities.Error

data class CustomerResponse(
    val errors: Error,
    val message: String,
    val data: CustomerDataResponse
)