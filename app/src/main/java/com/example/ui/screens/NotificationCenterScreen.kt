package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.notifications.NotificationItem
import com.example.ui.notifications.NotificationType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationCount.collectAsState()

    var selectedType by remember { mutableStateOf(NotificationType.SEMUA) }
    var unreadOnly by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showClearAllConfirm by remember { mutableStateOf(false) }
    var viewingNotification by remember { mutableStateOf<NotificationItem?>(null) }

    val filteredNotifications = remember(notifications, selectedType, unreadOnly) {
        notifications.filter { notif ->
            val matchType = (selectedType == NotificationType.SEMUA || notif.type == selectedType)
            val matchUnread = if (unreadOnly) !notif.isRead else true
            matchType && matchUnread
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pusat Notifikasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (unreadCount > 0) "$unreadCount belum dibaca" else "Semua sudah dibaca",
                            fontSize = 11.sp,
                            color = if (unreadCount > 0) BrandRose else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("notif_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Beranda"
                        )
                    }
                },
                actions = {
                    // Mark all as read
                    if (unreadCount > 0) {
                        IconButton(
                            onClick = { viewModel.markAllNotificationsAsRead() },
                            modifier = Modifier.testTag("notif_mark_all_read_btn")
                        ) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Tandai Semua Dibaca",
                                tint = BrandEmerald
                            )
                        }
                    }

                    // Create test / broadcast notif
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("notif_compose_btn")
                    ) {
                        Icon(
                            Icons.Default.AddAlert,
                            contentDescription = "Kirim Notifikasi Uji",
                            tint = BrandIndigo
                        )
                    }

                    // Clear all
                    if (notifications.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearAllConfirm = true },
                            modifier = Modifier.testTag("notif_clear_all_btn")
                        ) {
                            Icon(
                                Icons.Outlined.DeleteSweep,
                                contentDescription = "Hapus Semua Notifikasi",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- TOP STATS & QUICK FILTER BAR ---
            Card(
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Category Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(NotificationType.values()) { type ->
                            val isSelected = selectedType == type
                            val count = remember(notifications, type) {
                                if (type == NotificationType.SEMUA) notifications.size
                                else notifications.count { it.type == type }
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedType = type },
                                label = {
                                    Text(
                                        "${type.title} ($count)",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = type.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else type.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandIndigo,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("notif_chip_${type.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary Bar: Unread Toggle & Total Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menampilkan ${filteredNotifications.size} pemberitahuan",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hanya Belum Dibaca",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = unreadOnly,
                                onCheckedChange = { unreadOnly = it },
                                modifier = Modifier.testTag("notif_unread_switch")
                            )
                        }
                    }
                }
            }

            // --- NOTIFICATION FEED LIST ---
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandIndigo.copy(alpha = 0.12f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.NotificationsNone,
                                        contentDescription = null,
                                        tint = BrandIndigo,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tidak Ada Pemberitahuan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (unreadOnly)
                                    "Semua notifikasi pada kategori ini sudah Anda baca."
                                else
                                    "Belum ada riwayat aktivitas atau pengumuman pada kategori ini.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buat Notifikasi Uji", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        NotificationItemCard(
                            notification = notif,
                            onClick = {
                                viewModel.markNotificationAsRead(notif.id)
                                viewingNotification = notif
                            },
                            onActionClick = {
                                viewModel.markNotificationAsRead(notif.id)
                                notif.actionRoute?.let { targetScreen ->
                                    viewModel.navigateTo(targetScreen)
                                }
                            },
                            onDeleteClick = {
                                viewModel.deleteNotification(notif.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // --- DETAIL NOTIFICATION DIALOG ---
    viewingNotification?.let { notif ->
        AlertDialog(
            onDismissRequest = { viewingNotification = null },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = notif.type.color.copy(alpha = 0.15f),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = notif.type.icon,
                            contentDescription = null,
                            tint = notif.type.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = notif.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = notif.type.color.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = notif.type.title.uppercase(),
                                color = notif.type.color,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = notif.timestamp,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = notif.message,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                if (notif.actionRoute != null && notif.actionLabel != null) {
                    Button(
                        onClick = {
                            viewingNotification = null
                            viewModel.navigateTo(notif.actionRoute)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = notif.type.color),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(notif.actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(onClick = { viewingNotification = null }) {
                        Text("Tutup", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNotification(notif.id)
                        viewingNotification = null
                    }
                ) {
                    Text("Hapus", color = BrandRose)
                }
            }
        )
    }

    // --- CLEAR ALL CONFIRMATION DIALOG ---
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text("Hapus Semua Notifikasi?", fontWeight = FontWeight.Bold) },
            text = { Text("Seluruh riwayat notifikasi akan dibersihkan secara permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllNotifications()
                        showClearAllConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                ) {
                    Text("Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // --- COMPOSE / BROADCAST NOTIFICATION DIALOG ---
    if (showCreateDialog) {
        CreateNotificationDialog(
            onDismiss = { showCreateDialog = false },
            onSend = { title, message, type ->
                viewModel.addNotification(
                    title = title,
                    message = message,
                    type = type,
                    actionRoute = if (type == NotificationType.PROMO) AppScreen.CATALOG else AppScreen.HOME,
                    actionLabel = if (type == NotificationType.PROMO) "Lihat Promo" else "Buka Detail",
                    targetRole = "ALL"
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun NotificationItemCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!notification.isRead) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notif_card_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Type Icon Indicator
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = notification.type.color.copy(alpha = 0.14f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = notification.type.icon,
                        contentDescription = notification.type.title,
                        tint = notification.type.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header: Type Chip + Timestamp + Unread Dot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = notification.type.color.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = notification.type.title,
                                color = notification.type.color,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(BrandCrimson)
                            )
                        }
                    }

                    Text(
                        text = notification.timestamp,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = notification.title,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Message snippet
                Text(
                    text = notification.message,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Action button if available
                if (notification.actionRoute != null && notification.actionLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onActionClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("notif_action_${notification.id}")
                    ) {
                        Text(
                            text = "${notification.actionLabel} →",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = notification.type.color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Delete button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("notif_delete_${notification.id}")
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotificationDialog(
    onDismiss: () -> Unit,
    onSend: (title: String, message: String, type: NotificationType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(NotificationType.PROMO) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = BrandIndigo)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kirim Notifikasi Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pilih Kategori Pemberitahuan:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        NotificationType.PROMO,
                        NotificationType.TRANSAKSI,
                        NotificationType.KEAMANAN,
                        NotificationType.SISTEM
                    ).forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type.title, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Notifikasi") },
                    placeholder = { Text("Contoh: Flash Sale 50% Dimulai!") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Isi Pesan Notifikasi") },
                    placeholder = { Text("Ketik detail pesan atau pengumuman promo...") },
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title.trim(), message.trim(), selectedType)
                    }
                },
                enabled = title.isNotBlank() && message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)
            ) {
                Text("Kirim Notifikasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
