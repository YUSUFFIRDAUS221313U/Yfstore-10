package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.model.PAYMENT_METHODS
import com.example.data.model.PaymentMethod
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.FileTypeBadge
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.userCartItems.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponError by viewModel.couponError.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val recentOrder by viewModel.recentCompletedOrder.collectAsState()

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    var couponInput by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf("Bambang Kurnia") }
    var guestEmail by remember { mutableStateOf("bambang.kurnia@gmail.com") }
    var guestPhone by remember { mutableStateOf("0812-9988-7766") }
    var isProcessingPayment by remember { mutableStateOf(false) }

    val productsInCart = remember(cartItems, allProducts) {
        cartItems.mapNotNull { cartItem ->
            val p = allProducts.find { it.id == cartItem.productId }
            if (p != null) cartItem to p else null
        }
    }

    val subtotal = remember(productsInCart) {
        productsInCart.sumOf { it.second.price }
    }

    val discount = remember(subtotal, appliedCoupon) {
        val c = appliedCoupon
        if (c != null) {
            val calc = subtotal * (c.discountPercent / 100.0)
            calc.coerceAtMost(c.discountMax)
        } else 0.0
    }

    val finalTotal = remember(subtotal, discount, selectedPaymentMethod) {
        (subtotal - discount + selectedPaymentMethod.fee).coerceAtLeast(0.0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_checkout_screen")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Keranjang & Pembayaran",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Surface(
                    color = BrandIndigo.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${productsInCart.size} Item",
                        color = BrandIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (productsInCart.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Keranjang Belanja Masih Kosong",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Jelajahi katalog untuk menemukan software, e-book, kursus online, dan aset digital.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CATALOG) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("browse_catalog_btn")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Eksplor Katalog Digital")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cart Items Section
                item {
                    Text(
                        text = "Produk Digital yang Dipilih",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                items(productsInCart) { (cartItem, product) ->
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrandIndigo.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                FileTypeBadge(fileType = product.fileType)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ukuran: ${product.fileSize} • Instant Delivery",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatRupiah(product.price),
                                    color = BrandIndigo,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }

                            IconButton(
                                onClick = { viewModel.removeFromCart(cartItem.id) },
                                modifier = Modifier.testTag("remove_cart_item_${cartItem.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Hapus",
                                    tint = BrandRose
                                )
                            }
                        }
                    }
                }

                // Customer Info (Guest vs Member)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Informasi Pembeli",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (currentUser.role == UserRole.GUEST) {
                                    Surface(color = BrandAmber.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "Mode Guest",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandAmber,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (currentUser.role == UserRole.GUEST) {
                                Text(
                                    text = "Email ini akan digunakan untuk mengirimkan link download token & invoice digital:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = guestName,
                                    onValueChange = { guestName = it },
                                    label = { Text("Nama Lengkap") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = guestEmail,
                                    onValueChange = { guestEmail = it },
                                    label = { Text("Email Penerima File Digital") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = guestPhone,
                                    onValueChange = { guestPhone = it },
                                    label = { Text("Nomor WhatsApp (Opsional)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = "Penerima: ${currentUser.name}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Email: ${currentUser.email}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "No. HP: ${currentUser.phone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Coupon / Promo Section
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Kode Promo / Kupon Diskon",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (appliedCoupon != null) {
                                Surface(
                                    color = BrandEmerald.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Kupon: ${appliedCoupon?.code}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = BrandEmerald
                                                )
                                                Text(
                                                    text = "Hemat ${appliedCoupon?.discountPercent}% (Potongan ${formatRupiah(discount)})",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        TextButton(onClick = { viewModel.removeCoupon() }) {
                                            Text("Hapus", color = BrandRose, fontSize = 11.sp)
                                        }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it },
                                        placeholder = { Text("Contoh: DISKON20, MERDEKA") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("coupon_input_field")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.applyCoupon(couponInput, subtotal)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                        modifier = Modifier.testTag("apply_coupon_btn")
                                    ) {
                                        Text("Pakai")
                                    }
                                }

                                if (couponError != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = couponError ?: "",
                                        color = BrandRose,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Payment Method Selector
                item {
                    Column {
                        Text(
                            text = "Metode Pembayaran (Payment Gateway)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        PAYMENT_METHODS.forEach { method ->
                            val isSelected = selectedPaymentMethod.id == method.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BrandIndigo.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BrandIndigo else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.selectedPaymentMethod.value = method }
                                    .testTag("payment_method_${method.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.selectedPaymentMethod.value = method }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = method.accountOrNumber,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (method.fee > 0) {
                                        Text(
                                            text = "+${formatRupiah(method.fee)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Surface(color = BrandEmerald.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                text = "Bebas Biaya",
                                                color = BrandEmerald,
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

                // Payment Instruction Preview
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Instruksi Pembayaran:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedPaymentMethod.instructions,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (selectedPaymentMethod.type == "VA") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedPaymentMethod.accountOrNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    TextButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(selectedPaymentMethod.accountOrNumber))
                                            viewModel.showNotification("Nomor Virtual Account disalin!")
                                        }
                                    ) {
                                        Text("Salin VA", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Cost Breakdown
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Rincian Pembayaran", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal (${productsInCart.size} item)", fontSize = 12.sp)
                                Text(formatRupiah(subtotal), fontSize = 12.sp)
                            }

                            if (discount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Diskon Promo (${appliedCoupon?.code})", color = BrandEmerald, fontSize = 12.sp)
                                    Text("- ${formatRupiah(discount)}", color = BrandEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Biaya Layanan Payment Gateway", fontSize = 12.sp)
                                Text(if (selectedPaymentMethod.fee > 0) formatRupiah(selectedPaymentMethod.fee) else "Gratis", fontSize = 12.sp)
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Tagihan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = formatRupiah(finalTotal),
                                    color = BrandIndigo,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Checkout Button
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            isProcessingPayment = true
                            viewModel.executeCheckout(
                                guestName = guestName,
                                guestEmail = guestEmail,
                                guestPhone = guestPhone
                            )
                            isProcessingPayment = false
                        },
                        enabled = !isProcessingPayment,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("pay_and_instant_deliver_btn")
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memproses Webhook...")
                        } else {
                            Icon(Icons.Default.FlashOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bayar Sekarang • ${formatRupiah(finalTotal)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Recent Order Completed Dialog (PRD Instant Delivery)
    if (recentOrder != null) {
        val order = recentOrder!!
        AlertDialog(
            onDismissRequest = { viewModel.recentCompletedOrder.value = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BrandEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            },
            title = {
                Text(
                    text = "Pembayaran Berhasil!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Order ID: ${order.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Total: ${formatRupiah(order.totalAmount)} via ${order.paymentMethodName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = BrandEmerald.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚡ Instant Delivery Aktif:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandEmerald
                            )
                            Text(
                                text = "Produk digital dan token download sudah masuk ke Download Center akun Anda.",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recentCompletedOrder.value = null
                        viewModel.navigateTo(AppScreen.MEMBER_CENTER)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                    modifier = Modifier.testTag("go_to_download_center_btn")
                ) {
                    Text("Buka Download Center")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.recentCompletedOrder.value = null }
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}
