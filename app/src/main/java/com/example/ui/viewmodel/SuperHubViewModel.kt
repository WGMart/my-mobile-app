package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActiveCall
import com.example.data.model.CallType
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.model.EarningTask
import com.example.data.model.MarketplaceProduct
import com.example.data.model.MediaType
import com.example.data.model.Order
import com.example.data.model.PaymentMethod
import com.example.data.model.UserAccount
import com.example.data.model.WalletTransaction
import com.example.data.repository.SuperHubRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen(val title: String) {
    object Home : Screen("Home")
    object Chats : Screen("Messages")
    data class ChatDetail(val chatId: String) : Screen("Chat")
    object Marketplace : Screen("Marketplace")
    data class ProductDetail(val productId: String) : Screen("Product")
    object EarnMoney : Screen("Earn Cash")
    object OrdersAndMap : Screen("Delivery Tracker")
    data class OrderTracking(val orderId: String) : Screen("Live Tracking")
    object Wallet : Screen("Wallet")
    object Profile : Screen("My Account")
}

class SuperHubViewModel : ViewModel() {

    private val repository = SuperHubRepository(viewModelScope)

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation back stack for BackHandler
    private val backStack = mutableListOf<Screen>()

    // Repository flows
    val currentUser: StateFlow<UserAccount> = repository.currentUser
    val conversations: StateFlow<List<ChatConversation>> = repository.conversations
    val messages: StateFlow<Map<String, List<ChatMessage>>> = repository.messages
    val activeCall: StateFlow<ActiveCall?> = repository.activeCall
    val products: StateFlow<List<MarketplaceProduct>> = repository.products
    val activeOrders: StateFlow<List<Order>> = repository.activeOrders
    val transactions: StateFlow<List<WalletTransaction>> = repository.transactions
    val earningTasks: StateFlow<List<EarningTask>> = repository.earningTasks

    // Dialog & Flow States
    private val _showSellItemDialog = MutableStateFlow(false)
    val showSellItemDialog: StateFlow<Boolean> = _showSellItemDialog.asStateFlow()

    private val _selectedProductForBuy = MutableStateFlow<MarketplaceProduct?>(null)
    val selectedProductForBuy: StateFlow<MarketplaceProduct?> = _selectedProductForBuy.asStateFlow()

    private val _showFileUploadChatId = MutableStateFlow<String?>(null)
    val showFileUploadChatId: StateFlow<String?> = _showFileUploadChatId.asStateFlow()

    private val _showDepositModal = MutableStateFlow(false)
    val showDepositModal: StateFlow<Boolean> = _showDepositModal.asStateFlow()

    private val _showWithdrawModal = MutableStateFlow(false)
    val showWithdrawModal: StateFlow<Boolean> = _showWithdrawModal.asStateFlow()

    private val _showWatchAdModal = MutableStateFlow(false)
    val showWatchAdModal: StateFlow<Boolean> = _showWatchAdModal.asStateFlow()

    private val _adCountdownSeconds = MutableStateFlow(10)
    val adCountdownSeconds: StateFlow<Int> = _adCountdownSeconds.asStateFlow()
    private var adJob: Job? = null

    private val _showSurveyModal = MutableStateFlow(false)
    val showSurveyModal: StateFlow<Boolean> = _showSurveyModal.asStateFlow()

    private val _completedOrderReceipt = MutableStateFlow<Order?>(null)
    val completedOrderReceipt: StateFlow<Order?> = _completedOrderReceipt.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _downloadProgressMap = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgressMap: StateFlow<Map<String, Float>> = _downloadProgressMap.asStateFlow()

    // Navigation
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    // Chat actions
    fun openChat(chatId: String) {
        navigateTo(Screen.ChatDetail(chatId))
    }

    fun sendChatMessage(chatId: String, text: String) {
        repository.sendTextMessage(chatId, text)
    }

    fun openFileUpload(chatId: String) {
        _showFileUploadChatId.value = chatId
    }

    fun closeFileUpload() {
        _showFileUploadChatId.value = null
    }

    fun uploadFile(chatId: String, fileName: String, fileSize: String, mediaType: MediaType, note: String) {
        repository.uploadAndSendFile(chatId, fileName, fileSize, mediaType, note)
        closeFileUpload()
    }

