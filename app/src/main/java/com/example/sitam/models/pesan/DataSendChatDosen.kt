package com.example.sitam.models.pesan

data class DataSendChatDosen(
    val created_at: String,
    val from: String,
    val id: Int,
    val pesan: String,
    val to: String,
    val updated_at: String
)