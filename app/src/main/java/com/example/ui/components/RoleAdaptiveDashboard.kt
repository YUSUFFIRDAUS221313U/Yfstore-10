package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.ProductEntity
import com.example.data.model.UserRole
import com.example.ui.AdminTab
import com.example.ui.CurrentUserState
import com.example.ui.theme.*

/**
 * Basic Dashboard UI Component that dynamically adapts its layout, widgets,
 * actionable tools, and data visualizations based on the user's role (Admin vs. Member).
 */
@Composable
fun RoleAdaptiveDashboard(
    currentUser: CurrentUserState,
    allOrders: List<OrderEntity> = emptyList(),
    allProducts: List<ProductEntity> = emptyList(),
    purchasedDownloads: List<OrderItemEntity> = emptyList(),
    userOrders: List<OrderEntity> = emptyList(),
    wishlistCount: Int = 0,
    registeredUsersCount: Int = 0,
    onNavigateToProducts: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToDownloads: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onNavigateToAdminTab: ((AdminTab) -> Unit)? = null,
    onSwitchRole: ((UserRole) -> Unit)? = null,
    onDownloadItem: ((OrderItemEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Determine active layout mode: Admin (or Staff/SuperAdmin) vs. Member (or Guest)
    val isAdminMode = currentUser.role == UserRole.ADMIN ||
            currentUser.role == UserRole.SUPER_ADMIN ||
            currentUser.role == UserRole.STAFF

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("role_adaptive_dashboard")
    ) {
        // Role Switcher / Simulator Bar
        RoleSwitcherBar(
            currentRole = currentUser.role,
            userName = currentUser.name,
            onSwitchRole = onSwitchRole
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Dynamic Layout Animation based on Role
        AnimatedContent(
            targetState = isAdminMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "role_dashboard_transition"
        ) { isAdmin ->
            if (isAdmin) {
                AdminDashboardLayout(
                    currentUser = currentUser,
                    orders = allOrders,
                    products = allProducts,
                    totalUsers = registeredUsersCount,
                    onNavigateToAdminTab = onNavigateToAdminTab,
                    onNavigateToProducts = onNavigateToProducts
                )
            } else {
                MemberDashboardLayout(
                    currentUser = currentUser,
                    purchasedDownloads = purchasedDownloads,
                    orders = userOrders,
                    wishlistCount = wishlistCount,
                    onNavigateToProducts = onNavigateToProducts,
                    onNavigateToOrders = onNavigateToOrders,
                    onNavigateToDownloads = onNavigateToDownloads,
                    onNavigateToWishlist = onNavigateToWishlist,
                    onDownloadItem = onDownloadItem
                )
            }
        }
    }
}

/**
 * Top bar displaying current user persona and quick switch buttons to test Admin vs Member layouts.
 */
@Composable
private fun RoleSwitcherBar(
    currentRole: UserRole,
    userName: String,
    onSwitchRole: ((UserRole) -> Unit)?
) {
    val isAdmin = currentRole == UserRole.ADMIN || currentRole == UserRole.SUPER_ADMIN || currentRole == UserRole.STAFF

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAdmin) BrandCharcoal else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (isAdmin) BrandCrimson.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_role_switcher_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0C0F14),
                        border = BorderStroke(1.dp, BrandCrimson.copy(alpha = 0.4f)),
                        modifier = Modifier.size(36.dp)
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
                        Text(
                            text = if (isAdmin) "Executive Admin Console" else "Member Customer Hub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isAdmin) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$userName (${currentRole.displayName})",
                            fontSize = 11.sp,
                            color = if (isAdmin) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                RoleBadge(role = currentRole)
            }

            if (onSwitchRole != null) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    color = if (isAdmin) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Simulasi Tampilan:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAdmin) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = !isAdmin,
                            onClick = { onSwitchRole(UserRole.MEMBER) },
                            label = { Text("Layout Member", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            modifier = Modifier.testTag("switch_to_member_button")
                        )

                        FilterChip(
                            selected = isAdmin,
                            onClick = { onSwitchRole(UserRole.ADMIN) },
                            label = { Text("Layout Admin", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCrimson,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier.testTag("switch_to_admin_button")
                        )
                    }
                }
            }
        }
    }
}

/**
 * =====================================================================
 * ADMIN DASHBOARD LAYOUT
 * Focused on operational management, high-level metrics, revenue,
 * catalog status, pending approvals, and system stability.
 * =====================================================================
 */
