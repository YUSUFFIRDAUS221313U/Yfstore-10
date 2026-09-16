package com.example.ui.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.notifications.NotificationType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationBroadcastView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationCount.collectAsState()

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastType by remember { mutableStateOf(NotificationType.PROMO) }
    var targetAudience by remember { mutableStateOf("Semua Pengguna") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_broadcast_notif_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER BANNER ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BrandIndigo.copy(alpha = 0.08f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandIndigo.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandIndigo,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Broadcast & Siaran Notifikasi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Kirim pesan push, pengumuman diskon, peringatan keamanan, atau update ke pengguna secara instan.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pusat Notif", fontSize = 11.sp)
                    }
                }
            }
        }

        // --- STATS OVERVIEW ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Siaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${notifications.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrandIndigo)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Belum Dibaca", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$unreadCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrandRose)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Promo Aktif", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${notifications.count { it.type == NotificationType.PROMO }}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = BrandOrange
                        )
                    }
                }
            }
        }

        // --- COMPOSE BROADCAST FORM ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Formulir Kirim Siaran Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Category Selector
                    Text("Kategori Notifikasi:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            NotificationType.PROMO,
                            NotificationType.TRANSAKSI,
                            NotificationType.KEAMANAN,
                            NotificationType.SISTEM
                        ).forEach { type ->
                            val isSelected = broadcastType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { broadcastType = type },
                                label = { Text(type.title, fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(type.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Target Audience Selector
                    Text("Target Penerima Notifikasi:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Semua Pengguna", "Member Saja", "Staf & Admin").forEach { aud ->
                            val isSelected = targetAudience == aud
                            FilterChip(
                                selected = isSelected,
                                onClick = { targetAudience = aud },
                                label = { Text(aud, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Preset Templates Quick Fill
                    Text("Template Siaran Cepat:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                broadcastTitle = "⚡ Flash Sale Digital: Diskon 30% Terbatas!"
                                broadcastMessage = "Klaim kode kupon FLASHSALE30 untuk seluruh lisensi software dan course digital."
                                broadcastType = NotificationType.PROMO
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("⚡ Promo", fontSize = 10.sp)
                        }
                        FilledTonalButton(
                            onClick = {
                                broadcastTitle = "Maintenance & Peningkatan Keamanan YF-Shield"
                                broadcastMessage = "Seluruh server sinkronisasi telah dioptimalkan dengan firewall anti-tamper."
                                broadcastType = NotificationType.KEAMANAN
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🛡️ Keamanan", fontSize = 10.sp)
                        }
                        FilledTonalButton(
                            onClick = {
                                broadcastTitle = "Rilis Update Template Digital 2026"
                                broadcastMessage = "Katalog telah diperbarui dengan 5 produk source code andalan baru."
                                broadcastType = NotificationType.SISTEM
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🚀 Update", fontSize = 10.sp)
                        }
                    }

                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Judul Notifikasi") },
                        placeholder = { Text("Contoh: Promo Spesial Hari Ini") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Isi Pesan Siaran") },
                        placeholder = { Text("Ketik isi pengumuman broadcast yang akan tampil...") },
                        minLines = 3,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                                viewModel.broadcastNotification(
                                    title = broadcastTitle.trim(),
                                    message = broadcastMessage.trim(),
                                    type = broadcastType,
                                    targetRole = targetAudience
                                )
                                broadcastTitle = ""
                                broadcastMessage = ""
                                showSuccessBanner = true
                            }
                        },
                        enabled = broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim Broadcast Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SENT NOTIFICATIONS HISTORY ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat Pemberitahuan Terkirim",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { viewModel.markAllNotificationsAsRead() }
                ) {
                    Text("Tandai Dibaca", fontSize = 11.sp, color = BrandIndigo)
                }
            }
        }

        items(notifications, key = { it.id }) { notif ->
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
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = notif.type.color.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(notif.type.icon, contentDescription = null, tint = notif.type.color, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notif.type.title.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = notif.type.color
                            )
                            Text(
                                text = notif.timestamp,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = notif.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = notif.message,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { viewModel.deleteNotification(notif.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = BrandRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
