package com.example.sitam.utils

import android.content.Context

class SharedPreferenceProvider(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("myPreferences", 0)

    fun saveLevelUser(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getLevelUser(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveTokenUser(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getTokenUser(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveIdentifierUser(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getIdentifierUser(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveIdProposal(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getIdProposal(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveIdSeminar(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getIdSeminar(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveIdTa(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getIdTa(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveIdKolokium(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getIdKolokium(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun saveAlias(key: String, value: String){
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getAlias(key: String): String?{
        return sharedPreferences.getString(key, null)
    }

    fun clearSharedPreference(){
        sharedPreferences.edit().clear().apply()
    }
}