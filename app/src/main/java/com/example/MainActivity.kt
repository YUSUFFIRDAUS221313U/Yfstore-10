package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.screens.*
import com.example.ui.theme.BrandRose
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MarketplaceViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.userCartItems.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotification()
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        when (currentScreen) {
            AppScreen.PRODUCT_DETAIL -> viewModel.navigateTo(AppScreen.CATALOG)
            AppScreen.AUTH,
            AppScreen.CART_CHECKOUT,
            AppScreen.CATALOG,
            AppScreen.MEMBER_CENTER,
            AppScreen.ADMIN_PANEL,
            AppScreen.FAQ_CONTACT,
            AppScreen.NOTIFICATIONS -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.HOME -> { /* Exit */ }
        }
    }

    val canAccessAdmin = currentUser.role in listOf(UserRole.STAFF, UserRole.ADMIN, UserRole.SUPER_ADMIN)

    val navItems = listOf(
        BottomNavItem(
            screen = AppScreen.HOME,
            label = "Beranda",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            screen = AppScreen.CATALOG,
            label = "Katalog",
            selectedIcon = Icons.Filled.GridView,
            unselectedIcon = Icons.Outlined.GridView
        ),
        BottomNavItem(
            screen = AppScreen.CART_CHECKOUT,
            label = "Keranjang",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            badgeCount = cartItems.size
        ),
        BottomNavItem(
            screen = AppScreen.MEMBER_CENTER,
            label = "Unduhan",
            selectedIcon = Icons.Filled.CloudDownload,
            unselectedIcon = Icons.Outlined.CloudDownload
        ),
        BottomNavItem(
            screen = AppScreen.ADMIN_PANEL,
            label = if (currentUser.role == UserRole.STAFF) "Staff" else "Admin",
            selectedIcon = Icons.Filled.AdminPanelSettings,
            unselectedIcon = Icons.Outlined.AdminPanelSettings
        ),
        BottomNavItem(
            screen = AppScreen.FAQ_CONTACT,
            label = "Bantuan",
            selectedIcon = Icons.Filled.Help,
            unselectedIcon = Icons.Outlined.HelpOutline
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            // Hide bottom bar on product detail, cart, and auth for distraction-free view
            if (currentScreen != AppScreen.PRODUCT_DETAIL && currentScreen != AppScreen.CART_CHECKOUT && currentScreen != AppScreen.AUTH) {
                NavigationBar(
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (item.screen == AppScreen.ADMIN_PANEL && !canAccessAdmin) {
                                    viewModel.showNotification("Ganti Role ke Staff, Admin, atau Super Admin di atas untuk mengakses panel ini.")
                                } else {
                                    viewModel.navigateTo(item.screen)
                                }
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.badgeCount > 0) {
                                            Badge(containerColor = BrandRose) {
                                                Text("${item.badgeCount}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                }
                            },
                            label = { Text(item.label, fontSize = 10.sp) },
                            modifier = Modifier.testTag("nav_item_${item.screen.name.lowercase()}")
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.CATALOG -> CatalogScreen(viewModel = viewModel)
                AppScreen.PRODUCT_DETAIL -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(viewModel = viewModel, product = product)
                    } ?: run {
                        CatalogScreen(viewModel = viewModel)
                    }
                }
                AppScreen.CART_CHECKOUT -> CartCheckoutScreen(viewModel = viewModel)
                AppScreen.MEMBER_CENTER -> MemberCenterScreen(viewModel = viewModel)
                AppScreen.ADMIN_PANEL -> {
                    if (canAccessAdmin) {
                        AdminPanelScreen(viewModel = viewModel)
                    } else {
                        HomeScreen(viewModel = viewModel)
                    }
                }
                AppScreen.FAQ_CONTACT -> FaqContactScreen(viewModel = viewModel)
                AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                AppScreen.NOTIFICATIONS -> NotificationCenterScreen(viewModel = viewModel)
            }
        }
    }
}
