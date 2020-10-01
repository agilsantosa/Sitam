package com.example.sitam.models.kolokium

data class ResponseListBimbinganKolokiumMhs(
    val `data`: List<DataListBimbinganKolokiumMhs>,
    val message: String,
    val success: Boolean
)