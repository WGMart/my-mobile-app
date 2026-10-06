package com.example.data.model

enum class PaymentMethod(val displayName: String, val brandColorHex: Long) {
    TELEBIRR("telebirr", 0xFF0072CE),
    EBIRR("E-Birr / CBE Birr", 0xFF8A1538),
    MPESA("M-Pesa", 0xFF43B02A),
    WALLET("SuperHub Wallet", 0xFF0D9488)
}

data class MarketplaceProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "ETB",
    val category: String,
    val sellerName: String,
    val sellerPhone: String,
    val sellerRating: Float = 4.8f,
    val reviewCount: Int = 34,
    val imageUrl: String = "",
    val location: String = "Addis Ababa, Bole",
    val inStock: Boolean = true
)

enum class OrderStatus(val title: String, val stepIndex: Int) {
    CONFIRMED("Order Confirmed", 0),
    PACKED("Item Packed", 1),
    IN_TRANSIT("Out for Delivery", 2),
    DELIVERED("Delivered", 3)
}

data class Order(
    val id: String,
    val productId: String,
    val productTitle: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val paymentReference: String,
    val status: OrderStatus = OrderStatus.IN_TRANSIT,
    val driverName: String = "Abebe Kebede",
    val driverPhone: String = "+251 911 234 567",
    val driverVehicle: String = "Bajaj Express #2491",
    val deliveryAddress: String = "Bole Medhanialem, Addis Ababa",
    val estimatedMinutes: Int = 14,
    val driverLat: Float = 9.012f,
    val driverLng: Float = 38.789f,
    val customerLat: Float = 9.020f,
    val customerLng: Float = 38.799f,
    val storeLat: Float = 9.005f,
    val storeLng: Float = 38.775f,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    EARNED_TASK,
    ORDER_PAYMENT,
    WALLET_DEPOSIT,
    WALLET_WITHDRAWAL,
    REFERRAL_BONUS
}

data class WalletTransaction(
    val id: String,
    val title: String,
    val type: TransactionType,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val referenceCode: String,
    val timestamp: Long,
    val isPositive: Boolean
)
