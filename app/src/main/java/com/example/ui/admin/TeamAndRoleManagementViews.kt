package com.example.ui.admin

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
import com.example.data.local.UserEntity
import com.example.data.model.UserRole
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.RoleBadge
import com.example.ui.theme.*

/**
 * Team Management View:
 * Allows Admin & Super Admin to add new team members, edit roles,
 * suspend/activate accounts, and delete members.
 */
@Composable
fun TeamManagementView(
    viewModel: MarketplaceViewModel,
    users: List<UserEntity>,
    currentUserRole: UserRole,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<String?>("ALL") }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var selectedUserForRoleChange by remember { mutableStateOf<UserEntity?>(null) }
    var selectedUserForDelete by remember { mutableStateOf<UserEntity?>(null) }

    val rolePermissions by viewModel.rolePermissions.collectAsState()

    val filteredUsers = remember(users, searchQuery, selectedRoleFilter) {
        users.filter { user ->
            val matchesSearch = user.name.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true) ||
                    user.username.contains(searchQuery, ignoreCase = true) ||
                    user.phone.contains(searchQuery, ignoreCase = true)

            val matchesRole = when (selectedRoleFilter) {
                "ALL" -> true
                "STAFF" -> user.role == UserRole.STAFF.name
                "ADMIN" -> user.role == UserRole.ADMIN.name
                "SUPER_ADMIN" -> user.role == UserRole.SUPER_ADMIN.name
                "MEMBER" -> user.role == UserRole.MEMBER.name
                else -> true
            }

            matchesSearch && matchesRole
        }
    }

    val totalStaff = remember(users) { users.count { it.role == UserRole.STAFF.name } }
    val totalAdmin = remember(users) { users.count { it.role == UserRole.ADMIN.name } }
    val totalSuperAdmin = remember(users) { users.count { it.role == UserRole.SUPER_ADMIN.name } }
    val totalMembers = remember(users) { users.count { it.role == UserRole.MEMBER.name } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("team_management_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Metric Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TeamStatCard(title = "Total Staf", value = "$totalStaff", icon = Icons.Default.Badge, color = BrandIndigo, modifier = Modifier.weight(1f))
                TeamStatCard(title = "Admin", value = "$totalAdmin", icon = Icons.Default.AdminPanelSettings, color = BrandCrimson, modifier = Modifier.weight(1f))
                TeamStatCard(title = "Super Admin", value = "$totalSuperAdmin", icon = Icons.Default.Shield, color = BrandRose, modifier = Modifier.weight(1f))
                TeamStatCard(title = "Member", value = "$totalMembers", icon = Icons.Default.People, color = BrandEmerald, modifier = Modifier.weight(1f))
            }
        }

        // Header & Add Team Member Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pengaturan Manajemen Tim & Pengguna",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Kelola peran operasional, status akses, dan kredensial tim toko",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddMemberDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_team_member_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Staf", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama, email, username tim...", fontSize = 12.sp) },
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
                    .testTag("search_team_input")
            )
        }

        // Role Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val filters = listOf(
                    "ALL" to "Semua (${users.size})",
                    "STAFF" to "Staf ($totalStaff)",
                    "ADMIN" to "Admin ($totalAdmin)",
                    "SUPER_ADMIN" to "Super Admin ($totalSuperAdmin)",
                    "MEMBER" to "Member ($totalMembers)"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedRoleFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRoleFilter = key },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandCrimson.copy(alpha = 0.15f),
                            selectedLabelColor = BrandCrimson
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

        // User List
        if (filteredUsers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tidak ada anggota tim yang cocok dengan filter", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredUsers, key = { it.id }) { user ->
                val userRoleEnum = remember(user.role) {
                    UserRole.values().find { it.name == user.role } ?: UserRole.MEMBER
                }
                val activeFeatureCount = remember(userRoleEnum, rolePermissions) {
                    rolePermissions[userRoleEnum]?.size ?: 0
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_card_${user.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = when (userRoleEnum) {
                                        UserRole.SUPER_ADMIN -> BrandRose.copy(alpha = 0.15f)
                                        UserRole.ADMIN -> BrandCrimson.copy(alpha = 0.15f)
                                        UserRole.STAFF -> BrandIndigo.copy(alpha = 0.15f)
                                        else -> BrandEmerald.copy(alpha = 0.15f)
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = user.name.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = when (userRoleEnum) {
                                                UserRole.SUPER_ADMIN -> BrandRose
                                                UserRole.ADMIN -> BrandCrimson
                                                UserRole.STAFF -> BrandIndigo
                                                else -> BrandEmerald
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("@${user.username} • ${user.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (user.phone.isNotBlank()) {
                                        Text("WA: ${user.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                RoleBadge(role = userRoleEnum)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "$activeFeatureCount Fitur",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls: Active/Suspend, Change Role, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (user.isActive) BrandEmerald.copy(alpha = 0.15f) else BrandRose.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (user.isActive) "Status: Aktif" else "Status: Ditangguhkan",
                                    color = if (user.isActive) BrandEmerald else BrandRose,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                // Toggle active/suspend button
                                OutlinedButton(
                                    onClick = { viewModel.toggleUserStatus(user) },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(if (user.isActive) "Tangguhkan" else "Aktifkan", fontSize = 10.sp)
                                }

                                // Change Role button
                                if (currentUserRole == UserRole.SUPER_ADMIN || currentUserRole == UserRole.ADMIN) {
                                    Button(
                                        onClick = { selectedUserForRoleChange = user },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            "Ubah Role",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }

                                // Delete user button (Super Admin only)
                                if (currentUserRole == UserRole.SUPER_ADMIN && user.role != UserRole.SUPER_ADMIN.name) {
                                    IconButton(
                                        onClick = { selectedUserForDelete = user },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Hapus Anggota",
                                            tint = BrandRose,
                                            modifier = Modifier.size(16.dp)
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

    // Dialog: Add Team Member
    if (showAddMemberDialog) {
        AddTeamMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onSave = { name, username, email, phone, role ->
                viewModel.createTeamMember(name, username, email, phone, role)
                showAddMemberDialog = false
            }
        )
    }

    // Dialog: Change User Role
    selectedUserForRoleChange?.let { user ->
        ChangeRoleDialog(
            user = user,
            onDismiss = { selectedUserForRoleChange = null },
            onConfirm = { newRole ->
                viewModel.changeUserRole(user, newRole)
                selectedUserForRoleChange = null
            }
        )
    }

    // Dialog: Delete Confirmation
    selectedUserForDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { selectedUserForDelete = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = BrandRose) },
            title = { Text("Hapus Anggota Tim?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Akun ${user.name} (@${user.username}) dengan role ${user.role} akan dihapus secara permanen dari sistem.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(user)
                        selectedUserForDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun TeamStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTeamMemberDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, UserRole) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.STAFF) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Anggota Tim Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMsg != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandRose.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMsg!!,
                            color = BrandRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.lowercase().replace(" ", "") },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Alamat Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. WhatsApp / HP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Pilih Peran (Role):", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(UserRole.STAFF, UserRole.ADMIN, UserRole.SUPER_ADMIN).forEach { role ->
                        val isPicked = role == selectedRole
                        FilterChip(
                            selected = isPicked,
                            onClick = { selectedRole = role },
                            label = { Text(role.displayName, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCrimson.copy(alpha = 0.15f),
                                selectedLabelColor = BrandCrimson
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || username.isBlank() || email.isBlank()) {
                        errorMsg = "Nama, username, dan email wajib diisi!"
                        return@Button
                    }
                    onSave(name, username, email, phone, selectedRole)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
            ) {
                Text("Simpan Anggota")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun ChangeRoleDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (UserRole) -> Unit
) {
    val currentRoleEnum = remember(user.role) {
        UserRole.values().find { it.name == user.role } ?: UserRole.MEMBER
    }
    var selectedRole by remember { mutableStateOf(currentRoleEnum) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Role Pengguna", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Pilih tingkat hak akses baru untuk ${user.name}:", fontSize = 12.sp)

                UserRole.values().forEach { role ->
                    val isSelected = role == selectedRole
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) BrandCrimson.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = role }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = role.description,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedRole = role },
                                colors = RadioButtonDefaults.colors(selectedColor = BrandCrimson)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
            ) {
                Text("Terapkan Role")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * Dynamic Role Permissions Matrix:
 * Allows adding or removing features from any role,
 * adding custom system features, and restoring defaults.
 */
@Composable
fun DynamicRolePermissionsView(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.STAFF) }
    var featureSearchQuery by remember { mutableStateOf("") }
    var showAddFeatureDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val systemFeatures by viewModel.systemFeatures.collectAsState()
    val rolePermissions by viewModel.rolePermissions.collectAsState()

    val allowedFeaturesForSelectedRole = remember(selectedRole, rolePermissions) {
        rolePermissions[selectedRole] ?: emptySet()
    }

    // Filter features by search query
    val filteredFeatures = remember(systemFeatures, featureSearchQuery) {
        if (featureSearchQuery.isBlank()) {
            systemFeatures
        } else {
            systemFeatures.filter {
                it.name.contains(featureSearchQuery, ignoreCase = true) ||
                        it.category.contains(featureSearchQuery, ignoreCase = true) ||
                        it.description.contains(featureSearchQuery, ignoreCase = true)
            }
        }
    }

    val groupedFeatures = remember(filteredFeatures) {
        filteredFeatures.groupBy { it.category }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dynamic_role_permissions_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Banner & Action Buttons
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
                            Text(
                                text = "Konfigurasi Hak Akses Fitur Role (RBAC)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Tambah atau kurangi fitur operasional pada tiap tingkat peran",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { showResetConfirmDialog = true },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Reset", fontSize = 10.sp)
                            }

                            Button(
                                onClick = { showAddFeatureDialog = true },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Fitur Baru", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Role Selector Chips
        item {
            Column {
                Text("Pilih Role yang Akan Dikonfigurasi:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(UserRole.values()) { role ->
                        val isSelected = role == selectedRole
                        val allowedCount = rolePermissions[role]?.size ?: 0
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRole = role },
                            label = {
                                Text(
                                    text = "${role.displayName} ($allowedCount/${systemFeatures.size})",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCrimson.copy(alpha = 0.15f),
                                selectedLabelColor = BrandCrimson
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = BrandCrimson
                            ),
                            modifier = Modifier.testTag("role_chip_${role.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Search Filter for features
        item {
            OutlinedTextField(
                value = featureSearchQuery,
                onValueChange = { featureSearchQuery = it },
                placeholder = { Text("Filter fitur (mis: Hapus, Sekalipay, Kupon)...", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (featureSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { featureSearchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus", modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("filter_features_input")
            )
        }

        // Informational banner about Super Admin
        if (selectedRole == UserRole.SUPER_ADMIN) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandRose.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = BrandRose, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Super Admin memiliki hak akses penuh mutlak ke seluruh fitur sistem dan database.",
                            fontSize = 11.sp,
                            color = BrandRose,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Grouped Feature Matrix Cards
        groupedFeatures.forEach { (categoryName, featuresList) ->
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = categoryName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BrandCrimson
                            )
                            val activeInCategory = featuresList.count { allowedFeaturesForSelectedRole.contains(it.id) }
                            Text(
                                text = "$activeInCategory dari ${featuresList.size} aktif",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                        Spacer(modifier = Modifier.height(4.dp))

                        featuresList.forEach { feature ->
                            val isAllowed = allowedFeaturesForSelectedRole.contains(feature.id)
                            val isSuperAdmin = selectedRole == UserRole.SUPER_ADMIN

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = feature.name,
                                            fontWeight = if (isAllowed) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (isAllowed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (feature.isCustom) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = BrandAmber.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "Kustom",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandAmber,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = feature.description,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (feature.isCustom) {
                                        IconButton(
                                            onClick = { viewModel.removeCustomFeature(feature.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DeleteOutline,
                                                contentDescription = "Hapus Fitur",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    Switch(
                                        checked = isAllowed,
                                        onCheckedChange = {
                                            viewModel.toggleRoleFeature(selectedRole, feature.id)
                                        },
                                        enabled = !isSuperAdmin,
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = BrandCrimson,
                                            uncheckedThumbColor = Color.LightGray,
                                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier.testTag("toggle_feature_${feature.id.lowercase()}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Custom Feature
    if (showAddFeatureDialog) {
        AddCustomFeatureDialog(
            onDismiss = { showAddFeatureDialog = false },
            onSave = { name, category, desc, roles ->
                viewModel.addNewCustomFeature(name, category, desc, roles)
                showAddFeatureDialog = false
            }
        )
    }

    // Dialog: Reset Confirmation
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            icon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandCrimson) },
            title = { Text("Reset Hak Akses Role?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Semua konfigurasi penambahan/pengurangan fitur akan dikembalikan ke standar bawaan sistem RBAC.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetRolePermissionsToDefault()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
                ) {
                    Text("Ya, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun AddCustomFeatureDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, List<UserRole>) -> Unit
) {
    var featureName by remember { mutableStateOf("") }
    var featureCategory by remember { mutableStateOf("Operasional & Katalog") }
    var featureDescription by remember { mutableStateOf("") }
    val selectedRoles = remember { mutableStateListOf(UserRole.STAFF, UserRole.ADMIN) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Katalog & Transaksi",
        "Operasional & Katalog",
        "Keuangan & Reseller",
        "Bot & Kustomisasi",
        "Keamanan & Manajemen Tim"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Fitur Baru ke Sistem", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMsg != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BrandRose.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMsg!!,
                            color = BrandRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = featureName,
                    onValueChange = { featureName = it },
                    label = { Text("Nama Fitur (mis: Export Excel / Kurir)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = featureDescription,
                    onValueChange = { featureDescription = it },
                    label = { Text("Deskripsi Izin Fitur") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Kategori:", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = featureCategory == cat,
                            onClick = { featureCategory = cat },
                            label = { Text(cat, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCrimson.copy(alpha = 0.15f),
                                selectedLabelColor = BrandCrimson
                            )
                        )
                    }
                }

                Text("Semua Role yang Diizinkan:", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(UserRole.GUEST, UserRole.MEMBER, UserRole.STAFF, UserRole.ADMIN).forEach { r ->
                        val isChecked = selectedRoles.contains(r)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (isChecked) selectedRoles.remove(r) else selectedRoles.add(r)
                            },
                            label = { Text(r.displayName, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCrimson.copy(alpha = 0.15f),
                                selectedLabelColor = BrandCrimson
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (featureName.isBlank()) {
                        errorMsg = "Nama fitur wajib diisi!"
                        return@Button
                    }
                    onSave(featureName, featureCategory, featureDescription, selectedRoles.toList())
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson)
            ) {
                Text("Tambahkan Fitur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
