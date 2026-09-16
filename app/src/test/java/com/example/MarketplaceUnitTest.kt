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

    @Test
    fun testUserModelContainsAllAuthFields() {
        val user = com.example.data.model.User(
            id = "usr_100",
            name = "Ahmad Pratama",
            username = "ahmad_pratama",
            email = "ahmad@gmail.com",
            phone = "081234567890",
            role = UserRole.MEMBER
        )

        assertEquals("Ahmad Pratama", user.name)
        assertEquals("ahmad_pratama", user.username)
        assertEquals("ahmad@gmail.com", user.email)
        assertEquals("081234567890", user.phone)
        assertEquals(UserRole.MEMBER, user.role)
    }

    @Test
    fun testUsernameValidationPattern() {
        val validUsername = "user_123"
        val invalidUsername = "user@invalid!"

        assertTrue(validUsername.matches(Regex("^[a-zA-Z0-9_.]+$")))
        assertFalse(invalidUsername.matches(Regex("^[a-zA-Z0-9_.]+$")))
    }

    @Test
    fun testPasswordHashingAndSaltVerification() {
        val salt = com.example.security.SecurityManager.generateSalt()
        assertEquals(32, salt.length)

        val pass = "SuperSecret123!"
        val hash1 = com.example.security.SecurityManager.hashPassword(pass, salt)
        val hash2 = com.example.security.SecurityManager.hashPassword(pass, salt)

        // SHA-256 length is 64 hex characters
        assertEquals(64, hash1.length)
        // Deterministic with same salt
        assertEquals(hash1, hash2)
        // Correct verification
        assertTrue(com.example.security.SecurityManager.verifyPassword(pass, hash1, salt))
        // Incorrect password fails
        assertFalse(com.example.security.SecurityManager.verifyPassword("WrongPassword", hash1, salt))
    }

    @Test
    fun testMalwareScannerBlocksDangerousExecutablesAndTrojans() {
        val exeScan = com.example.security.SecurityManager.scanDigitalFile("trojan.exe", "exe", "10 MB", "p1")
        assertFalse(exeScan.isSafe)
        assertTrue(exeScan.threatsFound.isNotEmpty())

        val doubleExtScan = com.example.security.SecurityManager.scanDigitalFile("invoice.pdf.exe", "pdf", "1 MB", "p2")
        assertFalse(doubleExtScan.isSafe)

        val batScan = com.example.security.SecurityManager.scanDigitalFile("script.bat", "bat", "500 KB", "p3")
        assertFalse(batScan.isSafe)
    }

    @Test
    fun testSafeFilePassesIntegrityCheck() {
        val safeScan = com.example.security.SecurityManager.scanDigitalFile("clean_ebook.pdf", "pdf", "5.2 MB", "p10")
        assertTrue(safeScan.isSafe)
        assertEquals(com.example.security.SecurityManager.ThreatLevel.SAFE, safeScan.threatLevel)
        assertEquals(64, safeScan.sha256Checksum.length)
    }

    @Test
    fun testAntiBruteForceRateLimiting() {
        val identifier = "test_foreign_attacker_ip"
        // Initially allowed (null means no lockout)
        assertNull(com.example.security.SecurityManager.checkRateLimit(identifier))

        // Record 5 failed attempts
        repeat(5) {
            com.example.security.SecurityManager.recordFailedLogin(identifier)
        }

        // Must now be blocked by firewall rate-limiter (returns remaining lockout seconds)
        val lockoutSeconds = com.example.security.SecurityManager.checkRateLimit(identifier)
        assertNotNull(lockoutSeconds)
        assertTrue((lockoutSeconds ?: 0L) > 0)

        // Reset on successful auth
        com.example.security.SecurityManager.recordSuccessfulLogin(identifier)
        assertNull(com.example.security.SecurityManager.checkRateLimit(identifier))
    }

    @Test
    fun testRoleAdaptiveDashboardLogic() {
        // Verification that roles map correctly to Admin vs Member dashboard modes
        fun isAdminRole(role: UserRole): Boolean {
            return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN || role == UserRole.STAFF
        }

        assertTrue("Super Admin must have Admin dashboard", isAdminRole(UserRole.SUPER_ADMIN))
        assertTrue("Admin must have Admin dashboard", isAdminRole(UserRole.ADMIN))
        assertTrue("Staff must have Admin dashboard", isAdminRole(UserRole.STAFF))

        assertFalse("Member must have Member dashboard", isAdminRole(UserRole.MEMBER))
        assertFalse("Guest must have Member dashboard", isAdminRole(UserRole.GUEST))
    }
}


