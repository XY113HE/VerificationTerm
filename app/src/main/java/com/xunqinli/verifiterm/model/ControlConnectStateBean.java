package com.xunqinli.verifiterm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ControlConnectStateBean implements Parcelable {
    private boolean isConnect;

    public ControlConnectStateBean(boolean isConnect) {
        this.isConnect = isConnect;
    }

    protected ControlConnectStateBean(Parcel in) {
        isConnect = in.readByte() != 0;
    }

    public static final Creator<ControlConnectStateBean> CREATOR = new Creator<ControlConnectStateBean>() {
        @Override
        public ControlConnectStateBean createFromParcel(Parcel in) {
            return new ControlConnectStateBean(in);
        }

        @Override
        public ControlConnectStateBean[] newArray(int size) {
            return new ControlConnectStateBean[size];
        }
    };

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isConnect ? 1 : 0));
    }
}
