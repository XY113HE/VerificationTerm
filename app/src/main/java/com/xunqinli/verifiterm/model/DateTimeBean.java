package com.xunqinli.verifiterm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DateTimeBean implements Parcelable {
    private String time;

    public DateTimeBean(String time) {
        this.time = time;
    }

    protected DateTimeBean(Parcel in) {
        time = in.readString();
    }

    public static final Creator<DateTimeBean> CREATOR = new Creator<DateTimeBean>() {
        @Override
        public DateTimeBean createFromParcel(Parcel in) {
            return new DateTimeBean(in);
        }

        @Override
        public DateTimeBean[] newArray(int size) {
            return new DateTimeBean[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
    }
}
