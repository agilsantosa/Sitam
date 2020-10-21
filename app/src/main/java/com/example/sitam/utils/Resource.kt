package com.example.sitam.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val success: Boolean? = null
) {
    class Succes<T>(message: String ,data: T) : Resource<T>(data, message)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}