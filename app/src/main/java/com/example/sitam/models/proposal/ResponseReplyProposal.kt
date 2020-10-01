package com.example.sitam.models.proposal

data class ResponseReplyProposal(
    val `data`: DataReplyProposal,
    val message: String,
    val success: Boolean
)