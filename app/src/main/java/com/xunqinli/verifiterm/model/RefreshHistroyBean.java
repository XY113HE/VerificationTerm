package com.xunqinli.verifiterm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RefreshHistroyBean implements Parcelable {
    private List<VerificationNotifyBean> list;

    public RefreshHistroyBean(List<VerificationNotifyBean> list){
        this.list = list;
    }


    protected RefreshHistroyBean(Parcel in) {
        list = in.createTypedArrayList(VerificationNotifyBean.CREATOR);
    }

    public static final Creator<RefreshHistroyBean> CREATOR = new Creator<RefreshHistroyBean>() {
        @Override
        public RefreshHistroyBean createFromParcel(Parcel in) {
            return new RefreshHistroyBean(in);
        }

        @Override
        public RefreshHistroyBean[] newArray(int size) {
            return new RefreshHistroyBean[size];
        }
    };

    public List<VerificationNotifyBean> getList() {
        return list;
    }

    public void setList(List<VerificationNotifyBean> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(list);
    }
}
