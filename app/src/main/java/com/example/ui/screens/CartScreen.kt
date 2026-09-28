package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.StickyBannerAd
import com.example.data.model.CartItem
import com.example.data.model.DeliveryMode
import com.example.data.model.PortionOption
import com.example.data.repository.MenuRepository
import com.example.ui.components.StreetFoodTopBar
import com.example.ui.components.VegBadge
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameOrangeDark
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodOrderViewModel

@Composable
fun CartScreen(
    viewModel: FoodOrderViewModel,
    onNavigateBack: () -> Unit,
    onProceedToCheckout: () -> Unit
) {
    val cartMap by viewModel.cartItems.collectAsState()
    val cartItems = cartMap.values.toList()
    val deliveryMode by viewModel.deliveryMode.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val grandTotal by viewModel.grandTotal.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        StreetFoodTopBar(
            title = "My Cart",
            subtitle = "${cartItems.sumOf { it.quantity }} items in order",
            canNavigateBack = true,
            onBackClick = onNavigateBack,
            cartItemCount = cartItems.sumOf { it.quantity }
        )

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CharcoalSurfaceVariant,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = null,
                                tint = FlameOrange,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Cart is Empty",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Delicious Chowmein, spicy Rolls and hot Momos are waiting!",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlameOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("explore_menu_btn")
                    ) {
                        Text("Explore Salman Food Menu", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cart Items Section
                item {
                    Text(
                        text = "Ordered Items",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(cartItems, key = { it.cartKey }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            val menuItem = MenuRepository.items.firstOrNull { it.id == item.menuItemId }
                            if (menuItem != null) {
                                viewModel.addToCart(menuItem, PortionOption(item.portionName, item.unitPrice))
                            }
                        },
                        onDecrease = {
                            viewModel.removeFromCart(item.menuItemId, item.portionName)
                        }
                    )
                }

                // Add more items button
                item {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameOrange)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "+ Add More Street Food Items", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Delivery Options Section
                item {
                    DeliveryOptionsCard(
                        selectedMode = deliveryMode,
                        onModeSelected = { viewModel.setDeliveryMode(it) }
                    )
                }

                // Bill Summary Section
                item {
                    BillSummaryCard(
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        grandTotal = grandTotal,
                        deliveryMode = deliveryMode
                    )
                }

                // Checkout button
                item {
                    Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("proceed_checkout_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlameOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "₹$grandTotal",
                                    color = GoldenYellow,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "TOTAL TO PAY",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Proceed to Checkout",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sticky AdMob Banner at bottom
        StickyBannerAd()
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        border = BorderStroke(1.dp, CharcoalSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VegBadge(dietType = item.dietType)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${item.portionName} • ₹${item.unitPrice} each",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FlameOrange.copy(alpha = 0.15f))
                        .border(1.dp, FlameOrange, RoundedCornerShape(8.dp))
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Decrease",
                            tint = FlameOrange,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase",
                            tint = FlameOrange,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "₹${item.totalPrice}",
                    color = GoldenYellow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    modifier = Modifier.width(50.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun DeliveryOptionsCard(
    selectedMode: DeliveryMode,
    onModeSelected: (DeliveryMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        border = BorderStroke(1.dp, CharcoalSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Moped,
                    contentDescription = null,
                    tint = GoldenYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Choose Delivery Mode",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Home Delivery Option
            DeliveryOptionRow(
                mode = DeliveryMode.HOME_DELIVERY,
                isSelected = selectedMode == DeliveryMode.HOME_DELIVERY,
                onClick = { onModeSelected(DeliveryMode.HOME_DELIVERY) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Takeaway Option
            DeliveryOptionRow(
                mode = DeliveryMode.TAKEAWAY,
                isSelected = selectedMode == DeliveryMode.TAKEAWAY,
                onClick = { onModeSelected(DeliveryMode.TAKEAWAY) }
            )
        }
    }
}

@Composable
private fun DeliveryOptionRow(
    mode: DeliveryMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) FlameOrange.copy(alpha = 0.12f) else CharcoalSurface,
        border = BorderStroke(
            1.dp,
            if (isSelected) FlameOrange else CharcoalSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = FlameOrange,
                    unselectedColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mode.displayName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (mode.fee > 0) GoldenYellow.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (mode.fee > 0) "+₹${mode.fee}" else "FREE",
                            color = if (mode.fee > 0) GoldenYellow else SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = mode.description,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun BillSummaryCard(
    subtotal: Int,
    deliveryFee: Int,
    grandTotal: Int,
    deliveryMode: DeliveryMode
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        border = BorderStroke(1.dp, CharcoalSurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "Bill Details",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            BillRow(label = "Item Subtotal", amount = "₹$subtotal")

            Spacer(modifier = Modifier.height(6.dp))

            BillRow(
                label = if (deliveryMode == DeliveryMode.HOME_DELIVERY) "Delivery Fee (Within 3 KM)" else "Takeaway Fee",
                amount = if (deliveryFee > 0) "₹$deliveryFee" else "FREE",
                amountColor = if (deliveryFee > 0) TextPrimary else SuccessGreen
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CharcoalSurfaceVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To Pay",
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "₹$grandTotal",
                    color = GoldenYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp
                )
            }
        }
    }
}

@Composable
private fun BillRow(
    label: String,
    amount: String,
    amountColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(text = amount, color = amountColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
