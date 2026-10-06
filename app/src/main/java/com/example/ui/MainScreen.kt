package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CallingOverlay
import com.example.ui.components.FileUploadDialog
import com.example.ui.components.OrderReceiptDialog
import com.example.ui.components.PaymentBottomSheet
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AuthModalDialog
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.DepositDialog
import com.example.ui.screens.EarnMoneyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.OrdersAndMapScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SellItemDialog
import com.example.ui.screens.SurveyModalDialog
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WatchAdDialog
import com.example.ui.screens.WithdrawDialog
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SuperHubViewModel

data class NavTab(val screen: Screen, val label: String, val icon: ImageVector, val tag: String)

@Composable
fun MainScreen(
    viewModel: SuperHubViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val uploadChatId by viewModel.showFileUploadChatId.collectAsState()
    val buyProduct by viewModel.selectedProductForBuy.collectAsState()
    val showSellModal by viewModel.showSellItemDialog.collectAsState()
    val receiptOrder by viewModel.completedOrderReceipt.collectAsState()
    val showWatchAd by viewModel.showWatchAdModal.collectAsState()
    val adCountdown by viewModel.adCountdownSeconds.collectAsState()
    val showSurvey by viewModel.showSurveyModal.collectAsState()
    val showDeposit by viewModel.showDepositModal.collectAsState()
    val showWithdraw by viewModel.showWithdrawModal.collectAsState()
    val showAuth by viewModel.showAuthDialog.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    val tabs = listOf(
        NavTab(Screen.Home, "Home", Icons.Default.Home, "tab_home"),
        NavTab(Screen.Chats, "Chats", Icons.Default.Chat, "tab_chats"),
        NavTab(Screen.Marketplace, "Market", Icons.Default.ShoppingBag, "tab_market"),
        NavTab(Screen.EarnMoney, "Earn", Icons.Default.Paid, "tab_earn"),
        NavTab(Screen.OrdersAndMap, "Delivery", Icons.Default.DeliveryDining, "tab_orders")
    )

    val isTopLevelScreen = currentScreen is Screen.Home ||
            currentScreen is Screen.Chats ||
            currentScreen is Screen.Marketplace ||
            currentScreen is Screen.EarnMoney ||
            currentScreen is Screen.OrdersAndMap

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (isTopLevelScreen) {
                    TopHubBar(
                        userName = user.name,
                        walletBalance = user.walletBalance,
                        onWalletClick = { viewModel.navigateTo(Screen.Wallet) },
                        onProfileClick = { viewModel.navigateTo(Screen.Profile) }
                    )
                }
            },
            bottomBar = {
                if (isTopLevelScreen) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .testTag("bottom_nav_bar"),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = currentScreen::class == tab.screen::class
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(tab.screen) },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = Color(0xFF00897B),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Home -> HomeScreen(viewModel = viewModel)
                    is Screen.Chats -> ChatListScreen(viewModel = viewModel)
                    is Screen.ChatDetail -> ChatDetailScreen(chatId = screen.chatId, viewModel = viewModel)
                    is Screen.Marketplace -> MarketplaceScreen(viewModel = viewModel)
                    is Screen.ProductDetail -> ProductDetailScreen(productId = screen.productId, viewModel = viewModel)
                    is Screen.EarnMoney -> EarnMoneyScreen(viewModel = viewModel)
                    is Screen.OrdersAndMap -> OrdersAndMapScreen(viewModel = viewModel)
                    is Screen.OrderTracking -> OrdersAndMapScreen(viewModel = viewModel, initialOrderId = screen.orderId)
                    is Screen.Wallet -> WalletScreen(viewModel = viewModel)
                    is Screen.Profile -> AccountScreen(viewModel = viewModel)
                }
            }
        }

        // ============================================
        // MODALS & DIALOGS
        // ============================================

        // 1. Audio & Video Calling Fullscreen Overlay
        AnimatedVisibility(
            visible = activeCall != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            activeCall?.let { call ->
                CallingOverlay(
                    activeCall = call,
                    onMuteToggle = { viewModel.toggleCallMute() },
                    onVideoToggle = { viewModel.toggleCallVideo() },
                    onSpeakerToggle = { viewModel.toggleCallSpeaker() },
                    onEndCall = { viewModel.endActiveCall() }
                )
            }
        }

        // 2. File Upload & Sharing Dialog
        if (uploadChatId != null) {
            FileUploadDialog(
                chatId = uploadChatId!!,
                onDismiss = { viewModel.closeFileUpload() },
                onUpload = { name, size, type, note ->
                    viewModel.uploadFile(uploadChatId!!, name, size, type, note)
                }
            )
        }

        // 3. Payment Bottom Sheet
        if (buyProduct != null) {
            PaymentBottomSheet(
                product = buyProduct!!,
                userAccount = user,
                onDismiss = { viewModel.cancelBuyProduct() },
                onConfirmPayment = { prod, method, addr ->
                    viewModel.confirmPurchase(prod, method, addr)
                }
            )
        }

        // 4. Order Receipt Dialog
        if (receiptOrder != null) {
            OrderReceiptDialog(
                order = receiptOrder!!,
                onTrackOnMap = {
                    val orderId = receiptOrder!!.id
                    viewModel.dismissReceipt()
                    viewModel.openOrderTracking(orderId)
                },
                onDismiss = { viewModel.dismissReceipt() }
            )
        }

        // 5. Sell Item Modal
        if (showSellModal) {
            SellItemDialog(
                onDismiss = { viewModel.closeSellItemModal() },
                onSubmit = { title, desc, price, cat, loc, phone ->
                    viewModel.addMarketplaceItem(title, desc, price, cat, loc, phone)
                }
            )
        }

        // 6. Watch Video Ad Modal
        if (showWatchAd) {
            WatchAdDialog(
                countdownSeconds = adCountdown,
                onClose = { viewModel.closeWatchAdModal() }
            )
        }

        // 7. Market Survey Modal
        if (showSurvey) {
            SurveyModalDialog(
                onDismiss = { viewModel.closeSurveyModal() },
                onSubmit = { viewModel.completeSurvey("task_survey_1") }
            )
        }

        // 8. Deposit Modal
        if (showDeposit) {
            DepositDialog(
                onDismiss = { viewModel.closeDeposit() },
                onConfirm = { amt, meth -> viewModel.confirmDeposit(amt, meth) }
            )
        }

        // 9. Withdraw Modal
        if (showWithdraw) {
            WithdrawDialog(
                userBalance = user.walletBalance,
                onDismiss = { viewModel.closeWithdraw() },
                onConfirm = { amt, meth, acc -> viewModel.confirmWithdraw(amt, meth, acc) }
            )
        }

        // 10. Account Switch / Auth Modal
        if (showAuth) {
            AuthModalDialog(
                currentName = user.name,
                currentEmail = user.email,
                currentPhone = user.phone,
                onDismiss = { viewModel.closeAuthDialog() },
                onSubmit = { name, email, phone, pay ->
                    viewModel.registerOrUpdateAccount(name, email, phone, pay)
                }
            )
        }
    }
}

@Composable
private fun TopHubBar(
    userName: String,
    walletBalance: Double,
    onWalletClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Identity Brand with Circular Green, Yellow & Red WG Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF078930)) // Green outer
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFCDD09)) // Yellow middle
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDA121A)) // Red inner
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A)), // Center badge
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "WG",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "WG SuperHub",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Chat • Pay • Earn",
                        fontSize = 10.sp,
                        color = Color(0xFF078930),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Wallet Pill & Profile Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Wallet Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF00897B).copy(alpha = 0.12f))
                        .clickable(onClick = onWalletClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("top_bar_wallet_pill")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = Color(0xFF00897B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${String.format("%,.0f", walletBalance)} ETB",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF00897B)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = onProfileClick)
                        .testTag("top_bar_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
