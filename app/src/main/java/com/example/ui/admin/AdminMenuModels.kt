package com.example.ui.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Group categories for the aligned Admin & Reseller Console hierarchy.
 */
enum class AdminMenuGroup(
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    PANEL_RESELLER("Panel Reseller", Icons.Default.Storefront, "Operasional reseller, saldo, API & sinkronisasi"),
    PEMBAYARAN("Pembayaran", Icons.Default.Payments, "Metode pembayaran & margin keuntungan"),
    BOT_SETTINGS("Bot Settings", Icons.Default.SmartToy, "Otomasi bot Telegram & WhatsApp"),
    KELOLA_PRODUK("Kelola Produk", Icons.Default.Inventory2, "Katalog digital, stok, varian & review"),
    KUSTOMISASI("Kustomisasi", Icons.Default.Tune, "Banner, flash sale, SEO, PWA & toko"),
    PELANGGAN("Pelanggan", Icons.Default.People, "Database pelanggan & riwayat pembelian"),
    BANTUAN_SETUP("Bantuan & Setup", Icons.Default.HelpOutline, "Panduan integrasi & setup sistem"),
    MANAJEMEN_TIM("Manajemen Tim", Icons.Default.SupervisorAccount, "User admin, hak akses, audit log & alert"),
    AKUN_SAYA("Akun Saya", Icons.Default.AccountCircle, "Profil admin & keluar sesi")
}

/**
 * Submenus aligned with the exact specifications requested by the user.
 */
