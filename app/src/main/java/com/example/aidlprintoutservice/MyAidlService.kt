package com.example.aidlprintoutservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyAidlService: Service() {

    private val binder: IMyAidlInterface.Stub = object : IMyAidlInterface.Stub() {

        private var myStatus: Int = MyStatus.UNKNOWN

        override fun getStatus(): Int {
            Log.i("TAGGED", "MyAidlService::getStatus: $myStatus")
            return myStatus
        }

        override fun setStatus(status: Int) {
            Log.d("TAGGED", "setStatus() called with: status = $status")
            myStatus = status
        }

        override fun sendDataWithCallback(
            data: MyData?,
            callback: IMyCallback?
        ) {
            Log.d(
                "TAGGED",
                "sendDataWithCallback() called with: data = $data, callback = $callback"
            )
            Log.i("TAGGED", "MyAidlService::sendDataWithCallback: ${Thread.currentThread().name}")
            for (i in 1 until 20){
                Log.i("TAGGED", "MyAidlService::sendDataWithCallback: sleeping: $i")
                Thread.sleep(1000)
            }
            callback?.onResult(data?.name)
        }

        override fun sendData(data: MyData?) {
            Log.d("TAGGED", "sendData() called with: data = $data")
        }

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