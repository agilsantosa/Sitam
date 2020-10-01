package com.example.sitam.models.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseProfileMhs(
    val data: DataProfileMhs,
    val message: String,
    val success: Boolean
) : Parcelable