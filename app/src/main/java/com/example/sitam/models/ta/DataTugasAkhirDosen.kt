package com.example.sitam.models.ta

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataTugasAkhirDosen(
    val `as`: String,
    val created_at: String,
    val id: Int,
    val id_proposal: Int,
    val konsentrasi: String,
    val mahasiswa: String,
    val nilai: Int?,
    val nilai_pembimbing1: Int?,
    val nilai_pembimbing2: Int?,
    val nilai_penguji1: Int?,
    val nilai_penguji2: Int?,
    val pembimbing1: String?,
    val pembimbing2: String?,
    val penguji1: String?,
    val penguji2: String?,
    val status: String,
    val tahun_pengajuan: Int,
    val topik_tugas_akhir: String,
    val updated_at: String
) : Parcelable