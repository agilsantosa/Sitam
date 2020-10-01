package com.example.sitam.models.ta

data class ResponseListBimbinganTaMhs(
    val `data`: List<DataListBimbinganTaMhs>,
    val message: String,
    val success: Boolean
)