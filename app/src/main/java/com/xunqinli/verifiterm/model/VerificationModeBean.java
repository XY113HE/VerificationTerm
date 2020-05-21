package com.xunqinli.verifiterm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VerificationModeBean implements Parcelable {
    private String model;//0 自动 1手动

    public VerificationModeBean(String model){
        this.model = model;
    }

    protected VerificationModeBean(Parcel in) {
        model = in.readString();
    }

    public static final Creator<VerificationModeBean> CREATOR = new Creator<VerificationModeBean>() {
        @Override
        public VerificationModeBean createFromParcel(Parcel in) {
            return new VerificationModeBean(in);
        }

        @Override
        public VerificationModeBean[] newArray(int size) {
            return new VerificationModeBean[size];
        }
    };

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(model);
    }
}
