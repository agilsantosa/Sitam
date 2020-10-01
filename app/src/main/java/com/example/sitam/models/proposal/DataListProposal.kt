package com.example.sitam.models.proposal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataListProposal(
    var by: String = "",
    var catatan: String = "",
    var created_at: String = "",
    var file_revisi: String? = null,
    var id: Int? = null,
    val id_proposal: Int? = null,
    val reply_to: String = "",
    var status: String = "",
    var updated_at: String = ""
) : Parcelable