    fun downloadMessageFile(chatId: String, messageId: String) {
        viewModelScope.launch {
            // Animate download progress
            for (step in 1..10) {
                _downloadProgressMap.value = _downloadProgressMap.value + (messageId to (step / 10f))
                delay(120)
            }
            repository.downloadFile(chatId, messageId)
            delay(500)
            _downloadProgressMap.value = _downloadProgressMap.value - messageId
        }
    }

    // Call actions
    fun startCall(name: String, phone: String, type: CallType) {
        repository.startCall(name, phone, type)
    }

    fun toggleCallMute() = repository.toggleMute()
    fun toggleCallVideo() = repository.toggleVideo()
    fun toggleCallSpeaker() = repository.toggleSpeaker()
    fun endActiveCall() = repository.endCall()

    // Marketplace
    fun openProductDetail(productId: String) {
        navigateTo(Screen.ProductDetail(productId))
    }

    fun openSellItemModal() {
        _showSellItemDialog.value = true
    }

    fun closeSellItemModal() {
        _showSellItemDialog.value = false
    }

    fun addMarketplaceItem(
        title: String,
        description: String,
        price: Double,
        category: String,
        location: String,
        phone: String
    ) {
        repository.addProduct(title, description, price, category, location, phone)
        closeSellItemModal()
    }

    fun initiateBuyProduct(product: MarketplaceProduct) {
        _selectedProductForBuy.value = product
    }

    fun cancelBuyProduct() {
        _selectedProductForBuy.value = null
    }

    fun confirmPurchase(
        product: MarketplaceProduct,
        paymentMethod: PaymentMethod,
        address: String
    ) {
        repository.placeOrder(product, paymentMethod, address) { order ->
            _selectedProductForBuy.value = null
            _completedOrderReceipt.value = order
        }
    }

    fun dismissReceipt() {
        _completedOrderReceipt.value = null
    }

    // Delivery tracking
    fun openOrderTracking(orderId: String) {
        navigateTo(Screen.OrderTracking(orderId))
    }

    fun advanceOrderMilestone(orderId: String) {
        repository.advanceOrderStatus(orderId)
    }

    // Wallet actions
    fun openDeposit() { _showDepositModal.value = true }
    fun closeDeposit() { _showDepositModal.value = false }
    fun confirmDeposit(amount: Double, method: PaymentMethod) {
        repository.depositFunds(amount, method)
        closeDeposit()
    }

    fun openWithdraw() { _showWithdrawModal.value = true }
    fun closeWithdraw() { _showWithdrawModal.value = false }
    fun confirmWithdraw(amount: Double, method: PaymentMethod, targetAccount: String): Boolean {
        val success = repository.withdrawFunds(amount, method, targetAccount)
        if (success) closeWithdraw()
        return success
    }

    // Money making / tasks
    fun triggerTask(task: EarningTask) {
        when (task.type) {
            com.example.data.model.EarningTaskType.DAILY_CHECKIN -> {
                repository.claimTaskReward(task.id)
            }
            com.example.data.model.EarningTaskType.WATCH_AD -> {
                startWatchAdModal(task.id)
            }
            com.example.data.model.EarningTaskType.SURVEY -> {
                _showSurveyModal.value = true
            }
            com.example.data.model.EarningTaskType.REFERRAL -> {
                // Return invite message to share
            }
            com.example.data.model.EarningTaskType.MICRO_TASK -> {
                repository.claimTaskReward(task.id)
            }
        }
    }

    private fun startWatchAdModal(taskId: String) {
        _showWatchAdModal.value = true
        _adCountdownSeconds.value = 10
        adJob?.cancel()
        adJob = viewModelScope.launch {
            while (_adCountdownSeconds.value > 0) {
                delay(1000)
                _adCountdownSeconds.value = _adCountdownSeconds.value - 1
            }
            repository.claimTaskReward(taskId)
        }
    }

    fun closeWatchAdModal() {
        adJob?.cancel()
        _showWatchAdModal.value = false
    }

    fun completeSurvey(taskId: String) {
        repository.claimTaskReward(taskId)
        _showSurveyModal.value = false
    }

    fun closeSurveyModal() {
        _showSurveyModal.value = false
    }

    fun getReferralShareText(): String {
        return repository.shareReferralInvite()
    }

    // Account & auth
    fun openAuthDialog() { _showAuthDialog.value = true }
    fun closeAuthDialog() { _showAuthDialog.value = false }
    fun registerOrUpdateAccount(name: String, email: String, phone: String, preferredPayment: PaymentMethod) {
        repository.updateProfile(name, email, phone, preferredPayment)
        closeAuthDialog()
    }
}
