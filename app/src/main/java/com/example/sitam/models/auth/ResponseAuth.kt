package com.example.sitam.models.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseAuth(
    val data: DataAuth,
    val message: String,
    val success: Boolean
) : Parcelable {
}