package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MarketplaceViewModel
import com.example.ui.theme.*

data class FaqItem(
    val question: String,
    val answer: String
)

val FAQ_LIST = listOf(
    FaqItem(
        question = "Bagaimana cara mengakses produk digital setelah pembayaran?",
        answer = "DigiMarket menggunakan sistem Instant Delivery otomatis. Segera setelah pembayaran QRIS atau Virtual Account Anda berhasil, berkas digital, token unduhan, dan kunci lisensi langsung aktif dan tersimpan di menu 'Member Center -> Download Center'."
    ),
    FaqItem(
        question = "Berapa lama masa aktif token dan tautan unduhan?",
        answer = "Semua produk digital yang dibeli di DigiMarket memiliki akses Lifetime (Permanen). Anda dapat mengunduh ulang berkas kapan saja melalui Download Center akun Anda tanpa biaya tambahan."
    ),
    FaqItem(
        question = "Apakah produk software dan template mendapat pembaruan (updates)?",
        answer = "Ya, pembaruan versi minor dan patch keamanan diberikan gratis seumur hidup. Saat rilis versi baru, Anda cukup mengunduh kembali file terbaru di Download Center."
    ),
    FaqItem(
        question = "Bagaimana jika saya checkout sebagai Tamu (Guest)?",
        answer = "Link token unduhan unik dan salinan invoice akan otomatis dikirimkan ke alamat email yang Anda masukkan saat checkout. Kami menyarankan membuat akun Member agar histori unduhan tersimpan rapi."
    ),
    FaqItem(
        question = "Apakah pembayaran QRIS dan Virtual Account diverifikasi otomatis?",
        answer = "Ya, sistem kami terintegrasi dengan Payment Gateway (Midtrans/Xendit simulator) dengan notifikasi Webhook instan. Konfirmasi pembayaran selesai dalam 3-5 detik."
    ),
    FaqItem(
        question = "Apakah ada garansi jika file rusak atau tidak sesuai?",
        answer = "Kami memberikan Garansi 100% Berfungsi. Jika berkas zip/pdf corrupt atau lisensi bermasalah, Staff kami siap membantu via WhatsApp dalam waktu maksimal 1x24 jam."
    )
)

@Composable
fun FaqContactScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("faq_contact_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Pusat Bantuan & Layanan Pelanggan", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("Jawaban pertanyaan umum dan jalur konsultasi resmi DigiMarket (PRD Section 5.1 & 7).", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // WhatsApp Direct Help Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Bantuan WhatsApp Resmi", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text("Fast Response • Jam Operasional 08:00 - 22:00 WIB", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Sesuai PRD MVP, layanan support langsung ditangani oleh tim Staff & Admin melalui WhatsApp agar penanganan kendala token dan lisensi berlangsung cepat.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            try {
                                val url = "https://wa.me/6281234567890?text=Halo%20Admin%20DigiMarket,%20saya%20butuh%20bantuan%20terkait%20produk%20digital"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showNotification("Menghubungkan ke WhatsApp: +62 812-3456-7890")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("whatsapp_help_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hubungi via WhatsApp (+62 812-3456-7890)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Channels Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChannelCard(
                    title = "Email Helpdesk",
                    value = "support@digimarket.id",
                    icon = Icons.Default.Email,
                    onClick = { viewModel.showNotification("Email bantuan: support@digimarket.id") },
                    modifier = Modifier.weight(1f)
                )
                ChannelCard(
                    title = "Komunitas Telegram",
                    value = "@DigiMarketID",
                    icon = Icons.Default.Forum,
                    onClick = { viewModel.showNotification("Komunitas Telegram: @DigiMarketID") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // FAQ Title
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Pertanyaan yang Sering Diajukan (FAQ)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(FAQ_LIST) { faq ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = faq.question,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = BrandIndigo
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = faq.answer,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
