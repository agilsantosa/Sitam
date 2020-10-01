package com.example.sitam.models.ta

data class ResponseReplyTa(
    val `data`: List<DataReplyTa>,
    val message: String,
    val success: Boolean
)