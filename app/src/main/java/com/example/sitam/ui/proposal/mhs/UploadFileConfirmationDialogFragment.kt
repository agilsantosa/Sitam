package com.example.sitam.ui.proposal.mhs

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
import com.example.sitam.ui.proposal.mhs.viewmodel.DetailProposalViewModel
import com.example.sitam.utils.Constants
import com.example.sitam.utils.SharedPreferenceProvider
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.upload_file_confirmation_dialog_fragment.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class UploadFileConfirmationDialogFragment(
    private val uri: Uri,
    private val filename: String
) : DialogFragment() {
    private lateinit var detailProposalViewModel: DetailProposalViewModel
    private lateinit var preferenceProvider: SharedPreferenceProvider
    private var onUploadDialogListener: OnUploadDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.upload_file_confirmation_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailProposalViewModel =
            (activity as DetailBimbinganProposalMhsActivity).detailProposalViewModel

        preferenceProvider = SharedPreferenceProvider(requireActivity().applicationContext)
        val token = preferenceProvider.getTokenUser(Constants.KEY_TOKEN_USER).toString()
        val idProposal = preferenceProvider.getIdProposal(Constants.KEY_ID_PROPOSAL)
        upload_file_confirmation_material_name_edit_text.setText(filename)

        upload_file_confirmation_upload_button.setOnClickListener {
            val materialNote = upload_file_confirmation_note_edit_text.text.toString()
            if (materialNote.isNotEmpty()) {
                uploadFile(token, idProposal!!, materialNote)
            } else {
                Toast.makeText(context, "Field Cannot be Empty", Toast.LENGTH_SHORT).show()
            }
        }
        upload_file_confirmation_cancel_button.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun uploadFile(token: String, idProposal: String, catatan: String) {
        val url: String = BuildConfig.URL_BASE + "/api/mahasiswa/proposal/bimbingan/create"
        var message: String? = null
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.setForceMultipartEntityContentType(true)
        try {
            params.put("token", token)
            params.put("id_proposal", idProposal)
            params.put(
                "file_revisi",
                activity?.contentResolver?.openInputStream(uri),
                "application/pdf"
            )
            params.put("catatan", catatan)
        } catch (e: IOException) {
            e.message
        }

        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onStart() {
                super.onStart()
                upload_file_confirmation_progress_bar.visibility = View.VISIBLE
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

                    if (onUploadDialogListener != null) {
                        onUploadDialogListener?.onMessageUpload(status)
                    }
                    dialog?.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                super.onFinish()
                upload_file_confirmation_progress_bar.visibility = View.GONE
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                try {
                    val responBody = String(responseBody)
                    val jsonObject = JSONObject(responBody)
                    val status = jsonObject.getString("message")
                    if (onUploadDialogListener != null) {
                        onUploadDialogListener?.onMessageUpload(status)
                    }
                    dialog?.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    interface OnUploadDialogListener {
        fun onMessageUpload(text: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = activity
        if (fragment is DetailBimbinganProposalMhsActivity) {
            val detailCategoryFragment = fragment
            this.onUploadDialogListener = detailCategoryFragment.onUploadDialog
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.onUploadDialogListener = null
    }

}