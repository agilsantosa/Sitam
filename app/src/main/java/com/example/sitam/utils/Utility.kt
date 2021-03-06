package com.example.sitam.utils

import android.app.Application
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.sitam.models.Material
import java.text.SimpleDateFormat
import java.util.*


object Utility {
    const val EXTRA_SUBJECT_NAME = "subject_name"
    const val EXTRA_SUBJECT_CODE = "subject_code"
    const val EXTRA_SUBJECT_ID = "subject_id"
    const val EXTRA_SESSION_KEY = "session_id"
    const val EXTRA_USER_ROLE = "user_role"
    const val EXTRA_GRADE = "user_grade"
    const val EXTRA_JID = "jid"
    const val LOG_DEBUG_TAG = "MySchool"

    enum class ConnectServerState {
        UNKNOWN,
        LOADING,
        SUCCESS,
        CONNECTION_ERROR
    }

    enum class DataLoadState {
        LOADED,
        UNLOADED,
        LOADING,
        ERROR
    }

    enum class NetworkState {
        CONNECTED,
        DISCONNECTED,
        UNAVAILABLE
    }

    enum class UploadFileState {
        IDLE,
        UPLOADING,
        UPLOADED,
        FAILED,
        CANCELED
    }

    fun Date?.convertToString() : String {
        return if (this != null) {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            simpleDateFormat.format(this)
        } else {
            ""
        }
    }


    fun getCurrentStringDate() : String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(calendar.time)
    }

    fun getMimeTypeFromUri(app: Application, uri: Uri) : String? {
        val contentResolver = app.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return contentResolver.getType(uri)
    }

    interface IAddNewChannelItem {
        fun onSendButtonClicked(message: String)
        fun onCancelButtonClicked()
    }

    interface IUploadFile {
        fun onUploadButtonClicked()
        fun onCancelButtonClicked()
    }

    interface IAddNewSession {
        fun onAddSessionButtonClicked(topic: String, description: String)
        fun onCancelSessionButtonClicked()
    }

    sealed class AddContactToRoasterState {
        class Loading(val adapterPosition: Int) : AddContactToRoasterState()
        class Finished(val adapterPosition: Int, val name: String) : AddContactToRoasterState()
        class Failed(val errorMessage: String, val adapterPosition: Int) : AddContactToRoasterState()
        object Idle : AddContactToRoasterState()
    }

    sealed class ChatFilterState {
        class FilterConversation(val constraint: String) : ChatFilterState()
        class FilterContact(val constraint: String) : ChatFilterState()
        object NoFilter : ChatFilterState()
    }

    sealed class SearchViewState {
        object Opened : SearchViewState()
        object Closed : SearchViewState()
    }

    sealed class ClassroomSessionEvent {
        object SessionAttendanceDeleteSuccess : ClassroomSessionEvent()
        class SessionAttendanceDeleteFailed(val errorMessage: String) : ClassroomSessionEvent()
        object SessionDeleteSuccess : ClassroomSessionEvent()
        class SessionDeleteFailed(val errorMessage: String) : ClassroomSessionEvent()
        object CloseSessionSuccess : ClassroomSessionEvent()
        class CloseSessionFailed(val errorMessage: String) : ClassroomSessionEvent()
        object SubmitAttendanceSuccess : ClassroomSessionEvent()
        class SubmitAttendanceFailed(val errorMessage: String) : ClassroomSessionEvent()
        object Idle : ClassroomSessionEvent()
    }

    sealed class ClassroomDetailPopupMenuCallback {
        class OnDownloadClicked(val item: Material) : ClassroomDetailPopupMenuCallback()
        class OnDeleteClicked(val item: Any) : ClassroomDetailPopupMenuCallback()
    }
}