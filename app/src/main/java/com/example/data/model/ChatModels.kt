package com.example.data.model

enum class MediaType {
    NONE,
    IMAGE,
    VIDEO,
    PDF,
    TEXT_DOC
}

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val mediaType: MediaType = MediaType.NONE,
    val mediaUrl: String = "",
    val fileName: String = "",
    val fileSize: String = "",
    val isDownloaded: Boolean = false,
    val isSentByMe: Boolean = false
)

data class ChatConversation(
    val id: String,
    val participantName: String,
    val participantPhone: String,
    val participantRole: String = "User", // Buyer, Seller, Driver, Support
    val avatarBgColor: Long = 0xFF0D9488,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isVerified: Boolean = true
)

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallState {
    IDLE,
    OUTGOING,
    CONNECTED,
    ENDED
}

data class ActiveCall(
    val contactName: String,
    val contactPhone: String,
    val callType: CallType,
    val state: CallState = CallState.OUTGOING,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val isSpeakerOn: Boolean = true
)
