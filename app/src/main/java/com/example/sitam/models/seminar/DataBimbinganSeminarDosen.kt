package com.example.sitam.models.seminar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataBimbinganSeminarDosen(
    val approval: String,
    val `as`: String,
    val created_at: String,
    val id: Int,
    val id_proposal: Int,
    val mahasiswa: String,
    val nilai_pembimbing1: String,
    val nilai_pembimbing2: String,
    val pembimbing: String,
    val penguji: String,
    val tanggal_sidang: String,
    val total_nilai: String,
    val updated_at: String
) : Parcelable