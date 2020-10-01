package com.example.sitam.models.seminar

data class ResponseListBimbinganSeminarDosen(
    val `data`: List<DataListBimbinganSeminarDosen>,
    val message: String,
    val success: Boolean
)