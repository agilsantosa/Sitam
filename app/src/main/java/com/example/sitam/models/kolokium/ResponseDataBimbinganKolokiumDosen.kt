package com.example.sitam.models.kolokium

data class ResponseDataBimbinganKolokiumDosen(
    val `data`: List<DataBimbinganKolokiumDosen>,
    val message: String,
    val success: Boolean
)