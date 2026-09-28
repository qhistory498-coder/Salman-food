package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.StickyBannerAd
import com.example.data.local.OrderEntity
import com.example.data.model.DeliveryMode
import com.example.data.model.PaymentMode
import com.example.ui.components.StreetFoodTopBar
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.PhonePePurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.FoodOrderViewModel
import com.example.util.OrderDispatcher

@Composable
fun CheckoutScreen(
    viewModel: FoodOrderViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (OrderEntity, String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val deliveryMode by viewModel.deliveryMode.collectAsState()
    val savedCustomerInfo by viewModel.customerInfo.collectAsState()
    val paymentMode by viewModel.paymentMode.collectAsState()
    val grandTotal by viewModel.grandTotal.collectAsState()

    var name by remember(savedCustomerInfo.name) { mutableStateOf(savedCustomerInfo.name) }
    var phone by remember(savedCustomerInfo.phone) { mutableStateOf(savedCustomerInfo.phone) }
    var address by remember(savedCustomerInfo.address) { mutableStateOf(savedCustomerInfo.address) }
    var landmark by remember(savedCustomerInfo.landmark) { mutableStateOf(savedCustomerInfo.landmark) }
    var notes by remember(savedCustomerInfo.notes) { mutableStateOf(savedCustomerInfo.notes) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        StreetFoodTopBar(
            title = "Checkout",
            subtitle = "Salman Food • Confirm Details",
            canNavigateBack = true,
            onBackClick = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Details Header
            item {
                Text(
                    text = "Customer Information",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name & Phone Input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    border = BorderStroke(1.dp, CharcoalSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                errorMessage = null
                            },
                            label = { Text("Your Full Name *", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = FlameOrange)
                            },
                            singleLine = true,
                            colors = getTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_input_name")
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                    phone = it
                                    errorMessage = null
                                }
                            },
                            label = { Text("Phone Number (10 Digits) *", color = TextSecondary) },
                            placeholder = { Text("e.g. 9876543210", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Phone, contentDescription = null, tint = FlameOrange)
                            },
                            prefix = { Text("+91 ", color = TextPrimary, fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = getTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_input_phone")
                        )
                    }
                }
            }

            // Delivery Address (for Home Delivery)
            if (deliveryMode == DeliveryMode.HOME_DELIVERY) {
                item {
                    Text(
                        text = "Delivery Address (within 3 KM)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                        border = BorderStroke(1.dp, CharcoalSurfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = address,
                                onValueChange = {
                                    address = it
                                    errorMessage = null
                                },
                                label = { Text("Complete Street Address *", color = TextSecondary) },
                                placeholder = { Text("House/Flat No., Building name, Street", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Home, contentDescription = null, tint = GoldenYellow)
                                },
                                minLines = 2,
                                maxLines = 4,
                                colors = getTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkout_input_address")
                            )

                            OutlinedTextField(
                                value = landmark,
                                onValueChange = { landmark = it },
                                label = { Text("Nearby Landmark (Optional)", color = TextSecondary) },
                                placeholder = { Text("e.g. Near Mosque / Petrol Pump", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GoldenYellow)
                                },
                                singleLine = true,
                                colors = getTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkout_input_landmark")
                            )
                        }
                    }
                }
            }

            // Cooking / Special Request Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    border = BorderStroke(1.dp, CharcoalSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Cooking / Delivery Notes (Optional)", color = TextSecondary) },
                            placeholder = { Text("e.g. Extra spicy, green chutney, less oil", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Filled.Notes, contentDescription = null, tint = TextSecondary)
                            },
                            singleLine = true,
                            colors = getTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_input_notes")
                        )
                    }
                }
            }

            // Payment Options
            item {
                Text(
                    text = "Payment Method",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    border = BorderStroke(1.dp, CharcoalSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // UPI Option
                        PaymentMethodRow(
                            title = "UPI / PhonePe",
                            subtitle = "Pay directly to 7033680705@ybl",
                            icon = Icons.Filled.ElectricBolt,
                            iconTint = GoldenYellow,
                            badgeText = "Fast & Instant",
                            badgeColor = PhonePePurple,
                            isSelected = paymentMode == PaymentMode.UPI_PHONEPE,
                            onClick = { viewModel.setPaymentMode(PaymentMode.UPI_PHONEPE) },
                            testTag = "pay_method_upi"
                        )

                        // Cash on Delivery Option
                        PaymentMethodRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash when receiving your food",
                            icon = Icons.Filled.AccountBalanceWallet,
                            iconTint = FlameOrange,
                            badgeText = "Cash",
                            badgeColor = FlameOrange,
                            isSelected = paymentMode == PaymentMode.CASH_ON_DELIVERY,
                            onClick = { viewModel.setPaymentMode(PaymentMode.CASH_ON_DELIVERY) },
                            testTag = "pay_method_cod"
                        )
                    }
                }
            }

            // Error display
            if (errorMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NonVegRed.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, NonVegRed.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = NonVegRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Place Order & Dispatch Button
            item {
                Button(
                    onClick = {
                        // Form validations
                        if (name.trim().isBlank()) {
                            errorMessage = "Please enter your name"
                            return@Button
                        }
                        if (phone.trim().length != 10) {
                            errorMessage = "Please enter a valid 10-digit mobile number"
                            return@Button
                        }
                        if (deliveryMode == DeliveryMode.HOME_DELIVERY && address.trim().isBlank()) {
                            errorMessage = "Please provide your delivery address"
                            return@Button
                        }

                        errorMessage = null
                        isSubmitting = true
                        viewModel.updateCustomerInfo(name.trim(), phone.trim(), address.trim(), landmark.trim(), notes.trim())

                        if (activity != null) {
                            viewModel.placeOrder(activity) { order, receipt ->
                                isSubmitting = false
                                // Automatically open WhatsApp with order receipt!
                                OrderDispatcher.sendOrderToWhatsApp(context, receipt)
                                onOrderSuccess(order, receipt)
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("place_order_whatsapp_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSubmitting) "Placing Order..." else "Place Order & Send to WhatsApp (₹$grandTotal)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Sticky AdMob Banner at bottom
        StickyBannerAd()
    }
}

@Composable
private fun PaymentMethodRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    badgeText: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
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

            Surface(
                shape = CircleShape,
                color = CharcoalDark,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CharcoalSurface,
    unfocusedContainerColor = CharcoalSurface,
    focusedBorderColor = FlameOrange,
    unfocusedBorderColor = CharcoalSurfaceVariant,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = FlameOrange
)
