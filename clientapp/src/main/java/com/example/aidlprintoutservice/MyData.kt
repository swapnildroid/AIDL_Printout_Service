package com.example.aidlprintoutservice

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyData(
    var name: String,
    var age: Int
) : Parcelable
