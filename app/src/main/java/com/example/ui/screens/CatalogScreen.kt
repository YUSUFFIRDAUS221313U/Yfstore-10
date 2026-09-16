package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CATEGORIES
import com.example.ui.AppScreen
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.ProductCard
import com.example.ui.theme.BrandIndigo

enum class SortOption(val title: String) {
    POPULAR("Terpopuler"),
    NEWEST("Terbaru"),
    PRICE_LOW("Harga Terendah"),
    PRICE_HIGH("Harga Tertinggi"),
    RATING("Rating Tertinggi")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val allActiveProducts by viewModel.activeProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val wishlist by viewModel.userWishlist.collectAsState()
    val wishlistedIds = remember(wishlist) { wishlist.map { it.productId }.toSet() }

    var selectedSort by remember { mutableStateOf(SortOption.POPULAR) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    // Filter & sort logic
    val filteredProducts = remember(allActiveProducts, searchQuery, selectedCategory, selectedSort) {
        allActiveProducts
            .filter { product ->
                val matchesCat = (selectedCategory == "all" || selectedCategory == null || product.categoryId == selectedCategory)
                val matchesQuery = searchQuery.isBlank() ||
                        product.title.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)
                matchesCat && matchesQuery
            }
            .let { list ->
                when (selectedSort) {
                    SortOption.POPULAR -> list.sortedByDescending { it.salesCount }
                    SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
                    SortOption.PRICE_LOW -> list.sortedBy { it.price }
                    SortOption.PRICE_HIGH -> list.sortedByDescending { it.price }
                    SortOption.RATING -> list.sortedByDescending { it.rating }
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_screen")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Katalog Kebutuhan Digital",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // Sort Button
                    Box {
                        FilledTonalButton(
                            onClick = { sortMenuExpanded = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("sort_filter_btn")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(selectedSort.title, fontSize = 11.sp)
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title) },
                                    onClick = {
                                        selectedSort = option
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Cari software, ebook, kursus, UI kit...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == "all" || selectedCategory == null,
                        onClick = { viewModel.selectedCategory.value = "all" },
                        label = { Text("Semua") },
                        modifier = Modifier.testTag("cat_filter_all")
                    )

                    CATEGORIES.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat.id,
                            onClick = { viewModel.selectedCategory.value = cat.id },
                            label = { Text(cat.name) },
                            modifier = Modifier.testTag("cat_filter_${cat.id}")
                        )
                    }
                }
            }
        }

        // Product Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Menampilkan ${filteredProducts.size} produk",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Products Grid
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tidak ada produk digital yang sesuai",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Coba ubah kata kunci pencarian atau pilih kategori lain.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = pair[0],
                                onClick = { viewModel.openProductDetail(pair[0]) },
                                onAddToCart = { viewModel.addToCart(pair[0]) },
                                isWishlisted = wishlistedIds.contains(pair[0].id),
                                onToggleWishlist = { viewModel.toggleWishlist(pair[0]) }
                            )
                        }
                        if (pair.size > 1) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = pair[1],
                                    onClick = { viewModel.openProductDetail(pair[1]) },
                                    onAddToCart = { viewModel.addToCart(pair[1]) },
                                    isWishlisted = wishlistedIds.contains(pair[1].id),
                                    onToggleWishlist = { viewModel.toggleWishlist(pair[1]) }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
