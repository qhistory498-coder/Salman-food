package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val itemsSummary: String, // Formatted text of items for receipt
    val subtotal: Int,
    val deliveryFee: Int,
    val grandTotal: Int,
    val deliveryMode: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val paymentMode: String,
    val status: String = "Placed"
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val phone: String,
    val address: String,
    val landmark: String
)
