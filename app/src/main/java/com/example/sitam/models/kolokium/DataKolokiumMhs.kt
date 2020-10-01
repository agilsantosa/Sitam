package com.example.sitam.models.kolokium

data class DataKolokiumMhs(
    val approval: String,
    val created_at: String,
    val id: Int,
    val id_proposal: Int,
    val mahasiswa: String,
    val nilai_pembimbing1: String,
    val nilai_pembimbing2: String,
    val nilai_penguji1: String,
    val nilai_penguji2: String,
    val pembimbing1: String,
    val pembimbing2: String,
    val penguji1: String,
    val penguji2: String,
    val status: String,
    val tanggal_pelaksanaan: String,
    val total_nilai: String,
    val updated_at: String
)