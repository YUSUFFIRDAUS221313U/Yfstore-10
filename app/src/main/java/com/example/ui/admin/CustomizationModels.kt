package com.example.ui.admin

import java.util.UUID

/**
 * Data models for comprehensive e-commerce CMS & UI Customization
 */

data class BannerItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val actionUrl: String = "",
    val badge: String = "PROMO",
    val isActive: Boolean = true,
    val sortOrder: Int = 1
)

data class FlashSaleCampaign(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val discountPercent: Int,
    val bannerUrl: String,
    val endsAtText: String,
    val targetCategory: String = "Semua Produk",
    val isActive: Boolean = true
)

data class CustomWidget(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String, // e.g., "FLOAT_CHAT", "TESTIMONIALS", "SECURITY_SHIELD", "LIVE_STATS", "ANNOUNCEMENT"
    val description: String,
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 1
)

data class StaticPage(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val slug: String,
    val summary: String,
    val content: String,
    val lastUpdated: String = "16 Sep 2026",
    val isPublished: Boolean = true
)

data class BlogPost(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val author: String = "Yusuf Firdaus",
    val date: String = "16 Sep 2026",
    val coverImageUrl: String,
    val readTime: String = "4 min baca",
    val excerpt: String,
    val content: String,
    val isPublished: Boolean = true
)

data class SeoPixelSettings(
    val metaTitle: String = "YF STORE | Toko Produk Digital, Source Code & Lisensi Resmi",
    val metaDescription: String = "Marketplace produk digital terlengkap dengan sistem lisensi instan, anti-tamper YF-Shield™, dan integrasi payment Sekalipay.",
    val metaKeywords: String = "source code, template saas, nextjs 15, flutter ui, lisensi digital, yfstore",
    val ogImageUrl: String = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1200&q=80",
    val googleAnalyticsId: String = "G-9YFSTORE88",
    val facebookPixelId: String = "104928192847192",
    val tiktokPixelId: String = "C9381029318",
    val searchConsoleVerification: String = "google-site-verification=yf-store-master-2026"
)

data class PwaSettings(
    val appName: String = "YF STORE Digital Hub",
    val shortName: String = "YFSTORE",
    val themeColor: String = "#E11D48",
    val backgroundColor: String = "#0F172A",
    val appIconUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=512&q=80",
    val enableOfflineMode: Boolean = true,
    val enableInstallPrompt: Boolean = true
)

data class GeneralStoreConfig(
    val storeName: String = "YF STORE",
    val tagline: String = "Official Digital Software & Source Code Marketplace",
    val logoUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&q=80",
    val faviconUrl: String = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=64&q=80",
    val supportEmail: String = "support@yfstore.id",
    val supportWhatsapp: String = "+62 821-8899-0011",
    val officeAddress: String = "YF Technology Center, Jakarta, Indonesia",
    val currencySymbol: String = "Rp"
)

/**
 * Curated preset high-quality images for easy 1-click selection in customization
 */
object CustomizationPresets {
    val BANNER_PRESETS = listOf(
        "Promo Merdeka Tech" to "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&q=80",
        "Cyber Security YF-Shield" to "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800&q=80",
        "Modern SaaS Architecture" to "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&q=80",
        "Mobile App UI Starter" to "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&q=80",
        "AI Agent & Cloud Server" to "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80"
    )

    val LOGO_PRESETS = listOf(
        "Metallic Ruby Emblem" to "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=256&q=80",
        "Cyber Shield Tech" to "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=256&q=80",
        "Digital Cube Matrix" to "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=256&q=80"
    )

    fun getDefaultBanners(): List<BannerItem> = listOf(
        BannerItem(
            id = "banner_1",
            title = "Promo Spesial SaaS Template 2026",
            subtitle = "Dapatkan diskon potongan 25% untuk seluruh source code enterprise",
            imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&q=80",
            actionUrl = "saas_bundle",
            badge = "HOT DEAL",
            isActive = true,
            sortOrder = 1
        ),
        BannerItem(
            id = "banner_2",
            title = "Proteksi Penuh YF-Shield™ Anti-Malware",
            subtitle = "Setiap file binary diverifikasi dengan hash SHA-256 dan bebas backdoor",
            imageUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800&q=80",
            actionUrl = "security_center",
            badge = "SECURED",
            isActive = true,
            sortOrder = 2
        ),
        BannerItem(
            id = "banner_3",
            title = "Integrasi Pembayaran QRIS & Sekalipay",
            subtitle = "Checkout instan otomatis aktif dalam 5 detik dengan verifikasi webhook",
            imageUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&q=80",
            actionUrl = "payment_gateways",
            badge = "INSTANT",
            isActive = true,
            sortOrder = 3
        )
    )

    fun getDefaultFlashSales(): List<FlashSaleCampaign> = listOf(
        FlashSaleCampaign(
            id = "fs_1",
            title = "Flash Sale Midnight Tech Rush",
            discountPercent = 40,
            bannerUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&q=80",
            endsAtText = "Berakhir dalam 08j : 45m : 20d",
            targetCategory = "Templates",
            isActive = true
        ),
        FlashSaleCampaign(
            id = "fs_2",
            title = "Weekend Developer Blitz",
            discountPercent = 30,
            bannerUrl = "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&q=80",
            endsAtText = "Berakhir dalam 24j : 00m : 00d",
            targetCategory = "Mobile UI",
            isActive = true
        )
    )

