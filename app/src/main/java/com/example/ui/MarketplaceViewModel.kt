package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.MarketplaceRepository
import com.example.security.SecurityManager
import com.example.ui.admin.RoleFeature
import com.example.ui.admin.RolePermissionDefaults
import com.example.ui.admin.*
import com.example.ui.notifications.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    HOME,
    CATALOG,
    PRODUCT_DETAIL,
    CART_CHECKOUT,
    MEMBER_CENTER,
    ADMIN_PANEL,
    FAQ_CONTACT,
    AUTH,
    NOTIFICATIONS
}

enum class AdminTab {
    DASHBOARD,
    PRODUCTS,
    ORDERS,
    MEMBERS,
    COUPONS,
    ANALYTICS,
    AUDIT_LOGS,
    PERMISSION_MATRIX,
    SECURITY_CENTER
}

data class CurrentUserState(
    val id: String = "usr_member_1",
    val name: String = "Budi Santoso",
    val username: String = "budisantoso",
    val email: String = "budi.santoso@gmail.com",
    val phone: String = "0812-3456-7890",
    val role: UserRole = UserRole.MEMBER
)

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = MarketplaceRepository(db.marketplaceDao())

    // Screen navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Current logged in user / active persona
    private val _currentUser = MutableStateFlow(
        CurrentUserState(
            id = "usr_member_1",
            name = "Budi Santoso",
            email = "budi.santoso@gmail.com",
            phone = "0812-3456-7890",
            role = UserRole.MEMBER
        )
    )
    val currentUser: StateFlow<CurrentUserState> = _currentUser.asStateFlow()

    // Selected product for detail view
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    // Search and filters
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>("all")

    // Admin active sub-tab
    val adminTab = MutableStateFlow(AdminTab.DASHBOARD)

    // Auth state
    val authTab = MutableStateFlow(0) // 0 = Login, 1 = Register
    val authErrorMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)

    // Applied coupon
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()
    val couponError = MutableStateFlow<String?>(null)

    // Selected payment method
    val selectedPaymentMethod = MutableStateFlow(PAYMENT_METHODS.first())

    // Checkout completed order state for instant confirmation dialog
    val recentCompletedOrder = MutableStateFlow<OrderEntity?>(null)

    // Snackbar notification message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Security & Integrity Center States (YF-Shield Defense)
    val isIntegrityDialogOpen = MutableStateFlow(false)
    val activeScanResult = MutableStateFlow<SecurityManager.MalwareScanResult?>(null)
    val activeScanTargetName = MutableStateFlow("")
    val isSystemScanning = MutableStateFlow(false)
    val systemScanCompleted = MutableStateFlow(false)

    // Dynamic Role Permissions Management
    val systemFeatures = MutableStateFlow<List<RoleFeature>>(RolePermissionDefaults.SYSTEM_FEATURES)
    val rolePermissions = MutableStateFlow<Map<UserRole, Set<String>>>(RolePermissionDefaults.getDefaultPermissions())

    // Store Customization & CMS States (Slider, Flash Sale, Widget, Halaman Statis, Blog, SEO, PWA, Konfigurasi Umum)
    val banners = MutableStateFlow<List<BannerItem>>(CustomizationPresets.getDefaultBanners())
    val flashSales = MutableStateFlow<List<FlashSaleCampaign>>(CustomizationPresets.getDefaultFlashSales())
    val customWidgets = MutableStateFlow<List<CustomWidget>>(CustomizationPresets.getDefaultWidgets())
    val staticPages = MutableStateFlow<List<StaticPage>>(CustomizationPresets.getDefaultStaticPages())
    val blogPosts = MutableStateFlow<List<BlogPost>>(CustomizationPresets.getDefaultBlogPosts())
    val seoSettings = MutableStateFlow<SeoPixelSettings>(SeoPixelSettings())
    val pwaSettings = MutableStateFlow<PwaSettings>(PwaSettings())
    val generalConfig = MutableStateFlow<GeneralStoreConfig>(GeneralStoreConfig())

    // Notification Center States
    val notifications = MutableStateFlow<List<NotificationItem>>(NotificationPresets.getDefaultNotifications())
    val unreadNotificationCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    // Data flows from Repository
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProducts: StateFlow<List<ProductEntity>> = repository.activeProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Member-specific reactive flows
    val userCartItems: StateFlow<List<CartItemEntity>> = _currentUser.flatMapLatest { user ->
        repository.getCartItems(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        repository.getOrdersByUser(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPurchasedDownloads: StateFlow<List<OrderItemEntity>> = _currentUser.flatMapLatest { user ->
        repository.getPurchasedItemsByUser(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userWishlist: StateFlow<List<WishlistEntity>> = _currentUser.flatMapLatest { user ->
        repository.getWishlistByUser(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openProductDetail(product: ProductEntity) {
        _selectedProduct.value = product
        _currentScreen.value = AppScreen.PRODUCT_DETAIL
    }

    fun openAuth(tab: Int = 0) {
        authTab.value = tab
        authErrorMessage.value = null
        authSuccessMessage.value = null
        _currentScreen.value = AppScreen.AUTH
    }

    fun login(identifier: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authErrorMessage.value = null
            authSuccessMessage.value = null
            isAuthLoading.value = true

            val result = repository.loginUser(identifier, password)
            isAuthLoading.value = false

            result.onSuccess { userEntity ->
                val userRole = try {
                    UserRole.valueOf(userEntity.role)
                } catch (e: Exception) {
                    UserRole.MEMBER
                }
                _currentUser.value = CurrentUserState(
                    id = userEntity.id,
                    name = userEntity.name,
                    username = userEntity.username,
                    email = userEntity.email,
                    phone = userEntity.phone,
                    role = userRole
                )
                authSuccessMessage.value = "Selamat datang kembali, ${userEntity.name}!"
                showNotification("Login berhasil sebagai ${userEntity.name}")
                onSuccess()
                _currentScreen.value = if (userRole.level >= UserRole.STAFF.level) AppScreen.ADMIN_PANEL else AppScreen.HOME
            }.onFailure { err ->
                authErrorMessage.value = err.message ?: "Terjadi kesalahan saat login"
            }
        }
    }

    fun register(
        name: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            authErrorMessage.value = null
            authSuccessMessage.value = null
            isAuthLoading.value = true

            val result = repository.registerUser(
                name = name,
                username = username,
                email = email,
                phone = phone,
                password = password
            )
            isAuthLoading.value = false

            result.onSuccess { newUser ->
                _currentUser.value = CurrentUserState(
                    id = newUser.id,
                    name = newUser.name,
                    username = newUser.username,
                    email = newUser.email,
                    phone = newUser.phone,
                    role = UserRole.MEMBER
                )
                authSuccessMessage.value = "Akun berhasil dibuat! Selamat datang di YFSTORE."
                showNotification("Registrasi berhasil! Selamat datang, ${newUser.name}")
                onSuccess()
                _currentScreen.value = AppScreen.HOME
            }.onFailure { err ->
                authErrorMessage.value = err.message ?: "Registrasi gagal. Silakan periksa kembali data Anda."
            }
        }
    }

    fun logout() {
        _currentUser.value = CurrentUserState(
            id = "usr_guest_temp",
            name = "Tamu (Guest)",
            username = "guest",
            email = "",
            phone = "",
            role = UserRole.GUEST
        )
        if (_currentScreen.value == AppScreen.ADMIN_PANEL || _currentScreen.value == AppScreen.MEMBER_CENTER) {
            _currentScreen.value = AppScreen.HOME
        }
        showNotification("Anda telah keluar dari akun.")
    }

    // Role switcher to test the PRD's 5 user personas
    fun switchRole(role: UserRole) {
        when (role) {
            UserRole.GUEST -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_guest_temp",
                    name = "Tamu (Guest)",
                    username = "guest",
                    email = "",
                    phone = "",
                    role = UserRole.GUEST
                )
                // If on admin panel or member center, redirect to home
                if (_currentScreen.value == AppScreen.ADMIN_PANEL || _currentScreen.value == AppScreen.MEMBER_CENTER) {
                    _currentScreen.value = AppScreen.HOME
                }
                showNotification("Beralih ke mode Tamu (Guest) - Belum login")
            }
            UserRole.MEMBER -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_member_1",
                    name = "Budi Santoso",
                    username = "budisantoso",
                    email = "budi.santoso@gmail.com",
                    phone = "0812-3456-7890",
                    role = UserRole.MEMBER
                )
                if (_currentScreen.value == AppScreen.ADMIN_PANEL) {
                    _currentScreen.value = AppScreen.HOME
                }
                showNotification("Beralih ke akun Member: Budi Santoso (@budisantoso)")
            }
            UserRole.STAFF -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_staff_1",
                    name = "Ahmad Fauzi (Staff)",
                    username = "ahmadstaff",
                    email = "staff.ahmad@digimarket.id",
                    phone = "0813-9876-5432",
                    role = UserRole.STAFF
                )
                showNotification("Beralih ke akun Staff: Ahmad Fauzi (@ahmadstaff)")
            }
            UserRole.ADMIN -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_admin_1",
                    name = "Siti Rahmawati (Admin)",
                    username = "sitiadmin",
                    email = "admin.siti@digimarket.id",
                    phone = "0821-4433-2211",
                    role = UserRole.ADMIN
                )
                showNotification("Beralih ke akun Admin: Siti Rahmawati (@sitiadmin)")
            }
            UserRole.SUPER_ADMIN -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_superadmin_1",
                    name = "Hendra Wijaya (Super Admin)",
                    username = "hendrasuper",
                    email = "superadmin.hendra@digimarket.id",
                    phone = "0811-0011-2233",
                    role = UserRole.SUPER_ADMIN
                )
                showNotification("Beralih ke Super Admin: Hendra Wijaya (@hendrasuper)")
            }
        }
    }

    fun addToCart(product: ProductEntity) {
        viewModelScope.launch {
            repository.addToCart(_currentUser.value.id, product.id)
            showNotification("${product.title} ditambahkan ke Keranjang!")
        }
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
        }
    }

    fun toggleWishlist(product: ProductEntity) {
        viewModelScope.launch {
            if (_currentUser.value.role == UserRole.GUEST) {
                showNotification("Silakan login sebagai Member untuk menyimpan Wishlist")
                return@launch
            }
            val isCurrentlyWishlisted = userWishlist.value.any { it.productId == product.id }
            repository.toggleWishlist(_currentUser.value.id, product.id, isCurrentlyWishlisted)
            showNotification(
                if (isCurrentlyWishlisted) "Dihapus dari Wishlist" else "Disimpan ke Wishlist ❤️"
            )
        }
    }

    fun applyCoupon(code: String, subtotal: Double) {
        viewModelScope.launch {
            val trimmed = code.trim().uppercase()
            val coupon = allCoupons.value.find { it.code.equals(trimmed, ignoreCase = true) && it.isActive }
            if (coupon == null) {
                couponError.value = "Kode kupon tidak valid atau sudah kadaluarsa"
                _appliedCoupon.value = null
            } else if (subtotal < coupon.minPurchase) {
                couponError.value = "Minimal belanja untuk kupon ini adalah Rp ${coupon.minPurchase.toLong()}"
                _appliedCoupon.value = null
            } else {
                _appliedCoupon.value = coupon
                couponError.value = null
                showNotification("Kupon ${coupon.code} berhasil dipasang! Hemat ${coupon.discountPercent}%")
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        couponError.value = null
    }

    fun executeCheckout(
        guestName: String = "",
        guestEmail: String = "",
        guestPhone: String = ""
    ) {
        viewModelScope.launch {
            val cartList = userCartItems.value
            val currentProducts = allProducts.value
            val productsToBuy = cartList.mapNotNull { cartItem ->
                currentProducts.find { it.id == cartItem.productId }
            }

            if (productsToBuy.isEmpty()) {
                showNotification("Keranjang belanja masih kosong!")
                return@launch
            }

            val user = _currentUser.value
            val finalName = if (user.role == UserRole.GUEST) guestName.ifBlank { "Guest Customer" } else user.name
            val finalEmail = if (user.role == UserRole.GUEST) guestEmail.ifBlank { "guest@example.com" } else user.email
            val finalPhone = if (user.role == UserRole.GUEST) guestPhone.ifBlank { "08123456789" } else user.phone

            val subtotal = productsToBuy.sumOf { it.price }
            val coupon = _appliedCoupon.value
            val discount = if (coupon != null) {
                val calc = subtotal * (coupon.discountPercent / 100.0)
                calc.coerceAtMost(coupon.discountMax)
            } else 0.0

            val order = repository.processCheckout(
                userId = user.id,
                userName = finalName,
                userEmail = finalEmail,
                userPhone = finalPhone,
                products = productsToBuy,
                discountAmount = discount,
                couponCode = coupon?.code,
                paymentMethod = selectedPaymentMethod.value
            )

            _appliedCoupon.value = null
            recentCompletedOrder.value = order
            showNotification("Pembayaran Berhasil! File digital siap diunduh.")

            // Trigger In-App Notification
            addNotification(
                title = "Pesanan #${order.id} Berhasil Dikonfirmasi",
                message = "Pembayaran transaksi Anda berhasil diverifikasi. Berkas digital dan lisensi telah aktif di Download Center.",
                type = NotificationType.TRANSAKSI,
                actionRoute = AppScreen.MEMBER_CENTER,
                actionLabel = "Buka Unduhan"
            )
        }
    }

    fun simulateDownload(orderItem: OrderItemEntity) {
        viewModelScope.launch {
            repository.markDownloadCompleted(orderItem.id)
            showNotification("Mengunduh ${orderItem.downloadFileName} [Token: ${orderItem.downloadToken}]... File digital tersimpan!")
        }
    }

    // --- Admin / Staff Handlers ---
    fun saveProduct(
        id: String?,
        title: String,
        categoryId: String,
        price: Double,
        description: String,
        fileType: String,
        fileSize: String,
        downloadFileName: String,
        licensePrefix: String,
        version: String,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val isNew = id.isNullOrBlank()
            val productId = id ?: ("prod_" + UUID.randomUUID().toString().take(8))
            val product = ProductEntity(
                id = productId,
                title = title,
                categoryId = categoryId,
                price = price,
                description = description,
                fileType = fileType,
                fileSize = fileSize,
                downloadFileName = downloadFileName,
                licensePrefix = licensePrefix,
                version = version,
                isFeatured = isFeatured,
                isActive = true
            )
            repository.saveProduct(product, isNew, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Produk digital '${title}' berhasil disimpan!")
        }
    }

    fun toggleProductActive(product: ProductEntity) {
        viewModelScope.launch {
            repository.setProductActive(product.id, !product.isActive, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Status produk '${product.title}' diperbarui")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            if (_currentUser.value.role == UserRole.STAFF) {
                showNotification("Akses ditolak: Staff tidak memiliki izin menghapus produk!")
                return@launch
            }
            repository.deleteProduct(product.id, product.title, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Produk '${product.title}' berhasil dihapus.")
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Status order $orderId diubah ke $newStatus")
        }
    }

    fun regenerateDownloadLink(orderItemId: String) {
        viewModelScope.launch {
            repository.regenerateDownloadLink(orderItemId, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Token unduhan berhasil diperbarui & dikirim ulang!")
        }
    }

    fun createCoupon(code: String, percent: Int, maxDiscount: Double, minPurchase: Double) {
        viewModelScope.launch {
            if (_currentUser.value.role == UserRole.STAFF) {
                showNotification("Akses ditolak: Staff tidak memiliki izin mengelola kupon!")
                return@launch
            }
            val coupon = CouponEntity(
                code = code.trim().uppercase(),
                discountPercent = percent,
                discountMax = maxDiscount,
                minPurchase = minPurchase,
                isActive = true
            )
            repository.addCoupon(coupon, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Kupon promo ${coupon.code} berhasil ditambahkan!")
        }
    }

    fun toggleUserStatus(user: UserEntity) {
        viewModelScope.launch {
            if (_currentUser.value.role == UserRole.STAFF) {
                showNotification("Akses ditolak: Staff tidak dapat mengubah status user!")
                return@launch
            }
            if (user.role == UserRole.SUPER_ADMIN.name && _currentUser.value.role != UserRole.SUPER_ADMIN) {
                showNotification("Akses ditolak: Akun Super Admin tidak bisa dimodifikasi oleh Admin!")
                return@launch
            }
            repository.toggleUserActive(user.id, user.isActive, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Status akun ${user.name} diperbarui")
        }
    }

    fun changeUserRole(user: UserEntity, newRole: UserRole) {
        viewModelScope.launch {
            if (_currentUser.value.role != UserRole.SUPER_ADMIN && _currentUser.value.role != UserRole.ADMIN) {
                showNotification("Akses ditolak: Hanya Admin / Super Admin yang berhak memodifikasi Role pengguna!")
                return@launch
            }
            repository.updateUserRole(user.id, newRole.name, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Role ${user.name} berhasil diubah ke ${newRole.displayName}")
        }
    }

    fun createTeamMember(
        name: String,
        username: String,
        email: String,
        phone: String,
        role: UserRole
    ) {
        viewModelScope.launch {
            val actor = _currentUser.value
            if (actor.role != UserRole.SUPER_ADMIN && actor.role != UserRole.ADMIN) {
                showNotification("Akses ditolak: Hanya Admin / Super Admin yang dapat menambah anggota tim!")
                return@launch
            }
            val res = repository.createTeamMember(
                name = name,
                username = username,
                email = email,
                phone = phone,
                role = role,
                actorName = actor.name,
                actorRole = actor.role.name
            )
            res.onSuccess {
                showNotification("Anggota tim ${it.name} (${role.displayName}) berhasil didaftarkan!")
            }.onFailure {
                showNotification("Gagal: ${it.message}")
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            val actor = _currentUser.value
            if (actor.role != UserRole.SUPER_ADMIN) {
                showNotification("Akses ditolak: Hanya Super Admin yang berhak menghapus pengguna!")
                return@launch
            }
            if (user.id == actor.id) {
                showNotification("Tidak dapat menghapus akun Anda sendiri!")
                return@launch
            }
            repository.deleteUser(user.id, actor.name, actor.role.name)
            showNotification("Pengguna ${user.name} berhasil dihapus.")
        }
    }

    fun toggleRoleFeature(role: UserRole, featureId: String) {
        val currentRole = _currentUser.value.role
        if (currentRole != UserRole.SUPER_ADMIN && currentRole != UserRole.ADMIN) {
            showNotification("Akses ditolak: Hanya Admin / Super Admin yang berhak mengubah hak akses fitur role!")
            return
        }
        val currentMap = rolePermissions.value.toMutableMap()
        val roleSet = (currentMap[role] ?: emptySet()).toMutableSet()
        val feature = systemFeatures.value.find { it.id == featureId }
        val featureName = feature?.name ?: featureId

        val isNowGranted = if (roleSet.contains(featureId)) {
            roleSet.remove(featureId)
            false
        } else {
            roleSet.add(featureId)
            true
        }
        currentMap[role] = roleSet
        rolePermissions.value = currentMap

        showNotification(
            if (isNowGranted) "Fitur '$featureName' berhasil DITAMBAHKAN ke role ${role.displayName}"
            else "Fitur '$featureName' berhasil DIHAPUS dari role ${role.displayName}"
        )
    }

    fun addNewCustomFeature(name: String, category: String, description: String, assignedRoles: List<UserRole>) {
        val id = "FEAT_" + name.trim().uppercase().replace(" ", "_").take(20) + "_" + UUID.randomUUID().toString().take(4).uppercase()
        val newFeature = RoleFeature(
            id = id,
            name = name,
            category = category.ifBlank { "Kustom" },
            description = description.ifBlank { "Fitur tambahan kustom" },
            isCustom = true
        )
        systemFeatures.value = systemFeatures.value + newFeature

        val currentMap = rolePermissions.value.toMutableMap()
        assignedRoles.forEach { r ->
            val set = (currentMap[r] ?: emptySet()).toMutableSet()
            set.add(id)
            currentMap[r] = set
        }
        val saSet = (currentMap[UserRole.SUPER_ADMIN] ?: emptySet()).toMutableSet()
        saSet.add(id)
        currentMap[UserRole.SUPER_ADMIN] = saSet

        rolePermissions.value = currentMap
        showNotification("Fitur kustom '$name' berhasil ditambahkan ke sistem!")
    }

    fun removeCustomFeature(featureId: String) {
        val feature = systemFeatures.value.find { it.id == featureId }
        systemFeatures.value = systemFeatures.value.filter { it.id != featureId }
        val currentMap = rolePermissions.value.toMutableMap()
        currentMap.keys.forEach { role ->
            val set = (currentMap[role] ?: emptySet()).toMutableSet()
            set.remove(featureId)
            currentMap[role] = set
        }
        rolePermissions.value = currentMap
        showNotification("Fitur '${feature?.name ?: featureId}' telah dihapus dari sistem.")
    }

    fun resetRolePermissionsToDefault() {
        systemFeatures.value = RolePermissionDefaults.SYSTEM_FEATURES
        rolePermissions.value = RolePermissionDefaults.getDefaultPermissions()
        showNotification("Hak akses fitur seluruh role berhasil di-reset ke standar default.")
    }

    fun isFeatureAllowedForRole(role: UserRole, featureId: String): Boolean {
        if (role == UserRole.SUPER_ADMIN) return true
        return rolePermissions.value[role]?.contains(featureId) == true
    }

    fun addReview(productId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user.role == UserRole.GUEST) {
                showNotification("Silakan login sebagai Member untuk memberikan ulasan")
                return@launch
            }
            val review = ReviewEntity(
                id = "rev_" + UUID.randomUUID().toString().take(8),
                productId = productId,
                userId = user.id,
                userName = user.name,
                rating = rating,
                comment = comment
            )
            repository.addReview(review)
            showNotification("Terima kasih atas ulasan Anda!")
        }
    }

    fun showNotification(msg: String) {
        _userMessage.value = msg
    }

    fun clearNotification() {
        _userMessage.value = null
    }

    fun inspectProductIntegrity(product: ProductEntity) {
        val result = SecurityManager.scanDigitalFile(product.downloadFileName, product.fileType, product.fileSize, product.id)
        activeScanTargetName.value = product.title
        activeScanResult.value = result
        isIntegrityDialogOpen.value = true
    }

    fun inspectOrderItemIntegrity(orderItem: OrderItemEntity) {
        val result = SecurityManager.scanDigitalFile(orderItem.downloadFileName, orderItem.fileType, orderItem.fileSize, orderItem.productId)
        activeScanTargetName.value = orderItem.productTitle
        activeScanResult.value = result
        isIntegrityDialogOpen.value = true
    }

    fun closeIntegrityDialog() {
        isIntegrityDialogOpen.value = false
        activeScanResult.value = null
        activeScanTargetName.value = ""
    }

    fun runSecuritySystemAudit() {
        viewModelScope.launch {
            isSystemScanning.value = true
            repository.runSecuritySystemAudit(_currentUser.value.name, _currentUser.value.role.name)
            isSystemScanning.value = false
            systemScanCompleted.value = true
            showNotification("Audit Menyeluruh Selesai: Seluruh berkas digital & proteksi akun 100% Bersih & Terenkripsi!")
        }
    }

    // =========================================================================
    // STORE CUSTOMIZATION & CMS MANAGEMENT METHODS
    // =========================================================================

    // 1. Slider / Banner Management
    fun addBanner(title: String, subtitle: String, imageUrl: String, actionUrl: String, badge: String) {
        val newBanner = BannerItem(
            title = title,
            subtitle = subtitle,
            imageUrl = imageUrl.ifBlank { CustomizationPresets.BANNER_PRESETS.first().second },
            actionUrl = actionUrl,
            badge = badge.ifBlank { "PROMO" },
            isActive = true,
            sortOrder = (banners.value.maxOfOrNull { it.sortOrder } ?: 0) + 1
        )
        banners.value = banners.value + newBanner
        showNotification("Banner '${newBanner.title}' berhasil ditambahkan ke slider!")
    }

    fun deleteBanner(bannerId: String) {
        val banner = banners.value.find { it.id == bannerId }
        banners.value = banners.value.filter { it.id != bannerId }
        showNotification("Banner '${banner?.title ?: "Item"}' berhasil dihapus.")
    }

    fun toggleBannerStatus(bannerId: String) {
        banners.value = banners.value.map {
            if (it.id == bannerId) it.copy(isActive = !it.isActive) else it
        }
        val target = banners.value.find { it.id == bannerId }
        showNotification("Status banner '${target?.title}' diubah: ${if (target?.isActive == true) "Aktif" else "Nonaktif"}")
    }

    // 2. Flash Sale Management
    fun addFlashSale(title: String, discountPercent: Int, bannerUrl: String, endsAtText: String, targetCategory: String) {
        val newFs = FlashSaleCampaign(
            title = title,
            discountPercent = discountPercent,
            bannerUrl = bannerUrl.ifBlank { CustomizationPresets.BANNER_PRESETS[1].second },
            endsAtText = endsAtText.ifBlank { "Berakhir dalam 24j : 00m : 00d" },
            targetCategory = targetCategory.ifBlank { "Semua Produk" },
            isActive = true
        )
        flashSales.value = flashSales.value + newFs
        showNotification("Flash Sale promo '$title' berhasil dijadwalkan!")
    }

    fun deleteFlashSale(fsId: String) {
        val fs = flashSales.value.find { it.id == fsId }
        flashSales.value = flashSales.value.filter { it.id != fsId }
        showNotification("Flash Sale promo '${fs?.title}' telah dihapus.")
    }

    fun toggleFlashSaleStatus(fsId: String) {
        flashSales.value = flashSales.value.map {
            if (it.id == fsId) it.copy(isActive = !it.isActive) else it
        }
        val target = flashSales.value.find { it.id == fsId }
        showNotification("Status promo Flash Sale '${target?.title}': ${if (target?.isActive == true) "Aktif" else "Nonaktif"}")
    }

    // 3. Widget Management
    fun addWidget(name: String, type: String, description: String, imageUrl: String) {
        val newWidget = CustomWidget(
            name = name,
            type = type,
            description = description,
            imageUrl = imageUrl.ifBlank { CustomizationPresets.LOGO_PRESETS.first().second },
            isActive = true,
            sortOrder = (customWidgets.value.maxOfOrNull { it.sortOrder } ?: 0) + 1
        )
        customWidgets.value = customWidgets.value + newWidget
        showNotification("Widget '$name' berhasil ditambahkan ke tata letak!")
    }

    fun deleteWidget(widgetId: String) {
        val wgt = customWidgets.value.find { it.id == widgetId }
        customWidgets.value = customWidgets.value.filter { it.id != widgetId }
        showNotification("Widget '${wgt?.name}' berhasil dihapus.")
    }

    fun toggleWidgetStatus(widgetId: String) {
        customWidgets.value = customWidgets.value.map {
            if (it.id == widgetId) it.copy(isActive = !it.isActive) else it
        }
        val target = customWidgets.value.find { it.id == widgetId }
        showNotification("Widget '${target?.name}': ${if (target?.isActive == true) "Ditampilkan" else "Disembunyikan"}")
    }

    // 4. Static Page Management
    fun addStaticPage(title: String, slug: String, summary: String, content: String) {
        val cleanSlug = slug.ifBlank { title.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9-]"), "") }
        val newPage = StaticPage(
            title = title,
            slug = cleanSlug,
            summary = summary,
            content = content,
            isPublished = true
        )
        staticPages.value = staticPages.value + newPage
        showNotification("Halaman statis '${newPage.title}' berhasil diterbitkan!")
    }

    fun updateStaticPage(pageId: String, title: String, slug: String, summary: String, content: String, isPublished: Boolean) {
        staticPages.value = staticPages.value.map {
            if (it.id == pageId) {
                it.copy(
                    title = title,
                    slug = slug,
                    summary = summary,
                    content = content,
                    isPublished = isPublished,
                    lastUpdated = "16 Sep 2026"
                )
            } else it
        }
        showNotification("Halaman '$title' berhasil diperbarui!")
    }

    fun deleteStaticPage(pageId: String) {
        val page = staticPages.value.find { it.id == pageId }
        staticPages.value = staticPages.value.filter { it.id != pageId }
        showNotification("Halaman '${page?.title}' berhasil dihapus.")
    }

    // 5. Blog Post Management
    fun addBlogPost(title: String, category: String, author: String, coverImageUrl: String, excerpt: String, content: String) {
        val newPost = BlogPost(
            title = title,
            category = category.ifBlank { "Teknologi" },
            author = author.ifBlank { _currentUser.value.name },
            coverImageUrl = coverImageUrl.ifBlank { CustomizationPresets.BANNER_PRESETS.first().second },
            excerpt = excerpt,
            content = content,
            isPublished = true
        )
        blogPosts.value = blogPosts.value + newPost
        showNotification("Artikel '${newPost.title}' berhasil dipublikasikan!")
    }

    fun deleteBlogPost(postId: String) {
        val post = blogPosts.value.find { it.id == postId }
        blogPosts.value = blogPosts.value.filter { it.id != postId }
        showNotification("Artikel '${post?.title}' telah dihapus.")
    }

    fun toggleBlogPostPublish(postId: String) {
        blogPosts.value = blogPosts.value.map {
            if (it.id == postId) it.copy(isPublished = !it.isPublished) else it
        }
        val target = blogPosts.value.find { it.id == postId }
        showNotification("Status artikel '${target?.title}': ${if (target?.isPublished == true) "Published" else "Draft"}")
    }

    // 6. SEO & Pixel Settings
    fun updateSeoSettings(newSettings: SeoPixelSettings) {
        seoSettings.value = newSettings
        showNotification("Konfigurasi SEO & Pixel Tracking berhasil disimpan!")
    }

    // 7. PWA Settings
    fun updatePwaSettings(newSettings: PwaSettings) {
        pwaSettings.value = newSettings
        showNotification("Pengaturan Progressive Web App (PWA) berhasil disimpan!")
    }

    // 8. General Configuration
    fun updateGeneralConfig(newConfig: GeneralStoreConfig) {
        generalConfig.value = newConfig
        showNotification("Konfigurasi Umum Toko & Branding berhasil diperbarui!")
    }

    // --- NOTIFICATION MANAGEMENT ---
    fun markNotificationAsRead(id: String) {
        notifications.value = notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        notifications.value = notifications.value.map { it.copy(isRead = true) }
        showNotification("Semua notifikasi telah ditandai dibaca.")
    }

    fun deleteNotification(id: String) {
        notifications.value = notifications.value.filterNot { it.id == id }
        showNotification("Notifikasi berhasil dihapus.")
    }

    fun clearAllNotifications() {
        notifications.value = emptyList()
        showNotification("Seluruh riwayat notifikasi telah dibersihkan.")
    }

    fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        actionRoute: AppScreen? = null,
        actionLabel: String? = null,
        targetRole: String = "ALL"
    ) {
        val newNotif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            title = title,
            message = message,
            type = type,
            timestamp = "Baru saja",
            isRead = false,
            actionRoute = actionRoute,
            actionLabel = actionLabel,
            targetRole = targetRole
        )
        notifications.value = listOf(newNotif) + notifications.value
    }

    fun broadcastNotification(
        title: String,
        message: String,
        type: NotificationType,
        targetRole: String = "ALL"
    ) {
        addNotification(
            title = title,
            message = message,
            type = type,
            actionRoute = if (type == NotificationType.PROMO) AppScreen.CATALOG else AppScreen.HOME,
            actionLabel = if (type == NotificationType.PROMO) "Lihat Promo" else "Buka Aplikasi",
            targetRole = targetRole
        )
        showNotification("Broadcast notifikasi '$title' berhasil dikirim ke $targetRole!")
    }
}
