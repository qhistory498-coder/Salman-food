package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryChips
import com.example.ui.components.HeroBanner
import com.example.ui.components.MenuItemCard
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodOrderViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(
    viewModel: FoodOrderViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val menuItems by viewModel.filteredMenuItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val vegOnly by viewModel.vegOnlyFilter.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()

    // Admin Stock Control State (Key: Item Name, Value: IsInStock)
    val prefs = remember { context.getSharedPreferences("SalmanFoodAdminPrefs", Context.MODE_PRIVATE) }
    val stockMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            menuItems.forEach { item ->
                this[item.name] = prefs.getBoolean("stock_${item.name}", true)
            }
        }
    }

    var showPinDialog by remember { mutableStateOf(false) }
    var showAdminDashboard by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = CharcoalDark,
        bottomBar = {
            if (cartCount > 0 && !showAdminDashboard) {
                Surface(
                    color = CharcoalCard,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "$cartCount Items in Cart", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "₹$subtotal", color = FlameOrange, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        Button(
                            onClick = onNavigateToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "View Cart", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // VIP Top Bar with Secret Long-Press Admin Access
            Surface(
                color = CharcoalDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.combinedClickable(
                            onClick = {},
                            onLongClick = {
                                showPinDialog = true
                            }
                        )
                    ) {
                        Text(
                            text = "SALMAN FOOD VIP",
                            color = GoldenYellow,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "🔥 Hot, Fresh & Fast Delivery (Hold for Admin)",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    if (showAdminDashboard) {
                        IconButton(onClick = { showAdminDashboard = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Admin", tint = FlameOrange)
                        }
                    }
                }
            }

            if (showAdminDashboard) {
                // Admin Dashboard UI: Out of Stock Toggles
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = FlameOrange)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Restaurant Stock Control",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            TextButton(onClick = { showAdminDashboard = false }) {
                                Text("Done", color = GoldenYellow, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Switch off dishes that are currently finished today.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(menuItems, key = { it.id }) { item ->
                        val isAvailable = stockMap[item.name] ?: true
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = if (isAvailable) "🟢 In Stock (Ready to cook)" else "🔴 Sold Out (Today Finished)",
                                        color = if (isAvailable) GoldenYellow else Color.Red,
                                        fontSize = 11.sp
                                    )
                                }
                                Switch(
                                    checked = isAvailable,
                                    onCheckedChange = { checked ->
                                        stockMap[item.name] = checked
                                        prefs.edit().putBoolean("stock_${item.name}", checked).apply()
                                        Toast.makeText(context, "${item.name}: ${if (checked) "In Stock" else "Sold Out"}", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = FlameOrange,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = CharcoalSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // Regular Customer View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = if (cartCount > 0) 80.dp else 16.dp)
                ) {
                    item {
                        HeroBanner()
                    }

                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search 'Chowmein', 'Chicken Roll', 'Biryani'...", color = TextSecondary, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = FlameOrange) },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CharcoalSurface,
                                unfocusedContainerColor = CharcoalSurface,
                                focusedBorderColor = FlameOrange,
                                unfocusedBorderColor = CharcoalSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    item {
                        CategoryChips(
                            selectedCategory = selectedCategory,
                            vegOnly = vegOnly,
                            onCategorySelected = { viewModel.selectCategory(it) },
                            onToggleVegOnly = { viewModel.toggleVegOnly() }
                        )
                    }

                    if (menuItems.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = CharcoalSurfaceVariant,
                                    modifier = Modifier.size(68.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Fastfood, contentDescription = null, tint = GoldenYellow, modifier = Modifier.size(32.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No dishes found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Try searching for something else in the menu.", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(menuItems, key = { it.id }) { item ->
                            val isAvailable = stockMap[item.name] ?: true
                            Box {
                                MenuItemCard(
                                    menuItem = item,
                                    getQuantity = { portionName -> if (isAvailable) viewModel.getCartQuantity(item.id, portionName) else 0 },
                                    onAddToCart = { portion -> if (isAvailable) viewModel.addToCart(item, portion) },
                                    onRemoveFromCart = { portionName -> if (isAvailable) viewModel.removeFromCart(item.id, portionName) }
                                )
                                if (!isAvailable) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.72f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .matchParentSize()
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Surface(
                                                color = Color.Red,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "SOLD OUT / आज खत्म हो गया",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
        }
    }

    // Secret Admin Password Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pinInput = ""
            },
            title = { Text("Salman Food Admin", color = GoldenYellow, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter secret 4-digit PIN to manage stock:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4) pinInput = it },
                        placeholder = { Text("1234") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = CharcoalSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                                                modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput == "1234") {
                            showPinDialog = false
                            showAdminDashboard = true
                            pinInput = ""
                        } else {
                            Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("Enter Dashboard", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    pinInput = ""
                }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CharcoalCard
        )
    }
}
