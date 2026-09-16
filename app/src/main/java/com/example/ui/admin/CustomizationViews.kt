package com.example.ui.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MarketplaceViewModel
import com.example.ui.theme.*

/**
 * Master View that routes customization submenu to dedicated,
 * fully interactive CMS management views.
 */
@Composable
fun StoreCustomizationMasterView(
    submenu: AdminSubmenu,
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    when (submenu) {
        AdminSubmenu.SLIDER_BANNER -> SliderBannerCustomizationView(viewModel, modifier)
        AdminSubmenu.FLASH_SALE -> FlashSaleCustomizationView(viewModel, modifier)
        AdminSubmenu.WIDGET -> WidgetCustomizationView(viewModel, modifier)
        AdminSubmenu.HALAMAN_STATIS -> StaticPagesCustomizationView(viewModel, modifier)
        AdminSubmenu.ARTIKEL_BLOG -> BlogPostsCustomizationView(viewModel, modifier)
        AdminSubmenu.SEO_PIXEL -> SeoPixelCustomizationView(viewModel, modifier)
        AdminSubmenu.PWA -> PwaCustomizationView(viewModel, modifier)
        AdminSubmenu.KONFIGURASI_UMUM -> GeneralConfigCustomizationView(viewModel, modifier)
        else -> CustomizationDetailView(submenu = submenu, onShowNotification = { viewModel.showNotification(it) })
    }
}

