package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CATEGORIES
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.ProductCard
import com.example.ui.components.RoleBadge
import com.example.ui.components.formatRupiah
import com.example.ui.components.getCategoryVector
import com.example.ui.theme.*
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val activeProducts by viewModel.activeProducts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.userCartItems.collectAsState()
    val wishlist by viewModel.userWishlist.collectAsState()
    val wishlistedIds = remember(wishlist) { wishlist.map { it.productId }.toSet() }
    val banners by viewModel.banners.collectAsState()
    val activeBanners = remember(banners) { banners.filter { it.isActive } }
    val unreadNotifCount by viewModel.unreadNotificationCount.collectAsState()

    var roleSelectorExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // --- TOP BAR WITH ROLE SELECTOR & PERSONA SWITCHER ---
        item {
            Card(
                shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0C0F14),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson.copy(alpha = 0.35f)),
                                shadowElevation = 3.dp,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.yf_logo),
                                    contentDescription = "YF STORE Logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "YF STORE",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    letterSpacing = 0.5.sp,
                                    color = BrandCrimson
                                )
                                Text(
                                    text = "YUSUF FIRDAUS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.5.sp,
                                    color = BrandCharcoal
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentUser.role == UserRole.GUEST) {
                                Button(
                                    onClick = { viewModel.openAuth(0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("top_login_btn")
                                ) {
                                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Masuk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            } else {
                                IconButton(
                                    onClick = { viewModel.openAuth(0) },
                                    modifier = Modifier.testTag("top_auth_switch_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwitchAccount,
                                        contentDescription = "Ganti Akun",
                                        tint = BrandIndigo
                                    )
                                }
                            }

                            // Notification bell badge
                            IconButton(
                                onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                                modifier = Modifier.testTag("home_notification_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifCount > 0) {
                                            Badge(containerColor = BrandRose) {
                                                Text(
                                                    text = if (unreadNotifCount > 9) "9+" else "$unreadNotifCount",
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (unreadNotifCount > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                        contentDescription = "Pusat Notifikasi",
                                        tint = if (unreadNotifCount > 0) BrandIndigo else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Cart badge
                            IconButton(
                                onClick = { viewModel.navigateTo(AppScreen.CART_CHECKOUT) },
                                modifier = Modifier.testTag("home_cart_button")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (cartItems.isNotEmpty()) {
                                            Badge(containerColor = BrandRose) {
                                                Text("${cartItems.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingCart,
                                        contentDescription = "Keranjang"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Role & User Account Strip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentUser.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        RoleBadge(role = currentUser.role)
                                    }
                                    Text(
                                        text = if (currentUser.role == UserRole.GUEST) "Belum login • Mode Tamu" else "@${currentUser.username} • ${currentUser.email}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentUser.role == UserRole.GUEST) {
                                    FilledTonalButton(
                                        onClick = { viewModel.openAuth(1) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier
                                            .height(30.dp)
                                            .testTag("home_register_btn")
                                    ) {
                                        Text("Daftar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                } else {
                                    OutlinedButton(
                                        onClick = { viewModel.logout() },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier
                                            .height(30.dp)
                                            .testTag("home_logout_btn")
                                    ) {
                                        Text("Keluar", fontSize = 10.sp, color = BrandRose)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                FilledTonalButton(
                                    onClick = { roleSelectorExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .testTag("switch_role_btn")
                                ) {
                                    Text("Role", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Role Dropdown Menu
                    DropdownMenu(
                        expanded = roleSelectorExpanded,
                        onDismissRequest = { roleSelectorExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pilih Persona Pengujian (PRD):", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            onClick = { },
                            enabled = false
                        )
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RoleBadge(role = role)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = when(role) {
                                                    UserRole.GUEST -> "Guest (Pengunjung)"
                                                    UserRole.MEMBER -> "Member (Budi Santoso)"
                                                    UserRole.STAFF -> "Staff (Ahmad Fauzi)"
                                                    UserRole.ADMIN -> "Admin (Siti Rahma)"
                                                    UserRole.SUPER_ADMIN -> "Super Admin (Hendra)"
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = if (currentUser.role == role) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = role.description,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.switchRole(role)
                                    roleSelectorExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- HERO BANNER (CUSTOMIZABLE SLIDER) ---
        item {
            if (activeBanners.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeBanners, key = { it.id }) { banner ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillParentMaxWidth(if (activeBanners.size > 1) 0.92f else 1.0f)
                                .height(180.dp)
                                .testTag("home_banner_${banner.id}")
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = banner.imageUrl,
                                    contentDescription = banner.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color(0xEE0B0F19))
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Surface(
                                        color = BrandCrimson,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "⚡ ${banner.badge}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = banner.title,
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = banner.subtitle,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_banner_market),
                            contentDescription = "Digital Marketplace Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xDD0B0F19))
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                color = BrandEmerald,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "⚡ INSTANT DELIVERY",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kebutuhan Digital Siap Pakai",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Source Code, E-Book, Course, UI Kit & Voucher Digital",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // --- PROMO VOUCHERS BANNER CHIP ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = BrandCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("KODE: DISKON20", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(" (Diskon 20%)", color = BrandCyan, fontSize = 11.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("KODE: MERDEKA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(" (Potongan 30%)", color = BrandEmerald, fontSize = 11.sp)
                    }
                }
            }
        }

        // --- CATEGORIES ROW ---
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kategori Produk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.CATALOG) }) {
                        Text("Lihat Semua", fontSize = 12.sp, color = BrandIndigo)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(CATEGORIES) { category ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .width(110.dp)
                                .clickable {
                                    viewModel.selectedCategory.value = category.id
                                    viewModel.navigateTo(AppScreen.CATALOG)
                                }
                                .testTag("cat_chip_${category.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(BrandIndigo.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryVector(category.id),
                                        contentDescription = category.name,
                                        tint = BrandIndigo,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = category.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- FEATURED PRODUCTS ---
        item {
            Column(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Whatshot, contentDescription = null, tint = BrandRose, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Produk Digital Unggulan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "${activeProducts.size} item",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Grid of products
        val featured = activeProducts.filter { it.isFeatured }
        val displayProducts = if (featured.isNotEmpty()) featured else activeProducts

        items(displayProducts.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProductCard(
                        product = pair[0],
                        onClick = { viewModel.openProductDetail(pair[0]) },
                        onAddToCart = { viewModel.addToCart(pair[0]) },
                        isWishlisted = wishlistedIds.contains(pair[0].id),
                        onToggleWishlist = { viewModel.toggleWishlist(pair[0]) }
                    )
                }
                if (pair.size > 1) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = pair[1],
                            onClick = { viewModel.openProductDetail(pair[1]) },
                            onAddToCart = { viewModel.addToCart(pair[1]) },
                            isWishlisted = wishlistedIds.contains(pair[1].id),
                            onToggleWishlist = { viewModel.toggleWishlist(pair[1]) }
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // --- INFO FOOTER ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = BrandEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Garansi 100% Produk Digital Berfungsi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Download langsung aktif dalam 5 detik setelah pembayaran terverifikasi. Link berlaku lifetime.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
