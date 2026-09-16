package com.example.data.model

enum class UserRole(val displayName: String, val level: Int, val description: String) {
    GUEST("Guest", 0, "Pengunjung umum, melihat katalog & tambah ke cart"),
    MEMBER("Member", 1, "Pembeli terdaftar, checkout, download center & wishlist"),
    STAFF("Staff", 2, "Kelola produk & order, kirim ulang link download"),
    ADMIN("Admin", 3, "Manajemen member, kupon, laporan penjualan lengkap"),
    SUPER_ADMIN("Super Admin", 4, "Kontrol penuh, audit log, manajemen role & sistem")
}

data class User(
    val id: String,
    val name: String,
    val username: String = "",
    val email: String,
    val phone: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val avatarUrl: String = ""
)

data class ProductCategory(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String
)

val CATEGORIES = listOf(
    ProductCategory("software", "Software & Tools", "Code", "Lisensi tools, plugin & starter template"),
    ProductCategory("ebook", "E-Book & Books", "MenuBook", "Buku digital format PDF & EPUB"),
    ProductCategory("course", "Kursus Online", "School", "Video tutorial intensif & sertifikasi"),
    ProductCategory("design", "Design Assets", "Palette", "UI Kit, font, 3D icon & vector pack"),
    ProductCategory("services", "Digital Services", "Handyman", "Jasa setup server, API & audit"),
    ProductCategory("voucher", "Voucher Digital", "ConfirmationNumber", "Akun cloud & gift card digital")
)

data class PaymentMethod(
    val id: String,
    val name: String,
    val type: String, // "QRIS", "VA", "EWALLET"
    val accountOrNumber: String,
    val fee: Double = 0.0,
    val instructions: String
)

val PAYMENT_METHODS = listOf(
    PaymentMethod(
        id = "qris",
        name = "QRIS (Semua E-Wallet / Bank)",
        type = "QRIS",
        accountOrNumber = "NMID: ID202619481928",
        fee = 0.0,
        instructions = "Buka aplikasi m-banking atau e-wallet Anda (GoPay, OVO, Dana, ShopeePay, BCA Mobile) dan scan kode QR."
    ),
    PaymentMethod(
        id = "bca_va",
        name = "BCA Virtual Account",
        type = "VA",
        accountOrNumber = "8808 1928 3847 2910",
        fee = 1000.0,
        instructions = "Transfer melalui BCA Mobile / KlikBCA / ATM ke nomor Virtual Account di atas."
    ),
    PaymentMethod(
        id = "mandiri_va",
        name = "Mandiri Virtual Account",
        type = "VA",
        accountOrNumber = "8930 4819 2837 4619",
        fee = 1000.0,
        instructions = "Transfer melalui Livin by Mandiri atau ATM ke nomor Virtual Account di atas."
    ),
    PaymentMethod(
        id = "gopay",
        name = "GoPay / GoPay Coins",
        type = "EWALLET",
        accountOrNumber = "0812-8941-8920",
        fee = 0.0,
        instructions = "Pembayaran instan langsung melalui saldo GoPay."
    ),
    PaymentMethod(
        id = "ovo",
        name = "OVO Cash",
        type = "EWALLET",
        accountOrNumber = "0812-8941-8920",
        fee = 0.0,
        instructions = "Notifikasi pembayaran akan dikirimkan ke nomor OVO Anda."
    )
)
