package com.example.ui.admin

import com.example.data.model.UserRole

/**
 * Model representing a modular system feature or capability
 * that can be assigned or removed from roles.
 */
data class RoleFeature(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val isCustom: Boolean = false
)

/**
 * Default permission definitions and standard RBAC mappings.
 */
object RolePermissionDefaults {
    val SYSTEM_FEATURES: List<RoleFeature> = listOf(
        // 1. Katalog & Transaksi
        RoleFeature("KATALOG_BROWSE", "Melihat & Mencari Produk Digital", "Katalog & Transaksi", "Akses daftar katalog, filter kategori & pencarian"),
        RoleFeature("PRODUCT_PREVIEW", "Melihat Berkas Preview & Demo", "Katalog & Transaksi", "Preview tangkapan layar, spesifikasi teknis & video demo"),
        RoleFeature("CART_CHECKOUT", "Keranjang Belanja & Checkout", "Katalog & Transaksi", "Menambahkan item ke keranjang dan proses pembayaran"),
        RoleFeature("DOWNLOAD_CENTER", "Download Center & Lisensi Lifetime", "Katalog & Transaksi", "Akses unduh file master & token lisensi yang dibeli"),
        RoleFeature("REVIEWS_RATING", "Memberikan Review & Rating", "Katalog & Transaksi", "Tulis ulasan pembeli dan berikan rating bintang produk"),
        RoleFeature("WISHLIST_ACCESS", "Simpan ke Wishlist Favorit", "Katalog & Transaksi", "Simpan produk favorit ke daftar keinginan akun"),

        // 2. Operasional & Katalog
        RoleFeature("PANEL_ADMIN_ACCESS", "Akses Konsol Admin & Reseller", "Operasional & Katalog", "Izin membuka dasbor administrasi & operasional toko"),
        RoleFeature("PRODUCT_CREATE_EDIT", "Tambah & Modifikasi Produk", "Operasional & Katalog", "Unggah produk baru, ubah file, perbarui harga & deskripsi"),
        RoleFeature("PRODUCT_DELETE", "Hapus Produk Permanen", "Operasional & Katalog", "Menghapus item produk digital secara permanen dari basis data"),
        RoleFeature("ORDER_STATUS_MANAGE", "Ubah Status Pesanan & Kirim Lisensi", "Operasional & Katalog", "Update status transaksi, kirim lisensi & regenerasi tautan"),
        RoleFeature("STOCK_MANAGE", "Kelola Data Stok Serial & Lisensi", "Operasional & Katalog", "Manajemen inventaris serial key, token voucher & kode aktivasi"),
        RoleFeature("REVIEW_MODERATION", "Moderasi Ulasan & Testimoni", "Operasional & Katalog", "Menyetujui, menyembunyikan atau membalas feedback pembeli"),

        // 3. Keuangan & Reseller
        RoleFeature("COUPON_MANAGE", "Manajemen Kupon & Kode Promo", "Keuangan & Reseller", "Buat voucher diskon, atur persentase potongan & kuota pakai"),
        RoleFeature("PAYMENT_GATEWAY_CONFIG", "Konfigurasi Metode Bayar", "Keuangan & Reseller", "Aktifkan atau nonaktifkan saluran pembayaran QRIS & VA"),
        RoleFeature("PRICE_MARGIN_MARKUP", "Atur Margin Keuntungan & Markup", "Keuangan & Reseller", "Pengaturan persentase margin laba dan markup harga reseller"),
        RoleFeature("RESELLER_SALDO_TOPUP", "Top Up Saldo Deposit Reseller", "Keuangan & Reseller", "Pengisian saldo deposit operasional dan cek riwayat mutasi"),
        RoleFeature("SEKALIPAY_API_ACCESS", "Kredensial API & Webhook Sekalipay", "Keuangan & Reseller", "Akses API Key, Secret Key, IP Whitelist & pengujian webhook"),
        RoleFeature("ANALYTICS_FINANCE_VIEW", "Laporan Omzet & Analitik Penjualan", "Keuangan & Reseller", "Melihat grafik omzet, tren konversi & laporan keuangan"),

        // 4. Bot & Kustomisasi
        RoleFeature("BOT_CONFIG_MANAGE", "Konfigurasi Bot Telegram & WA", "Bot & Kustomisasi", "Pengaturan token bot, webhook auto-dispatch & notifikasi pesan"),
        RoleFeature("BANNER_CUSTOMIZE", "Kustomisasi Banner Slider Beranda", "Bot & Kustomisasi", "Unggah dan atur visibilitas spanduk promo visual toko"),
        RoleFeature("FLASH_SALE_CONFIG", "Atur Program Promo Flash Sale", "Bot & Kustomisasi", "Jadwalkan promo kilat dengan hitung mundur dan harga miring"),
        RoleFeature("SEO_PWA_SETTINGS", "Pengaturan SEO, Meta Tag & PWA", "Bot & Kustomisasi", "Konfigurasi Google Analytics, Meta Pixel & caching offline PWA"),

        // 5. Keamanan & Manajemen Tim
        RoleFeature("TEAM_MEMBERS_MANAGE", "Kelola Pengguna Tim & Staf", "Keamanan & Manajemen Tim", "Tambah akun staf baru, ubah peran & tangguhkan akun"),
        RoleFeature("ROLE_PERMISSION_CONFIG", "Ubah & Atur Hak Akses Fitur Role", "Keamanan & Manajemen Tim", "Menambah atau mengurangi izin fitur pada masing-masing role"),
        RoleFeature("AUDIT_LOG_INSPECT", "Inspeksi Log Audit Aktivitas", "Keamanan & Manajemen Tim", "Melihat catatan forensik aktivitas dan jejak audit admin"),
        RoleFeature("YF_SHIELD_SECURITY", "Pusat Keamanan & Anti-Tamper YF-Shield", "Keamanan & Manajemen Tim", "Kontrol scan malware, verifikasi SHA-256 & mitigasi ancaman")
    )

