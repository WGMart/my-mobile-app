package com.example.data.model

data class UserAccount(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val walletBalance: Double = 350.0,
    val preferredPayment: PaymentMethod = PaymentMethod.TELEBIRR,
    val telebirrNumber: String = "+251 922 456 789",
    val ebirrNumber: String = "+251 933 654 321",
    val mpesaNumber: String = "+254 712 345 678",
    val referralCode: String = "SHUB99X",
    val totalEarned: Double = 1250.0,
    val tasksCompletedCount: Int = 18,
    val isVerified: Boolean = true
)

enum class EarningTaskType {
    DAILY_CHECKIN,
    WATCH_AD,
    SURVEY,
    REFERRAL,
    MICRO_TASK
}

data class EarningTask(
    val id: String,
    val title: String,
    val description: String,
    val rewardETB: Double,
    val type: EarningTaskType,
    val iconName: String,
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val actionLabel: String = "Start Task"
)
