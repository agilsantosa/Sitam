package com.example.sitam.models.ta


data class DataTugasAkhirMhs(
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
    val status_pembimbing1: String?,
    val status_pembimbing2: String?,
    val noSk: String?,
    val tahun_pengajuan: Int,
    val topik_tugas_akhir: String,
    val updated_at: String
)