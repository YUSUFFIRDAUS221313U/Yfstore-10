package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.authTab.collectAsState()
    val errorMessage by viewModel.authErrorMessage.collectAsState()
    val successMessage by viewModel.authSuccessMessage.collectAsState()
    val isLoading by viewModel.isAuthLoading.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("auth_screen")
    ) {
        // Top App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("auth_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali ke Beranda")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedTab == 0) "Masuk ke YFSTORE" else "Pendaftaran Akun Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                TextButton(
                    onClick = {
                        viewModel.switchRole(com.example.data.model.UserRole.GUEST)
                        viewModel.navigateTo(AppScreen.HOME)
                    },
                    modifier = Modifier.testTag("continue_as_guest_top_btn")
                ) {
                    Text("Lewati", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Brand Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFF0C0F14),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandCrimson.copy(alpha = 0.45f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(86.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yf_logo),
                        contentDescription = "YF STORE Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "YF STORE",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp,
                    color = BrandCrimson
                )
                Text(
                    text = "YUSUF FIRDAUS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.5.sp,
                    color = BrandCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Akses lisensi software, template, aset digital & instant download",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tab Switcher (Masuk vs Registrasi)
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = BrandIndigo,
                        indicator = {},
                        divider = {},
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                viewModel.authTab.value = 0
                                viewModel.authErrorMessage.value = null
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent
                                )
                                .testTag("auth_tab_login")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 0) BrandIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Masuk (Login)",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 0) BrandIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                viewModel.authTab.value = 1
                                viewModel.authErrorMessage.value = null
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent
                                )
                                .testTag("auth_tab_register")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 1) BrandIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daftar (Registrasi)",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == 1) BrandIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Error / Success Feedback Banners
            item {
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = BrandRose.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandRose.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("auth_error_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = BrandRose, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = BrandRose,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = successMessage != null) {
                    Surface(
                        color = BrandEmerald.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("auth_success_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = successMessage ?: "",
                                color = BrandEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Forms
            item {
                if (selectedTab == 0) {
                    LoginForm(
                        isLoading = isLoading,
                        onLogin = { identifier, password ->
                            viewModel.login(identifier, password)
                        },
                        onSwitchToRegister = {
                            viewModel.authTab.value = 1
                            viewModel.authErrorMessage.value = null
                        },
                        onQuickLogin = { ident, pass ->
                            viewModel.login(ident, pass)
                        },
                        onContinueAsGuest = {
                            viewModel.switchRole(com.example.data.model.UserRole.GUEST)
                            viewModel.navigateTo(AppScreen.HOME)
                        }
                    )
                } else {
                    RegisterForm(
                        isLoading = isLoading,
                        onRegister = { name, username, email, phone, password ->
                            viewModel.register(name, username, email, phone, password)
                        },
                        onSwitchToLogin = {
                            viewModel.authTab.value = 0
                            viewModel.authErrorMessage.value = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    isLoading: Boolean,
    onLogin: (identifier: String, pass: String) -> Unit,
    onSwitchToRegister: () -> Unit,
    onQuickLogin: (identifier: String, pass: String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Masuk Akun Pengguna",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            // Identifier field
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text("Username atau Email") },
                placeholder = { Text("Contoh: budisantoso atau budi@gmail.com") },
                leadingIcon = {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = BrandIndigo)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_identifier_input")
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password / Kata Sandi") },
                placeholder = { Text("Masukkan password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandIndigo)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (identifier.isNotBlank() && password.isNotBlank()) {
                        onLogin(identifier, password)
                    }
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Submit Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLogin(identifier, password)
                },
                enabled = identifier.isNotBlank() && password.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("login_submit_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Memproses Masuk...")
                } else {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Masuk ke Akun", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Quick Demo Accounts
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "⚡ Akses Cepat Akun Demo (1-Tap):",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { onQuickLogin("budisantoso", "password123") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_chip_member")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Member", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Budi", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedButton(
                    onClick = { onQuickLogin("ahmadstaff", "password123") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_chip_staff")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Staff", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Ahmad", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedButton(
                    onClick = { onQuickLogin("sitiadmin", "password123") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_chip_admin")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Admin", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Siti", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedButton(
                    onClick = { onQuickLogin("hendrasuper", "password123") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_chip_superadmin")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Super", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandRose)
                        Text("Hendra", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Switch to Register
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum punya akun?",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onSwitchToRegister,
                    modifier = Modifier.testTag("goto_register_link")
                ) {
                    Text("Daftar Sekarang", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandIndigo)
                }
            }

            // Continue as guest
            OutlinedButton(
                onClick = onContinueAsGuest,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("guest_mode_btn")
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lanjutkan Menjelajah sebagai Tamu", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RegisterForm(
    isLoading: Boolean,
    onRegister: (name: String, username: String, email: String, phone: String, pass: String) -> Unit,
    onSwitchToLogin: () -> Unit
) {
    // Exact requested fields: nama lengkap, username, email, nomor hp, password
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val passwordsMatch = password.isNotEmpty() && password == confirmPassword
    val isFormValid = fullName.isNotBlank() &&
            username.isNotBlank() &&
            email.isNotBlank() &&
            phone.isNotBlank() &&
            password.length >= 6 &&
            passwordsMatch

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Formulir Registrasi Akun",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Lengkapi 5 data di bawah ini untuk membuat akun baru YFSTORE:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 1. Nama Lengkap
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    localError = null
                },
                label = { Text("1. Nama Lengkap") },
                placeholder = { Text("Contoh: Firdaus Pratama") },
                leadingIcon = {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = BrandIndigo)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = FocusDirection.Down.let { ImeAction.Next }),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_name_input")
            )

            // 2. Username
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it.lowercase().filter { c -> c.isLetterOrDigit() || c == '_' || c == '.' }
                    localError = null
                },
                label = { Text("2. Username") },
                placeholder = { Text("Contoh: firdaus26") },
                leadingIcon = {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = BrandIndigo)
                },
                supportingText = {
                    Text("Gunakan huruf kecil, angka, atau underscore. Min. 3 karakter", fontSize = 10.sp)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_username_input")
            )

            // 3. Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = null
                },
                label = { Text("3. Alamat Email") },
                placeholder = { Text("Contoh: firdaus@gmail.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = BrandIndigo)
                },
                supportingText = {
                    Text("Untuk pengiriman file digital & tautan lisensi", fontSize = 10.sp)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_email_input")
            )

            // 4. Nomor HP
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it.filter { c -> c.isDigit() || c == '+' || c == '-' }
                    localError = null
                },
                label = { Text("4. Nomor HP / WhatsApp") },
                placeholder = { Text("Contoh: 081234567890") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = BrandIndigo)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_phone_input")
            )

            // 5. Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    localError = null
                },
                label = { Text("5. Password") },
                placeholder = { Text("Minimal 6 karakter") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandIndigo)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                supportingText = {
                    Text(
                        text = if (password.isNotEmpty() && password.length < 6) "Password terlalu pendek (min 6 karakter)" else "Minimal 6 karakter kombinasi",
                        color = if (password.isNotEmpty() && password.length < 6) BrandRose else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_password_input")
            )

            // 6. Konfirmasi Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    localError = null
                },
                label = { Text("Konfirmasi Password") },
                placeholder = { Text("Ketik ulang password") },
                leadingIcon = {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = BrandIndigo)
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Sembunyikan" else "Tampilkan"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                supportingText = {
                    if (confirmPassword.isNotEmpty()) {
                        if (passwordsMatch) {
                            Text("✓ Password cocok", color = BrandEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("✗ Password tidak sama", color = BrandRose, fontSize = 10.sp)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_confirm_password_input")
            )

            if (localError != null) {
                Text(
                    text = localError ?: "",
                    color = BrandRose,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Submit Register
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (password != confirmPassword) {
                        localError = "Konfirmasi kata sandi tidak cocok"
                        return@Button
                    }
                    if (password.length < 6) {
                        localError = "Password minimal 6 karakter"
                        return@Button
                    }
                    onRegister(fullName, username, email, phone, password)
                },
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("register_submit_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Membuat Akun...")
                } else {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Daftar Akun Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Switch to Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun?",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onSwitchToLogin,
                    modifier = Modifier.testTag("goto_login_link")
                ) {
                    Text("Masuk Sekarang", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandIndigo)
                }
            }
        }
    }
}
