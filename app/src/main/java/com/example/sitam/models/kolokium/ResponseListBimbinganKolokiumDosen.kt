package com.example.sitam.models.kolokium

data class ResponseListBimbinganKolokiumDosen(
    val `data`: List<DataListBimbinganKolokiumDosen>,
    val message: String,
    val success: Boolean
)