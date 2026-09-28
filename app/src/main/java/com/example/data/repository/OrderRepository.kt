package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.local.OrderEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun insertOrder(order: OrderEntity): Long = orderDao.insertOrder(order)

    fun getUserProfile(): Flow<UserProfileEntity?> = orderDao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfileEntity) = orderDao.saveUserProfile(profile)
}
