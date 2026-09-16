package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.MarketplaceRepository
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
    FAQ_CONTACT
}

enum class AdminTab {
    DASHBOARD,
    PRODUCTS,
    ORDERS,
    MEMBERS,
    COUPONS,
    ANALYTICS,
    AUDIT_LOGS,
    PERMISSION_MATRIX
}

data class CurrentUserState(
    val id: String = "usr_member_1",
    val name: String = "Budi Santoso",
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

    // Role switcher to test the PRD's 5 user personas
    fun switchRole(role: UserRole) {
        when (role) {
            UserRole.GUEST -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_guest_temp",
                    name = "Tamu (Guest)",
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
                    email = "budi.santoso@gmail.com",
                    phone = "0812-3456-7890",
                    role = UserRole.MEMBER
                )
                if (_currentScreen.value == AppScreen.ADMIN_PANEL) {
                    _currentScreen.value = AppScreen.HOME
                }
                showNotification("Beralih ke akun Member: Budi Santoso")
            }
            UserRole.STAFF -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_staff_1",
                    name = "Ahmad Fauzi (Staff)",
                    email = "staff.ahmad@digimarket.id",
                    phone = "0813-9876-5432",
                    role = UserRole.STAFF
                )
                showNotification("Beralih ke akun Staff: Ahmad Fauzi (Akses Panel Operasional)")
            }
            UserRole.ADMIN -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_admin_1",
                    name = "Siti Rahmawati (Admin)",
                    email = "admin.siti@digimarket.id",
                    phone = "0821-4433-2211",
                    role = UserRole.ADMIN
                )
                showNotification("Beralih ke akun Admin: Siti Rahmawati (Akses Penuh Toko)")
            }
            UserRole.SUPER_ADMIN -> {
                _currentUser.value = CurrentUserState(
                    id = "usr_superadmin_1",
                    name = "Hendra Wijaya (Super Admin)",
                    email = "superadmin.hendra@digimarket.id",
                    phone = "0811-0011-2233",
                    role = UserRole.SUPER_ADMIN
                )
                showNotification("Beralih ke Super Admin: Hendra Wijaya (Akses Sistem & Audit Log)")
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
            if (_currentUser.value.role != UserRole.SUPER_ADMIN) {
                showNotification("Akses ditolak: Hanya Super Admin yang berhak memodifikasi Role pengguna!")
                return@launch
            }
            repository.updateUserRole(user.id, newRole.name, _currentUser.value.name, _currentUser.value.role.name)
            showNotification("Role ${user.name} berhasil diubah ke ${newRole.displayName}")
        }
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
}
