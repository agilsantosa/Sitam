package com.example.sitam.models.proposal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataPembimbingProposal(
    val nidn: Int,
    val nama: String,
    val jafung: String,
    val created_at: String,
    val updated_at: String,
) : Parcelable