    fun getDefaultWidgets(): List<CustomWidget> = listOf(
        CustomWidget(
            id = "wgt_1",
            name = "Tombol WhatsApp Live Chat Floating",
            type = "FLOAT_CHAT",
            description = "Tombol melayang di pojok kanan bawah untuk konsultasi langsung",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&q=80",
            isActive = true,
            sortOrder = 1
        ),
        CustomWidget(
            id = "wgt_2",
            name = "Testimonial & Review Carousel",
            type = "TESTIMONIALS",
            description = "Menampilkan ulasan bintang 5 pembeli terverifikasi di beranda",
            imageUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=200&q=80",
            isActive = true,
            sortOrder = 2
        ),
        CustomWidget(
            id = "wgt_3",
            name = "Lencana Sertifikasi Keamanan YF-Shield",
            type = "SECURITY_SHIELD",
            description = "Menampilkan garansi garansi anti-trojan dan lisensi original",
            imageUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=200&q=80",
            isActive = true,
            sortOrder = 3
        ),
        CustomWidget(
            id = "wgt_4",
            name = "Live Counter Transaksi Berhasil",
            type = "LIVE_STATS",
            description = "Pop-up notifikasi penjualan terbaru secara live",
            imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=200&q=80",
            isActive = false,
            sortOrder = 4
        )
    )

    fun getDefaultStaticPages(): List<StaticPage> = listOf(
        StaticPage(
            id = "page_terms",
            title = "Syarat & Ketentuan Lisensi",
            slug = "terms-and-conditions",
            summary = "Ketentuan pemakaian source code, varian lisensi personal dan komersial.",
            content = "1. Lisensi Standar: Berhak digunakan untuk 1 proyek komersial.\n2. Dilarang mendistribusikan ulang atau menjual kembali source code master tanpa otorisasi.\n3. Hak cipta arsitektur tetap dimiliki oleh Yusuf Firdaus (YF STORE).\n4. Seluruh update berkala dan patch keamanan disediakan gratis seumur hidup melalui Download Center.",
            lastUpdated = "15 Sep 2026",
            isPublished = true
        ),
        StaticPage(
            id = "page_privacy",
            title = "Kebijakan Privasi & Enkripsi Data",
            slug = "privacy-policy",
            summary = "Informasi perlindungan data pengguna, enkripsi token dan log audit.",
            content = "YF STORE menjunjung tinggi privasi Anda. Semua data transaksi, kunci lisensi, dan identitas diverifikasi menggunakan enkripsi AES-256 dan signature SHA-256. Kami tidak pernah membagikan data kepada pihak ketiga yang tidak berwenang.",
            lastUpdated = "14 Sep 2026",
            isPublished = true
        ),
        StaticPage(
            id = "page_refund",
            title = "Kebijakan Garansi & Pengembalian Dana",
            slug = "warranty-refund",
            summary = "Jaminan 100% file berfungsi sesuai dokumentasi teknis yang disertakan.",
            content = "Garansi 7 hari berlaku apabila berkas digital terbukti korup, tidak dapat di-build sesuai panduan dokumentasi, atau ditemukan celah keamanan yang tidak dapat diselesaikan oleh tim dukungan kami.",
            lastUpdated = "10 Sep 2026",
            isPublished = true
        ),
        StaticPage(
            id = "page_faq",
            title = "Tanya Jawab Seputar Produk Digital (FAQ)",
            slug = "frequently-asked-questions",
            summary = "Pertanyaan populer seputar cara unduh berkas, generate invoice, dan API.",
            content = "Q: Bagaimana cara mendapatkan berkas setelah bayar?\nA: Sistem Sekalipay mengonfirmasi otomatis dan tautan download master zip langsung muncul di menu Lisensi Saya.\n\nQ: Apakah ada dukungan instalasi?\nA: Ya! Tim teknis kami siap memandu proses deploy via WhatsApp atau remote support.",
            lastUpdated = "12 Sep 2026",
            isPublished = true
        )
    )

    fun getDefaultBlogPosts(): List<BlogPost> = listOf(
        BlogPost(
            id = "blog_1",
            title = "Panduan Memilih Arsitektur SaaS Berbasis Next.js 15 & Jetpack Compose",
            category = "Arsitektur",
            author = "Yusuf Firdaus",
            date = "15 Sep 2026",
            coverImageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&q=80",
            readTime = "5 min baca",
            excerpt = "Tips membangun ekosistem software multiplatform yang cepat, aman, dan siap scale-up.",
            content = "Dalam membangun aplikasi digital masa kini, keselarasan antara backend microservices dan frontend modern sangat krusial. Artikel ini membedah praktik terbaik implementasi Clean Architecture, StateFlow, dan enkripsi payload.",
            isPublished = true
        ),
        BlogPost(
            id = "blog_2",
            title = "Keamanan Digital: Mengapa Checksum SHA-256 Wajib untuk File Source Code",
            category = "Keamanan",
            author = "Security Team YF",
            date = "12 Sep 2026",
            coverImageUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800&q=80",
            readTime = "4 min baca",
            excerpt = "Mencegah serangan man-in-the-middle dan injeksi malware pada template digital.",
            content = "Integritas berkas binary adalah kunci kepercayaan pembeli digital. Sistem YF-Shield™ menerapkan hashing otomatis pada setiap upload berkas master untuk memastikan kode 100% steril dan otentik.",
            isPublished = true
        ),
        BlogPost(
            id = "blog_3",
            title = "Optimalisasi Gateway Pembayaran Instan Menggunakan Sekalipay Webhook",
            category = "Fintech",
            author = "DevOps Lead",
            date = "08 Sep 2026",
            coverImageUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&q=80",
            readTime = "6 min baca",
            excerpt = "Strategi auto-dispatch lisensi dalam hitungan milidetik setelah mutasi QRIS terdeteksi.",
            content = "Dengan arsitektur event-driven webhook Sekalipay, order status berpindah ke Completed secara asinkron tanpa perlunya pembeli melakukan konfirmasi bukti transfer secara manual.",
            isPublished = true
        )
    )
}
