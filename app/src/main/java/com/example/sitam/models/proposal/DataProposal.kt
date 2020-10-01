package com.example.sitam.models.proposal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataProposal(
    val created_at: String,
    val id: Int,
    val judul_proposal: String,
    val konsentrasi: String,
    val mahasiswa: String,
    val nilai: Int,
    val pembimbing: String,
    val penguji: String,
    val seminar: Int,
    val status: String,
    val tahun_pengajuan: Int,
    val topik: String,
    val updated_at: String
) : Parcelable