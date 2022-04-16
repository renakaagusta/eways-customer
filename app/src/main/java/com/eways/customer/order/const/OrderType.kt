package com.eways.customer.order.const

enum class OrderType(val value: String) {
    PSB("Pesan Pasang Baru"),
    PickupTicket("Pesan Perbaikan"),
    GantiPaket("Pesan Ganti Paket"),
    TitipPaket("Titip Paket"),
    SOPP("SOPP"),
    TitipBelanja("Titip Belanja"),
    LayananBebas("Layanan Bebas")
}