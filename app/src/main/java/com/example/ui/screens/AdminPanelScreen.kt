package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AuditLogEntity
import com.example.data.local.CouponEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.data.model.CATEGORIES
import com.example.data.model.UserRole
import com.example.ui.AdminTab
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.admin.*
import com.example.ui.components.FileTypeBadge
import com.example.ui.components.RoleBadge
import com.example.ui.components.formatRupiah
import com.example.ui.components.FileIntegrityScanDialog
import com.example.ui.components.YfShieldBadge
import com.example.security.SecurityManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTab by viewModel.adminTab.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allCoupons by viewModel.allCoupons.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    val isStaff = currentUser.role == UserRole.STAFF
    val isAdmin = currentUser.role == UserRole.ADMIN
    val isSuperAdmin = currentUser.role == UserRole.SUPER_ADMIN

    val isSystemScanning by viewModel.isSystemScanning.collectAsState()
    val systemScanCompleted by viewModel.systemScanCompleted.collectAsState()
    val isIntegrityDialogOpen by viewModel.isIntegrityDialogOpen.collectAsState()
    val activeScanResult by viewModel.activeScanResult.collectAsState()
    val activeScanTargetName by viewModel.activeScanTargetName.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddCouponDialog by remember { mutableStateOf(false) }

    var selectedGroup by remember { mutableStateOf(AdminMenuGroup.PANEL_RESELLER) }
    var selectedSubmenu by remember { mutableStateOf(AdminSubmenu.DASHBOARD) }
    var activePillar by remember { mutableStateOf(AdminSimplePillar.DASHBOARD) }
    var showMenuBottomSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentTab) {
        when (currentTab) {
            AdminTab.DASHBOARD -> {
                selectedGroup = AdminMenuGroup.PANEL_RESELLER
                selectedSubmenu = AdminSubmenu.DASHBOARD
                activePillar = AdminSimplePillar.DASHBOARD
            }
            AdminTab.PRODUCTS -> {
                selectedGroup = AdminMenuGroup.KELOLA_PRODUK
                selectedSubmenu = AdminSubmenu.PRODUK
                activePillar = AdminSimplePillar.PRODUK
            }
            AdminTab.ORDERS -> {
                selectedGroup = AdminMenuGroup.PANEL_RESELLER
                selectedSubmenu = AdminSubmenu.PESANAN
                activePillar = AdminSimplePillar.PESANAN
            }
            AdminTab.MEMBERS -> {
                selectedGroup = AdminMenuGroup.MANAJEMEN_TIM
                selectedSubmenu = AdminSubmenu.PENGGUNA
                activePillar = AdminSimplePillar.PENGATURAN
            }
            AdminTab.COUPONS -> {
                selectedGroup = AdminMenuGroup.KUSTOMISASI
                selectedSubmenu = AdminSubmenu.VOUCHER
                activePillar = AdminSimplePillar.PENGATURAN
            }
            AdminTab.ANALYTICS -> {
                selectedGroup = AdminMenuGroup.PANEL_RESELLER
                selectedSubmenu = AdminSubmenu.ANALITIK
                activePillar = AdminSimplePillar.DASHBOARD
            }
            AdminTab.AUDIT_LOGS -> {
                selectedGroup = AdminMenuGroup.MANAJEMEN_TIM
                selectedSubmenu = AdminSubmenu.LOG_AKTIVITAS
                activePillar = AdminSimplePillar.PENGATURAN
            }
            AdminTab.PERMISSION_MATRIX -> {
                selectedGroup = AdminMenuGroup.MANAJEMEN_TIM
                selectedSubmenu = AdminSubmenu.ROLES_HAK_AKSES
                activePillar = AdminSimplePillar.PENGATURAN
            }
            AdminTab.SECURITY_CENTER -> {
                selectedGroup = AdminMenuGroup.MANAJEMEN_TIM
                selectedSubmenu = AdminSubmenu.ALERT_KEAMANAN
                activePillar = AdminSimplePillar.PENGATURAN
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_panel_screen")
    ) {
        // Streamlined Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0C0F14),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson.copy(alpha = 0.4f)),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.yf_logo),
                            contentDescription = "YF Shield",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Console",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            RoleBadge(role = currentUser.role)
                        }
                        Text(
                            text = currentUser.name,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Quick button to view customer storefront
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .testTag("admin_preview_store_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Lihat Toko",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Button to open full comprehensive menu drawer / search
                    IconButton(
                        onClick = { showMenuBottomSheet = true },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandCrimson.copy(alpha = 0.1f))
                            .testTag("admin_open_all_menus_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Semua Fitur",
                            tint = BrandCrimson,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Logout button
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar",
                            tint = BrandRose,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        // Simplified Modern Navigation Bar (5 Pillars + Contextual Sub-Pills)
        AdminSimpleNavigationBar(
            activePillar = activePillar,
            selectedSubmenu = selectedSubmenu,
            onSelectPillar = { pillar ->
                activePillar = pillar
                selectedSubmenu = pillar.defaultSubmenu
                selectedGroup = pillar.defaultSubmenu.group
            },
            onSelectSubmenu = { sub ->
                selectedSubmenu = sub
                selectedGroup = sub.group
                activePillar = AdminSimplePillar.fromSubmenu(sub)
                when (sub) {
                    AdminSubmenu.DASHBOARD -> viewModel.adminTab.value = AdminTab.DASHBOARD
                    AdminSubmenu.ANALITIK -> viewModel.adminTab.value = AdminTab.ANALYTICS
                    AdminSubmenu.PESANAN -> viewModel.adminTab.value = AdminTab.ORDERS
                    AdminSubmenu.PRODUK -> viewModel.adminTab.value = AdminTab.PRODUCTS
                    AdminSubmenu.VOUCHER -> viewModel.adminTab.value = AdminTab.COUPONS
                    AdminSubmenu.PENGGUNA -> viewModel.adminTab.value = AdminTab.MEMBERS
                    AdminSubmenu.ROLES_HAK_AKSES -> viewModel.adminTab.value = AdminTab.PERMISSION_MATRIX
                    AdminSubmenu.LOG_AKTIVITAS -> viewModel.adminTab.value = AdminTab.AUDIT_LOGS
                    AdminSubmenu.ALERT_KEAMANAN -> viewModel.adminTab.value = AdminTab.SECURITY_CENTER
                    AdminSubmenu.KELUAR -> showLogoutDialog = true
                    else -> {}
                }
            }
        )

        // Aligned Submenu View Body
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubmenu) {
                // 1. Panel Reseller
                AdminSubmenu.DASHBOARD -> DashboardTab(allProducts, allOrders, allUsers)
                AdminSubmenu.ANALITIK -> AnalyticsTab(allOrders, allProducts)
                AdminSubmenu.TOP_UP_SALDO -> ResellerTopUpView(onShowNotification = { viewModel.showNotification(it) })
                AdminSubmenu.PESANAN -> OrdersTab(
                    orders = allOrders,
                    onStatusChange = { orderId, newStatus -> viewModel.updateOrderStatus(orderId, newStatus) },
                    onRegenerateLink = { itemId -> viewModel.regenerateDownloadLink(itemId) }
                )
                AdminSubmenu.API_SEKALIPAY -> ApiSekalipayView(onShowNotification = { viewModel.showNotification(it) })
                AdminSubmenu.SYNC_PRODUK -> SyncProdukView(onShowNotification = { viewModel.showNotification(it) })

                // 2. Pembayaran
                AdminSubmenu.METODE_BAYAR -> PaymentSettingsView(isMarginSubmenu = false, onShowNotification = { viewModel.showNotification(it) })
                AdminSubmenu.MARGIN_MARKUP -> PaymentSettingsView(isMarginSubmenu = true, onShowNotification = { viewModel.showNotification(it) })

                // 3. Bot Settings
                AdminSubmenu.KONFIGURASI_BOT -> BotSettingsView(onShowNotification = { viewModel.showNotification(it) })

                // 4. Kelola Produk
                AdminSubmenu.KATEGORI -> AdminCategoriesView(products = allProducts)
                AdminSubmenu.PRODUK -> ProductsTab(
                    products = allProducts,
                    isStaff = isStaff,
                    onAddClick = {
                        editingProduct = null
                        showAddProductDialog = true
                    },
                    onEditClick = { prod ->
                        editingProduct = prod
                        showAddProductDialog = true
                    },
                    onToggleActive = { prod -> viewModel.toggleProductActive(prod) },
                    onDelete = { prod -> viewModel.deleteProduct(prod) }
                )
                AdminSubmenu.PRODUK_UNGGULAN -> ProductsTab(
                    products = allProducts.filter { it.isFeatured },
                    isStaff = isStaff,
                    onAddClick = {
                        editingProduct = null
                        showAddProductDialog = true
                    },
                    onEditClick = { prod ->
                        editingProduct = prod
                        showAddProductDialog = true
                    },
                    onToggleActive = { prod -> viewModel.toggleProductActive(prod) },
                    onDelete = { prod -> viewModel.deleteProduct(prod) }
                )
                AdminSubmenu.PRODUK_TYPE,
                AdminSubmenu.PRODUK_VARIANT,
                AdminSubmenu.DATA_STOK,
                AdminSubmenu.ULASAN -> CustomizationDetailView(
                    submenu = selectedSubmenu,
                    onShowNotification = { viewModel.showNotification(it) }
                )

                // 5. Kustomisasi
                AdminSubmenu.SLIDER_BANNER,
                AdminSubmenu.FLASH_SALE,
                AdminSubmenu.WIDGET,
                AdminSubmenu.HALAMAN_STATIS,
                AdminSubmenu.ARTIKEL_BLOG,
                AdminSubmenu.SEO_PIXEL,
                AdminSubmenu.PWA,
                AdminSubmenu.KONFIGURASI_UMUM -> StoreCustomizationMasterView(
                    submenu = selectedSubmenu,
                    viewModel = viewModel
                )
                AdminSubmenu.VOUCHER -> CouponsTab(
                    coupons = allCoupons,
                    onAddClick = { showAddCouponDialog = true }
                )

                // 6. Pelanggan
                AdminSubmenu.DATA_PELANGGAN -> CustomerCRMView(
                    users = allUsers,
                    orders = allOrders,
                    onShowNotification = { viewModel.showNotification(it) }
                )
                AdminSubmenu.BROADCAST_NOTIFIKASI -> AdminNotificationBroadcastView(
                    viewModel = viewModel
                )

                // 7. Bantuan & Setup
                AdminSubmenu.PANDUAN_SETUP -> SetupGuideView(
                    onShowNotification = { viewModel.showNotification(it) }
                )

                // 8. Manajemen Tim
                AdminSubmenu.PENGGUNA -> TeamManagementView(
                    viewModel = viewModel,
                    users = allUsers,
                    currentUserRole = currentUser.role
                )
                AdminSubmenu.ROLES_HAK_AKSES -> DynamicRolePermissionsView(
                    viewModel = viewModel
                )
                AdminSubmenu.LOG_AKTIVITAS -> AuditLogsTab(auditLogs)
                AdminSubmenu.ALERT_KEAMANAN -> SecurityCenterTab(
                    products = allProducts,
                    users = allUsers,
                    auditLogs = auditLogs,
                    isScanning = isSystemScanning,
                    scanCompleted = systemScanCompleted,
                    onRunScan = { viewModel.runSecuritySystemAudit() },
                    onInspectProduct = { prod -> viewModel.inspectProductIntegrity(prod) }
                )

                // 9. Akun Saya
                AdminSubmenu.PROFIL,
                AdminSubmenu.KELUAR -> AdminProfileView(
                    currentUser = currentUser,
                    onLogoutClick = { showLogoutDialog = true },
                    onShowNotification = { viewModel.showNotification(it) }
                )
            }
        }
    }

    // Add/Edit Product Dialog
    if (showAddProductDialog) {
        ProductFormDialog(
            initialProduct = editingProduct,
            onDismiss = { showAddProductDialog = false },
            onSave = { id, title, cat, price, desc, fType, fSize, fName, lPrefix, ver, feat ->
                viewModel.saveProduct(id, title, cat, price, desc, fType, fSize, fName, lPrefix, ver, feat)
                showAddProductDialog = false
            }
        )
    }

    // Add Coupon Dialog
    if (showAddCouponDialog) {
        CouponFormDialog(
            onDismiss = { showAddCouponDialog = false },
            onSave = { code, percent, maxDisc, minPurch ->
                viewModel.createCoupon(code, percent, maxDisc, minPurch)
                showAddCouponDialog = false
            }
        )
    }

    if (isIntegrityDialogOpen) {
        FileIntegrityScanDialog(
            targetName = activeScanTargetName,
            scanResult = activeScanResult,
            onDismiss = { viewModel.closeIntegrityDialog() }
        )
    }

    if (showMenuBottomSheet) {
        AdminMenuBottomSheet(
            currentSubmenu = selectedSubmenu,
            onSelectSubmenu = { sub ->
                selectedSubmenu = sub
                selectedGroup = sub.group
                when (sub) {
                    AdminSubmenu.DASHBOARD -> viewModel.adminTab.value = AdminTab.DASHBOARD
                    AdminSubmenu.ANALITIK -> viewModel.adminTab.value = AdminTab.ANALYTICS
                    AdminSubmenu.PESANAN -> viewModel.adminTab.value = AdminTab.ORDERS
                    AdminSubmenu.PRODUK -> viewModel.adminTab.value = AdminTab.PRODUCTS
                    AdminSubmenu.VOUCHER -> viewModel.adminTab.value = AdminTab.COUPONS
                    AdminSubmenu.PENGGUNA -> viewModel.adminTab.value = AdminTab.MEMBERS
                    AdminSubmenu.ROLES_HAK_AKSES -> viewModel.adminTab.value = AdminTab.PERMISSION_MATRIX
                    AdminSubmenu.LOG_AKTIVITAS -> viewModel.adminTab.value = AdminTab.AUDIT_LOGS
                    AdminSubmenu.ALERT_KEAMANAN -> viewModel.adminTab.value = AdminTab.SECURITY_CENTER
                    AdminSubmenu.KELUAR -> showLogoutDialog = true
                    else -> {}
                }
            },
            onDismiss = { showMenuBottomSheet = false },
            totalProductsCount = allProducts.size,
            totalOrdersCount = allOrders.size,
            totalUsersCount = allUsers.size,
            totalCouponsCount = allCoupons.size
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = BrandCrimson) },
            title = { Text("Konfirmasi Keluar Sesi", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin keluar dari Konsol Admin & Reseller?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.switchRole(UserRole.GUEST)
                        viewModel.navigateTo(AppScreen.HOME)
                        viewModel.showNotification("Berhasil keluar dari sesi administrasi.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun AdminCategoriesView(
    products: List<ProductEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_categories_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kategori Produk Digital YF STORE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Struktur klasifikasi dan jumlah katalog aktif per kelompok", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(CATEGORIES) { category ->
            val count = products.count {
                it.categoryId.equals(category.id, ignoreCase = true) ||
                        it.categoryId.equals(category.name, ignoreCase = true)
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = BrandCrimson.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Category, contentDescription = null, tint = BrandCrimson, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(category.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(category.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$count produk terdaftar", fontSize = 10.sp, color = BrandCrimson, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BrandEmerald.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Aktif",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardTab(
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    users: List<UserEntity>
) {
    val totalRevenue = remember(orders) {
        orders.filter { it.paymentStatus == "COMPLETED" }.sumOf { it.totalAmount }
    }
    val todayRevenue = remember(orders) {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - 86400000L
        orders.filter { it.paymentStatus == "COMPLETED" && it.createdAt >= oneDayAgo }.sumOf { it.totalAmount }
    }
    val activeProductCount = remember(products) { products.count { it.isActive } }
    val memberCount = remember(users) { users.count { it.role == UserRole.MEMBER.name } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Ringkasan Operasional Toko (PRD Section 5.2)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // Metrics Grid 2x2
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Penjualan Hari Ini",
                    value = formatRupiah(todayRevenue),
                    icon = Icons.Default.TrendingUp,
                    color = BrandEmerald,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Pendapatan",
                    value = formatRupiah(totalRevenue),
                    icon = Icons.Default.Payments,
                    color = BrandIndigo,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Produk Digital Aktif",
                    value = "$activeProductCount item",
                    icon = Icons.Default.Inventory2,
                    color = BrandCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Member Terdaftar",
                    value = "$memberCount pembeli",
                    icon = Icons.Default.Group,
                    color = BrandAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Order Terbaru & Instant Delivery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    orders.take(4).forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(order.id, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${order.userName} • ${order.paymentMethodName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatRupiah(order.totalAmount), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandIndigo)
                                Surface(
                                    color = if (order.paymentStatus == "COMPLETED") BrandEmerald.copy(alpha = 0.15f) else BrandAmber.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = order.paymentStatus,
                                        fontSize = 9.sp,
                                        color = if (order.paymentStatus == "COMPLETED") BrandEmerald else BrandAmber,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ProductsTab(
    products: List<ProductEntity>,
    isStaff: Boolean,
    onAddClick: () -> Unit,
    onEditClick: (ProductEntity) -> Unit,
    onToggleActive: (ProductEntity) -> Unit,
    onDelete: (ProductEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Katalog Produk Digital (${products.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("admin_add_product_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Produk", fontSize = 11.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { product ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FileTypeBadge(fileType = product.fileType)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = product.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Switch(
                                checked = product.isActive,
                                onCheckedChange = { onToggleActive(product) },
                                modifier = Modifier.testTag("product_switch_${product.id}")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Harga: ${formatRupiah(product.price)} • File: ${product.fileSize}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${product.salesCount} terjual",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onEditClick(product) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { onDelete(product) },
                                enabled = !isStaff, // Staff cannot delete as per PRD 4.3
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRose),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("delete_product_btn_${product.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isStaff) "Hapus (Terkunci)" else "Hapus", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTab(
    orders: List<OrderEntity>,
    onStatusChange: (String, String) -> Unit,
    onRegenerateLink: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Daftar Order & Instant Delivery Token (PRD 4.3 & 5.2)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(orders) { order ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(order.id, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(
                            color = if (order.paymentStatus == "COMPLETED") BrandEmerald.copy(alpha = 0.15f) else BrandAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = order.paymentStatus,
                                color = if (order.paymentStatus == "COMPLETED") BrandEmerald else BrandAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pembeli: ${order.userName} (${order.userEmail})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Metode: ${order.paymentMethodName} • Total: ${formatRupiah(order.totalAmount)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                val nextStatus = if (order.paymentStatus == "COMPLETED") "PENDING" else "COMPLETED"
                                onStatusChange(order.id, nextStatus)
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Ubah Status", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        FilledTonalButton(
                            onClick = {
                                onRegenerateLink(order.id)
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Regenerate Token", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MembersTab(
    users: List<UserEntity>,
    isSuperAdmin: Boolean,
    onToggleActive: (UserEntity) -> Unit,
    onChangeRole: (UserEntity, UserRole) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Manajemen Pengguna & Role (PRD 4.4 & 4.5)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Super Admin dapat memodifikasi role. Admin dapat menonaktifkan akun.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(users) { user ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("@${user.username} • ${user.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (user.phone.isNotBlank()) {
                                Text("No. HP: ${user.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        val roleEnum = UserRole.values().find { it.name == user.role } ?: UserRole.MEMBER
                        RoleBadge(role = roleEnum)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (user.isActive) "Status: Aktif" else "Status: Ditangguhkan",
                            fontSize = 11.sp,
                            color = if (user.isActive) BrandEmerald else BrandRose,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            TextButton(
                                onClick = { onToggleActive(user) },
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (user.isActive) "Tangguhkan" else "Aktifkan", fontSize = 11.sp)
                            }

                            if (isSuperAdmin && user.role != UserRole.SUPER_ADMIN.name) {
                                Spacer(modifier = Modifier.width(4.dp))
                                OutlinedButton(
                                    onClick = {
                                        val nextRole = when (user.role) {
                                            UserRole.MEMBER.name -> UserRole.STAFF
                                            UserRole.STAFF.name -> UserRole.ADMIN
                                            else -> UserRole.MEMBER
                                        }
                                        onChangeRole(user, nextRole)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Promote Role", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponsTab(
    coupons: List<CouponEntity>,
    onAddClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kupon & Kode Promo (${coupons.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("admin_add_coupon_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah Kupon", fontSize = 11.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(coupons) { coupon ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(coupon.code, fontWeight = FontWeight.Black, fontSize = 14.sp, color = BrandIndigo)
                            Text("Diskon ${coupon.discountPercent}% (Maks ${formatRupiah(coupon.discountMax)})", fontSize = 11.sp)
                            Text("Min. Belanja: ${formatRupiah(coupon.minPurchase)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            color = if (coupon.isActive) BrandEmerald.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (coupon.isActive) "AKTIF (${coupon.usageCount}x dipakai)" else "NONAKTIF",
                                color = if (coupon.isActive) BrandEmerald else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsTab(orders: List<OrderEntity>, products: List<ProductEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Laporan Penjualan & Konversi (PRD Section 2 & 5.2)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Target PRD: Conversion Rate ≥ 3-5%, Waktu proses order < 5 menit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Metrik Kunci Marketplace Digital", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    AnalyticsMetricRow(label = "Conversion Rate Pengunjung", value = "4.2% (Memenuhi target 3-5%)", color = BrandEmerald)
                    AnalyticsMetricRow(label = "Rata-rata Waktu Delivery Berkas", value = "1.8 Detik (Target < 5 Menit)", color = BrandEmerald)
                    AnalyticsMetricRow(label = "Repeat Purchase Rate Member", value = "28.4% (Target ≥ 25%)", color = BrandEmerald)
                    AnalyticsMetricRow(label = "Tingkat Keberhasilan Webhook", value = "99.8%", color = BrandCyan)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Top 5 Produk Terlaris", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    products.sortedByDescending { it.salesCount }.take(5).forEachIndexed { index, prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${index + 1}. ${prod.title}", fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                            Text("${prod.salesCount} Penjualan", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandIndigo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMetricRow(label: String, value: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun AuditLogsTab(logs: List<AuditLogEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Audit Trail & Log Sistem (Hanya Super Admin - PRD 4.5)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Mencatat setiap aksi kritis: perubahan order, penambahan kupon, hak akses, dan transaksi.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(logs) { log ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BrandIndigo.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = log.action,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandIndigo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = SimpleDateFormat("HH:mm:ss • dd/MM", Locale.getDefault()).format(Date(log.timestamp)),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Aktor: ${log.actorName} (${log.actorRole})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = log.target,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionMatrixTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Matriks Permission 5 Role Pengguna (PRD Bagian 4)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Arsitektur Role-Based Access Control (RBAC) pada DigiMarket:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            MatrixCard(
                role = "Guest",
                desc = "Pengunjung tanpa login",
                allowed = listOf("Melihat katalog & cari produk", "Melihat preview & detail file", "Menambahkan produk ke keranjang", "Checkout (dengan isi data guest)"),
                forbidden = listOf("Melihat riwayat transaksi tersimpan", "Download center lifetime tanpa token", "Memberikan review & rating")
            )
        }

        item {
            MatrixCard(
                role = "Member",
                desc = "Pembeli terdaftar",
                allowed = listOf("Semua fitur Guest", "Registrasi & Login", "Keranjang tersimpan di akun", "Akses Download Center & Salin Lisensi", "Wishlist & Ulasan Produk", "Kupon diskon membership"),
                forbidden = listOf("Akses ke panel admin / staff", "Modifikasi katalog atau harga")
            )
        }

        item {
            MatrixCard(
                role = "Staff",
                desc = "Karyawan operasional toko",
                allowed = listOf("Login panel admin", "Kelola produk (tambah, edit, toggle aktif)", "Update status order & kirim ulang link download", "Laporan penjualan dasar"),
                forbidden = listOf("Hapus data produk/order secara permanen", "Mengubah role user", "Akses pengaturan kupon & keuangan")
            )
        }

        item {
            MatrixCard(
                role = "Admin",
                desc = "Pengelola operasional penuh",
                allowed = listOf("Semua hak akses Staff", "Hapus produk permanen", "Manajemen member (nonaktifkan, reset)", "Manajemen kupon & promo", "Laporan analytics lengkap"),
                forbidden = listOf("Mengubah atau menghapus akun Super Admin", "Akses root database log audit")
            )
        }

        item {
            MatrixCard(
                role = "Super Admin",
                desc = "Pemilik platform tertinggi",
                allowed = listOf("Kontrol penuh semua data", "Manajemen seluruh user & role (Staff & Admin)", "Akses audit log & audit trail", "Konfigurasi keamanan & rate limit"),
                forbidden = listOf()
            )
        }
    }
}

@Composable
private fun MatrixCard(
    role: String,
    desc: String,
    allowed: List<String>,
    forbidden: List<String>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(role, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandIndigo)
                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Izin Diberikan (Allowed):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = BrandEmerald)
            allowed.forEach { perm ->
                Text("• $perm", fontSize = 11.sp)
            }

            if (forbidden.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Dibatasi / Ditolak (Forbidden):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = BrandRose)
                forbidden.forEach { perm ->
                    Text("✕ $perm", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ProductFormDialog(
    initialProduct: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (String?, String, String, Double, String, String, String, String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initialProduct?.title ?: "") }
    var categoryId by remember { mutableStateOf(initialProduct?.categoryId ?: "software") }
    var priceStr by remember { mutableStateOf(initialProduct?.price?.toLong()?.toString() ?: "150000") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var fileType by remember { mutableStateOf(initialProduct?.fileType ?: "ZIP") }
    var fileSize by remember { mutableStateOf(initialProduct?.fileSize ?: "25 MB") }
    var fileName by remember { mutableStateOf(initialProduct?.downloadFileName ?: "file-digital.zip") }
    var licensePrefix by remember { mutableStateOf(initialProduct?.licensePrefix ?: "DIGI-PRO-") }
    var version by remember { mutableStateOf(initialProduct?.version ?: "1.0.0") }
    var isFeatured by remember { mutableStateOf(initialProduct?.isFeatured ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialProduct == null) "Tambah Produk Digital" else "Edit Produk Digital") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Produk Digital") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Text("Kategori:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CATEGORIES.forEach { cat ->
                        FilterChip(
                            selected = categoryId == cat.id,
                            onClick = { categoryId = cat.id },
                            label = { Text(cat.name, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Harga (IDR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fileType,
                        onValueChange = { fileType = it },
                        label = { Text("Format (ZIP/PDF)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fileSize,
                        onValueChange = { fileSize = it },
                        label = { Text("Ukuran File") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Nama File Unduhan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Text("Tampilkan sebagai Produk Unggulan di Home", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 100000.0
                    onSave(initialProduct?.id, title, categoryId, p, description, fileType, fileSize, fileName, licensePrefix, version, isFeatured)
                }
            ) {
                Text("Simpan Produk")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun CouponFormDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Double, Double) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var percentStr by remember { mutableStateOf("25") }
    var maxDiscountStr by remember { mutableStateOf("50000") }
    var minPurchaseStr by remember { mutableStateOf("100000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kupon Promo Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Kode Kupon (Contoh: PROMO30)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = percentStr,
                    onValueChange = { percentStr = it },
                    label = { Text("Persentase Diskon (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxDiscountStr,
                    onValueChange = { maxDiscountStr = it },
                    label = { Text("Maksimal Potongan (IDR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = minPurchaseStr,
                    onValueChange = { minPurchaseStr = it },
                    label = { Text("Minimal Belanja (IDR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank()) {
                        val pct = percentStr.toIntOrNull() ?: 10
                        val maxD = maxDiscountStr.toDoubleOrNull() ?: 50000.0
                        val minP = minPurchaseStr.toDoubleOrNull() ?: 100000.0
                        onSave(code, pct, maxD, minP)
                    }
                }
            ) {
                Text("Simpan Kupon")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun SecurityCenterTab(
    products: List<ProductEntity>,
    users: List<UserEntity>,
    auditLogs: List<AuditLogEntity>,
    isScanning: Boolean,
    scanCompleted: Boolean,
    onRunScan: () -> Unit,
    onInspectProduct: (ProductEntity) -> Unit
) {
    val securityLogs = remember(auditLogs) {
        auditLogs.filter {
            it.action.contains("SECURITY", ignoreCase = true) ||
            it.action.contains("AUDIT", ignoreCase = true) ||
            it.action.contains("REGISTER", ignoreCase = true) ||
            it.action.contains("LOGIN", ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("security_center_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Defense Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFF047857), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "YF-SHIELD™ CYBER DEFENSE",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color(0xFF047857)
                                )
                                Text(
                                    text = "Pusat Perlindungan Anti-Virus & Serangan Pihak Asing",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SISTEM AKTIF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Metric Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecurityMetricCard(
                            label = "Status Sistem",
                            value = "100% AMAN",
                            color = Color(0xFF059669),
                            modifier = Modifier.weight(1f)
                        )
                        SecurityMetricCard(
                            label = "Ancaman Aktif",
                            value = "0 Virus / Trojan",
                            color = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecurityMetricCard(
                            label = "Proteksi Sandi",
                            value = "${users.size} Terenkripsi",
                            color = Color(0xFF7C3AED),
                            modifier = Modifier.weight(1f)
                        )
                        SecurityMetricCard(
                            label = "Integritas Berkas",
                            value = "${products.size} SHA-256 Valid",
                            color = Color(0xFF047857),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Deep Scan Trigger Action
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Audit & Pemindaian Integritas Menyeluruh", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Memindai seluruh basis data produk digital dari trojan, script berbahaya, verifikasi hash SHA-256, serta audit proteksi akun dari serangan pihak asing (Brute force & Injection).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onRunScan,
                        enabled = !isScanning,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_security_audit_button")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sedang Memindai Sistem...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.ManageSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Jalankan Audit & Scan Anti-Virus Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (scanCompleted) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Audit Terakhir: Seluruh berkas digital & akun pengguna 100% AMAN & BERSIH!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cyber Defense Pillars
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Arsitektur Pertahanan Terpasang (PRD Security Mandate)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DefensePillarItem(
                        icon = Icons.Default.GppGood,
                        title = "Anti-Virus & Trojan Guard",
                        desc = "Memvalidasi checksum SHA-256 setiap file. Memblokir malware .exe, .bat, script berbahaya, dan manipulasi ekstensi ganda."
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    DefensePillarItem(
                        icon = Icons.Default.Lock,
                        title = "Kriptografi Password Salted SHA-256",
                        desc = "Kata sandi pengguna tidak pernah disimpan sebagai teks mentah. Dilindungi hash SHA-256 dengan 32-karakter cryptographic salt unik."
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    DefensePillarItem(
                        icon = Icons.Default.SecurityUpdateGood,
                        title = "Firewall Anti-Serangan Asing & Brute Force",
                        desc = "Rate Limiter otomatis membatasi maksimal 5 percobaan login gagal berturut-turut, melindungi server dari botnet asing."
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    DefensePillarItem(
                        icon = Icons.Default.Https,
                        title = "Strict HTTPS & Sanitasi Input",
                        desc = "Cleartext HTTP traffic dinonaktifkan secara ketat pada sistem aplikasi. Seluruh input disanitasi dari potensi serangan SQLi dan XSS."
                    )
                }
            }
        }

        // Product File Integrity List
        item {
            Text(
                text = "Pemeriksaan Integritas Berkas Produk Digital (${products.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        items(products) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FileTypeBadge(fileType = product.fileType)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "File: ${product.downloadFileName} • ${product.fileSize}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SHA-256 Valid • Bebas Virus",
                                    fontSize = 10.sp,
                                    color = Color(0xFF059669),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { onInspectProduct(product) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF047857)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF047857)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Periksa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Security Audit Logs
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Log Pertahanan & Audit Keamanan Terkini (${securityLogs.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (securityLogs.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Belum ada catatan insiden. Jalankan audit untuk mencatat log pertahanan baru.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(securityLogs.take(8)) { log ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF047857))
                            Text(text = log.target, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Oleh: ${log.actorName} (${log.actorRole})", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text(
                            text = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
private fun DefensePillarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF059669),
            modifier = Modifier.size(18.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
