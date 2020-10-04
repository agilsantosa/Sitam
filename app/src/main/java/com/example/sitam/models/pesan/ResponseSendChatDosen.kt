package com.example.sitam.models.pesan

data class ResponseSendChatDosen(
    val `data`: List<DataSendChatDosen>,
    val message: String,
    val success: Boolean
)