@Composable
private fun AdminDashboardLayout(
    currentUser: CurrentUserState,
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    totalUsers: Int,
    onNavigateToAdminTab: ((AdminTab) -> Unit)?,
    onNavigateToProducts: () -> Unit
) {
    val totalRevenue = remember(orders) {
        orders.filter { it.paymentStatus == "COMPLETED" }.sumOf { it.totalAmount }
    }
    val completedOrders = remember(orders) {
        orders.count { it.paymentStatus == "COMPLETED" }
    }
    val pendingOrders = remember(orders) {
        orders.count { it.paymentStatus == "PENDING" }
    }
    val activeProducts = remember(products) {
        products.count { it.isActive }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // High-Level Operational KPI Cards Grid
        Text(
            text = "METRIK OPERASIONAL & PENJUALAN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = BrandCrimson
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminMetricCard(
                title = "Total Pendapatan",
                value = formatRupiah(totalRevenue),
                subtext = "+18.4% bulan ini",
                icon = Icons.Default.Payments,
                color = BrandCrimson,
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_metric_revenue")
            )

            AdminMetricCard(
                title = "Pesanan Selesai",
                value = "$completedOrders Transaksi",
                subtext = "$pendingOrders menunggu bayar",
                icon = Icons.Default.ShoppingBag,
                color = BrandEmerald,
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_metric_orders")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminMetricCard(
                title = "Katalog Digital",
                value = "$activeProducts Produk Aktif",
                subtext = "${products.size} total master data",
                icon = Icons.Default.Inventory2,
                color = BrandIndigo,
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_metric_products")
            )

            AdminMetricCard(
                title = "Member Terdaftar",
                value = "$totalUsers Akun",
                subtext = "Semua role terverifikasi",
                icon = Icons.Default.People,
                color = BrandAmber,
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_metric_users")
            )
        }

        // Quick Administrative Actions Bar
        Text(
            text = "KONTROL CEPAT ADMINISTRATOR",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminActionButton(
                        title = "Tambah Produk",
                        icon = Icons.Default.AddCircleOutline,
                        color = BrandCrimson,
                        onClick = { onNavigateToAdminTab?.invoke(AdminTab.PRODUCTS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_action_add_product")
                    )

                    AdminActionButton(
                        title = "Kelola Order",
                        icon = Icons.Default.AssignmentTurnedIn,
                        color = BrandEmerald,
                        badge = if (pendingOrders > 0) "$pendingOrders" else null,
                        onClick = { onNavigateToAdminTab?.invoke(AdminTab.ORDERS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_action_orders")
                    )

                    AdminActionButton(
                        title = "Kupon Promo",
                        icon = Icons.Default.Discount,
                        color = BrandAmber,
                        onClick = { onNavigateToAdminTab?.invoke(AdminTab.COUPONS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_action_coupons")
                    )

                    AdminActionButton(
                        title = "Audit & Keamanan",
                        icon = Icons.Default.Security,
                        color = BrandCharcoal,
                        onClick = { onNavigateToAdminTab?.invoke(AdminTab.AUDIT_LOGS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_action_security")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminActionButton(
                        title = "Manajemen Tim",
                        icon = Icons.Default.People,
                        color = BrandIndigo,
                        onClick = { onNavigateToAdminTab?.invoke(AdminTab.MEMBERS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_action_team")
                    )

                    AdminActionButton(
                        title = "Hak Akses Role",
                        icon = Icons.Default.Tune,
                        color = BrandRose,
                        onClick = { onNavigateToAdminTab?.invoke(AdminTab.PERMISSION_MATRIX) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_action_roles")
                    )
                }
            }
        }

        // System Stability & Live Security Pulse
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1017)),
            border = BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(BrandEmerald)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "YF Server Engine & Instant Delivery: ONLINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Lisensi digital terenkripsi SHA-256 • Respon <120ms",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandEmerald.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Stabil",
                        color = BrandEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Recent Orders Pulse Feed
        if (orders.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AKTIVITAS TRANSAKSI TERAKHIR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = { onNavigateToAdminTab?.invoke(AdminTab.ORDERS) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Semua Order", fontSize = 11.sp, color = BrandCrimson)
                }
            }

            orders.take(3).forEach { order ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = order.id,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${order.userName} • ${order.paymentMethodName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatRupiah(order.totalAmount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BrandCrimson
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (order.paymentStatus == "COMPLETED") BrandEmerald.copy(alpha = 0.15f) else BrandAmber.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (order.paymentStatus == "COMPLETED") "Selesai" else "Menunggu",
                                    color = if (order.paymentStatus == "COMPLETED") BrandEmerald else BrandAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * =====================================================================
 * MEMBER DASHBOARD LAYOUT
 * Focused on customer perks, digital library / instant downloads,
 * personal order tracking, loyalty tier, and quick shopping actions.
 * =====================================================================
 */
@Composable
private fun MemberDashboardLayout(
    currentUser: CurrentUserState,
    purchasedDownloads: List<OrderItemEntity>,
    orders: List<OrderEntity>,
    wishlistCount: Int,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onDownloadItem: ((OrderItemEntity) -> Unit)?
) {
    val completedOrdersCount = orders.count { it.paymentStatus == "COMPLETED" }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // VIP Member Greeting Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("member_tier_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(BrandCrimson, BrandCharcoal)
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "VERIFIED MEMBER PRIVILEGE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Selamat datang, ${currentUser.name}!",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Akses file asli bergaransi, auto-update & lisensi seumur hidup.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP Badge",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Member Key Metrics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MemberSummaryCard(
                title = "File Siap Unduh",
                value = "${purchasedDownloads.size} Aset",
                icon = Icons.Default.CloudDownload,
                color = BrandCrimson,
                onClick = onNavigateToDownloads,
                modifier = Modifier
                    .weight(1f)
                    .testTag("member_metric_downloads")
            )

            MemberSummaryCard(
                title = "Total Belanja",
                value = "$completedOrdersCount Transaksi",
                icon = Icons.Default.ReceiptLong,
                color = BrandEmerald,
                onClick = onNavigateToOrders,
                modifier = Modifier
                    .weight(1f)
                    .testTag("member_metric_orders")
            )

            MemberSummaryCard(
                title = "Wishlist Tersimpan",
                value = "$wishlistCount Item",
                icon = Icons.Default.Favorite,
                color = BrandRose,
                onClick = onNavigateToWishlist,
                modifier = Modifier
                    .weight(1f)
                    .testTag("member_metric_wishlist")
            )
        }

        // Member Fast Action Bar
        Text(
            text = "MENU KEBUTUHAN ANDA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemberActionButton(
                    title = "Download Center",
                    icon = Icons.Default.DownloadForOffline,
                    color = BrandCrimson,
                    onClick = onNavigateToDownloads,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("member_action_downloads")
                )

                MemberActionButton(
                    title = "Riwayat Order",
                    icon = Icons.Default.History,
                    color = BrandIndigo,
                    onClick = onNavigateToOrders,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("member_action_orders")
                )

                MemberActionButton(
                    title = "Katalog Store",
                    icon = Icons.Default.Storefront,
                    color = BrandEmerald,
                    onClick = onNavigateToProducts,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("member_action_catalog")
                )
            }
        }

        // Ready-to-Download Digital Assets Shelf
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KOLEKSI DIGITAL SAYA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (purchasedDownloads.isNotEmpty()) {
                TextButton(
                    onClick = onNavigateToDownloads,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Semua File (${purchasedDownloads.size})", fontSize = 11.sp, color = BrandCrimson)
                }
            }
        }

        if (purchasedDownloads.isEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum Ada Pembelian Digital",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Temukan software, template source code, atau ebook favorit Anda di katalog.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToProducts,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Jelajahi Katalog Digital", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            purchasedDownloads.take(3).forEach { item ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            FileTypeBadge(fileType = item.fileType)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.productTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.fileSize} • Lisensi Aktif",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDownloadItem?.invoke(item) ?: onNavigateToDownloads() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Unduh ${item.productTitle}",
                                tint = BrandCrimson
                            )
                        }
                    }
                }
            }
        }

        // Exclusive Member Perk Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, BrandAmber.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandAmber.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = BrandAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Kupon Spesial Member: HEMAT10",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Diskon 10% untuk pembelian digital berikutnya. Gunakan saat checkout!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------
// Helper Sub-Components for Dashboard Cards & Buttons
// ---------------------------------------------------------

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AdminActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            if (badge != null) {
                Surface(
                    shape = CircleShape,
                    color = BrandRose,
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = badge,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MemberSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = title,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MemberActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
