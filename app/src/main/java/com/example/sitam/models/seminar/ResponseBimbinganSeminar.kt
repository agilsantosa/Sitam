package com.example.sitam.models.seminar

data class ResponseBimbinganSeminar(
    val `data`: List<DataBimbinganSeminar>,
    val message: String,
    val success: Boolean
)