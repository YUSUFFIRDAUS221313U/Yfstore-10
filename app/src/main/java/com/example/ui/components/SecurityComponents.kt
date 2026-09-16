package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.security.SecurityManager
import com.example.ui.theme.*

/**
 * Modern YF-Shield Badge indicating anti-virus and SHA-256 verification
 */
@Composable
fun YfShieldBadge(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = Color(0xFF064E3B).copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("yf_shield_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = if (compact) 6.dp else 10.dp, vertical = if (compact) 3.dp else 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 16.dp else 22.dp)
                    .background(Color(0xFF10B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "YF-Shield Protected",
                    tint = Color.White,
                    modifier = Modifier.size(if (compact) 10.dp else 14.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "YF-SHIELD™",
                        fontWeight = FontWeight.Black,
                        fontSize = if (compact) 9.sp else 11.sp,
                        color = Color(0xFF047857)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VERIFIED CLEAN",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 8.sp else 9.sp,
                        color = Color(0xFF059669)
                    )
                }
                if (!compact) {
                    Text(
                        text = "Bebas Virus & Trojan • SHA-256 Aman",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Detailed File Integrity & Antivirus Scan Result Dialog
 */
@Composable
fun FileIntegrityScanDialog(
    targetName: String,
    scanResult: SecurityManager.MalwareScanResult?,
    onDismiss: () -> Unit
) {
    if (scanResult == null) return

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var copiedToClipboard by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("file_integrity_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF10B981), Color(0xFF047857))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Laporan Keamanan Berkas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "YF-Shield™ Anti-Malware & Integrity Scanner",
                    fontSize = 11.sp,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Banner
                Surface(
                    color = if (scanResult.isSafe) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (scanResult.isSafe) Color(0xFF10B981) else Color(0xFFEF4444)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (scanResult.isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (scanResult.isSafe) Color(0xFF059669) else Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (scanResult.isSafe) "STATUS: 100% AMAN & BERSIH" else "PERINGATAN: ANCAMAN TERDETEKSI",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (scanResult.isSafe) Color(0xFF065F46) else Color(0xFF991B1B)
                            )
                            Text(
                                text = if (scanResult.isSafe) "Tidak ada virus, trojan, atau kode berbahaya." else scanResult.threatsFound.joinToString(", "),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    SecurityDetailRow(label = "Nama Berkas", value = targetName)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SecurityDetailRow(label = "Tingkat Ancaman", value = scanResult.threatLevel.name)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SecurityDetailRow(label = "Sertifikasi", value = "YFSTORE Certified Safe™")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // SHA-256 Checksum with Copy button
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "SHA-256 Checksum", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(scanResult.sha256Checksum))
                                    copiedToClipboard = true
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Salin Hash",
                                        modifier = Modifier.size(12.dp),
                                        tint = if (copiedToClipboard) Color(0xFF059669) else BrandIndigo
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (copiedToClipboard) "Tersalin!" else "Salin Hash",
                                        fontSize = 10.sp,
                                        color = if (copiedToClipboard) Color(0xFF059669) else BrandIndigo
                                    )
                                }
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = scanResult.sha256Checksum,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Security Features bullet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SecurityPill(icon = Icons.Default.Lock, label = "Enkripsi Kriptografis")
                    SecurityPill(icon = Icons.Default.GppGood, label = "Anti-Trojan")
                    SecurityPill(icon = Icons.Default.Https, label = "HTTPS Strict")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_security_dialog_button")
                ) {
                    Text("Tutup Laporan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SecurityDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SecurityPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFF059669).copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
    }
}

/**
 * Security summary card for User Profile / Member Center
 */
@Composable
fun AccountSecurityCard(
    onInspectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("account_security_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF047857), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pusat Keamanan & Anti-Virus",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "YF-Shield Defense Engine Aktif",
                            fontSize = 11.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Text(
                        text = "100% AMAN",
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SecurityFeatureCheck("Proteksi Kata Sandi: SHA-256 + Cryptographic Salt")
                SecurityFeatureCheck("Pencegahan Serangan Asing: Firewall Anti-Brute Force")
                SecurityFeatureCheck("Scanner Anti-Virus & Anti-Trojan Berkas Unduhan")
                SecurityFeatureCheck("Koneksi Aman: Cleartext HTTP Dinonaktifkan (Strict HTTPS)")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onInspectClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF047857)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF047857)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("check_system_security_btn")
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Periksa Integritas File & Akun", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SecurityFeatureCheck(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF059669),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
