package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class MarketplaceRepository(private val dao: MarketplaceDao) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val activeProducts: Flow<List<ProductEntity>> = dao.getActiveProducts()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allCoupons: Flow<List<CouponEntity>> = dao.getAllCoupons()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAuditLogs()

    fun getCartItems(userId: String): Flow<List<CartItemEntity>> = dao.getCartItems(userId)
    fun getOrdersByUser(userId: String): Flow<List<OrderEntity>> = dao.getOrdersByUser(userId)
    fun getPurchasedItemsByUser(userId: String): Flow<List<OrderItemEntity>> = dao.getCompletedOrderItemsByUser(userId)
    fun getWishlistByUser(userId: String): Flow<List<WishlistEntity>> = dao.getWishlistByUser(userId)
    fun getProductReviews(productId: String): Flow<List<ReviewEntity>> = dao.getReviewsByProduct(productId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val existingUsers = dao.getUserById("usr_member_1")
        if (existingUsers == null) {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        // Users for all 5 roles
        val users = listOf(
            UserEntity(
                id = "usr_member_1",
                name = "Budi Santoso",
                email = "budi.santoso@gmail.com",
                phone = "0812-3456-7890",
                role = UserRole.MEMBER.name,
                isActive = true
            ),
            UserEntity(
                id = "usr_staff_1",
                name = "Ahmad Fauzi (Staff)",
                email = "staff.ahmad@digimarket.id",
                phone = "0813-9876-5432",
                role = UserRole.STAFF.name,
                isActive = true
            ),
            UserEntity(
                id = "usr_admin_1",
                name = "Siti Rahmawati (Admin)",
                email = "admin.siti@digimarket.id",
                phone = "0821-4433-2211",
                role = UserRole.ADMIN.name,
                isActive = true
            ),
            UserEntity(
                id = "usr_superadmin_1",
                name = "Hendra Wijaya (Super Admin)",
                email = "superadmin.hendra@digimarket.id",
                phone = "0811-0011-2233",
                role = UserRole.SUPER_ADMIN.name,
                isActive = true
            )
        )
        dao.insertUsers(users)

        // Products matching the 6 PRD categories
        val products = listOf(
            ProductEntity(
                id = "prod_saas_kit",
                title = "SaaS Landing Kit & Next.js 15 Starter",
                categoryId = "software",
                price = 349000.0,
                description = "Template boiler plate siap produksi dengan Next.js 15 App Router, Tailwind CSS, Auth, Stripe/Midtrans integration, dan SEO metadata.",
                fileType = "ZIP",
                fileSize = "48 MB",
                downloadFileName = "saas-starter-v15.zip",
                licensePrefix = "SAAS-PRO-",
                version = "2.1.0",
                rating = 4.9f,
                reviewCount = 38,
                salesCount = 142,
                isFeatured = true,
                isActive = true
            ),
            ProductEntity(
                id = "prod_pos_kasir",
                title = "POS Kasir Pro Android & Desktop Source Code",
                categoryId = "software",
                price = 499000.0,
                description = "Full source code aplikasi kasir POS dengan fitur cetak struk thermal bluetooth, laporan laba rugi, scan barcode, dan multi-cabang.",
                fileType = "ZIP",
                fileSize = "115 MB",
                downloadFileName = "pos-kasir-pro-src.zip",
                licensePrefix = "POS-FULL-",
                version = "3.4.0",
                rating = 4.8f,
                reviewCount = 24,
                salesCount = 89,
                isFeatured = true,
                isActive = true
            ),
            ProductEntity(
                id = "prod_seo_plugin",
                title = "Ultimate SEO & Speed WordPress Plugin Pro",
                categoryId = "software",
                price = 189000.0,
                description = "Plugin optimasi kecepatan Core Web Vitals, auto WebP conversion, schema JSON-LD, dan XML sitemap otomatis untuk WordPress.",
                fileType = "ZIP",
                fileSize = "12 MB",
                downloadFileName = "ultimate-seo-pro-plugin.zip",
                licensePrefix = "WP-SEO-",
                version = "1.8.2",
                rating = 4.7f,
                reviewCount = 19,
                salesCount = 67,
                isFeatured = false,
                isActive = true
            ),
            ProductEntity(
                id = "prod_ebook_sysdesign",
                title = "E-Book: Mastering System Design & Microservices",
                categoryId = "ebook",
                price = 95000.0,
                description = "Buku panduan lengkap arsitektur backend skala jutaan user: database sharding, redis cache, kafka event-driven, dan kubernetes dalam Bahasa Indonesia.",
                fileType = "PDF",
                fileSize = "32 MB",
                downloadFileName = "system-design-microservices-id.pdf",
                licensePrefix = "EBK-SYS-",
                version = "1.2.0",
                rating = 5.0f,
                reviewCount = 52,
                salesCount = 310,
                isFeatured = true,
                isActive = true
            ),
            ProductEntity(
                id = "prod_ebook_freelance",
                title = "E-Book: Blueprint Freelance Global Raih $3,000/Bulan",
                categoryId = "ebook",
                price = 79000.0,
                description = "Strategi praktis menembus klien luar negeri di platform Upwork & LinkedIn, cara proposal accepted, invoicing internasional, dan negosiasi rate.",
                fileType = "PDF",
                fileSize = "18 MB",
                downloadFileName = "blueprint-freelance-global.pdf",
                licensePrefix = "EBK-FRL-",
                version = "2.0.0",
                rating = 4.8f,
                reviewCount = 41,
                salesCount = 220,
                isFeatured = false,
                isActive = true
            ),
            ProductEntity(
                id = "prod_course_compose",
                title = "Kursus: Master Jetpack Compose & Clean Architecture",
                categoryId = "course",
                price = 299000.0,
                description = "Akses modul video interaktif dari nol hingga mahir membuat aplikasi modern Android dengan Kotlin M3, Room, Flow, KSP, dan modularisasi.",
                fileType = "PORTAL",
                fileSize = "18 Modul HD",
                downloadFileName = "akses-portal-kursus-compose.html",
                licensePrefix = "CRS-KOT-",
                version = "2026 Batch",
                rating = 4.9f,
                reviewCount = 63,
                salesCount = 195,
                isFeatured = true,
                isActive = true
            ),
            ProductEntity(
                id = "prod_course_uiux",
                title = "Kursus: UI/UX Design System: Figma to Production",
                categoryId = "course",
                price = 249000.0,
                description = "Pelajari desain token, variables, auto layout advanced, micro-interaction, dan hand-off ke frontend developer secara profesional.",
                fileType = "PORTAL",
                fileSize = "14 Modul HD",
                downloadFileName = "akses-portal-kursus-figma.html",
                licensePrefix = "CRS-FIG-",
                version = "2026 Batch",
                rating = 4.8f,
                reviewCount = 30,
                salesCount = 110,
                isFeatured = false,
                isActive = true
            ),
            ProductEntity(
                id = "prod_design_fintech",
                title = "Fintech & Crypto Mobile App UI Kit (140+ Screens)",
                categoryId = "design",
                price = 159000.0,
                description = "File desain Figma premium komponen lengkap: wallet, send money, crypto trading charts, KYC flow, dark & light mode support.",
                fileType = "FIG",
                fileSize = "92 MB",
                downloadFileName = "fintech-crypto-uikit-figma.fig",
                licensePrefix = "FIG-FIN-",
                version = "1.5.0",
                rating = 4.9f,
                reviewCount = 29,
                salesCount = 164,
                isFeatured = true,
                isActive = true
            ),
            ProductEntity(
                id = "prod_design_3d",
                title = "3D Tech & Isometric Business Illustration Pack",
                categoryId = "design",
                price = 129000.0,
                description = "Koleksi 80+ aset 3D render transparan PNG dan file Blender source (.blend) bertema cloud computing, analytics, dan robotika.",
                fileType = "ZIP",
                fileSize = "210 MB",
                downloadFileName = "3d-tech-isometric-pack.zip",
                licensePrefix = "DSG-3D-",
                version = "1.0.0",
                rating = 4.7f,
                reviewCount = 15,
                salesCount = 76,
                isFeatured = false,
                isActive = true
            ),
            ProductEntity(
                id = "prod_service_gateway",
                title = "Jasa Integrasi Payment Gateway (Midtrans / Xendit)",
                categoryId = "services",
                price = 450000.0,
                description = "Layanan konfigurasi webhook, server notifications, implementasi QRIS, VA, dan testing sandbox hingga go-live siap transaksi.",
                fileType = "DOC",
                fileSize = "Layanan 1-Hari",
                downloadFileName = "panduan-setup-konsultasi.pdf",
                licensePrefix = "SRV-PGW-",
                version = "Layanan VIP",
                rating = 5.0f,
                reviewCount = 14,
                salesCount = 42,
                isFeatured = false,
                isActive = true
            ),
            ProductEntity(
                id = "prod_voucher_cloud",
                title = "Cloud Server Credit Voucher $50 USD (Digital Redeem)",
                categoryId = "voucher",
                price = 220000.0,
                description = "Voucher kode resmi mitra cloud hosting internasional. Saldo $50 untuk VPS, database cloud, dan object storage.",
                fileType = "KEY",
                fileSize = "Instan Token",
                downloadFileName = "voucher-cloud-redeem.txt",
                licensePrefix = "VCH-CLD-",
                version = "Exp Des 2027",
                rating = 4.9f,
                reviewCount = 45,
                salesCount = 188,
                isFeatured = true,
                isActive = true
            )
        )
        dao.insertProducts(products)

        // Coupons
        val coupons = listOf(
            CouponEntity(code = "DISKON20", discountPercent = 20, discountMax = 50000.0, minPurchase = 100000.0, isActive = true, usageCount = 18),
            CouponEntity(code = "MERDEKA", discountPercent = 30, discountMax = 100000.0, minPurchase = 150000.0, isActive = true, usageCount = 42),
            CouponEntity(code = "SUPERDEAL", discountPercent = 50, discountMax = 150000.0, minPurchase = 250000.0, isActive = true, usageCount = 9)
        )
        dao.insertCoupons(coupons)

        // Seed an initial completed order for Budi Santoso (Member) so Download Center is immediately active
        val demoOrderId = "ORD-2026-8941"
        val demoOrder = OrderEntity(
            id = demoOrderId,
            userId = "usr_member_1",
            userName = "Budi Santoso",
            userEmail = "budi.santoso@gmail.com",
            userPhone = "0812-3456-7890",
            totalAmount = 244200.0,
            discountAmount = 60800.0,
            couponCode = "DISKON20",
            paymentMethodId = "qris",
            paymentMethodName = "QRIS (Semua E-Wallet / Bank)",
            paymentStatus = "COMPLETED",
            paymentReference = "REF-QRIS-99201",
            createdAt = System.currentTimeMillis() - 86400000L
        )
        dao.insertOrder(demoOrder)

        val demoOrderItems = listOf(
            OrderItemEntity(
                id = "item_demo_1",
                orderId = demoOrderId,
                productId = "prod_saas_kit",
                productTitle = "SaaS Landing Kit & Next.js 15 Starter",
                price = 349000.0,
                fileType = "ZIP",
                fileSize = "48 MB",
                downloadFileName = "saas-starter-v15.zip",
                downloadToken = "TOK-9A82-FF41-B891-2026",
                downloadCount = 1,
                maxDownloads = 999,
                licenseKey = "SAAS-PRO-8891-4912-3021",
                expiryDays = 0 // Lifetime
            )
        )
        dao.insertOrderItems(demoOrderItems)

        // Initial Reviews
        val reviews = listOf(
            ReviewEntity(
                id = "rev_1",
                productId = "prod_saas_kit",
                userId = "usr_member_1",
                userName = "Budi Santoso",
                rating = 5,
                comment = "Sangat rapi kodenya! Boilerplate Next.js 15 ini menghemat waktu pembuatan produk SaaS saya berminggu-minggu. Download file instan langsung setelah bayar.",
                createdAt = System.currentTimeMillis() - 43200000L
            ),
            ReviewEntity(
                id = "rev_2",
                productId = "prod_ebook_sysdesign",
                userId = "usr_member_2",
                userName = "Rian Pratama",
                rating = 5,
                comment = "Buku system design terbaik dalam bahasa Indonesia. Penjelasan sharding dan kafka sangat mudah dipahami.",
                createdAt = System.currentTimeMillis() - 72000000L
            )
        )
        dao.insertReviews(reviews)

        // Initial Audit Logs (Super Admin trail)
        val logs = listOf(
            AuditLogEntity(
                id = "log_1",
                actorName = "System Initializer",
                actorRole = "SYSTEM",
                action = "INITIALIZE_STORE",
                target = "Katalog & Konfigurasi Toko Digital Berhasil Disiapkan",
                timestamp = System.currentTimeMillis() - 172800000L
            ),
            AuditLogEntity(
                id = "log_2",
                actorName = "Hendra Wijaya (Super Admin)",
                actorRole = "SUPER_ADMIN",
                action = "CREATE_ROLE_POLICY",
                target = "Matrix Permission Guest, Member, Staff, Admin disetujui",
                timestamp = System.currentTimeMillis() - 120000000L
            ),
            AuditLogEntity(
                id = "log_3",
                actorName = "Siti Rahmawati (Admin)",
                actorRole = "ADMIN",
                action = "ADD_COUPON",
                target = "Kupon Promo DISKON20 & MERDEKA diaktifkan",
                timestamp = System.currentTimeMillis() - 90000000L
            )
        )
        for (log in logs) {
            dao.insertAuditLog(log)
        }
    }

    // --- Actions ---
    suspend fun addToCart(userId: String, productId: String) = withContext(Dispatchers.IO) {
        val cartItem = CartItemEntity(
            id = "cart_" + UUID.randomUUID().toString().take(8),
            productId = productId,
            quantity = 1,
            userId = userId
        )
        dao.insertCartItem(cartItem)
    }

    suspend fun removeFromCart(cartItemId: String) = withContext(Dispatchers.IO) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart(userId: String) = withContext(Dispatchers.IO) {
        dao.clearCart(userId)
    }

    suspend fun toggleWishlist(userId: String, productId: String, isWishlisted: Boolean) = withContext(Dispatchers.IO) {
        if (isWishlisted) {
            dao.removeWishlist(userId, productId)
        } else {
            dao.insertWishlist(WishlistEntity(id = "wish_" + UUID.randomUUID().toString().take(8), userId = userId, productId = productId))
        }
    }

    suspend fun processCheckout(
        userId: String,
        userName: String,
        userEmail: String,
        userPhone: String,
        products: List<ProductEntity>,
        discountAmount: Double,
        couponCode: String?,
        paymentMethod: com.example.data.model.PaymentMethod
    ): OrderEntity = withContext(Dispatchers.IO) {
        val orderId = "ORD-" + (2026) + "-" + (1000..9999).random()
        val totalRaw = products.sumOf { it.price }
        val finalTotal = (totalRaw - discountAmount + paymentMethod.fee).coerceAtLeast(0.0)

        val order = OrderEntity(
            id = orderId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            userPhone = userPhone,
            totalAmount = finalTotal,
            discountAmount = discountAmount,
            couponCode = couponCode,
            paymentMethodId = paymentMethod.id,
            paymentMethodName = paymentMethod.name,
            paymentStatus = "COMPLETED", // Instant delivery as per PRD
            paymentReference = "PAY-" + UUID.randomUUID().toString().take(8).uppercase(),
            createdAt = System.currentTimeMillis()
        )
        dao.insertOrder(order)

        // Generate Order Items with secure download token and license key
        val orderItems = products.map { product ->
            val token = "DL-" + UUID.randomUUID().toString().take(12).uppercase()
            val license = product.licensePrefix + (1000..9999).random() + "-" + (1000..9999).random()
            OrderItemEntity(
                id = "item_" + UUID.randomUUID().toString().take(8),
                orderId = orderId,
                productId = product.id,
                productTitle = product.title,
                price = product.price,
                fileType = product.fileType,
                fileSize = product.fileSize,
                downloadFileName = product.downloadFileName,
                downloadToken = token,
                downloadCount = 0,
                maxDownloads = 999,
                licenseKey = license,
                expiryDays = 0
            )
        }
        dao.insertOrderItems(orderItems)

        // Update product sales counters
        for (prod in products) {
            val updated = prod.copy(salesCount = prod.salesCount + 1)
            dao.updateProduct(updated)
        }

        // Clear cart
        dao.clearCart(userId)

        // Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = userName,
                actorRole = "CUSTOMER",
                action = "ORDER_COMPLETED_INSTANT_DELIVERY",
                target = "Order $orderId senilai Rp ${finalTotal.toLong()} via ${paymentMethod.name}"
            )
        )

        order
    }

    suspend fun markDownloadCompleted(orderItemId: String) = withContext(Dispatchers.IO) {
        dao.incrementDownloadCount(orderItemId)
    }

    // --- Admin / Staff Actions ---
    suspend fun updateOrderStatus(orderId: String, newStatus: String, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        dao.updateOrderStatus(orderId, newStatus)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = "UPDATE_ORDER_STATUS",
                target = "Order $orderId diubah menjadi $newStatus"
            )
        )
    }

    suspend fun regenerateDownloadLink(orderItemId: String, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        val newToken = "REGEN-" + UUID.randomUUID().toString().take(12).uppercase()
        dao.regenerateDownloadToken(orderItemId, newToken)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = "REGENERATE_DOWNLOAD_LINK",
                target = "Item $orderItemId link download diperbarui ke token baru"
            )
        )
    }

    suspend fun saveProduct(product: ProductEntity, isNew: Boolean, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        if (isNew) {
            dao.insertProduct(product)
        } else {
            dao.updateProduct(product)
        }
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = if (isNew) "ADD_PRODUCT" else "UPDATE_PRODUCT",
                target = "Produk digital '${product.title}' (Rp ${product.price.toLong()})"
            )
        )
    }

    suspend fun setProductActive(productId: String, isActive: Boolean, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        dao.setProductActive(productId, isActive)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = if (isActive) "ACTIVATE_PRODUCT" else "DEACTIVATE_PRODUCT",
                target = "Product ID $productId diubah status aktifnya"
            )
        )
    }

    suspend fun deleteProduct(productId: String, productTitle: String, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        dao.deleteProductById(productId)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = "DELETE_PRODUCT",
                target = "Produk '$productTitle' ($productId) dihapus permanen"
            )
        )
    }

    suspend fun addCoupon(coupon: CouponEntity, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        dao.insertCoupon(coupon)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = "CREATE_COUPON",
                target = "Kupon ${coupon.code} (${coupon.discountPercent}%)"
            )
        )
    }

    suspend fun toggleUserActive(userId: String, currentActive: Boolean, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        dao.setUserActive(userId, !currentActive)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = if (currentActive) "SUSPEND_USER" else "ACTIVATE_USER",
                target = "User ID $userId"
            )
        )
    }

    suspend fun updateUserRole(userId: String, newRole: String, actorName: String, actorRole: String) = withContext(Dispatchers.IO) {
        dao.updateUserRole(userId, newRole)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorName = actorName,
                actorRole = actorRole,
                action = "CHANGE_USER_ROLE",
                target = "User ID $userId dipromosikan ke role $newRole"
            )
        )
    }

    suspend fun addReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        dao.insertReview(review)
    }
}
