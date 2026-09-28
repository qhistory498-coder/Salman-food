package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.StickyBannerAd
import com.example.ui.components.CartSummaryBottomBar
import com.example.ui.components.CategoryChips
import com.example.ui.components.HeroBanner
import com.example.ui.components.MenuItemCard
import com.example.ui.components.StreetFoodTopBar
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodOrderViewModel

@Composable
fun MenuScreen(
    viewModel: FoodOrderViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val menuItems by viewModel.filteredMenuItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val vegOnly by viewModel.vegOnlyFilter.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        // App Top Bar
        StreetFoodTopBar(
            title = "Salman Food",
            subtitle = "सलमान फ़ूड • Street Food King",
            canNavigateBack = false,
            cartItemCount = cartCount,
            onCartClick = onNavigateToCart,
            onHistoryClick = onNavigateToHistory
        )

        // Main content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("menu_items_list"),
                contentPadding = PaddingValues(bottom = if (cartCount > 0) 80.dp else 16.dp)
            ) {
                // Hero Banner
                item {
                    HeroBanner()
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = "Search Chowmein, Rolls, Momos, Starters...",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = FlameOrange
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CharcoalSurface,
                            unfocusedContainerColor = CharcoalSurface,
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = CharcoalSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = FlameOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("menu_search_bar")
                    )
                }

                // Category & Veg Filter Chips
                item {
                    CategoryChips(
                        selectedCategory = selectedCategory,
                        vegOnly = vegOnly,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        onToggleVegOnly = { viewModel.toggleVegOnly() }
                    )
                }

                // Menu items or empty state
                if (menuItems.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = CharcoalSurfaceVariant,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Fastfood,
                                        contentDescription = null,
                                        tint = GoldenYellow,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No items found",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Try changing your search term or filter category.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(menuItems, key = { it.id }) { item ->
                        MenuItemCard(
                            menuItem = item,
                            getQuantity = { portionName ->
                                viewModel.getCartQuantity(item.id, portionName)
                            },
                            onAddToCart = { portion ->
                                viewModel.addToCart(item, portion)
                            },
                            onRemoveFromCart = { portionName ->
                                viewModel.removeFromCart(item.id, portionName)
                            }
                        )
                    }
                }
            }

            // Floating Cart summary at bottom of content area
            CartSummaryBottomBar(
                itemCount = cartCount,
                subtotal = subtotal,
                onViewCartClick = onNavigateToCart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
            )
        }

        // Sticky AdMob Banner at bottom
        StickyBannerAd()
    }
}
