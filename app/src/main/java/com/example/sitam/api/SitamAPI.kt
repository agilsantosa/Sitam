package com.example.sitam.api

import com.example.sitam.models.auth.ResponseAuth
import com.example.sitam.models.kolokium.*
import com.example.sitam.models.pesan.ResponseChat
import com.example.sitam.models.pesan.ResponseSendChatDosen
import com.example.sitam.models.profile.ResponseProfileDosen
import com.example.sitam.models.profile.ResponseProfileMhs
import com.example.sitam.models.proposal.ResponseBimbinganProposal
import com.example.sitam.models.proposal.ResponseProposal
import com.example.sitam.models.proposal.ResponseProposalDosen
import com.example.sitam.models.proposal.ResponseReplyProposal
import com.example.sitam.models.seminar.*
import com.example.sitam.models.ta.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface SitamAPI {

    //authentication
    @FormUrlEncoded
    @POST("/api/login")
    suspend fun loginRequest(
        @Field("identifier")
        npm: String,
        @Field("password")
        password: String
    ): Response<ResponseAuth>

    @FormUrlEncoded
    @POST("/api/register")
    suspend fun registerRequest(
        @Field("npm")
        npm: String,
        @Field("nama")
        nama: String,
        @Field("no_telp")
        noTelp: String,
        @Field("alamat")
        alamat: String,
        @Field("password")
        password: String,
        @Field("retype_password")
        rePassword: String,
    ): Response<ResponseAuth>

    //getProfile
    @FormUrlEncoded
    @POST("/api/mahasiswa/profil/view")
    suspend fun getProfileMahasiswa(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String
    ): Response<ResponseProfileMhs>

    //
    @FormUrlEncoded
    @POST("/api/dosen/profil/view")
    suspend fun getProfileDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String
    ): Response<ResponseProfileDosen>

    //getpesan
    @FormUrlEncoded
    @POST("/api/mahasiswa/pesan/get")
    suspend fun getPesanMahasiswa(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String
    ): Response<ResponseChat>

    @FormUrlEncoded
    @POST("/api/dosen/pesan/get")
    suspend fun getPesanDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String
    ): Response<ResponseChat>

    @FormUrlEncoded
    @POST("/api/dosen/pesan/send")
    suspend fun sendPesanDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
        @Field("to")
        to: String,
        @Field("pesan")
        pesan: String,
    ): Response<ResponseSendChatDosen>

    //getProposal
    @FormUrlEncoded
    @POST("/api/mahasiswa/proposal/get")
    suspend fun getProposalMhs(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String
    ): Response<ResponseProposal>

    //registerProposal
    @FormUrlEncoded
    @POST("/api/mahasiswa/proposal/create")
    suspend fun addNewProposal(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
        @Field("judul_proposal")
        judulProposal: String,
        @Field("konsentrasi")
        konsentrasi: String,
        @Field("topik")
        topik: String
    ): Response<ResponseProposal>

    //uploadProposalMahasiswa
    @Multipart
    @POST("/api/mahasiswa/proposal/bimbingan/create")
    suspend fun uploadProposal(
        @Part("token")
        token: String,
        @Part("id_proposal")
        idProposal: String,
        @Part("catatan")
        catatan: String,
        @Part file_revisi: MultipartBody.Part
    ): Response<ResponseProposal>

    @FormUrlEncoded
    @POST("/api/mahasiswa/proposal/bimbingan/list")
    suspend fun listBimbinganProposal(
        @Field("token")
        token: String,
        @Field("id_proposal")
        idProposal: Int,
    ): Response<ResponseBimbinganProposal>

    @FormUrlEncoded
    @POST("/api/dosen/proposal/list")
    suspend fun listProposalDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
        @Field("level")
        level: String
    ): Response<ResponseProposalDosen>

    @FormUrlEncoded
    @POST("/api/dosen/proposal/bimbingan/view")
    suspend fun listBimbinganProposalDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
        @Field("id_proposal")
        idProposal: Int
    ): Response<ResponseBimbinganProposal>

    @FormUrlEncoded
    @POST("/api/dosen/proposal/bimbingan/reply")
    suspend fun replyBimbinganProposal(
        @Field("token")
        token: String,
        @Field("id_proposal")
        idProposal: String,
        @Field("by")
        by: String,
        @Field("catatan")
        catatan: String,
        @Field("status")
        status: String,
        @Field("reply_to")
        idBimbingan: Int
    ): Response<ResponseReplyProposal>

    @FormUrlEncoded
    @POST("/api/mahasiswa/seminar/get")
    suspend fun getSeminar(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
    ): Response<ResponseSeminarMhs>

    @FormUrlEncoded
    @POST("/api/mahasiswa/seminar/register")
    suspend fun requestDaftarSeminar(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
        @Field("id_proposal")
        idProposal: String
    ): Response<ResponseDaftarSeminar>

    @FormUrlEncoded
    @POST("/api/mahasiswa/seminar/bimbingan/list")
    suspend fun requestListBimbinganSeminar(
        @Field("token")
        token: String,
        @Field("id_seminar")
        idSeminar: String,
        @Field("to")
        to: String
    ): Response<ResponseBimbinganSeminar>