enum class AdminSubmenu(
    val group: AdminMenuGroup,
    val title: String,
    val icon: ImageVector,
    val description: String = ""
) {
    // 1. Panel Reseller
    DASHBOARD(
        group = AdminMenuGroup.PANEL_RESELLER,
        title = "Dashboard",
        icon = Icons.Default.Dashboard,
        description = "Ringkasan performa penjualan, pesanan & saldo"
    ),
    ANALITIK(
        group = AdminMenuGroup.PANEL_RESELLER,
        title = "Analitik",
        icon = Icons.Default.Analytics,
        description = "Statistik omset, rasio konversi & tren produk"
    ),
    TOP_UP_SALDO(
        group = AdminMenuGroup.PANEL_RESELLER,
        title = "Top Up Saldo",
        icon = Icons.Default.AccountBalanceWallet,
        description = "Deposit saldo reseller Sekalipay & riwayat mutasi"
    ),
    PESANAN(
        group = AdminMenuGroup.PANEL_RESELLER,
        title = "Pesanan",
        icon = Icons.Default.ReceiptLong,
        description = "Manajemen pesanan & status pengiriman lisensi"
    ),
    API_SEKALIPAY(
        group = AdminMenuGroup.PANEL_RESELLER,
        title = "API Sekalipay",
        icon = Icons.Default.Api,
        description = "Kredensial API, webhook endpoint & testing"
    ),
    SYNC_PRODUK(
        group = AdminMenuGroup.PANEL_RESELLER,
        title = "Sync Produk",
        icon = Icons.Default.Sync,
        description = "Sinkronisasi otomatis katalog & stok dari provider"
    ),

    // 2. Pembayaran
    METODE_BAYAR(
        group = AdminMenuGroup.PEMBAYARAN,
        title = "Metode Bayar",
        icon = Icons.Default.Payment,
        description = "Pengaturan QRIS, Virtual Account & E-Wallet"
    ),
    MARGIN_MARKUP(
        group = AdminMenuGroup.PEMBAYARAN,
        title = "Margin / Markup",
        icon = Icons.Default.PriceChange,
        description = "Konfigurasi margin profit & pembulatan harga"
    ),

    // 3. Bot Settings
    KONFIGURASI_BOT(
        group = AdminMenuGroup.BOT_SETTINGS,
        title = "Konfigurasi Bot",
        icon = Icons.Default.SmartToy,
        description = "Token bot Telegram & WhatsApp auto-reply dispatch"
    ),

    // 4. Kelola Produk
    KATEGORI(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Kategori",
        icon = Icons.Default.Category,
        description = "Struktur & taksonomi kategori produk digital"
    ),
    PRODUK(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Produk",
        icon = Icons.Default.Inventory2,
        description = "Master data produk digital, berkas & lisensi"
    ),
    PRODUK_UNGGULAN(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Produk Unggulan",
        icon = Icons.Default.Star,
        description = "Produk pilihan yang disematkan di beranda"
    ),
    PRODUK_TYPE(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Produk Type",
        icon = Icons.Default.Extension,
        description = "Tipe produk: Software, Ebook, Source Code, Akun"
    ),
    PRODUK_VARIANT(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Produk Variant",
        icon = Icons.Default.Layers,
        description = "Tingkatan lisensi: Personal, Komersial, Extended"
    ),
    DATA_STOK(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Data Stok",
        icon = Icons.Default.Storage,
        description = "Kelola stok kode lisensi, serial keys & voucher"
    ),
    ULASAN(
        group = AdminMenuGroup.KELOLA_PRODUK,
        title = "Ulasan",
        icon = Icons.Default.RateReview,
        description = "Moderasi ulasan & feedback bintang pembeli"
    ),

    // 5. Kustomisasi
    SLIDER_BANNER(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Slider/Banner",
        icon = Icons.Default.ViewCarousel,
        description = "Pengaturan spanduk promosi beranda aplikasi"
    ),
    FLASH_SALE(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Flash Sale",
        icon = Icons.Default.FlashOn,
        description = "Program promo kilat dengan hitung mundur"
    ),
    VOUCHER(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Voucher",
        icon = Icons.Default.Discount,
        description = "Kupon diskon, potongan harga & cashback"
    ),
    WIDGET(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Widget",
        icon = Icons.Default.Widgets,
        description = "Komponen floating chat, popup & badge jaminan"
    ),
    HALAMAN_STATIS(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Halaman Statis",
        icon = Icons.Default.Description,
        description = "Syarat ketentuan, kebijakan privasi & FAQ"
    ),
    ARTIKEL_BLOG(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Artikel Blog",
        icon = Icons.Default.Article,
        description = "Publikasi panduan teknologi, rilis update & tips"
    ),
    SEO_PIXEL(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "SEO & Pixel",
        icon = Icons.Default.TravelExplore,
        description = "Google Analytics, Meta Pixel & optimasi meta tag"
    ),
    PWA(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "PWA",
        icon = Icons.Default.InstallMobile,
        description = "Konfigurasi Progressive Web App & offline mode"
    ),
    KONFIGURASI_UMUM(
        group = AdminMenuGroup.KUSTOMISASI,
        title = "Konfigurasi Umum",
        icon = Icons.Default.Settings,
        description = "Pengaturan identitas toko, kontak & preferensi"
    ),

    // 6. Pelanggan
    DATA_PELANGGAN(
        group = AdminMenuGroup.PELANGGAN,
        title = "Data Pelanggan",
        icon = Icons.Default.People,
        description = "Database pelanggan terdaftar, kontak & akumulasi belanja"
    ),
    BROADCAST_NOTIFIKASI(
        group = AdminMenuGroup.PELANGGAN,
        title = "Broadcast Notifikasi",
        icon = Icons.Default.Campaign,
        description = "Kirim siaran notifikasi push & promo ke pelanggan"
    ),

    // 7. Bantuan & Setup
    PANDUAN_SETUP(
        group = AdminMenuGroup.BANTUAN_SETUP,
        title = "Panduan Setup",
        icon = Icons.Default.MenuBook,
        description = "Panduan langkah integrasi Sekalipay, bot & toko"
    ),

    // 8. Manajemen Tim
    PENGGUNA(
        group = AdminMenuGroup.MANAJEMEN_TIM,
        title = "Pengguna",
        icon = Icons.Default.ManageAccounts,
        description = "Akun operator toko, staf CS & administrator"
    ),
    ROLES_HAK_AKSES(
        group = AdminMenuGroup.MANAJEMEN_TIM,
        title = "Roles & Hak Akses",
        icon = Icons.Default.Lock,
        description = "Matriks wewenang Super Admin, Admin, dan Staf"
    ),
    LOG_AKTIVITAS(
        group = AdminMenuGroup.MANAJEMEN_TIM,
        title = "Log Aktivitas",
        icon = Icons.Default.History,
        description = "Jejak audit forensik keamanan & aktivitas sistem"
    ),
    ALERT_KEAMANAN(
        group = AdminMenuGroup.MANAJEMEN_TIM,
        title = "Alert Keamanan",
        icon = Icons.Default.Shield,
        description = "YF-Shield anti-tamper, scan malware & proteksi brute force"
    ),

    // 9. Akun Saya
    PROFIL(
        group = AdminMenuGroup.AKUN_SAYA,
        title = "Profil",
        icon = Icons.Default.Person,
        description = "Pengaturan data profil & kredensial akun aktif"
    ),
    KELUAR(
        group = AdminMenuGroup.AKUN_SAYA,
        title = "Keluar",
        icon = Icons.AutoMirrored.Filled.Logout,
        description = "Akhiri sesi login administrasi"
    );

    companion object {
        fun byGroup(group: AdminMenuGroup): List<AdminSubmenu> {
            return values().filter { it.group == group }
        }
    }
}
