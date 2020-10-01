package com.example.sitam.models.proposal

data class ResponseProposalDosen(
    val `data`: ArrayList<DataProposalDosen>,
    val message: String,
    val success: Boolean
)