package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProductEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount).replace("Rp", "Rp ")
}

@Composable
fun RoleBadge(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (role) {
        UserRole.GUEST -> Color(0xFF64748B) to Color.White
        UserRole.MEMBER -> Color(0xFF3B82F6) to Color.White
        UserRole.STAFF -> Color(0xFF10B981) to Color.White
        UserRole.ADMIN -> Color(0xFF8B5CF6) to Color.White
        UserRole.SUPER_ADMIN -> Color(0xFFEF4444) to Color.White
    }

    Surface(
        color = bgColor.copy(alpha = 0.18f),
        contentColor = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, bgColor.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(bgColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = role.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FileTypeBadge(
    fileType: String,
    modifier: Modifier = Modifier
) {
    val (bg, text) = when (fileType.uppercase()) {
        "ZIP" -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
        "PDF" -> Color(0xFFFFE4E6) to Color(0xFF9F1239)
        "PORTAL" -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        "FIG" -> Color(0xFFFCE7F3) to Color(0xFF831843)
        "KEY", "VOUCHER" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        else -> Color(0xFFE2E8F0) to Color(0xFF334155)
    }

    Surface(
        color = bg,
        contentColor = text,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = fileType.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isWishlisted: Boolean = false,
    onToggleWishlist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Header Graphic simulation with category badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
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
                    .padding(10.dp)
            ) {
                // Category Icon
                Icon(
                    imageVector = getCategoryVector(product.categoryId),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .size(68.dp)
                        .align(Alignment.BottomEnd)
                )

                // Badges top row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FileTypeBadge(fileType = product.fileType)

                    IconButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                            .testTag("wishlist_btn_${product.id}")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) BrandRose else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // File size / Instant tag
                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = BrandAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Instant Delivery • ${product.fileSize}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Info Body
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Rating & Sales
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = BrandAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${product.rating}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = " (${product.reviewCount})",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${product.salesCount} Terjual",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price & Add to Cart
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Harga",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatRupiah(product.price),
                            color = BrandIndigo,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onAddToCart,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = BrandIndigo.copy(alpha = 0.12f),
                            contentColor = BrandIndigo
                        ),
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("add_cart_btn_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Tambah ke Keranjang",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryVector(categoryId: String): ImageVector {
    return when (categoryId) {
        "software" -> Icons.Default.Code
        "ebook" -> Icons.Default.MenuBook
        "course" -> Icons.Default.School
        "design" -> Icons.Default.Palette
        "services" -> Icons.Default.Handyman
        "voucher" -> Icons.Default.ConfirmationNumber
        else -> Icons.Default.Category
    }
}
