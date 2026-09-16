package com.example.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.data.model.UserRole
import com.example.ui.CurrentUserState
import com.example.ui.components.RoleBadge
import com.example.ui.components.formatRupiah
import com.example.ui.components.YfShieldBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Submenu: Top Up Saldo Reseller (Deposit & Mutasi)
 */
@Composable
fun ResellerTopUpView(
    onShowNotification: (String) -> Unit
) {
    var balance by remember { mutableStateOf(2450000.0) }
    var selectedNominal by remember { mutableStateOf(250000.0) }
    var selectedMethod by remember { mutableStateOf("QRIS Otomatis (Instan)") }
    var isProcessing by remember { mutableStateOf(false) }

    val nominals = listOf(50000.0, 100000.0, 250000.0, 50000.0 * 10, 1000000.0)
    val methods = listOf("QRIS Otomatis (Instan)", "BCA Virtual Account", "Mandiri VA", "BRI VA")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reseller_topup_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        Column {
                            Text(
                                text = "Saldo Deposit Reseller",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatRupiah(balance),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandCrimson
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = BrandEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = BrandEmerald)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status Akun Reseller: Terverifikasi VIP", fontSize = 11.sp, color = BrandEmerald, fontWeight = FontWeight.SemiBold)
                        Text("ID: YF-RSL-8921", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pilih Nominal Top Up Saldo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        nominals.take(3).forEach { nom ->
                            OutlinedButton(
                                onClick = { selectedNominal = nom },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedNominal == nom) BrandCrimson.copy(alpha = 0.1f) else Color.Transparent
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.takeIf { selectedNominal != nom }
                                    ?: ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BrandCrimson)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = formatRupiah(nom).replace("Rp", "").trim(),
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedNominal == nom) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedNominal == nom) BrandCrimson else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        nominals.drop(3).forEach { nom ->
                            OutlinedButton(
                                onClick = { selectedNominal = nom },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedNominal == nom) BrandCrimson.copy(alpha = 0.1f) else Color.Transparent
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.takeIf { selectedNominal != nom }
                                    ?: ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BrandCrimson)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = formatRupiah(nom).replace("Rp", "").trim(),
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedNominal == nom) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedNominal == nom) BrandCrimson else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pilih Jalur Pembayaran Deposit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    methods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMethod == method,
                                onClick = { selectedMethod = method }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(method, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            balance += selectedNominal
                            onShowNotification("Top up deposit ${formatRupiah(selectedNominal)} via $selectedMethod berhasil!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_topup_button")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Konfirmasi Top Up Saldo Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Riwayat Mutasi Saldo Terakhir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    val mutations = listOf(
                        Triple("Top Up Deposit QRIS", "+Rp 500.000", "16 Sep 2026, 09:15"),
                        Triple("Order Dispatched: Next.js Starter", "-Rp 149.000", "16 Sep 2026, 08:30"),
                        Triple("Order Dispatched: Flutter SaaS", "-Rp 199.000", "15 Sep 2026, 21:04"),
                        Triple("Top Up Saldo BCA VA", "+Rp 1.000.000", "15 Sep 2026, 14:10")
                    )

                    mutations.forEach { (desc, amount, date) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(desc, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = amount,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (amount.startsWith("+")) BrandEmerald else BrandCrimson
                            )
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

/**
 * Submenu: API Sekalipay Credentials & Testing
 */
@Composable
fun ApiSekalipayView(
    onShowNotification: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var apiKey by remember { mutableStateOf("sk_live_sekalipay_yf_9941a87b32c94") }
    var apiSecret by remember { mutableStateOf("sec_92bc490d1f734491a083ebc2910fa") }
    var webhookUrl by remember { mutableStateOf("https://api.yfstore.com/v1/webhook/sekalipay") }
    var ipWhitelist by remember { mutableStateOf("103.144.12.88, 103.144.12.89") }
    var isTestPingRunning by remember { mutableStateOf(false) }
    var pingStatusMessage by remember { mutableStateOf("Status Koneksi: Terhubung (Ping: 42ms - HTTPS 200 OK)") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("api_sekalipay_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        Text("Kredensial Gateway Sekalipay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BrandEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "PRODUCTION LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("API Key Reseller", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(apiKey))
                                onShowNotification("API Key disalin ke clipboard!")
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("API Secret Key", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = apiSecret,
                        onValueChange = { apiSecret = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(apiSecret))
                                onShowNotification("API Secret disalin ke clipboard!")
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Webhook URL (Auto Dispatch Listener)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = webhookUrl,
                        onValueChange = { webhookUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("IP Whitelist Callback Server", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = ipWhitelist,
                        onValueChange = { ipWhitelist = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onShowNotification("Pengaturan API Sekalipay tersimpan aman!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan Kredensial")
                        }

                        OutlinedButton(
                            onClick = {
                                onShowNotification("Mengirim ping uji coba ke server Sekalipay...")
                                pingStatusMessage = "Ping Berhasil: Response 200 OK (Latency: 38ms)"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Ping API")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = BrandEmerald.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(pingStatusMessage, fontSize = 11.sp, color = BrandEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Submenu: Sync Produk dari Provider Sekalipay
 */
@Composable
fun SyncProdukView(
    onShowNotification: (String) -> Unit
) {
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var syncInterval by remember { mutableStateOf("Setiap 10 Menit") }
    var isSyncing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sync_produk_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sinkronisasi Katalog & Stok Sekalipay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Sinkronisasi real-time memastikan harga beli reseller dan ketersediaan stok lisensi otomatis terbarui.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Otomatis Sinkronisasi Stok & Harga", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Sinkronkan berkala di background", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { autoSyncEnabled = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onShowNotification("Memulai sinkronisasi katalog digital dengan server Sekalipay...")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sinkronkan Semua Produk Sekarang")
                    }
                }
            }
        }
    }
}

/**
 * Submenu: Metode Bayar & Margin Markup
 */
@Composable
fun PaymentSettingsView(
    isMarginSubmenu: Boolean,
    onShowNotification: (String) -> Unit
) {
    var globalMarginPercent by remember { mutableStateOf("15") }
    var fixedMarkupNominal by remember { mutableStateOf("2500") }
    var roundToHundred by remember { mutableStateOf(true) }

    val paymentMethods = remember {
        mutableStateListOf(
            Triple("QRIS All Payment (Gopay/OVO/Shopee)", true, "MDR 0.7%"),
            Triple("BCA Virtual Account", true, "Rp 2.500 / trx"),
            Triple("Mandiri Virtual Account", true, "Rp 2.500 / trx"),
            Triple("BRI Virtual Account", true, "Rp 2.500 / trx"),
            Triple("DANA E-Wallet", true, "1.2%"),
            Triple("ShopeePay Direct", true, "1.5%"),
            Triple("Indomaret / Alfamart", false, "Rp 3.500")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("payment_settings_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isMarginSubmenu) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Metode Pembayaran Tersedia", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Aktifkan atau nonaktifkan channel pembayaran untuk pelanggan.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        paymentMethods.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(item.first, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Biaya Layanan: ${item.third}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = item.second,
                                    onCheckedChange = { isChecked ->
                                        paymentMethods[index] = item.copy(second = isChecked)
                                        onShowNotification("${item.first} ${if (isChecked) "diaktifkan" else "dinonaktifkan"}")
                                    }
                                )
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Aturan Margin / Markup Keuntungan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Markup Persentase Global (%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = globalMarginPercent,
                            onValueChange = { globalMarginPercent = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Markup Tambahan Flat (Rp)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = fixedMarkupNominal,
                            onValueChange = { fixedMarkupNominal = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Bulatkan Harga ke Ratusan Teratas", fontSize = 12.sp)
                            Switch(checked = roundToHundred, onCheckedChange = { roundToHundred = it })
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { onShowNotification("Pengaturan Margin berhasil disimpan!") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Terapkan Margin ke Semua Produk")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Submenu: Bot Settings (Telegram / WhatsApp)
 */
@Composable
fun BotSettingsView(
    onShowNotification: (String) -> Unit
) {
    var telegramToken by remember { mutableStateOf("bot782910381:AAH82k_w9v8xX-YFStoreBot") }
    var waGatewayToken by remember { mutableStateOf("wagw_live_session_yf_enterprise_9182") }
    var autoReplyEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bot_settings_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Konfigurasi Bot Otomasi & Notifikasi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Telegram Bot Token (@BotFather)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = telegramToken,
                        onValueChange = { telegramToken = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("WhatsApp Gateway API Token", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = waGatewayToken,
                        onValueChange = { waGatewayToken = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-Reply Format Perintah Bot", fontSize = 12.sp)
                        Switch(checked = autoReplyEnabled, onCheckedChange = { autoReplyEnabled = it })
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onShowNotification("Pengaturan Bot disimpan!") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan Token Bot")
                        }

                        OutlinedButton(
                            onClick = { onShowNotification("Tes broadcast notifikasi terkirim ke Telegram Admin!") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kirim Uji Coba")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Submenu: Customer CRM (Data Pelanggan)
 */
@Composable
fun CustomerCRMView(
    users: List<UserEntity>,
    orders: List<OrderEntity>,
    onShowNotification: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customer_crm_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Database Pelanggan Terdaftar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Total: ${users.size} pengguna aktif", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onShowNotification("Data pelanggan diekspor ke format CSV!") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export CSV", fontSize = 11.sp)
                    }
                }
            }
        }

        items(users) { user ->
            val userOrders = orders.filter { it.userId == user.id }
            val totalSpend = userOrders.filter { it.paymentStatus == "COMPLETED" }.sumOf { it.totalAmount }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${user.email} • ${user.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        RoleBadge(role = try { UserRole.valueOf(user.role) } catch(e: Exception) { UserRole.MEMBER })
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Belanja: ${formatRupiah(totalSpend)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrandCrimson)
                        Text("${userOrders.size} Pesanan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * Submenu: Panduan Setup & Checklist Integrasi
 */
@Composable
fun SetupGuideView(
    onShowNotification: (String) -> Unit
) {
    val steps = listOf(
        "1. Masukkan API Key & Secret Sekalipay pada menu Panel Reseller > API Sekalipay" to true,
        "2. Konfigurasi Webhook URL untuk auto-dispatch kode lisensi produk" to true,
        "3. Tentukan Margin Keuntungan pada menu Pembayaran > Margin / Markup" to true,
        "4. Tambahkan atau Sinkronkan Katalog Produk Digital pertama Anda" to true,
        "5. Hubungkan Bot Telegram / WA untuk notifikasi pesanan masuk otomatis" to false,
        "6. Uji Transaksi Pembelian menggunakan simulasi pembayaran QRIS" to false
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("setup_guide_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = BrandCrimson)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Panduan Setup Toko Digital YF STORE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Ikuti langkah-langkah di bawah ini untuk menyelesaikan setup operasional toko dan integrasi provider Sekalipay.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(steps) { (desc, isDone) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isDone) BrandEmerald else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Submenu: Kustomisasi Umum, SEO, PWA, Widget, Halaman Statis, dll.
 */
@Composable
fun CustomizationDetailView(
    submenu: AdminSubmenu,
    onShowNotification: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customization_detail_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(submenu.icon, contentDescription = null, tint = BrandCrimson)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(submenu.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(submenu.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(14.dp))

                    when (submenu) {
                        AdminSubmenu.SLIDER_BANNER -> {
                            Text("Daftar Banner Promosi Aktif", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            listOf(
                                "1. Banner Promo Merdeka Digital (Diskon 20%)" to true,
                                "2. Banner Peluncuran SaaS Template 2026" to true,
                                "3. Banner Jaminan Keamanan Lisensi YF-Shield" to false
                            ).forEach { (title, active) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(title, fontSize = 12.sp)
                                    Switch(checked = active, onCheckedChange = { onShowNotification("Status banner diperbarui!") })
                                }
                            }
                        }
                        AdminSubmenu.SEO_PIXEL -> {
                            Text("Google Analytics Measurement ID", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = "G-9YFSTORE88",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Meta Pixel ID (Facebook Ads)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = "104928192847192",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        AdminSubmenu.PWA -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Aktifkan Service Worker Offline Mode", fontSize = 12.sp)
                                Switch(checked = true, onCheckedChange = { onShowNotification("PWA Offline mode diperbarui!") })
                            }
                        }
                        AdminSubmenu.HALAMAN_STATIS -> {
                            listOf(
                                "Syarat & Ketentuan Penggunaan Lisensi",
                                "Kebijakan Privasi & Enkripsi Data",
                                "Kebijakan Garansi & Pengembalian Dana",
                                "Tanya Jawab Seputar Produk Digital (FAQ)"
                            ).forEach { page ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onShowNotification("Membuka editor halaman '$page'") }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(page, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                                }
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                            }
                        }
                        AdminSubmenu.KONFIGURASI_UMUM -> {
                            Text("Nama Toko Digital", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = "YF STORE",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Tagline / Slogan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = "Solusi Kebutuhan Digital Terbaik",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("WhatsApp Customer Support", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = "0812-3456-7890",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        else -> {
                            Text("Pengaturan ${submenu.title} terhubung secara otomatis dengan database utama.", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onShowNotification("Perubahan pada ${submenu.title} berhasil disimpan!") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}

/**
 * Submenu: Profil Admin & Aksi Keluar
 */
@Composable
fun AdminProfileView(
    currentUser: CurrentUserState,
    onLogoutClick: () -> Unit,
    onShowNotification: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var email by remember { mutableStateOf(currentUser.email) }
    var phone by remember { mutableStateOf(currentUser.phone) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_profile_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandCrimson,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            RoleBadge(role = currentUser.role)
                            Text(
                                text = "Sesi Aktif: Terproteksi YF-Shield Token",
                                fontSize = 11.sp,
                                color = BrandEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Nama Lengkap", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Alamat Email", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Nomor WhatsApp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onShowNotification("Data profil admin diperbarui!") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Perubahan Profil")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRose),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keluar dari Sesi Admin", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
