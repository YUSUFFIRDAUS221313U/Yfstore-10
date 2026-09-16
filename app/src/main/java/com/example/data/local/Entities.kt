package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // Stored as enum name
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val price: Double,
    val description: String,
    val fileType: String, // e.g. "ZIP", "PDF", "MP4", "FIG"
    val fileSize: String, // e.g. "45 MB", "120 MB"
    val downloadFileName: String, // e.g. "nextjs15-saas-starter.zip"
    val licensePrefix: String, // e.g. "SAAS-PRO-"
    val version: String = "1.0.0",
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val salesCount: Int = 34,
    val isFeatured: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val quantity: Int = 1,
    val userId: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String, // e.g. "ORD-2026-8941"
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val totalAmount: Double,
    val discountAmount: Double,
    val couponCode: String?,
    val paymentMethodId: String,
    val paymentMethodName: String,
    val paymentStatus: String, // "COMPLETED", "PENDING", "CANCELLED"
    val paymentReference: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val productId: String,
    val productTitle: String,
    val price: Double,
    val fileType: String,
    val fileSize: String,
    val downloadFileName: String,
    val downloadToken: String,
    val downloadCount: Int = 0,
    val maxDownloads: Int = 999,
    val licenseKey: String,
    val expiryDays: Int = 0 // 0 = Lifetime
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val discountPercent: Int, // e.g. 20 for 20%
    val discountMax: Double, // e.g. 50000.0
    val minPurchase: Double, // e.g. 100000.0
    val isActive: Boolean = true,
    val usageCount: Int = 0
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actorName: String,
    val actorRole: String,
    val action: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis()
)
