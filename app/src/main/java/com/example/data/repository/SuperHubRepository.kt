package com.example.data.repository

import com.example.data.model.ActiveCall
import com.example.data.model.CallState
import com.example.data.model.CallType
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.EarningTask
import com.example.data.model.EarningTaskType
import com.example.data.model.MarketplaceProduct
import com.example.data.model.MediaType
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.data.model.UserAccount
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SuperHubRepository(private val scope: CoroutineScope) {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Current User
    private val _currentUser = MutableStateFlow(
        UserAccount(
            id = "user_101",
            name = "Dawit Haile",
            email = "dawit.h@superhub.et",
            phone = "+251 911 887 766",
            walletBalance = 1450.0,
            preferredPayment = PaymentMethod.TELEBIRR,
            telebirrNumber = "+251 911 887 766",
            ebirrNumber = "+251 911 887 766",
            mpesaNumber = "+254 700 887 766",
            referralCode = "DAWIT99",
            totalEarned = 2300.0,
            tasksCompletedCount = 14,
            isVerified = true
        )
    )
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    // Conversations
    private val _conversations = MutableStateFlow(
        listOf(
            ChatConversation(
                id = "chat_store_1",
                participantName = "Selam Tech Electronics",
                participantPhone = "+251 912 345 678",
                participantRole = "Verified Seller",
                avatarBgColor = 0xFF0072CE,
                lastMessage = "Yes, Samsung Galaxy S24 Ultra is in stock with 1 year warranty!",
                lastMessageTime = "12:45 PM",
                unreadCount = 1,
                isOnline = true,
                isVerified = true
            ),
            ChatConversation(
                id = "chat_driver_1",
                participantName = "Abebe Kebede (Driver)",
                participantPhone = "+251 922 888 123",
                participantRole = "Delivery Courier",
                avatarBgColor = 0xFF43B02A,
                lastMessage = "I have picked up your package at Bole Medhanialem. On my way!",
                lastMessageTime = "12:30 PM",
                unreadCount = 0,
                isOnline = true,
                isVerified = true
            ),
            ChatConversation(
                id = "chat_buyer_1",
                participantName = "Bethlehem Fashion Store",
                participantPhone = "+251 933 111 222",
                participantRole = "Merchant",
                avatarBgColor = 0xFF8A1538,
                lastMessage = "Here is the PDF catalog of authentic Ethiopian traditional dresses.",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isOnline = false,
                isVerified = true
            ),
            ChatConversation(
                id = "chat_support",
                participantName = "SuperHub Support & Payouts",
                participantPhone = "+251 900 000 000",
                participantRole = "Official Support",
                avatarBgColor = 0xFF0D9488,
                lastMessage = "Your Telebirr payout of 500 ETB has been successfully credited.",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isOnline = true,
                isVerified = true
            )
        )
    )
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    // Messages map: chatId -> List<ChatMessage>
    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "chat_store_1" to listOf(
                ChatMessage(
                    id = "m1",
                    chatId = "chat_store_1",
                    senderId = "user_101",
                    senderName = "Dawit Haile",
                    text = "Hello! Do you have the Galaxy S24 Ultra and what payment methods do you accept?",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isSentByMe = true
                ),
                ChatMessage(
                    id = "m2",
                    chatId = "chat_store_1",
                    senderId = "store_1",
                    senderName = "Selam Tech Electronics",
                    text = "We accept Telebirr, E-Birr, M-Pesa, and SuperHub Wallet! Here is our spec sheet:",
                    timestamp = System.currentTimeMillis() - 3000000,
                    isSentByMe = false
                ),
                ChatMessage(
                    id = "m3",
                    chatId = "chat_store_1",
                    senderId = "store_1",
                    senderName = "Selam Tech Electronics",
                    text = "Attached tech specs and receipt warranty card",
                    timestamp = System.currentTimeMillis() - 2500000,
                    mediaType = MediaType.PDF,
                    fileName = "Galaxy_S24_Warranty_Card.pdf",
                    fileSize = "1.8 MB",
                    isDownloaded = true,
                    isSentByMe = false
                ),
                ChatMessage(
                    id = "m4",
                    chatId = "chat_store_1",
                    senderId = "store_1",
                    senderName = "Selam Tech Electronics",
                    text = "Yes, Samsung Galaxy S24 Ultra is in stock with 1 year warranty!",
                    timestamp = System.currentTimeMillis() - 900000,
                    isSentByMe = false
                )
            ),
            "chat_driver_1" to listOf(
                ChatMessage(
                    id = "m_d1",
                    chatId = "chat_driver_1",
                    senderId = "user_101",
                    senderName = "Dawit Haile",
                    text = "Hello Abebe, will you reach Bole Medhanialem soon?",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isSentByMe = true
                ),
                ChatMessage(
                    id = "m_d2",
                    chatId = "chat_driver_1",
                    senderId = "driver_1",
                    senderName = "Abebe Kebede",
                    text = "I have picked up your package at Bole Medhanialem. On my way!",
                    timestamp = System.currentTimeMillis() - 1200000,
                    isSentByMe = false
                ),
                ChatMessage(
                    id = "m_d3",
                    chatId = "chat_driver_1",
                    senderId = "driver_1",
                    senderName = "Abebe Kebede",
                    text = "Package photo verification at pickup point",
                    timestamp = System.currentTimeMillis() - 1100000,
                    mediaType = MediaType.IMAGE,
                    fileName = "package_photo_pickup.jpg",
                    fileSize = "2.4 MB",
                    isDownloaded = true,
                    isSentByMe = false
                )
            ),
            "chat_buyer_1" to listOf(
                ChatMessage(
                    id = "m_b1",
                    chatId = "chat_buyer_1",
                    senderId = "buyer_1",
                    senderName = "Bethlehem Fashion Store",
                    text = "Here is the PDF catalog of authentic Ethiopian traditional dresses.",
                    timestamp = System.currentTimeMillis() - 86400000,
                    mediaType = MediaType.PDF,
                    fileName = "Habesha_Kemis_Catalog_2026.pdf",
                    fileSize = "4.2 MB",
                    isDownloaded = false,
                    isSentByMe = false
                )
            ),
            "chat_support" to listOf(
                ChatMessage(
                    id = "m_s1",
                    chatId = "chat_support",
                    senderId = "support",
                    senderName = "SuperHub Support",
                    text = "Welcome to SuperHub! You can chat, place calls, order products, pay with Telebirr/E-Birr/M-Pesa, and complete online gigs to earn money daily.",
                    timestamp = System.currentTimeMillis() - 90000000,
                    isSentByMe = false
                ),
                ChatMessage(
                    id = "m_s2",
                    chatId = "chat_support",
                    senderId = "support",
                    senderName = "SuperHub Support",
                    text = "Your Telebirr payout of 500 ETB has been successfully credited.",
                    timestamp = System.currentTimeMillis() - 86400000,
                    isSentByMe = false
                )
            )
        )
    )
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    // Marketplace Products
    private val _products = MutableStateFlow(
        listOf(
            MarketplaceProduct(
                id = "prod_1",
                title = "Samsung Galaxy S24 Ultra 512GB",
                description = "Brand new sealed in box. Titanium Gray, 12GB RAM, 200MP Camera, S-Pen included. 1 Year warranty with receipt.",
                price = 145000.0,
                category = "Electronics",
                sellerName = "Selam Tech Store",
                sellerPhone = "+251 912 345 678",
                sellerRating = 4.9f,
                reviewCount = 88,
                location = "Addis Ababa, Bole Road",
                inStock = true
            ),
            MarketplaceProduct(
                id = "prod_2",
                title = "Apple MacBook Air M3 16GB / 512GB",
                description = "Midnight Blue, super fast M3 chip, liquid retina display, 18 hours battery life. Free laptop sleeve included.",
                price = 175000.0,
                category = "Electronics",
                sellerName = "Addis Gadgets Hub",
                sellerPhone = "+251 911 554 433",
                sellerRating = 4.8f,
                reviewCount = 42,
                location = "Addis Ababa, Piazza",
                inStock = true
            ),
            MarketplaceProduct(
                id = "prod_3",
                title = "Authentic Handcrafted Habesha Kemis Dress",
                description = "Pure Shemane handwoven cotton with vibrant royal tilet embroidery. Ideal for weddings and holidays.",
                price = 18500.0,
                category = "Fashion",
                sellerName = "Bethlehem Fashion House",
                sellerPhone = "+251 933 111 222",
                sellerRating = 5.0f,
                reviewCount = 120,
                location = "Addis Ababa, Shiro Meda",
                inStock = true
            ),
            MarketplaceProduct(
                id = "prod_4",
                title = "Sony WH-1000XM5 Wireless Headphones",
                description = "Industry-leading active noise cancellation, crystal clear calling mics, 30 hours battery, ultra comfortable.",
                price = 42000.0,
                category = "Electronics",
                sellerName = "AudioPro Addis",
                sellerPhone = "+251 944 776 655",
                sellerRating = 4.7f,
                reviewCount = 29,
                location = "Addis Ababa, Kazanchis",
                inStock = true
            ),
            MarketplaceProduct(
                id = "prod_5",
                title = "Nike Air Max 270 Sneakers (Size 42-45)",
                description = "Original breathable sports sneakers with responsive cushioning. Available in Black/White and Navy.",
                price = 7800.0,
                category = "Fashion",
                sellerName = "Kenenisa Sports Store",
                sellerPhone = "+251 922 998 877",
                sellerRating = 4.6f,
                reviewCount = 54,
                location = "Addis Ababa, Mexico Square",
                inStock = true
            ),
            MarketplaceProduct(
                id = "prod_6",
                title = "Original Yirgacheffe Organic Coffee Beans (1kg)",
                description = "Single-origin Grade 1 Ethiopian specialty Arabica beans. Medium roast with floral and citrus tasting notes.",
                price = 1200.0,
                category = "Groceries",
                sellerName = "Abyssinia Coffee Roasters",
                sellerPhone = "+251 915 223 344",
                sellerRating = 4.9f,
                reviewCount = 215,
                location = "Addis Ababa, Sarbet",
                inStock = true
            )
        )
    )
    val products: StateFlow<List<MarketplaceProduct>> = _products.asStateFlow()

    // Active Orders & Delivery
    private val _activeOrders = MutableStateFlow(
        listOf(
            Order(
                id = "ORD-8821",
                productId = "prod_4",
                productTitle = "Sony WH-1000XM5 Wireless Headphones",
                amount = 42000.0,
                paymentMethod = PaymentMethod.TELEBIRR,
                paymentReference = "TB-8941029482",
                status = OrderStatus.IN_TRANSIT,
                driverName = "Abebe Kebede",
                driverPhone = "+251 922 888 123",
                driverVehicle = "Bajaj Express #2491",
                deliveryAddress = "Bole Medhanialem, Addis Ababa",
                estimatedMinutes = 12,
                driverLat = 9.0135f,
                driverLng = 38.7885f,
                customerLat = 9.0200f,
                customerLng = 38.7990f,
                storeLat = 9.0050f,
                storeLng = 38.7750f
            )
        )
    )
    val activeOrders: StateFlow<List<Order>> = _activeOrders.asStateFlow()

    // Wallet Transactions
    private val _transactions = MutableStateFlow(
        listOf(
            WalletTransaction(
                id = "tx_1",
                title = "Order Payment: Sony Headphones",
                type = TransactionType.ORDER_PAYMENT,
                amount = 42000.0,
                paymentMethod = PaymentMethod.TELEBIRR,
                referenceCode = "TB-8941029482",
                timestamp = System.currentTimeMillis() - 7200000,
                isPositive = false
            ),
            WalletTransaction(
                id = "tx_2",
                title = "Daily Check-in Reward",
                type = TransactionType.EARNED_TASK,
                amount = 25.0,
                paymentMethod = PaymentMethod.WALLET,
                referenceCode = "REW-20261006",
                timestamp = System.currentTimeMillis() - 14400000,
                isPositive = true
            ),
            WalletTransaction(
                id = "tx_3",
                title = "Watched Sponsored Brand Video",
                type = TransactionType.EARNED_TASK,
                amount = 15.0,
                paymentMethod = PaymentMethod.WALLET,
                referenceCode = "REW-AD-4821",
                timestamp = System.currentTimeMillis() - 28800000,
                isPositive = true
            ),
            WalletTransaction(
                id = "tx_4",
                title = "Deposit via M-Pesa",
                type = TransactionType.WALLET_DEPOSIT,
                amount = 1000.0,
                paymentMethod = PaymentMethod.MPESA,
                referenceCode = "MP-7729103",
                timestamp = System.currentTimeMillis() - 86400000,
                isPositive = true
            ),
            WalletTransaction(
                id = "tx_5",
                title = "Friend Referral Bonus (Almaz K.)",
                type = TransactionType.REFERRAL_BONUS,
                amount = 100.0,
                paymentMethod = PaymentMethod.WALLET,
                referenceCode = "REF-ALMAZ99",
                timestamp = System.currentTimeMillis() - 120000000,
                isPositive = true
            )
        )
    )
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Earning Tasks
    private val _earningTasks = MutableStateFlow(
        listOf(
            EarningTask(
                id = "task_daily",
                title = "Daily Login Streak Bonus",
                description = "Check in every day to claim your instant ETB bonus into your wallet.",
                rewardETB = 25.0,
                type = EarningTaskType.DAILY_CHECKIN,
                iconName = "check_circle",
                isCompleted = false,
                progress = 0,
                maxProgress = 1,
                actionLabel = "Claim 25 ETB"
            ),
            EarningTask(
                id = "task_video_1",
                title = "Watch Partner Video (15s)",
                description = "Watch a short 15-second sponsor video to support African tech creators.",
                rewardETB = 15.0,
                type = EarningTaskType.WATCH_AD,
                iconName = "play_arrow",
                isCompleted = false,
                progress = 0,
                maxProgress = 1,
                actionLabel = "Watch & Earn 15 ETB"
            ),
            EarningTask(
                id = "task_survey_1",
                title = "East Africa E-Commerce Market Poll",
                description = "Answer 4 quick questions about online shopping and delivery preferences.",
                rewardETB = 75.0,
                type = EarningTaskType.SURVEY,
                iconName = "poll",
                isCompleted = false,
                progress = 0,
                maxProgress = 1,
                actionLabel = "Take Survey (+75 ETB)"
            ),
            EarningTask(
                id = "task_referral",
                title = "Invite Friends to SuperHub",
                description = "Share your unique invite code DAWIT99. Earn 100 ETB for each friend who signs up!",
                rewardETB = 100.0,
                type = EarningTaskType.REFERRAL,
                iconName = "share",
                isCompleted = false,
                progress = 2,
                maxProgress = 5,
                actionLabel = "Share Referral Code"
            ),
            EarningTask(
                id = "task_product_review",
                title = "Product Quality Feedback",
                description = "Leave a verified buyer review and rating for your recent marketplace order.",
                rewardETB = 50.0,
                type = EarningTaskType.MICRO_TASK,
                iconName = "rate_review",
                isCompleted = false,
                progress = 0,
                maxProgress = 1,
                actionLabel = "Write Review (+50 ETB)"
            ),
            EarningTask(
                id = "task_app_testing",
                title = "App Translation & Bug Testing",
                description = "Test English to Amharic / Oromo localization and submit quick feedback.",
                rewardETB = 120.0,
                type = EarningTaskType.MICRO_TASK,
                iconName = "bug_report",
                isCompleted = false,
                progress = 0,
                maxProgress = 1,
                actionLabel = "Submit Feedback (+120 ETB)"
            )
        )
    )
    val earningTasks: StateFlow<List<EarningTask>> = _earningTasks.asStateFlow()

    // ============================================
    // CHAT ACTIONS
    // ============================================

    fun sendTextMessage(chatId: String, text: String) {
        if (text.isBlank()) return
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.name,
            text = text,
            timestamp = System.currentTimeMillis(),
            isSentByMe = true
        )
        addMessage(chatId, newMsg)

        // Trigger intelligent automatic simulated reply based on contact
        simulateContactResponse(chatId, text)
    }

    fun uploadAndSendFile(chatId: String, fileName: String, fileSize: String, mediaType: MediaType, note: String = "") {
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.name,
            text = note.ifBlank { "Sent file: $fileName" },
            timestamp = System.currentTimeMillis(),
            mediaType = mediaType,
            fileName = fileName,
            fileSize = fileSize,
            isDownloaded = true, // sender already has it
            isSentByMe = true
        )
        addMessage(chatId, newMsg)

        scope.launch {
            delay(1200)
            val replyMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = "recipient",
                senderName = getParticipantName(chatId),
                text = "Received file '$fileName' successfully! Thank you.",
                timestamp = System.currentTimeMillis(),
                isSentByMe = false
            )
            addMessage(chatId, replyMsg)
        }
    }

    fun downloadFile(chatId: String, messageId: String) {
        val currentList = _messages.value[chatId] ?: return
        val updated = currentList.map { msg ->
            if (msg.id == messageId) msg.copy(isDownloaded = true) else msg
        }
        _messages.update { it + (chatId to updated) }
    }

    private fun addMessage(chatId: String, msg: ChatMessage) {
        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.update { it + (chatId to (currentList + msg)) }

        // Update conversation preview
        val timeStr = timeFormat.format(Date(msg.timestamp))
        val preview = if (msg.mediaType != MediaType.NONE) "[${msg.mediaType.name}] ${msg.text}" else msg.text
        _conversations.update { list ->
            list.map { conv ->
                if (conv.id == chatId) {
                    conv.copy(lastMessage = preview, lastMessageTime = timeStr)
                } else conv
            }
        }
    }

    private fun simulateContactResponse(chatId: String, userText: String) {
        scope.launch {
            delay(1000)
            val replyText = when {
                chatId == "chat_store_1" && userText.contains("price", ignoreCase = true) ->
                    "We offer discounts if you pay via Telebirr or SuperHub Wallet! Delivery is free inside Addis Ababa."
                chatId == "chat_store_1" ->
                    "Thanks for reaching out! We are ready to pack your order right away."
                chatId == "chat_driver_1" ->
                    "I am currently passing Dembel City Center. Traffic is clear, I will arrive in ~10 minutes!"
                chatId == "chat_buyer_1" ->
                    "Feel free to check our catalog. We do custom tailoring as well!"
                chatId == "chat_support" ->
                    "SuperHub Support agent is here to help you 24/7. Transactions are secured and instant."
                else -> "Got your message! I'll update you shortly."
            }

            val replyMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = "partner",
                senderName = getParticipantName(chatId),
                text = replyText,
                timestamp = System.currentTimeMillis(),
                isSentByMe = false
            )
            addMessage(chatId, replyMsg)
        }
    }

    private fun getParticipantName(chatId: String): String {
        return _conversations.value.find { it.id == chatId }?.participantName ?: "Contact"
    }

    // ============================================
    // CALLING ACTIONS
    // ============================================

    fun startCall(contactName: String, contactPhone: String, callType: CallType) {
        _activeCall.value = ActiveCall(
            contactName = contactName,
            contactPhone = contactPhone,
            callType = callType,
            state = CallState.OUTGOING,
            durationSeconds = 0
        )

        scope.launch {
            delay(2000)
            if (_activeCall.value?.state == CallState.OUTGOING) {
                _activeCall.update { it?.copy(state = CallState.CONNECTED) }
                // Start call timer
                while (_activeCall.value?.state == CallState.CONNECTED) {
                    delay(1000)
                    _activeCall.update { it?.copy(durationSeconds = (it.durationSeconds + 1)) }
                }
            }
        }
    }

    fun toggleMute() {
        _activeCall.update { it?.copy(isMuted = !it.isMuted) }
    }

    fun toggleVideo() {
        _activeCall.update { it?.copy(isVideoEnabled = !it.isVideoEnabled) }
    }

    fun toggleSpeaker() {
        _activeCall.update { it?.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun endCall() {
        _activeCall.update { it?.copy(state = CallState.ENDED) }
        scope.launch {
            delay(500)
            _activeCall.value = null
        }
    }

    // ============================================
    // MARKETPLACE & SELLING ACTIONS
    // ============================================

    fun addProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        location: String,
        sellerPhone: String
    ) {
        val newProduct = MarketplaceProduct(
            id = "prod_" + System.currentTimeMillis(),
            title = title,
            description = description,
            price = price,
            category = category,
            sellerName = _currentUser.value.name,
            sellerPhone = sellerPhone.ifBlank { _currentUser.value.phone },
            sellerRating = 5.0f,
            reviewCount = 1,
            location = location.ifBlank { "Addis Ababa" },
            inStock = true
        )
        _products.update { listOf(newProduct) + it }
    }

    // ============================================
    // ORDERING & PAYMENT ACTIONS
    // ============================================

    fun placeOrder(
        product: MarketplaceProduct,
        paymentMethod: PaymentMethod,
        deliveryAddress: String,
        onSuccess: (Order) -> Unit
    ) {
        val refPrefix = when (paymentMethod) {
            PaymentMethod.TELEBIRR -> "TB-"
            PaymentMethod.EBIRR -> "EB-"
            PaymentMethod.MPESA -> "MP-"
            PaymentMethod.WALLET -> "SHW-"
        }
        val refCode = refPrefix + (1000000000L + (Math.random() * 9000000000L).toLong())

        // If paying via wallet, deduct balance
        if (paymentMethod == PaymentMethod.WALLET) {
            _currentUser.update { it.copy(walletBalance = maxOf(0.0, it.walletBalance - product.price)) }
        }

        val newOrder = Order(
            id = "ORD-" + (1000 + (Math.random() * 9000).toInt()),
            productId = product.id,
            productTitle = product.title,
            amount = product.price,
            paymentMethod = paymentMethod,
            paymentReference = refCode,
            status = OrderStatus.CONFIRMED,
            driverName = "Abebe Kebede",
            driverPhone = "+251 922 888 123",
            driverVehicle = "Bajaj Courier Express #2491",
            deliveryAddress = deliveryAddress.ifBlank { "Bole Medhanialem, Addis Ababa" },
            estimatedMinutes = 20,
            driverLat = 9.0100f,
            driverLng = 38.7800f,
            customerLat = 9.0200f,
            customerLng = 38.7990f,
            storeLat = 9.0050f,
            storeLng = 38.7750f,
            timestamp = System.currentTimeMillis()
        )

        _activeOrders.update { listOf(newOrder) + it }

        // Log transaction
        val tx = WalletTransaction(
            id = "tx_" + System.currentTimeMillis(),
            title = "Purchased: ${product.title}",
            type = TransactionType.ORDER_PAYMENT,
            amount = product.price,
            paymentMethod = paymentMethod,
            referenceCode = refCode,
            timestamp = System.currentTimeMillis(),
            isPositive = false
        )
        _transactions.update { listOf(tx) + it }

        onSuccess(newOrder)
    }

    fun advanceOrderStatus(orderId: String) {
        _activeOrders.update { orders ->
            orders.map { order ->
                if (order.id == orderId) {
                    val nextStatus = when (order.status) {
                        OrderStatus.CONFIRMED -> OrderStatus.PACKED
                        OrderStatus.PACKED -> OrderStatus.IN_TRANSIT
                        OrderStatus.IN_TRANSIT -> OrderStatus.DELIVERED
                        OrderStatus.DELIVERED -> OrderStatus.DELIVERED
                    }
                    val nextMins = maxOf(0, order.estimatedMinutes - 6)
                    val stepLat = order.driverLat + (order.customerLat - order.driverLat) * 0.35f
                    val stepLng = order.driverLng + (order.customerLng - order.driverLng) * 0.35f
                    order.copy(
                        status = nextStatus,
                        estimatedMinutes = nextMins,
                        driverLat = stepLat,
                        driverLng = stepLng
                    )
                } else order
            }
        }
    }

    // ============================================
    // WALLET ACTIONS
    // ============================================

    fun depositFunds(amount: Double, method: PaymentMethod) {
        val ref = "DEP-" + (100000 + (Math.random() * 900000).toInt())
        _currentUser.update { it.copy(walletBalance = it.walletBalance + amount) }

        val tx = WalletTransaction(
            id = "tx_" + System.currentTimeMillis(),
            title = "Deposit via ${method.displayName}",
            type = TransactionType.WALLET_DEPOSIT,
            amount = amount,
            paymentMethod = method,
            referenceCode = ref,
            timestamp = System.currentTimeMillis(),
            isPositive = true
        )
        _transactions.update { listOf(tx) + it }
    }

    fun withdrawFunds(amount: Double, method: PaymentMethod, targetAccount: String): Boolean {
        if (_currentUser.value.walletBalance < amount) return false
        val ref = "WTH-" + (100000 + (Math.random() * 900000).toInt())
        _currentUser.update { it.copy(walletBalance = it.walletBalance - amount) }

        val tx = WalletTransaction(
            id = "tx_" + System.currentTimeMillis(),
            title = "Cashout to ${method.displayName} ($targetAccount)",
            type = TransactionType.WALLET_WITHDRAWAL,
            amount = amount,
            paymentMethod = method,
            referenceCode = ref,
            timestamp = System.currentTimeMillis(),
            isPositive = false
        )
        _transactions.update { listOf(tx) + it }
        return true
    }

    // ============================================
    // ONLINE MONEY MAKING ACTIONS
    // ============================================

    fun claimTaskReward(taskId: String) {
        val task = _earningTasks.value.find { it.id == taskId } ?: return
        if (task.isCompleted) return

        val reward = task.rewardETB
        _currentUser.update {
            it.copy(
                walletBalance = it.walletBalance + reward,
                totalEarned = it.totalEarned + reward,
                tasksCompletedCount = it.tasksCompletedCount + 1
            )
        }

        _earningTasks.update { tasks ->
            tasks.map {
                if (it.id == taskId) it.copy(isCompleted = true, progress = it.maxProgress) else it
            }
        }

        val tx = WalletTransaction(
            id = "tx_" + System.currentTimeMillis(),
            title = "Task Reward: ${task.title}",
            type = TransactionType.EARNED_TASK,
            amount = reward,
            paymentMethod = PaymentMethod.WALLET,
            referenceCode = "EARN-" + (10000 + (Math.random() * 90000).toInt()),
            timestamp = System.currentTimeMillis(),
            isPositive = true
        )
        _transactions.update { listOf(tx) + it }
    }

    fun shareReferralInvite(): String {
        val code = _currentUser.value.referralCode
        return "Join me on SuperHub to chat, make calls, shop, and earn real money online with Telebirr & E-Birr! Use my invitation code: $code. Download now: https://superhub.et/invite/$code"
    }

    // ============================================
    // ACCOUNT ACTIONS
    // ============================================

    fun updateProfile(name: String, email: String, phone: String, preferredPayment: PaymentMethod) {
        _currentUser.update {
            it.copy(
                name = name,
                email = email,
                phone = phone,
                preferredPayment = preferredPayment
            )
        }
    }
}
