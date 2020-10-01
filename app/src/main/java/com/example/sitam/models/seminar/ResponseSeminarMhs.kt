package com.example.sitam.models.seminar

data class ResponseSeminarMhs(
    val `data`: DataSeminar,
    val message: String,
    val success: Boolean
)