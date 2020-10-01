package com.example.sitam.models.proposal

data class ResponseProposal(
    val `data`: DataProposal,
    val message: String,
    val success: Boolean
)