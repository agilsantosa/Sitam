package com.example.sitam.models.ta

data class ResponseDataTugasAkhirDosen(
    val `data`: List<DataTugasAkhirDosen>,
    val message: String,
    val success: Boolean
)