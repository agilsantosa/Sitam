package com.example.sitam.models.seminar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataSeminarReply(
    val `by`: String,
    val catatan: String,
    val id_seminar: String,
    val nilai: String,
    val reply_to: String,
    val status: String
) : Parcelable