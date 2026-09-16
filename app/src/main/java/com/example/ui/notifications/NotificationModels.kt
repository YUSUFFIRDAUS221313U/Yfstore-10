package com.example.ui.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.AppScreen
import com.example.ui.theme.*

enum class NotificationType(
    val title: String,
    val icon: ImageVector,
    val color: Color
) {
    SEMUA("Semua", Icons.Default.Notifications, BrandIndigo),
    TRANSAKSI("Transaksi", Icons.Default.ReceiptLong, BrandEmerald),
    PROMO("Promo", Icons.Default.Discount, BrandOrange),
    KEAMANAN("Keamanan", Icons.Default.Shield, BrandCrimson),
    SISTEM("Sistem", Icons.Default.Info, BrandBlue)
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionRoute: AppScreen? = null,
    val actionLabel: String? = null,
    val targetRole: String? = "ALL" // "ALL", "MEMBER", "STAFF", "ADMIN"
)

object NotificationPresets {
    fun getDefaultNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = "notif_trans_1",
            title = "Pesanan #ORD-9281 Selesai Dikonfirmasi",
            message = "Pembayaran sebesar Rp 150.000 terverifikasi. Lisensi digital YF-SHIELD-PRO aktif dan berkas siap diunduh.",
            type = NotificationType.TRANSAKSI,
            timestamp = "10 menit yang lalu",
            isRead = false,
            actionRoute = AppScreen.MEMBER_CENTER,
            actionLabel = "Buka Download Center",
            targetRole = "ALL"
        ),
        NotificationItem(
            id = "notif_promo_1",
            title = "⚡ Promo Merdeka Digital 2026 Aktif!",
            message = "Dapatkan diskon 20% untuk pembelian template SaaS & Source Code dengan kode voucher MERDEKA20.",
            type = NotificationType.PROMO,
            timestamp = "1 jam yang lalu",
            isRead = false,
            actionRoute = AppScreen.CATALOG,
            actionLabel = "Lihat Katalog Promo",
            targetRole = "ALL"
        ),
        NotificationItem(
            id = "notif_sec_1",
            title = "Proteksi YF-Shield: Akun Aman & Terverifikasi",
            message = "Audit integritas berkas digital SHA-256 dan proteksi anti-tamper selesai tanpa ancaman terdeteksi.",
            type = NotificationType.KEAMANAN,
            timestamp = "3 jam yang lalu",
            isRead = false,
            actionRoute = AppScreen.MEMBER_CENTER,
            actionLabel = "Lihat Status Keamanan",
            targetRole = "ALL"
        ),
        NotificationItem(
            id = "notif_sys_1",
            title = "Pembaruan Aplikasi Toko Digital v2.5",
            message = "Fitur Pusat Notifikasi interaktif, live customization banner, dan download instan telah aktif secara penuh.",
            type = NotificationType.SISTEM,
            timestamp = "1 hari yang lalu",
            isRead = true,
            actionRoute = AppScreen.HOME,
            actionLabel = "Jelajahi Beranda",
            targetRole = "ALL"
        ),
        NotificationItem(
            id = "notif_promo_2",
            title = "Voucher Cashback 15% Siap Digunakan",
            message = "Gunakan kode kupon YFTECH15 saat checkout untuk pembelian minimal Rp 100.000.",
            type = NotificationType.PROMO,
            timestamp = "2 hari yang lalu",
            isRead = true,
            actionRoute = AppScreen.CATALOG,
            actionLabel = "Gunakan Voucher",
            targetRole = "ALL"
        ),
        NotificationItem(
            id = "notif_trans_2",
            title = "Lisensi Digital 'Android E-Commerce Starter Kit'",
            message = "Serial token lisensi Anda telah terdaftar resmi di basis data garansi seumur hidup YF STORE.",
            type = NotificationType.TRANSAKSI,
            timestamp = "3 hari yang lalu",
            isRead = true,
            actionRoute = AppScreen.MEMBER_CENTER,
            actionLabel = "Cek Serial Key",
            targetRole = "ALL"
        )
    )
}
