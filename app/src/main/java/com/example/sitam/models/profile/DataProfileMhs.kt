package com.example.sitam.models.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataProfileMhs(
    val alamat: String,
    val batas_studi: Int,
    val created_at: String,
    val ipk: Float,
    val kelayakan_ta: String,
    val nama: String,
    val no_telp: String,
    val npm: String,
    val password: String,
    val tahun_lulus: Int,
    val tahun_masuk: Int,
    val updated_at: String
) : Parcelable