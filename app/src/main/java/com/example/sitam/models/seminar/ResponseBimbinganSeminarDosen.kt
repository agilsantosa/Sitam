package com.example.sitam.models.seminar

data class ResponseBimbinganSeminarDosen(
    val `data`: List<DataBimbinganSeminarDosen>,
    val message: String,
    val success: Boolean
)