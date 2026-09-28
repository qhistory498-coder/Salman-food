package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.data.local.OrderEntity
import com.example.data.local.SalmanFoodDatabase
import com.example.data.local.UserProfileEntity
import com.example.data.model.CartItem
import com.example.data.model.CustomerInfo
import com.example.data.model.DeliveryMode
import com.example.data.model.DietType
import com.example.data.model.FoodCategory
import com.example.data.model.MenuItem
import com.example.data.model.PaymentMode
import com.example.data.model.PortionOption
import com.example.data.repository.MenuRepository
import com.example.data.repository.OrderRepository
import com.example.util.OrderDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SalmanFoodDatabase.getInstance(application)
    private val repository = OrderRepository(db.orderDao())

    init {
        // Initialize AdMob MobileAds
        AdManager.initialize(application)

        // Load saved user profile
        viewModelScope.launch {
            repository.getUserProfile().collect { savedProfile ->
                if (savedProfile != null) {
                    _customerInfo.value = CustomerInfo(
                        name = savedProfile.name,
                        phone = savedProfile.phone,
                        address = savedProfile.address,
                        landmark = savedProfile.landmark
                    )
                }
            }
        }
    }

    // Orders History from Room
    val orderHistory: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Menu Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FoodCategory.ALL)
    val selectedCategory: StateFlow<FoodCategory> = _selectedCategory.asStateFlow()

    private val _vegOnlyFilter = MutableStateFlow(false)
    val vegOnlyFilter: StateFlow<Boolean> = _vegOnlyFilter.asStateFlow()

    // Filtered menu items
    val filteredMenuItems: StateFlow<List<MenuItem>> = combine(
        _searchQuery,
        _selectedCategory,
        _vegOnlyFilter
    ) { query, category, vegOnly ->
        MenuRepository.items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.hindiName.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            val matchesCategory = category == FoodCategory.ALL || item.category == category

            val matchesVeg = !vegOnly || item.dietType == DietType.VEG

            matchesQuery && matchesCategory && matchesVeg
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MenuRepository.items)

    // Cart Management
    // Map of cartKey (e.g., "chow_veg_Half") -> CartItem
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems.asStateFlow()

    // Delivery Mode & Customer Info
    private val _deliveryMode = MutableStateFlow(DeliveryMode.HOME_DELIVERY)
    val deliveryMode: StateFlow<DeliveryMode> = _deliveryMode.asStateFlow()

    private val _customerInfo = MutableStateFlow(CustomerInfo())
    val customerInfo: StateFlow<CustomerInfo> = _customerInfo.asStateFlow()

    private val _paymentMode = MutableStateFlow(PaymentMode.UPI_PHONEPE)
    val paymentMode: StateFlow<PaymentMode> = _paymentMode.asStateFlow()

    // Computed totals
    val cartItemCount: StateFlow<Int> = _cartItems.combine(_cartItems) { map, _ ->
        map.values.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val subtotal: StateFlow<Int> = _cartItems.combine(_cartItems) { map, _ ->
        map.values.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val deliveryFee: StateFlow<Int> = _deliveryMode.combine(_deliveryMode) { mode, _ ->
        mode.fee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30)

    val grandTotal: StateFlow<Int> = combine(subtotal, deliveryFee) { sub, fee ->
        if (sub > 0) sub + fee else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Last Placed Order details
    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    private val _lastReceiptText = MutableStateFlow("")
    val lastReceiptText: StateFlow<String> = _lastReceiptText.asStateFlow()

    // Filter mutations
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: FoodCategory) {
        _selectedCategory.value = category
    }

    fun toggleVegOnly() {
        _vegOnlyFilter.value = !_vegOnlyFilter.value
    }

    // Cart actions
    fun getCartQuantity(menuItemId: String, portionName: String): Int {
        val key = "${menuItemId}_$portionName"
        return _cartItems.value[key]?.quantity ?: 0
    }

    fun getTotalQuantityForMenuItem(menuItemId: String): Int {
        return _cartItems.value.values.filter { it.menuItemId == menuItemId }.sumOf { it.quantity }
    }

    fun addToCart(menuItem: MenuItem, portion: PortionOption) {
        val key = "${menuItem.id}_${portion.name}"
        val current = _cartItems.value.toMutableMap()
        val existing = current[key]
        if (existing != null) {
            current[key] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[key] = CartItem(
                cartKey = key,
                menuItemId = menuItem.id,
                name = menuItem.name,
                hindiName = menuItem.hindiName,
                dietType = menuItem.dietType,
                portionName = portion.name,
                unitPrice = portion.price,
                quantity = 1
            )
        }
        _cartItems.value = current
    }

    fun removeFromCart(menuItemId: String, portionName: String) {
        val key = "${menuItemId}_$portionName"
        val current = _cartItems.value.toMutableMap()
        val existing = current[key] ?: return
        if (existing.quantity > 1) {
            current[key] = existing.copy(quantity = existing.quantity - 1)
        } else {
            current.remove(key)
        }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    fun setDeliveryMode(mode: DeliveryMode) {
        _deliveryMode.value = mode
    }

    fun updateCustomerInfo(name: String, phone: String, address: String, landmark: String, notes: String) {
        _customerInfo.value = CustomerInfo(
            name = name,
            phone = phone,
            address = address,
            landmark = landmark,
            notes = notes
        )
    }

    fun setPaymentMode(mode: PaymentMode) {
        _paymentMode.value = mode
    }

    fun placeOrder(
        activity: Activity,
        onOrderConfirmed: (OrderEntity, String) -> Unit
    ) {
        val currentCart = _cartItems.value.values.toList()
        if (currentCart.isEmpty()) return

        val orderNum = OrderDispatcher.generateOrderNumber()
        val sub = currentCart.sumOf { it.totalPrice }
        val fee = _deliveryMode.value.fee
        val total = sub + fee
        val cust = _customerInfo.value
        val mode = _deliveryMode.value
        val payment = _paymentMode.value

        val receiptText = OrderDispatcher.buildReceiptText(
            orderNumber = orderNum,
            timestamp = System.currentTimeMillis(),
            items = currentCart,
            subtotal = sub,
            deliveryMode = mode,
            deliveryFee = fee,
            grandTotal = total,
            customerInfo = cust,
            paymentMode = payment
        )

        val itemsSummary = currentCart.joinToString(", ") { "${it.name} (${it.portionName} x${it.quantity})" }

        val orderEntity = OrderEntity(
            orderNumber = orderNum,
            timestamp = System.currentTimeMillis(),
            itemsSummary = itemsSummary,
            subtotal = sub,
            deliveryFee = fee,
            grandTotal = total,
            deliveryMode = mode.displayName,
            customerName = cust.name,
            customerPhone = cust.phone,
            customerAddress = if (mode == DeliveryMode.HOME_DELIVERY) cust.address else "Takeaway",
            paymentMode = payment.displayName,
            status = "Placed"
        )

        // Save into Room DB asynchronously
        viewModelScope.launch {
            repository.insertOrder(orderEntity)
            repository.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = cust.name,
                    phone = cust.phone,
                    address = cust.address,
                    landmark = cust.landmark
                )
            )
        }

        _lastPlacedOrder.value = orderEntity
        _lastReceiptText.value = receiptText

        // Trigger AdMob Interstitial Ad on successful order placement as requested!
        AdManager.showInterstitial(activity) {
            // After ad is dismissed (or if ad not loaded/failed), clear cart and invoke confirmation callback
            clearCart()
            onOrderConfirmed(orderEntity, receiptText)
        }
    }
}
