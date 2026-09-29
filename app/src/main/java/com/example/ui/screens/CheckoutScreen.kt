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
import androidx.compose.material.icons.filled.DirectionsBike
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
import androidx.compose.runtime.mutableIntStateOf
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

    // Distance selection: 1 KM = ₹10, 2 KM = ₹20, 3 KM = ₹30
    var selectedDistanceKm by remember { mutableIntStateOf(1) }
    val deliveryFee = if (deliveryMode == DeliveryMode.HOME_DELIVERY) selectedDistanceKm * 10 else 0
    val finalPayableAmount = grandTotal + deliveryFee

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
                                Icon(Icons.Default.Person, contentDescription = null, tint = FlameOrange)
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
                                Icon(Icons.Default.Phone, contentDescription = null, tint = FlameOrange)
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

            // Delivery Address & Distance (for Home Delivery)
            if (deliveryMode == DeliveryMode.HOME_DELIVERY) {
                item {
                    Text(
                        text = "Delivery Address & Distance",
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
                                placeholder = { Text("House/Flat No., Street, Area", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Home, contentDescription = null, tint = GoldenYellow)
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
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldenYellow)
                                },
                                singleLine = true,
                                colors = getTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkout_input_landmark")
                            )

                            // Distance Selector (1 km, 2 km, 3 km)
                            Text(
                                text = "Select Distance from Salman Food:",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1 to 10, 2 to 20, 3 to 30).forEach { (km, charge) ->
                                    val isSelected = selectedDistanceKm == km
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedDistanceKm = km },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) FlameOrange.copy(alpha = 0.2f) else CharcoalSurface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) FlameOrange else CharcoalSurfaceVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "$km KM",
                                                color = if (isSelected) FlameOrange else TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "₹$charge Fee",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Special Instructions
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
                            placeholder = { Text("e.g. Extra spicy, less oil, green chutney", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Notes, contentDescription = null, tint = TextSecondary)
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

            // Payment Mode Selection
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
                        PaymentMethodRow(
                            title = "UPI / PhonePe",
                            subtitle = "Pay directly to shop UPI",
                            icon = Icons.Default.ElectricBolt,
                            iconTint = GoldenYellow,
                            badgeText = "Fast & Instant",
                            badgeColor = PhonePePurple,
                            isSelected = paymentMode == PaymentMode.UPI_PHONEPE,
                            onClick = { viewModel.setPaymentMode(PaymentMode.UPI_PHONEPE) },
                            testTag = "pay_method_upi"
                        )

                        PaymentMethodRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash when receiving your food",
                            icon = Icons.Default.AccountBalanceWallet,
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

            // Error Display
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

            // Place Order Button with Dynamic Amount
            item {
                Button(
                    onClick = {
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
                        
                        val finalNoteWithKm = if (deliveryMode == DeliveryMode.HOME_DELIVERY) {
                            "${notes.trim()} | [Distance: ${selectedDistanceKm} KM, Delivery Charge: Rs.$deliveryFee]".trim()
                        } else {
                            notes.trim()
                        }

                        viewModel.updateCustomerInfo(name.trim(), phone.trim(), address.trim(), landmark.trim(), finalNoteWithKm)

                        if (activity != null) {
                            viewModel.placeOrder(activity) { order, receipt ->
                                isSubmitting =
