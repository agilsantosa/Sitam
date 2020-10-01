package com.example.sitam.models.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataAuth(
    val identifier: String,
    val level: String,
    val token: String
) : Parcelable {
}