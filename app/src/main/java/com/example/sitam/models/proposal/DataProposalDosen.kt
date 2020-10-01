package com.example.sitam.models.proposal

data class DataProposalDosen(
    val created_at: String,
    val id: Int,
    val judul_proposal: String,
    val konsentrasi: String,
    val mahasiswa: String,
    val nilai: Any,
    val pembimbing: String,
    val penguji: String,
    val seminar: Int,
    val status: String,
    val tahun_pengajuan: Int,
    val topik: String,
    val updated_at: String
)