// =========================================================================
// 1. SLIDER / BANNER CUSTOMIZATION
// =========================================================================
@Composable
fun SliderBannerCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val banners by viewModel.banners.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var bannerToDelete by remember { mutableStateOf<BannerItem?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("slider_banner_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Slider & Banner Promosi Beranda", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "Kelola carousel banner hero, upload gambar promo, dan atur tautan tindakan",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_banner_button")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Banner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (banners.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada banner slider. Klik 'Tambah Banner' untuk menambahkan.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(banners, key = { it.id }) { banner ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth().testTag("banner_item_${banner.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Banner Preview Image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = banner.imageUrl,
                                contentDescription = banner.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Overlay Badge
                            Surface(
                                shape = RoundedCornerShape(bottomEnd = 8.dp),
                                color = BrandCrimson,
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = banner.badge,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(banner.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(banner.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (banner.actionUrl.isNotBlank()) {
                                    Text("Link: ${banner.actionUrl}", fontSize = 10.sp, color = BrandIndigo)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = banner.isActive,
                                    onCheckedChange = { viewModel.toggleBannerStatus(banner.id) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandCrimson),
                                    modifier = Modifier.testTag("toggle_banner_${banner.id}")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { bannerToDelete = banner },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus Banner", tint = BrandRose, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBannerDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, subtitle, imageUrl, actionUrl, badge ->
                viewModel.addBanner(title, subtitle, imageUrl, actionUrl, badge)
                showAddDialog = false
            }
        )
    }

    bannerToDelete?.let { banner ->
        AlertDialog(
            onDismissRequest = { bannerToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = BrandRose) },
            title = { Text("Hapus Banner?", fontWeight = FontWeight.Bold) },
            text = { Text("Banner '${banner.title}' akan dihapus dari slider beranda.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBanner(banner.id)
                        bannerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                ) { Text("Ya, Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { bannerToDelete = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun AddBannerDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf(CustomizationPresets.BANNER_PRESETS.first().second) }
    var actionUrl by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("PROMO") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Banner Slider Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMsg != null) {
                    Text(errorMsg!!, color = BrandRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Live Preview of selected Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Preview Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text("Pilih Gambar Preset Cepat:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(CustomizationPresets.BANNER_PRESETS) { (name, url) ->
                        val isSelected = imageUrl == url
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) BrandCrimson.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson) else null,
                            modifier = Modifier.clickable { imageUrl = url }
                        ) {
                            Text(name, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Atau Masukkan URL Gambar Kustom") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Promo Banner") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Deskripsi / Subtitle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = badge,
                        onValueChange = { badge = it },
                        label = { Text("Teks Badge (mis: HOT)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = actionUrl,
                        onValueChange = { actionUrl = it },
                        label = { Text("Target Link / Rute") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMsg = "Judul banner wajib diisi!"
                        return@Button
                    }
                    onSave(title, subtitle, imageUrl, actionUrl, badge)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
            ) { Text("Simpan Banner") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// =========================================================================
// 2. FLASH SALE CUSTOMIZATION
// =========================================================================
@Composable
fun FlashSaleCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val flashSales by viewModel.flashSales.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var fsToDelete by remember { mutableStateOf<FlashSaleCampaign?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("flash_sale_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Manajemen Program Flash Sale", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "Jadwalkan promo kilat dengan timer countdown dan banner diskon khusus",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_flash_sale_button")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Promo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(flashSales, key = { it.id }) { fs ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = fs.bannerUrl,
                                    contentDescription = fs.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(fs.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Diskon: ${fs.discountPercent}% • ${fs.targetCategory}", fontSize = 11.sp, color = BrandCrimson, fontWeight = FontWeight.SemiBold)
                                Text(fs.endsAtText, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = fs.isActive,
                                onCheckedChange = { viewModel.toggleFlashSaleStatus(fs.id) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandCrimson)
                            )
                            IconButton(onClick = { fsToDelete = fs }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = BrandRose, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var discount by remember { mutableStateOf("35") }
        var bannerUrl by remember { mutableStateOf(CustomizationPresets.BANNER_PRESETS[1].second) }
        var endsAt by remember { mutableStateOf("Berakhir dalam 12j : 30m : 00d") }
        var category by remember { mutableStateOf("Semua Produk") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Buat Flash Sale Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Nama Flash Sale") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("Persentase Diskon (%)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bannerUrl, onValueChange = { bannerUrl = it }, label = { Text("URL Gambar Banner Promo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = endsAt, onValueChange = { endsAt = it }, label = { Text("Teks Hitung Mundur / Durasi") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori Produk Target") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val disc = discount.toIntOrNull() ?: 20
                        viewModel.addFlashSale(title, disc, bannerUrl, endsAt, category)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
                ) { Text("Jadwalkan") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }

    fsToDelete?.let { fs ->
        AlertDialog(
            onDismissRequest = { fsToDelete = null },
            title = { Text("Hapus Flash Sale?", fontWeight = FontWeight.Bold) },
            text = { Text("Promo flash sale '${fs.title}' akan dihapus.") },
            confirmButton = {
                Button(onClick = { viewModel.deleteFlashSale(fs.id); fsToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = BrandRose)) {
                    Text("Hapus")
                }
            },
            dismissButton = { TextButton(onClick = { fsToDelete = null }) { Text("Batal") } }
        )
    }
}

// =========================================================================
// 3. WIDGET CUSTOMIZATION
// =========================================================================
@Composable
fun WidgetCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val widgets by viewModel.customWidgets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var widgetToDelete by remember { mutableStateOf<CustomWidget?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("widget_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Manajemen Widget & Elemen Toko", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Aktifkan atau sembunyikan widget pendukung pada etalase toko", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Widgets, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Widget", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(widgets, key = { it.id }) { wgt ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = BrandCrimson.copy(alpha = 0.12f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (wgt.type) {
                                        "FLOAT_CHAT" -> Icons.Default.Chat
                                        "SECURITY_SHIELD" -> Icons.Default.Security
                                        "TESTIMONIALS" -> Icons.Default.RateReview
                                        else -> Icons.Default.Widgets
                                    },
                                    contentDescription = null,
                                    tint = BrandCrimson,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(wgt.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(wgt.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = wgt.isActive,
                            onCheckedChange = { viewModel.toggleWidgetStatus(wgt.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandCrimson)
                        )
                        IconButton(onClick = { widgetToDelete = wgt }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = BrandRose, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("FLOAT_CHAT") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Widget Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Widget") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi Fungsi") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addWidget(name, type, desc, "")
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
                ) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    widgetToDelete?.let { wgt ->
        AlertDialog(
            onDismissRequest = { widgetToDelete = null },
            title = { Text("Hapus Widget?", fontWeight = FontWeight.Bold) },
            text = { Text("Widget '${wgt.name}' akan dihapus dari tata letak.") },
            confirmButton = {
                Button(onClick = { viewModel.deleteWidget(wgt.id); widgetToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = BrandRose)) {
                    Text("Hapus")
                }
            },
            dismissButton = { TextButton(onClick = { widgetToDelete = null }) { Text("Batal") } }
        )
    }
}

// =========================================================================
// 4. HALAMAN STATIS CUSTOMIZATION
// =========================================================================
@Composable
fun StaticPagesCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val staticPages by viewModel.staticPages.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var pageToPreview by remember { mutableStateOf<StaticPage?>(null) }
    var pageToDelete by remember { mutableStateOf<StaticPage?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("static_pages_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Manajemen Halaman Statis & Kebijakan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Kelola halaman legalitas, syarat & ketentuan, garansi dan FAQ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buat Halaman", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(staticPages, key = { it.id }) { page ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { pageToPreview = page }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(page.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = BrandEmerald.copy(alpha = 0.12f)) {
                                    Text("Terbit", color = BrandEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text("Slug: /${page.slug} • Diperbarui: ${page.lastUpdated}", fontSize = 10.sp, color = BrandIndigo)
                            Text(page.summary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { pageToPreview = page }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Visibility, contentDescription = "Baca", tint = BrandIndigo, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { pageToDelete = page }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = BrandRose, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var slug by remember { mutableStateOf("") }
        var summary by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Buat Halaman Statis Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Halaman") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = slug, onValueChange = { slug = it }, label = { Text("Slug URL (opsional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Ringkasan Singkat") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Konten Lengkap") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addStaticPage(title, slug, summary, content)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
                ) { Text("Terbitkan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    pageToPreview?.let { page ->
        AlertDialog(
            onDismissRequest = { pageToPreview = null },
            title = { Text(page.title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("URL: https://yfstore.id/${page.slug}", fontSize = 10.sp, color = BrandIndigo, fontWeight = FontWeight.SemiBold)
                    Divider()
                    Text(page.content, fontSize = 12.sp, lineHeight = 18.sp)
                }
            },
            confirmButton = {
                Button(onClick = { pageToPreview = null }) { Text("Tutup") }
            }
        )
    }

    pageToDelete?.let { page ->
        AlertDialog(
            onDismissRequest = { pageToDelete = null },
            title = { Text("Hapus Halaman?", fontWeight = FontWeight.Bold) },
            text = { Text("Halaman '${page.title}' akan dihapus secara permanen.") },
            confirmButton = {
                Button(onClick = { viewModel.deleteStaticPage(page.id); pageToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = BrandRose)) {
                    Text("Hapus")
                }
            },
            dismissButton = { TextButton(onClick = { pageToDelete = null }) { Text("Batal") } }
        )
    }
}

// =========================================================================
// 5. ARTIKEL BLOG CUSTOMIZATION
// =========================================================================
@Composable
fun BlogPostsCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val blogPosts by viewModel.blogPosts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<BlogPost?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("blog_posts_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Manajemen Artikel Blog & Panduan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Tulis panduan teknis, berita pembaruan software, dan tips developer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tulis Artikel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(blogPosts, key = { it.id }) { post ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = post.coverImageUrl,
                            contentDescription = post.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(4.dp), color = BrandIndigo.copy(alpha = 0.12f)) {
                                Text(post.category, fontSize = 9.sp, color = BrandIndigo, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• ${post.date}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(post.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("Oleh: ${post.author}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = post.isPublished,
                            onCheckedChange = { viewModel.toggleBlogPostPublish(post.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandCrimson)
                        )
                        IconButton(onClick = { postToDelete = post }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = BrandRose, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Teknologi") }
        var coverUrl by remember { mutableStateOf(CustomizationPresets.BANNER_PRESETS.first().second) }
        var excerpt by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tulis Artikel Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Artikel") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = coverUrl, onValueChange = { coverUrl = it }, label = { Text("URL Gambar Sampul") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = excerpt, onValueChange = { excerpt = it }, label = { Text("Ringkasan Artikel") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi Konten Lengkap") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addBlogPost(title, category, "Yusuf Firdaus", coverUrl, excerpt, content)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
                ) { Text("Terbitkan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text("Hapus Artikel?", fontWeight = FontWeight.Bold) },
            text = { Text("Artikel '${post.title}' akan dihapus.") },
            confirmButton = {
                Button(onClick = { viewModel.deleteBlogPost(post.id); postToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = BrandRose)) {
                    Text("Hapus")
                }
            },
            dismissButton = { TextButton(onClick = { postToDelete = null }) { Text("Batal") } }
        )
    }
}

// =========================================================================
// 6. SEO & PIXEL SETTINGS
// =========================================================================
@Composable
fun SeoPixelCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentSettings by viewModel.seoSettings.collectAsState()
    var metaTitle by remember(currentSettings) { mutableStateOf(currentSettings.metaTitle) }
    var metaDesc by remember(currentSettings) { mutableStateOf(currentSettings.metaDescription) }
    var metaKeywords by remember(currentSettings) { mutableStateOf(currentSettings.metaKeywords) }
    var ogImageUrl by remember(currentSettings) { mutableStateOf(currentSettings.ogImageUrl) }
    var gaId by remember(currentSettings) { mutableStateOf(currentSettings.googleAnalyticsId) }
    var fbPixel by remember(currentSettings) { mutableStateOf(currentSettings.facebookPixelId) }
    var tiktokPixel by remember(currentSettings) { mutableStateOf(currentSettings.tiktokPixelId) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("seo_pixel_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pengaturan Meta Tag, SEO & Pixel Analytics", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Optimalkan ranking mesin pencari dan integrasi tracking iklan pemasaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Divider()

                    Text("Meta Title Mesin Pencari", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = metaTitle, onValueChange = { metaTitle = it }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Text("Meta Description", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = metaDesc, onValueChange = { metaDesc = it }, minLines = 2, modifier = Modifier.fillMaxWidth())

                    Text("Meta Keywords", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = metaKeywords, onValueChange = { metaKeywords = it }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Text("Open Graph (OG) Image Banner Sharing", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    // Image preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                    ) {
                        AsyncImage(model = ogImageUrl, contentDescription = "OG Image Preview", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                    OutlinedTextField(value = ogImageUrl, onValueChange = { ogImageUrl = it }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Divider()

                    Text("Google Analytics Measurement ID (GA4)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = gaId, onValueChange = { gaId = it }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Text("Meta / Facebook Pixel ID", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = fbPixel, onValueChange = { fbPixel = it }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Text("TikTok Pixel ID", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = tiktokPixel, onValueChange = { tiktokPixel = it }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            viewModel.updateSeoSettings(
                                SeoPixelSettings(
                                    metaTitle = metaTitle,
                                    metaDescription = metaDesc,
                                    metaKeywords = metaKeywords,
                                    ogImageUrl = ogImageUrl,
                                    googleAnalyticsId = gaId,
                                    facebookPixelId = fbPixel,
                                    tiktokPixelId = tiktokPixel
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Konfigurasi SEO & Pixel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 7. PWA CUSTOMIZATION
// =========================================================================
@Composable
fun PwaCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentSettings by viewModel.pwaSettings.collectAsState()
    var appName by remember(currentSettings) { mutableStateOf(currentSettings.appName) }
    var shortName by remember(currentSettings) { mutableStateOf(currentSettings.shortName) }
    var themeColor by remember(currentSettings) { mutableStateOf(currentSettings.themeColor) }
    var iconUrl by remember(currentSettings) { mutableStateOf(currentSettings.appIconUrl) }
    var offlineMode by remember(currentSettings) { mutableStateOf(currentSettings.enableOfflineMode) }
    var installPrompt by remember(currentSettings) { mutableStateOf(currentSettings.enableInstallPrompt) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("pwa_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pengaturan Progressive Web App (PWA)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Konfigurasi instalasi web app mandiri dan kemampuan offline caching", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Divider()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(model = iconUrl, contentDescription = "PWA Icon", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Icon PWA (512x512)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Ikon yang tampil pada layar beranda perangkat saat diinstal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    OutlinedTextField(value = iconUrl, onValueChange = { iconUrl = it }, label = { Text("URL Ikon PWA") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("Nama Aplikasi PWA") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = shortName, onValueChange = { shortName = it }, label = { Text("Nama Singkat (Short Name)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = themeColor, onValueChange = { themeColor = it }, label = { Text("Theme Color (Hex)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Aktifkan Service Worker Offline Cache", fontSize = 12.sp)
                        Switch(checked = offlineMode, onCheckedChange = { offlineMode = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandCrimson))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tampilkan Pop-up Auto Install Prompt", fontSize = 12.sp)
                        Switch(checked = installPrompt, onCheckedChange = { installPrompt = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandCrimson))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.updatePwaSettings(
                                PwaSettings(
                                    appName = appName,
                                    shortName = shortName,
                                    themeColor = themeColor,
                                    appIconUrl = iconUrl,
                                    enableOfflineMode = offlineMode,
                                    enableInstallPrompt = installPrompt
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Pengaturan PWA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 8. KONFIGURASI UMUM CUSTOMIZATION
// =========================================================================
@Composable
fun GeneralConfigCustomizationView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val currentConfig by viewModel.generalConfig.collectAsState()
    var storeName by remember(currentConfig) { mutableStateOf(currentConfig.storeName) }
    var tagline by remember(currentConfig) { mutableStateOf(currentConfig.tagline) }
    var logoUrl by remember(currentConfig) { mutableStateOf(currentConfig.logoUrl) }
    var email by remember(currentConfig) { mutableStateOf(currentConfig.supportEmail) }
    var whatsapp by remember(currentConfig) { mutableStateOf(currentConfig.supportWhatsapp) }
    var address by remember(currentConfig) { mutableStateOf(currentConfig.officeAddress) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("general_config_customization_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Konfigurasi Umum & Identitas Toko", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Pengaturan branding nama toko, logo, kontak bantuan dan alamat resmi", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Divider()

                    // Store Logo Preview & Picker
                    Text("Logo Toko Resmi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(model = logoUrl, contentDescription = "Logo Toko", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Logo Branding", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Pilih dari preset logo atau masukkan URL gambar baru", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Presets
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CustomizationPresets.LOGO_PRESETS) { (presetName, url) ->
                            val isPicked = logoUrl == url
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isPicked) BrandCrimson.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isPicked) androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson) else null,
                                modifier = Modifier.clickable { logoUrl = url }
                            ) {
                                Text(presetName, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    OutlinedTextField(value = logoUrl, onValueChange = { logoUrl = it }, label = { Text("URL Gambar Logo") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = storeName, onValueChange = { storeName = it }, label = { Text("Nama Toko") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = tagline, onValueChange = { tagline = it }, label = { Text("Slogan / Tagline") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Layanan Pelanggan") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = { Text("WhatsApp Bantuan Resmi") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat Kantor / Legalitas") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            viewModel.updateGeneralConfig(
                                currentConfig.copy(
                                    storeName = storeName,
                                    tagline = tagline,
                                    logoUrl = logoUrl,
                                    supportEmail = email,
                                    supportWhatsapp = whatsapp,
                                    officeAddress = address
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Konfigurasi Umum", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
