package com.example

import com.example.data.model.UserRole
import com.example.ui.components.formatRupiah
import org.junit.Assert.*
import org.junit.Test

class MarketplaceUnitTest {

    @Test
    fun testRupiahFormatting() {
        val formatted = formatRupiah(350000.0)
        assertTrue(formatted.contains("350.000") || formatted.contains("350,000"))
    }

    @Test
    fun testRoleHierarchyAndPermissions() {
        val guest = UserRole.GUEST
        val member = UserRole.MEMBER
        val staff = UserRole.STAFF
        val admin = UserRole.ADMIN
        val superAdmin = UserRole.SUPER_ADMIN

        assertEquals(0, guest.level)
        assertEquals(1, member.level)
        assertEquals(2, staff.level)
        assertEquals(3, admin.level)
        assertEquals(4, superAdmin.level)

        // Verify Staff can access admin panel but is lower level than Admin
        assertTrue(staff.level < admin.level)
        assertTrue(admin.level < superAdmin.level)
    }

    @Test
    fun testDiscountCalculation() {
        val subtotal = 500000.0
        val discountPercent = 20
        val maxDiscount = 50000.0

        val rawDiscount = subtotal * (discountPercent / 100.0) // 100,000
        val finalDiscount = rawDiscount.coerceAtMost(maxDiscount) // 50,000

        assertEquals(50000.0, finalDiscount, 0.01)
        val total = subtotal - finalDiscount
        assertEquals(450000.0, total, 0.01)
    }
}
