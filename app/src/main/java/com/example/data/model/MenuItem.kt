package com.example.data.model

enum class FoodCategory(val displayName: String, val hindiName: String) {
    ALL("All Items", "सभी आइटम"),
    CHOWMEIN("Chowmein", "चाउमीन"),
    ROLLS("Rolls", "रोल्स"),
    STARTERS("Starters", "स्टार्टर्स"),
    MOMOS("Momos", "मोमोज़"),
    SOUPS("Soups", "सूप")
}

enum class DietType {
    VEG,
    EGG,
    NON_VEG
}

data class PortionOption(
    val name: String, // e.g. "Half", "Full", "6 pcs", "10 pcs", "Regular"
    val price: Int
)

data class MenuItem(
    val id: String,
    val name: String,
    val hindiName: String,
    val category: FoodCategory,
    val dietType: DietType,
    val description: String,
    val portions: List<PortionOption>,
    val isBestseller: Boolean = false,
    val isSpicy: Boolean = false,
    val rating: Double = 4.8
)

data class CartItem(
    val cartKey: String, // menuItemId + "_" + portionName
    val menuItemId: String,
    val name: String,
    val hindiName: String,
    val dietType: DietType,
    val portionName: String,
    val unitPrice: Int,
    val quantity: Int
) {
    val totalPrice: Int
        get() = unitPrice * quantity
}

enum class DeliveryMode(val displayName: String, val fee: Int, val description: String) {
    HOME_DELIVERY("Home Delivery", 30, "Delivered hot to your door within 3 KM (+₹30)"),
    TAKEAWAY("Takeaway / Self-Pickup", 0, "Pick up fresh at Salman Food counter (Free)")
}

enum class PaymentMode(val displayName: String, val description: String) {
    UPI_PHONEPE("UPI / PhonePe", "Instant payment to 7033680705@ybl"),
    CASH_ON_DELIVERY("Cash on Delivery (COD)", "Pay in cash when order arrives or at counter")
}

data class CustomerInfo(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val landmark: String = "",
    val notes: String = ""
)
