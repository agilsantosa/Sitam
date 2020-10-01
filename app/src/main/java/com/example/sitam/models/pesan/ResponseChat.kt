package com.example.sitam.models.pesan

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseChat(
    val `data`: List<DataPesan>,
    val message: String,
    val success: Boolean
) : Parcelable