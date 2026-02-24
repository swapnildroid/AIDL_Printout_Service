// IMyAidlInterface.aidl
package com.example.aidlprintoutservice;

import com.example.aidlprintoutservice.MyData;
import com.example.aidlprintoutservice.IMyCallback;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void sendData(in MyData data);

    oneway void sendDataWithCallback(in MyData data, IMyCallback callback);
}