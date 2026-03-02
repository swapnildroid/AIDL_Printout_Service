package com.example.aidlprintoutservice

import android.app.Application

class MyApp: Application() {

    companion object {

        var instance: MyApp? = null

    }

    init {
        instance = this
    }

    var appDatabase: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getInstance(this)
    }
}