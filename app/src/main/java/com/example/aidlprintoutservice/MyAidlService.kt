package com.example.aidlprintoutservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyAidlService: Service() {

    private val binder: IMyAidlInterface.Stub = object : IMyAidlInterface.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            Log.d(
                "TAGGED",
                "basicTypes() called with: anInt = $anInt, aLong = $aLong, aBoolean = $aBoolean, aFloat = $aFloat, aDouble = $aDouble, aString = $aString"
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("TAGGED", "onBind() called with: intent = $intent")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            "TAGGED",
            "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        return super.onStartCommand(intent, flags, startId)
    }
}