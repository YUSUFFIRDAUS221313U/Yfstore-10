package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Top Aligned Hierarchical Navigation Bar:
 * - Group selector tabs (9 groups)
 * - Submenu filter chips for the active group
 * - "Semua Menu" drawer button with badge count
 */
@Composable
fun AdminGroupAndSubmenuBar(
    selectedGroup: AdminMenuGroup,
    selectedSubmenu: AdminSubmenu,
    onSelectGroup: (AdminMenuGroup) -> Unit,
    onSelectSubmenu: (AdminSubmenu) -> Unit,
    onOpenFullMenuDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Group Bar: Clean, modern Material 3 ScrollableTabRow
            ScrollableTabRow(
                selectedTabIndex = selectedGroup.ordinal,
                edgePadding = 12.dp,
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrandCrimson
            ) {
                AdminMenuGroup.values().forEach { group ->
                    val isSelected = group == selectedGroup
                    Tab(
                        selected = isSelected,
                        onClick = {
                            onSelectGroup(group)
                            val firstSubmenu = AdminSubmenu.byGroup(group).firstOrNull()
                            if (firstSubmenu != null && selectedSubmenu.group != group) {
                                onSelectSubmenu(firstSubmenu)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = group.icon,
                                contentDescription = group.title,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        text = {
                            Text(
                                text = group.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("admin_group_${group.name.lowercase()}")
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

            // Submenu Bar: Sleek horizontal row of pills with Quick Search button
            val currentSubmenus = remember(selectedGroup) { AdminSubmenu.byGroup(selectedGroup) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button to open full search dialog
                FilledTonalIconButton(
                    onClick = onOpenFullMenuDrawer,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("open_admin_menu_drawer_button"),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = BrandCrimson.copy(alpha = 0.12f),
                        contentColor = BrandCrimson
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari Semua Fitur",
                        modifier = Modifier.size(16.dp)
                    )
                }

                currentSubmenus.forEach { submenu ->
                    val isSubmenuSelected = submenu == selectedSubmenu
                    FilterChip(
                        selected = isSubmenuSelected,
                        onClick = { onSelectSubmenu(submenu) },
                        label = {
                            Text(
                                text = submenu.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSubmenuSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = submenu.icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSubmenuSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandCrimson,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSubmenuSelected,
                            borderColor = if (isSubmenuSelected) BrandCrimson else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("admin_submenu_${submenu.name.lowercase()}")
                    )
                }
            }
        }
    }
}

/**
 * Full-screen Modal Bottom Sheet showing the 9 Aligned Groups & Submenus
 * with search and hierarchical grouping.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuBottomSheet(
    currentSubmenu: AdminSubmenu,
    onSelectSubmenu: (AdminSubmenu) -> Unit,
    onDismiss: () -> Unit,
    totalProductsCount: Int,
    totalOrdersCount: Int,
    totalUsersCount: Int,
    totalCouponsCount: Int
) {
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .testTag("admin_menu_bottom_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Navigasi Menu Admin & Reseller",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "9 Grup • 36 Submenu Terstruktur",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari menu atau fitur (mis: Sekalipay, Stok, Saldo)...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_menu_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Hierarchical Groups & Submenus
            val filteredSubmenus = remember(searchQuery) {
                if (searchQuery.isBlank()) {
                    null
                } else {
                    AdminSubmenu.values().filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.group.title.contains(searchQuery, ignoreCase = true) ||
                                it.description.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredSubmenus != null) {
                    // Filtered Flat List
                    item {
                        Text(
                            text = "Hasil Pencarian (${filteredSubmenus.size} menu ditemukan)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandCrimson
                        )
                    }
                    items(filteredSubmenus) { submenu ->
                        SubmenuRowItem(
                            submenu = submenu,
                            isSelected = submenu == currentSubmenu,
                            onClick = {
                                onSelectSubmenu(submenu)
                                onDismiss()
                            }
                        )
                    }
                } else {
                    // Grouped List by 9 Categories
                    AdminMenuGroup.values().forEach { group ->
                        val submenusInGroup = AdminSubmenu.byGroup(group)
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = BrandCrimson.copy(alpha = 0.12f),
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = group.icon,
                                                    contentDescription = null,
                                                    tint = BrandCrimson,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = group.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = group.description,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surface
                                        ) {
                                            Text(
                                                text = "${submenusInGroup.size} sub",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(4.dp))

                                    submenusInGroup.forEach { submenu ->
                                        val isSelected = submenu == currentSubmenu
                                        val badgeText = when (submenu) {
                                            AdminSubmenu.PRODUK -> "$totalProductsCount"
                                            AdminSubmenu.PESANAN -> "$totalOrdersCount"
                                            AdminSubmenu.DATA_PELANGGAN -> "$totalUsersCount"
                                            AdminSubmenu.BROADCAST_NOTIFIKASI -> "PUSH"
                                            AdminSubmenu.VOUCHER -> "$totalCouponsCount"
                                            AdminSubmenu.API_SEKALIPAY -> "LIVE"
                                            AdminSubmenu.ALERT_KEAMANAN -> "OK"
                                            else -> null
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) BrandCrimson.copy(alpha = 0.12f) else Color.Transparent
                                                )
                                                .clickable {
                                                    onSelectSubmenu(submenu)
                                                    onDismiss()
                                                }
                                                .padding(horizontal = 8.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = submenu.icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = submenu.title,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = submenu.description,
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            if (badgeText != null) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (isSelected) BrandCrimson else BrandEmerald.copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = badgeText,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else BrandEmerald,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
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
private fun SubmenuRowItem(
    submenu: AdminSubmenu,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) BrandCrimson.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = submenu.icon,
                    contentDescription = null,
                    tint = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = submenu.title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = submenu.group.title,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = submenu.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 5 Core Simplified Admin Pillars for clean, uncluttered navigation.
 */
enum class AdminSimplePillar(
    val title: String,
    val icon: ImageVector,
    val defaultSubmenu: AdminSubmenu
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, AdminSubmenu.DASHBOARD),
    PRODUK("Produk", Icons.Default.Inventory2, AdminSubmenu.PRODUK),
    PESANAN("Pesanan", Icons.Default.ReceiptLong, AdminSubmenu.PESANAN),
    PELANGGAN("Pelanggan", Icons.Default.People, AdminSubmenu.DATA_PELANGGAN),
    PENGATURAN("Pengaturan", Icons.Default.Settings, AdminSubmenu.KONFIGURASI_UMUM);

    companion object {
        fun fromSubmenu(submenu: AdminSubmenu): AdminSimplePillar {
            return when (submenu) {
                AdminSubmenu.DASHBOARD,
                AdminSubmenu.ANALITIK -> DASHBOARD

                AdminSubmenu.PRODUK,
                AdminSubmenu.KATEGORI,
                AdminSubmenu.PRODUK_UNGGULAN,
                AdminSubmenu.PRODUK_TYPE,
                AdminSubmenu.PRODUK_VARIANT,
                AdminSubmenu.DATA_STOK,
                AdminSubmenu.ULASAN,
                AdminSubmenu.SYNC_PRODUK -> PRODUK

                AdminSubmenu.PESANAN,
                AdminSubmenu.METODE_BAYAR,
                AdminSubmenu.MARGIN_MARKUP -> PESANAN

                AdminSubmenu.DATA_PELANGGAN,
                AdminSubmenu.BROADCAST_NOTIFIKASI -> PELANGGAN

                else -> PENGATURAN
            }
        }
    }
}

/**
 * Modern, Ultra-Clean Single Navigation Bar for Admin.
 * Replaces the complex two-row scrolling chips with a clean 5-pillar tab row
 * and contextual, non-intrusive sub-pills.
 */
@Composable
fun AdminSimpleNavigationBar(
    activePillar: AdminSimplePillar,
    selectedSubmenu: AdminSubmenu,
    onSelectPillar: (AdminSimplePillar) -> Unit,
    onSelectSubmenu: (AdminSubmenu) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Primary 5-Pillar Tabs (Single Row, perfectly balanced)
            TabRow(
                selectedTabIndex = activePillar.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrandCrimson,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activePillar.ordinal]),
                        color = BrandCrimson,
                        height = 3.dp
                    )
                },
                divider = {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                }
            ) {
                AdminSimplePillar.values().forEach { pillar ->
                    val isSelected = pillar == activePillar
                    Tab(
                        selected = isSelected,
                        onClick = {
                            onSelectPillar(pillar)
                            onSelectSubmenu(pillar.defaultSubmenu)
                        },
                        text = {
                            Text(
                                text = pillar.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = pillar.icon,
                                contentDescription = pillar.title,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("admin_pillar_${pillar.name.lowercase()}")
                    )
                }
            }

            // 2. Contextual Sub-Pills Bar (Only shown when helpful)
            when (activePillar) {
                AdminSimplePillar.DASHBOARD -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            AdminSubmenu.DASHBOARD to "Ringkasan Toko",
                            AdminSubmenu.ANALITIK to "Analitik Penjualan"
                        ).forEach { (sub, label) ->
                            val isSelected = selectedSubmenu == sub
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectSubmenu(sub) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    Icon(sub.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCrimson.copy(alpha = 0.12f),
                                    selectedLabelColor = BrandCrimson,
                                    selectedLeadingIconColor = BrandCrimson
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = BrandCrimson
                                )
                            )
                        }
                    }
                }

                AdminSimplePillar.PRODUK -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            AdminSubmenu.PRODUK to "Semua Produk",
                            AdminSubmenu.KATEGORI to "Kategori",
                            AdminSubmenu.PRODUK_UNGGULAN to "Unggulan",
                            AdminSubmenu.SYNC_PRODUK to "Sync API"
                        ).forEach { (sub, label) ->
                            val isSelected = selectedSubmenu == sub
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectSubmenu(sub) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    Icon(sub.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCrimson.copy(alpha = 0.12f),
                                    selectedLabelColor = BrandCrimson,
                                    selectedLeadingIconColor = BrandCrimson
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = BrandCrimson
                                )
                            )
                        }
                    }
                }

                AdminSimplePillar.PESANAN -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            AdminSubmenu.PESANAN to "Daftar Pesanan",
                            AdminSubmenu.METODE_BAYAR to "Metode Bayar",
                            AdminSubmenu.MARGIN_MARKUP to "Margin Markup"
                        ).forEach { (sub, label) ->
                            val isSelected = selectedSubmenu == sub
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectSubmenu(sub) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    Icon(sub.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCrimson.copy(alpha = 0.12f),
                                    selectedLabelColor = BrandCrimson,
                                    selectedLeadingIconColor = BrandCrimson
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = BrandCrimson
                                )
                            )
                        }
                    }
                }

                AdminSimplePillar.PELANGGAN -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            AdminSubmenu.DATA_PELANGGAN to "Data Pelanggan (CRM)",
                            AdminSubmenu.BROADCAST_NOTIFIKASI to "Broadcast Notifikasi"
                        ).forEach { (sub, label) ->
                            val isSelected = selectedSubmenu == sub
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectSubmenu(sub) },
                                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    Icon(sub.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCrimson.copy(alpha = 0.12f),
                                    selectedLabelColor = BrandCrimson,
                                    selectedLeadingIconColor = BrandCrimson
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = BrandCrimson
                                )
                            )
                        }
                    }
                }

                AdminSimplePillar.PENGATURAN -> {
                    if (selectedSubmenu != AdminSubmenu.KONFIGURASI_UMUM) {
                        // Show sleek back button to return to Settings Hub
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandCrimson.copy(alpha = 0.05f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { onSelectSubmenu(AdminSubmenu.KONFIGURASI_UMUM) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = BrandCrimson
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Kembali ke Menu Pengaturan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandCrimson
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandCrimson.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = selectedSubmenu.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandCrimson,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
 * Visual Settings Hub for the Simplified Admin UI.
 * Replaces messy horizontal scrolling with a clean, beautifully organized card grid.
 */
@Composable
fun AdminSimpleSettingsHub(
    onSelectSubmenu: (AdminSubmenu) -> Unit,
    onOpenFullDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingItems = listOf(
        SettingsHubItem(
            submenu = AdminSubmenu.SLIDER_BANNER,
            title = "Kustomisasi Tampilan & Toko",
            description = "Kelola banner slider, flash sale, widget, SEO & PWA",
            icon = Icons.Default.Tune,
            badge = "Desain"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.VOUCHER,
            title = "Kupon & Voucher Diskon",
            description = "Atur kode promo potongan harga untuk pembeli",
            icon = Icons.Default.LocalOffer,
            badge = "Promo"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.API_SEKALIPAY,
            title = "Gateway API Sekalipay",
            description = "Kredensial API, webhook & status transaksi live",
            icon = Icons.Default.Api,
            badge = "Live"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.TOP_UP_SALDO,
            title = "Top Up Saldo Reseller",
            description = "Deposit saldo operasional & mutasi saldo otomatis",
            icon = Icons.Default.AccountBalanceWallet,
            badge = "Keuangan"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.KONFIGURASI_BOT,
            title = "Bot Otomasi Transaksi",
            description = "Pengaturan notifikasi bot Telegram & WhatsApp",
            icon = Icons.Default.SmartToy,
            badge = "Otomasi"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.PENGGUNA,
            title = "Manajemen Tim & Staf",
            description = "Kelola akun pengguna, operator, staf & admin",
            icon = Icons.Default.SupervisorAccount,
            badge = "Tim"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.ROLES_HAK_AKSES,
            title = "Wewenang & Hak Akses",
            description = "Atur izin fitur untuk Super Admin, Admin, dan Staf",
            icon = Icons.Default.Lock,
            badge = "Izin"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.ALERT_KEAMANAN,
            title = "Keamanan & YF-Shield",
            description = "Audit hash SHA-256 berkas digital & anti-tamper",
            icon = Icons.Default.Shield,
            badge = "Shield"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.LOG_AKTIVITAS,
            title = "Log Aktivitas Sistem",
            description = "Rekaman jejak audit forensik & riwayat perubahan",
            icon = Icons.Default.History,
            badge = "Audit"
        ),
        SettingsHubItem(
            submenu = AdminSubmenu.PANDUAN_SETUP,
            title = "Panduan Setup & Bantuan",
            description = "Petunjuk langkah integrasi dan panduan admin",
            icon = Icons.Default.MenuBook,
            badge = "Bantuan"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_simple_settings_hub"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BrandCrimson.copy(alpha = 0.07f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandCrimson,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pusat Pengaturan Sistem & Toko",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Akses cepat ke konfigurasi kustomisasi, gateway, bot, tim dan keamanan.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onOpenFullDrawer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandCrimson.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = "Semua Menu", tint = BrandCrimson)
                    }
                }
            }
        }

        items(settingItems) { item ->
            Card(
                onClick = { onSelectSubmenu(item.submenu) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_hub_${item.submenu.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BrandCrimson.copy(alpha = 0.1f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = BrandCrimson,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = item.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private data class SettingsHubItem(
    val submenu: AdminSubmenu,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badge: String
)

