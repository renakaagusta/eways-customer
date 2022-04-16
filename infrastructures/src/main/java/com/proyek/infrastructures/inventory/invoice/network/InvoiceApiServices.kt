package com.proyek.infrastructures.inventory.invoice.network

import com.proyek.infrastructures.inventory.category.entities.Category
import retrofit2.Call
import retrofit2.http.*

interface InvoiceApiServices {
    @GET("inventory/invoice/data")
    fun getInvoiceDetail(@Query("invoiceId") id: String): Call<InvoiceResponse>

    @GET("inventory/invoice/all")
    fun getInvoiceList(): Call<InvoiceResponse>
}