package com.example.sitam.models.ta

data class DataReplyTa(
    val `by`: String,
    val catatan: String,
    val created_at: String,
    val file_revisi: String,
    val id: Int,
    val id_ta: Int,
    val reply_to: Any,
    val status: String,
    val to: String,
    val updated_at: String
)