package com.example.sitam.models.seminar

data class DataSeminar(
    val approval: String,
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
)