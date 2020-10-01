package com.example.sitam.models.seminar

data class ResponseDetailSeminarMhs(
    val `data`: List<DataDetailSeminarMhs>,
    val message: String,
    val success: Boolean
)