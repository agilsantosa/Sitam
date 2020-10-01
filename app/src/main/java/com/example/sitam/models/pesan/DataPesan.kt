package com.example.sitam.models.pesan

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataPesan(
    val created_at: String,
    val from: String,
    val id: Int,
    val pesan: String,
    val to: String,
    val updated_at: String
) : Parcelable