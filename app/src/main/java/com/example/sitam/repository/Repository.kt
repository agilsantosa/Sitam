package com.example.sitam.repository

import android.app.Application
import com.example.sitam.utils.ConnectivityInterceptor

class Repository(app: Application) {

    private val interceptor = ConnectivityInterceptor(app)

}