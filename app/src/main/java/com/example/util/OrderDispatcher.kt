package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.local.OrderEntity
import com.example.data.model.CartItem
import com.example.data.model.CustomerInfo
import com.example.data.model.DeliveryMode
import com.example.data.model.PaymentMode
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderDispatcher {
    const val SALMAN_PHONE = "+917033680705"
    const val SALMAN_PHONE_RAW = "917033680705"
    const val UPI_ID = "7033680705@ybl"
    const val BUSINESS_NAME = "Salman Food"

    fun generateOrderNumber(): String {
        val randomSuffix = (1000..9999).random()
        return "SF-2026-$randomSuffix"
    }

    fun buildReceiptText(
        orderNumber: String,
        timestamp: Long,
        items: List<CartItem>,
        subtotal: Int,
        deliveryMode: DeliveryMode,
        deliveryFee: Int,
        grandTotal: Int,
        customerInfo: CustomerInfo,
        paymentMode: PaymentMode
    ): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = sdf.format(Date(timestamp))

        val itemsListStr = items.mapIndexed { index, item ->
            "${index + 1}. *${item.name}* (${item.portionName}) x ${item.quantity} = ₹${item.totalPrice}"
        }.joinToString("\n")

        val addressSection = if (deliveryMode == DeliveryMode.HOME_DELIVERY) {
            "📍 *Delivery Address:*\n${customerInfo.address}" +
                    if (customerInfo.landmark.isNotBlank()) "\n*Landmark:* ${customerInfo.landmark}" else ""
        } else {
            "📍 *Pickup Counter:* Salman Food (Self-Pickup / Takeaway)"
        }

        val notesSection = if (customerInfo.notes.isNotBlank()) {
            "\n📝 *Special Request:* ${customerInfo.notes}"
        } else ""

        return """
🔥 *NEW ORDER - SALMAN FOOD (सलमान फ़ूड)* 🔥
━━━━━━━━━━━━━━━━━━━━━
📋 *Order ID:* #$orderNumber
🕒 *Time:* $dateStr
━━━━━━━━━━━━━━━━━━━━━
👤 *Customer Details:*
• *Name:* ${customerInfo.name}
• *Phone:* ${customerInfo.phone}
• *Mode:* ${deliveryMode.displayName} (+₹$deliveryFee)
$addressSection
━━━━━━━━━━━━━━━━━━━━━
🍱 *Ordered Items:*
$itemsListStr
━━━━━━━━━━━━━━━━━━━━━
💵 *Bill Details:*
• Item Subtotal: ₹$subtotal
• Delivery Charge: ₹$deliveryFee
• *Grand Total: ₹$grandTotal*
━━━━━━━━━━━━━━━━━━━━━
💳 *Payment Mode:* ${paymentMode.displayName}$notesSection
━━━━━━━━━━━━━━━━━━━━━
_Sent via Salman Food Mobile App_
""".trimIndent()
    }

    fun sendOrderToWhatsApp(context: Context, receiptText: String): Boolean {
        return try {
            val encodedMessage = URLEncoder.encode(receiptText, "UTF-8")
            val whatsappUri = Uri.parse("https://api.whatsapp.com/send?phone=$SALMAN_PHONE_RAW&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, whatsappUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                // Fallback to generic share intent
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, receiptText)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Send Order via").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                true
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not open WhatsApp: ${ex.message}", Toast.LENGTH_LONG).show()
                false
            }
        }
    }

    fun launchUpiPayment(
        context: Context,
        amount: Int,
        orderNumber: String,
        onUpiAppNotFound: () -> Unit = {}
    ) {
        val upiUri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", UPI_ID)
            .appendQueryParameter("pn", BUSINESS_NAME)
            .appendQueryParameter("mc", "")
            .appendQueryParameter("tid", orderNumber)
            .appendQueryParameter("tr", orderNumber)
            .appendQueryParameter("tn", "Order $orderNumber Salman Food")
            .appendQueryParameter("am", amount.toDouble().toString())
            .appendQueryParameter("cu", "INR")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, upiUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Pay via UPI App").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            copyUpiIdToClipboard(context)
            onUpiAppNotFound()
        }
    }

    fun launchPhonePeDirect(
        context: Context,
        amount: Int,
        orderNumber: String,
        onFallback: () -> Unit
    ) {
        val upiUri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", UPI_ID)
            .appendQueryParameter("pn", BUSINESS_NAME)
            .appendQueryParameter("tid", orderNumber)
            .appendQueryParameter("tr", orderNumber)
            .appendQueryParameter("tn", "Order $orderNumber Salman Food")
            .appendQueryParameter("am", amount.toDouble().toString())
            .appendQueryParameter("cu", "INR")
            .build()

        val phonePeIntent = Intent(Intent.ACTION_VIEW, upiUri).apply {
            `package` = "com.phonepe.app"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(phonePeIntent)
        } catch (e: Exception) {
            // PhonePe not installed or failed, fallback to generic UPI or copy
            launchUpiPayment(context, amount, orderNumber, onFallback)
        }
    }

    fun copyUpiIdToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Salman Food UPI ID", UPI_ID)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "UPI ID copied: $UPI_ID", Toast.LENGTH_SHORT).show()
    }

    fun callSalmanFood(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$SALMAN_PHONE")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Call +91 7033680705", Toast.LENGTH_SHORT).show()
        }
    }
}
