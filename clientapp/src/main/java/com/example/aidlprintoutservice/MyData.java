package com.example.aidlprintoutservice;

import android.os.Parcel;
import android.os.Parcelable;

// src/main/java/com/example/MyData.java
public class MyData implements Parcelable {
    public String name;
    public int age;

    @Override
    public String toString() {
        return "MyData{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public MyData(String name, int age) {
        this.name = name;
        this.age = age;
    }

    protected MyData(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<MyData> CREATOR = new Creator<>() {
        @Override public MyData createFromParcel(Parcel in) { return new MyData(in); }
        @Override public MyData[] newArray(int size) { return new MyData[size]; }
    };
}