    fun getDefaultPermissions(): Map<UserRole, Set<String>> = mapOf(
        UserRole.GUEST to setOf(
            "KATALOG_BROWSE",
            "PRODUCT_PREVIEW",
            "CART_CHECKOUT"
        ),
        UserRole.MEMBER to setOf(
            "KATALOG_BROWSE",
            "PRODUCT_PREVIEW",
            "CART_CHECKOUT",
            "DOWNLOAD_CENTER",
            "REVIEWS_RATING",
            "WISHLIST_ACCESS"
        ),
        UserRole.STAFF to setOf(
            "KATALOG_BROWSE",
            "PRODUCT_PREVIEW",
            "CART_CHECKOUT",
            "DOWNLOAD_CENTER",
            "REVIEWS_RATING",
            "WISHLIST_ACCESS",
            "PANEL_ADMIN_ACCESS",
            "PRODUCT_CREATE_EDIT",
            "ORDER_STATUS_MANAGE",
            "STOCK_MANAGE",
            "REVIEW_MODERATION",
            "ANALYTICS_FINANCE_VIEW"
        ),
        UserRole.ADMIN to setOf(
            "KATALOG_BROWSE",
            "PRODUCT_PREVIEW",
            "CART_CHECKOUT",
            "DOWNLOAD_CENTER",
            "REVIEWS_RATING",
            "WISHLIST_ACCESS",
            "PANEL_ADMIN_ACCESS",
            "PRODUCT_CREATE_EDIT",
            "PRODUCT_DELETE",
            "ORDER_STATUS_MANAGE",
            "STOCK_MANAGE",
            "REVIEW_MODERATION",
            "COUPON_MANAGE",
            "PAYMENT_GATEWAY_CONFIG",
            "PRICE_MARGIN_MARKUP",
            "RESELLER_SALDO_TOPUP",
            "SEKALIPAY_API_ACCESS",
            "ANALYTICS_FINANCE_VIEW",
            "BOT_CONFIG_MANAGE",
            "BANNER_CUSTOMIZE",
            "FLASH_SALE_CONFIG",
            "SEO_PWA_SETTINGS",
            "TEAM_MEMBERS_MANAGE"
        ),
        UserRole.SUPER_ADMIN to SYSTEM_FEATURES.map { it.id }.toSet()
    )
}
