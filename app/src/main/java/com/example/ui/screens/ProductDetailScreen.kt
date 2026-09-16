package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProductEntity
import com.example.data.model.CATEGORIES
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.FileTypeBadge
import com.example.ui.components.formatRupiah
import com.example.ui.components.getCategoryVector
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: MarketplaceViewModel,
    product: ProductEntity,
    modifier: Modifier = Modifier
) {
    val wishlist by viewModel.userWishlist.collectAsState()
    val isWishlisted = remember(wishlist, product.id) { wishlist.any { it.productId == product.id } }
    val category = remember(product.categoryId) {
        CATEGORIES.find { it.id == product.categoryId }
    }

    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Produk Digital",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.CATALOG) },
                        modifier = Modifier.testTag("detail_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleWishlist(product) },
                        modifier = Modifier.testTag("detail_wishlist_btn")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) BrandRose else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Harga",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatRupiah(product.price),
                            color = BrandIndigo,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.addToCart(product)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("detail_add_to_cart_btn")
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Keranjang")
                        }

                        Button(
                            onClick = {
                                viewModel.addToCart(product)
                                viewModel.navigateTo(AppScreen.CART_CHECKOUT)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                            modifier = Modifier.testTag("detail_buy_now_btn")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Beli Langsung")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("product_detail_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Graphic Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = when (product.categoryId) {
                                        "software" -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                                        "ebook" -> listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
                                        "course" -> listOf(Color(0xFF701A75), Color(0xFF86198F), Color(0xFFA21CAF))
                                        "design" -> listOf(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569))
                                        "voucher" -> listOf(Color(0xFF78350F), Color(0xFFB45309), Color(0xFFD97706))
                                        else -> listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                    }
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryVector(product.categoryId),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(110.dp)
                                .align(Alignment.BottomEnd)
                        )

                        Column(modifier = Modifier.align(Alignment.TopStart)) {
                            FileTypeBadge(fileType = product.fileType)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = category?.name ?: "Digital Product",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Surface(
                            color = BrandEmerald,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.BottomStart)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Instant Delivery",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Title & Meta
            item {
                Column {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = " (${product.reviewCount} ulasan)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.salesCount} Terjual",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "v${product.version}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Digital Specs Box (PRD Section 5.3)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Spesifikasi Berkas Digital",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem(label = "Format Berkas", value = product.fileType)
                            SpecItem(label = "Ukuran", value = product.fileSize)
                            SpecItem(label = "Akses", value = "Lifetime")
                            SpecItem(label = "Update", value = "Gratis")
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nama File: ${product.downloadFileName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Description
            item {
                Column {
                    Text(
                        text = "Deskripsi Produk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                }
            }

            // Delivery Assurance Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BrandEmerald.copy(alpha = 0.08f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sistem Pengiriman Otomatis (Instant Delivery)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BrandEmerald
                            )
                            Text(
                                text = "Setelah pembayaran dikonfirmasi, link download token aman dan lisensi langsung tersedia di Download Center akun Anda.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Reviews Header & Add Review Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ulasan Pembeli",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    OutlinedButton(
                        onClick = { showReviewDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_review_btn")
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tulis Ulasan", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Add Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Tulis Ulasan Pembeli") },
            text = {
                Column {
                    Text("Rating Bintang:")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star }) {
                                Icon(
                                    imageVector = if (star <= reviewRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$star Bintang",
                                    tint = BrandAmber
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Komentar / Ulasan Anda") },
                        placeholder = { Text("Ceritakan pengalaman Anda menggunakan produk digital ini...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewComment.isNotBlank()) {
                            viewModel.addReview(product.id, reviewRating, reviewComment)
                            showReviewDialog = false
                            reviewComment = ""
                        }
                    }
                ) {
                    Text("Kirim Ulasan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
