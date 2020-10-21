package com.example.sitam.models.proposal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseProposal(
    val `data`: DataProposal,
    val message: String,
    val success: Boolean
) : Parcelable