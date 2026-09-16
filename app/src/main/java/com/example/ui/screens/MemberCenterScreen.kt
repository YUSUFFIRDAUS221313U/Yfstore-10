package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderItemEntity
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.FileTypeBadge
import com.example.ui.components.RoleBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class MemberTab(val title: String) {
    DOWNLOADS("Download Center"),
    ORDERS("Riwayat Order"),
    WISHLIST("Wishlist"),
    PROFILE("Profil")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberCenterScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val purchasedItems by viewModel.userPurchasedDownloads.collectAsState()
    val orders by viewModel.userOrders.collectAsState()
    val wishlist by viewModel.userWishlist.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(MemberTab.DOWNLOADS) }

    val wishlistedProducts = remember(wishlist, allProducts) {
        wishlist.mapNotNull { w -> allProducts.find { it.id == w.productId } }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("member_center_screen")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrandIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentUser.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = currentUser.email.ifBlank { "Akun Pengunjung" },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    RoleBadge(role = currentUser.role)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable or Secondary Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    MemberTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("member_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            MemberTab.DOWNLOADS -> {
                if (purchasedItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Berkas Digital",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Setelah Anda membeli produk digital di katalog, file dan lisensinya langsung tersedia di sini.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.navigateTo(AppScreen.CATALOG) }) {
                                Text("Mulai Belanja")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Surface(
                                color = BrandIndigo.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Download token Anda terenkripsi & berlaku lifetime. Anda dapat mengunduh berkas kapan saja.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        items(purchasedItems) { item ->
                            DownloadItemCard(
                                item = item,
                                onDownload = { viewModel.simulateDownload(item) },
                                onCopyLicense = {
                                    clipboardManager.setText(AnnotatedString(item.licenseKey))
                                    viewModel.showNotification("Kunci lisensi '${item.licenseKey}' disalin!")
                                }
                            )
                        }
                    }
                }
            }

            MemberTab.ORDERS -> {
                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada riwayat transaksi.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = order.id,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )

                                        Surface(
                                            color = if (order.paymentStatus == "COMPLETED") BrandEmerald.copy(alpha = 0.15f) else BrandAmber.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (order.paymentStatus == "COMPLETED") "SELESAI (INSTANT)" else order.paymentStatus,
                                                color = if (order.paymentStatus == "COMPLETED") BrandEmerald else BrandAmber,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Waktu: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID")).format(Date(order.createdAt))}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Metode: ${order.paymentMethodName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total Pembayaran:", fontSize = 12.sp)
                                        Text(
                                            text = formatRupiah(order.totalAmount),
                                            color = BrandIndigo,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            MemberTab.WISHLIST -> {
                if (wishlistedProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Wishlist masih kosong", fontWeight = FontWeight.Bold)
                            Text("Simpan produk digital favorit Anda dengan menekan ikon hati.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(wishlistedProducts) { product ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FileTypeBadge(fileType = product.fileType)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                        Text(formatRupiah(product.price), color = BrandIndigo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    IconButton(onClick = { viewModel.addToCart(product) }) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Tambah ke Keranjang", tint = BrandIndigo)
                                    }
                                    IconButton(onClick = { viewModel.toggleWishlist(product) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = BrandRose)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            MemberTab.PROFILE -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Data Akun Pengguna", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                ProfileInfoRow(label = "Nama Lengkap", value = currentUser.name)
                                ProfileInfoRow(label = "Email", value = currentUser.email.ifBlank { "Tidak terdaftar" })
                                ProfileInfoRow(label = "Nomor Telepon", value = currentUser.phone.ifBlank { "-" })
                                ProfileInfoRow(label = "Tingkat Role", value = currentUser.role.displayName)
                                ProfileInfoRow(label = "Status Akun", value = "Aktif (Terverifikasi)")
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Benefit Keanggotaan Member (PRD 4.2)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("✓ Instant Delivery: File digital & token langsung aktif setelah bayar", fontSize = 11.sp)
                                Text("✓ Download Center: Akses unduhan lifetime tanpa biaya tambahan", fontSize = 11.sp)
                                Text("✓ Diskon Kupon Eksklusif setiap flash sale mingguan", fontSize = 11.sp)
                                Text("✓ Dukungan WhatsApp & pembaruan versi minor gratis", fontSize = 11.sp)
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = { viewModel.switchRole(UserRole.GUEST) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRose),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout (Beralih ke Tamu / Guest)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    item: OrderItemEntity,
    onDownload: () -> Unit,
    onCopyLicense: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FileTypeBadge(fileType = item.fileType)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Berkas: ${item.downloadFileName} • ${item.fileSize}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Token & License Section
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Token Akses:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(item.downloadToken, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kunci Lisensi:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.licenseKey, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick = onCopyLicense, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Salin Lisensi", modifier = Modifier.size(14.dp), tint = BrandIndigo)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Masa Aktif:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Lifetime (Permanen)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = BrandEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Download CTA Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Unduh: ${item.downloadCount} kali",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onDownload,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                    modifier = Modifier.testTag("unduh_berkas_btn_${item.id}")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Unduh Berkas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
