package com.example.sitam.repository

import android.app.Application
import com.example.sitam.api.RetrofitInstance
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import java.io.File

class SitamRepository() {
    //authentication
    suspend fun loginRequest(npm: String, password: String) =
        RetrofitInstance.api.loginRequest(npm, password)

    suspend fun registerRequest(
        npm: String,
        nama: String,
        noTelp: String,
        alamat: String,
        password: String,
        rePassword: String
    ) =
        RetrofitInstance.api.registerRequest(npm, nama, noTelp, alamat, password, rePassword)

    //profile User
    suspend fun profileUserRequest(token: String, identifier: String) =
        RetrofitInstance.api.getProfileMahasiswa(token, identifier)

    //profile dosen
    suspend fun profileDosenRequest(token: String, identifier: String) =
        RetrofitInstance.api.getProfileDosen(token, identifier)

    //chat mahasiswa
    suspend fun chatMahasiswaRequest(token: String, identifier: String) =
        RetrofitInstance.api.getPesanMahasiswa(token, identifier)

    //chat dosen
    suspend fun chatDosenRequest(token: String, identifier: String) =
        RetrofitInstance.api.getPesanDosen(token, identifier)

    suspend fun sendChatDosenRequest(token: String, identifier: String, to: String, pesan: String) =
        RetrofitInstance.api.sendPesanDosen(token, identifier, to, pesan)

    //proposal mahasiswa
    suspend fun proposalMhsRequest(token: String, identifier: String) =
        RetrofitInstance.api.getProposalMhs(token, identifier)

    suspend fun addNewProposal(
        token: String,
        identifier: String,
        judulProposal: String,
        konsentrasi: String,
        topik: String
    ) =
        RetrofitInstance.api.addNewProposal(token, identifier, judulProposal, konsentrasi, topik)

    suspend fun uploadFileProposal(
        token: String,
        idProposal: String,
        catatan: String,
        fileRevisi: MultipartBody.Part,
    ) = RetrofitInstance.api.uploadProposal(token, idProposal, catatan, fileRevisi)

    suspend fun listBimbinganProposal(
        token: String,
        idProposal: Int
    ) =
        RetrofitInstance.api.listBimbinganProposal(token, idProposal)

    suspend fun listProposalDosen(
        token: String,
        identifier: String,
        level: String
    ) =
        RetrofitInstance.api.listProposalDosen(token, identifier, level)

    suspend fun listBimbinganProposalDosen(
        token: String,
        identifier: String,
        idProposal: Int
    ) =
        RetrofitInstance.api.listBimbinganProposalDosen(token, identifier, idProposal)

    suspend fun replyBimbinganProposal(
        token: String,
        idProposal: String,
        by: String,
        catatan: String,
        status: String,
        idBimbingan: Int
    ) = RetrofitInstance.api.replyBimbinganProposal(
        token,
        idProposal,
        by,
        catatan,
        status,
        idBimbingan
    )

    suspend fun getSeminar(token: String, identifier: String) =
        RetrofitInstance.api.getSeminar(token, identifier)

    suspend fun requestDaftarSeminar(token: String, identifier: String, idProposal: String) =
        RetrofitInstance.api.requestDaftarSeminar(token, identifier, idProposal)

    suspend fun requestListBimbinganSeminarMhs(token: String, idSeminar: String, to: String) =
        RetrofitInstance.api.requestListBimbinganSeminar(token, idSeminar, to)

    suspend fun requestBimbinganSeminarDosen(token: String, identifier: String) =
        RetrofitInstance.api.requestBimbinganSeminarDosen(token, identifier)

    suspend fun requestListBimbinganSeminarDosen(token: String, idSeminar: String, alias: String) =
        RetrofitInstance.api.requestListBimbinganSeminarDosen(token, idSeminar, alias)

    suspend fun replyBimbinganSeminar(
        token: String,
        idSeminar: String,
        status: String,
        by: String,
        idBimbingan: Int,
        catatan: String,
        nilai: String?
    ) = RetrofitInstance.api.replyBimbinganSeminar(
        token,
        idSeminar,
        status,
        by,
        idBimbingan,
        catatan,
        nilai
    )

    suspend fun detailBimbinganSeminarMhs(token: String, idSeminar: String) =
        RetrofitInstance.api.detailBimbinganSeminarMhs(token, idSeminar)

    suspend fun requestTugasAkhirMhs(token: String, identifier: String) =
        RetrofitInstance.api.requestTugasAkhirMhs(token, identifier)

    suspend fun requestListBimbinganTaMhs(token: String, idTa: String, to: String) =
        RetrofitInstance.api.requestListBimbinganTaMhs(token, idTa, to)

    suspend fun listTaDosen(token: String, identifier: String) =
        RetrofitInstance.api.requestBimbinganTaMhsViewDosen(token, identifier)

    suspend fun requestListBimbinganTaDosen(token: String, idTa: String, alias: String) =
        RetrofitInstance.api.requestLisyBimbinganTaMhsViewDosen(token, idTa, alias)

    suspend fun replyBimbinganTa(
        token: String,
        idTa: String,
        by: String,
        catatan: String,
        status: String,
        idBimbingan: Int
    ) = RetrofitInstance.api.replyBimbinganTa(token, idTa, by, catatan, status, idBimbingan)

    suspend fun detailBimbinganTaMhs(token: String, idTa: String) =
        RetrofitInstance.api.requesDetailBimbinganTaMhsViewMhs(token, idTa)

    suspend fun requestDataKolokiumMhs(token: String, identifier: String) =
        RetrofitInstance.api.requestDataKolokiumMhs(token, identifier)

    suspend fun reqestDaftarKolokium(token: String, identifier: String, idProposal: String) =
        RetrofitInstance.api.requestDaftarKolokium(token, identifier, idProposal)

    suspend fun requestListBimbinganKolokiumMhs(token: String, idKolokium: String, to: String) =
        RetrofitInstance.api.requestListBimbinganKolokiumMhs(token, idKolokium, to)

    suspend fun requestBimbinganKolokiumDosen(token: String, identifier: String) =
        RetrofitInstance.api.requestBimbinganKolokiumDosen(token, identifier)

    suspend fun requestListBimbinganKolokiumDosen(token: String, idKolokium: String, alias: String) =
        RetrofitInstance.api.requestListBimbinganKolokiumDosen(token, idKolokium, alias)

    suspend fun replyBimbinganKolokium(
        token: String,
        idKolokium: String,
        status: String,
        by: String,
        idBimbingan: Int,
        catatan: String,
        nilai: String?
    ) = RetrofitInstance.api.replyBimbinganKolokium(
        token,
        idKolokium,
        status,
        by,
        idBimbingan,
        catatan,
        nilai
    )

    suspend fun replyBimbinganKolokiumMhs(
        token: String,
        idBimbingan: String
    ) = RetrofitInstance.api.replyBimbinganKolokiumMhs(
        token, idBimbingan)
}