package com.example.sitam.models.ta

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataListBimbinganTaMhs(
    val `by`: String,
    val catatan: String,
    val created_at: String,
    val file_revisi: String?,
    val id: Int,
    val id_ta: Int,
    val reply_to: String?,
    val status: String,
    val to: String?,
    val updated_at: String
) : Parcelable