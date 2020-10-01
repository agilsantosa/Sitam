package com.example.sitam.ui.kolokium.mhs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.sitam.BuildConfig
import com.example.sitam.R
import com.example.sitam.utils.Constants
import com.example.sitam.utils.SharedPreferenceProvider
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.upload_file_kolokium_dialog_fragment.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class UploadFileKolokiumDialogFragment(
    private val uri: Uri,
    private val filename: String,
    private val tabPosition: Int,
) : DialogFragment() {
    private var onUploadKolokiumDialogListener: OnUploadKolokiumDialogListener? = null
    private lateinit var preferenceProvider: SharedPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.upload_file_kolokium_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val idKolokium = preferenceProvider.getIdKolokium(Constants.KEY_ID_KOLOKIUM).toString()
        upload_file_confirmation_kolokium_material_name_edit_text.setText(filename)

        upload_file_kolokium_confirmation_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }

        upload_file_kolokium_confirmation_upload_button.setOnClickListener {
            val materialNote = upload_file_confirmation_note_edit_text.text.toString()
            if (materialNote.isNotEmpty()) {
                uploadFile(token, idKolokium, materialNote)
            } else {
                Toast.makeText(context, "Field Cannot be Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFile(token: String, idKolokium: String, catatan: String){
        val url: String = BuildConfig.URL_BASE + "/api/mahasiswa/kolokium/bimbingan/reply"
        var message: String? = null
        val client = AsyncHttpClient()
        val params = RequestParams()
        val to: String = when (tabPosition) {
            0 -> "Pembimbing 1"
            1 -> "Pembimbing 2"
            2 -> "Penguji 1"
            else -> "Penguji 2"
        }

        Log.i("TAG", "hasilnya: $to")
        params.setForceMultipartEntityContentType(true)
        try {
            params.put("token", token)
            params.put("id_kolokium", idKolokium)
            params.put(
                "file_revisi",
                activity?.contentResolver?.openInputStream(uri),
                "application/pdf"
            )
            params.put("catatan", catatan)
            params.put("by", "Mahasiswa")
            params.put("to", to)
            params.put("status", "Menunggu Konfirmasi")

        } catch (e: IOException) {
            e.message
        }

        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onStart() {
                super.onStart()
                upload_file_kolokium_confirmation_progress_bar.visibility = View.VISIBLE
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    val responBody = String(responseBody)
                    val jsonObject = JSONObject(responBody)
                    val status = jsonObject.getString("message")
                    Log.d("msg", jsonObject.getString("data"))

                    if (onUploadKolokiumDialogListener != null) {
                        onUploadKolokiumDialogListener?.onMessageUpload(status)
                    }
                    dialog?.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                try {
                    val responBody = String(responseBody)
                    val jsonObject = JSONObject(responBody)
                    val status = jsonObject.getString("message")
                    Log.d("msg", jsonObject.getString("data"))

                    if (onUploadKolokiumDialogListener != null) {
                        onUploadKolokiumDialogListener?.onMessageUpload(status)
                    }
                    dialog?.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = activity
        if (fragment is KolokiumMahasiswaActivity) {
            val kolokiumMahasiswaActivity = fragment
            this.onUploadKolokiumDialogListener = kolokiumMahasiswaActivity.onUploadDialog
        }
    }

    interface OnUploadKolokiumDialogListener {
        fun onMessageUpload(text: String)
    }

    override fun onDetach() {
        super.onDetach()
        this.onUploadKolokiumDialogListener = null
    }
}