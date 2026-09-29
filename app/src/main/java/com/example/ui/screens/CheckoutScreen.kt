package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: FoodOrderViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (OrderEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsState()
    val isSubmitting by viewModel.isSubmittingOrder.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedDistanceKm by remember { mutableIntStateOf(1) }
    val selectedPaymentMethod = "Cash on Delivery"

    val foodSubtotal = cartItems.sumOf { it.price * it.quantity }
    val deliveryFee = selectedDistanceKm * 10
    val grandTotal = foodSubtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout Order",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CharcoalDark)
            )
        },
        containerColor = CharcoalDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Customer Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customer Details",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Your Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FlameOrange) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = CharcoalSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = FlameOrange,
                            unfocusedLabelColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FlameOrange) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = CharcoalSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = FlameOrange,
                            unfocusedLabelColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Delivery Address") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = FlameOrange) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = CharcoalSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = FlameOrange,
                            unfocusedLabelColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery Distance Selector Card (1KM = 10, 2KM = 20, 3KM = 30)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Delivery Distance & Charge",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Salman Food Delivery: ₹10 per Kilometer",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3, 5).forEach { km ->
                            val isSelected = selectedDistanceKm == km
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) FlameOrange else CharcoalSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDistanceKm = km }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "$km KM",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Text(
                                        text = "₹${km * 10}",
                                        fontSize = 11.sp,
                                        color = if (isSelected) GoldenYellow else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bill Breakdown Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bill Summary",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Food Subtotal", color = TextSecondary, fontSize = 14.sp)
                        Text(text = "₹$foodSubtotal", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Delivery Fee ($selectedDistanceKm KM)", color = TextSecondary, fontSize = 14.sp)
                        Text(text = "₹$deliveryFee", color = GoldenYellow, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CharcoalSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "To Pay", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(text = "₹$grandTotal", color = FlameOrange, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Place Order Button
            Button(
                onClick = {
                    if (customerName.isBlank() || customerPhone.isBlank() || deliveryAddress.isBlank()) {
                        Toast.makeText(context, "Please fill in all details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.submitOrder(
                        customerName = customerName,
                        customerPhone = customerPhone,
                        deliveryAddress = deliveryAddress,
                        paymentMethod = selectedPaymentMethod,
                        deliveryFee = deliveryFee,
                        onSuccess = { order, receipt ->
                            sendWhatsAppOrder(context, order, deliveryFee, grandTotal)
                            onOrderSuccess(order, receipt)
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !isSubmitting && cartItems.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Place Order • ₹$grandTotal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun sendWhatsAppOrder(context: Context, order: OrderEntity, deliveryFee: Int, grandTotal: Int) {
    val message = StringBuilder().apply {
        appendLine("🍔 *NEW ORDER - SALMAN FOOD*")
        appendLine("━━━━━━━━━━━━━━━━━━━")
        appendLine("👤 *Customer:* ${order.customerName}")
        appendLine("📞 *Phone:* ${order.customerPhone}")
        appendLine("📍 *Address:* ${order.deliveryAddress}")
        appendLine("━━━━━━━━━━━━━━━━━━━")
        appendLine("📋 *Items Ordered:*")
        appendLine(order.itemsSummary)
        appendLine("━━━━━━━━━━━━━━━━━━━")
        appendLine("🚚 *Delivery Charge:* ₹$deliveryFee")
        appendLine("💰 *Grand Total:* ₹$grandTotal")
        appendLine("━━━━━━━━━━━━━━━━━━━")
        appendLine("Thank you for ordering with Salman Food!")
    }.toString()

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}")
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) { }
}