//

    @FormUrlEncoded
    @POST("/api/dosen/seminar/proposal/list")
    suspend fun requestBimbinganSeminarDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
    ): Response<ResponseBimbinganSeminarDosen>

    //
    @FormUrlEncoded
    @POST("/api/dosen/seminar/bimbingan/view")
    suspend fun requestListBimbinganSeminarDosen(
        @Field("token")
        token: String,
        @Field("id_seminar")
        idSeminar: String,
        @Field("as")
        alias: String
    ): Response<ResponseListBimbinganSeminarDosen>


    @FormUrlEncoded
    @POST("/api/dosen/seminar/bimbingan/reply")
    suspend fun replyBimbinganSeminar(
        @Field("token")
        token: String,
        @Field("id_seminar")
        idSeminar: String,
        @Field("status")
        status: String,
        @Field("by")
        by: String,
        @Field("reply_to")
        idBimbingan: Int,
        @Field("catatan")
        catatan: String,
        @Field("nilai")
        nilai: String?
    ): Response<ResponseSeminarReply>

    @FormUrlEncoded
    @POST("/api/mahasiswa/seminar/bimbingan/view")
    suspend fun detailBimbinganSeminarMhs(
        @Field("token")
        token: String,
        @Field("id")
        idSeminar: String,
    ): Response<ResponseDetailSeminarMhs>

    @FormUrlEncoded
    @POST("/api/mahasiswa/ta/get")
    suspend fun requestTugasAkhirMhs(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
    ): Response<ResponseTugasAkhirMhs>

    @FormUrlEncoded
    @POST("/api/mahasiswa/ta/bimbingan/list")
    suspend fun requestListBimbinganTaMhs(
        @Field("token")
        token: String,
        @Field("id_ta")
        idTa: String,
        @Field("to")
        to: String
    ): Response<ResponseListBimbinganTaMhs>

    @FormUrlEncoded
    @POST("/api/dosen/ta/list")
    suspend fun requestBimbinganTaMhsViewDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
    ): Response<ResponseDataTugasAkhirDosen>

    @FormUrlEncoded
    @POST("/api/dosen/ta/bimbingan/view")
    suspend fun requestLisyBimbinganTaMhsViewDosen(
        @Field("token")
        token: String,
        @Field("id_ta")
        id_ta: String,
        @Field("as")
        alias: String
    ): Response<ResponseListBimbinganTaMhs>
//

    @FormUrlEncoded
    @POST("/api/dosen/ta/bimbingan/reply")
    suspend fun replyBimbinganTa(
        @Field("token")
        token: String,
        @Field("id_ta")
        idProposal: String,
        @Field("by")
        by: String,
        @Field("catatan")
        catatan: String,
        @Field("status")
        status: String,
        @Field("reply_to")
        idBimbingan: Int
    ): Response<ResponseReplyTa>

    @FormUrlEncoded
    @POST("/api/mahasiswa/ta/bimbingan/view")
    suspend fun requesDetailBimbinganTaMhsViewMhs(
        @Field("token")
        token: String,
        @Field("id")
        id_ta: String
    ): Response<ResponseDetailBimbinganTaMhs>

    @FormUrlEncoded
    @POST("/api/mahasiswa/kolokium/get")
    suspend fun requestDataKolokiumMhs(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String
    ): Response<ResponseDataKolokiumMhs>

    @FormUrlEncoded
    @POST("/api/mahasiswa/kolokium/register")
    suspend fun requestDaftarKolokium(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
        @Field("id_proposal")
        idProposal: String
    ): Response<ResponseRegisterKolokiumMhs>

    @FormUrlEncoded
    @POST("/api/mahasiswa/kolokium/bimbingan/list")
    suspend fun requestListBimbinganKolokiumMhs(
        @Field("token")
        token: String,
        @Field("id_kolokium")
        identifier: String,
        @Field("to")
        to: String
    ): Response<ResponseListBimbinganKolokiumMhs>

    @FormUrlEncoded
    @POST("/api/dosen/kolokium/list")
    suspend fun requestBimbinganKolokiumDosen(
        @Field("token")
        token: String,
        @Field("identifier")
        identifier: String,
    ): Response<ResponseDataBimbinganKolokiumDosen>

    @FormUrlEncoded
    @POST("/api/dosen/kolokium/bimbingan/view")
    suspend fun requestListBimbinganKolokiumDosen(
        @Field("token")
        token: String,
        @Field("id_kolokium")
        idKolokium: String,
        @Field("as")
        alias: String
    ): Response<ResponseListBimbinganKolokiumDosen>
//

    @FormUrlEncoded
    @POST("/api/dosen/kolokium/bimbingan/reply")
    suspend fun replyBimbinganKolokium(
        @Field("token")
        token: String,
        @Field("id_kolokium")
        idSeminar: String,
        @Field("status")
        status: String,
        @Field("by")
        by: String,
        @Field("reply_to")
        idBimbingan: Int,
        @Field("catatan")
        catatan: String,
        @Field("nilai")
        nilai: String?
    ): Response<ResponseSeminarReply>

//

    @FormUrlEncoded
    @POST("/api/mahasiswa/kolokium/bimbingan/view")
    suspend fun replyBimbinganKolokiumMhs(
        @Field("token")
        token: String,
        @Field("id")
        idBimbingan: String
    ): Response<ResponseReplyBimbinganKolokiumMhs>
}