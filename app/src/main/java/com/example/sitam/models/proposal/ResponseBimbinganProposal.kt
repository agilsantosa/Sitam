package com.example.sitam.models.proposal

data class ResponseBimbinganProposal(
    val `data`: ArrayList<DataListProposal>,
    val message: String,
    val success: Boolean
)