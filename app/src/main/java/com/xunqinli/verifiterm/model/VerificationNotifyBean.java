package com.xunqinli.verifiterm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VerificationNotifyBean implements Parcelable {
    /**
     * num 核销序号
     * name 客户名称
     * carNum 车牌号
     * phone 手机号
     * dateTime 核销时间
     * operater 核销的洗车工
     */
    private int num;
    private String name;
    private String carNum;
    private String phone;
    private String dateTime;
    private String operater;

    private boolean isConfirm;

    public VerificationNotifyBean(int num, String name, String carNum, String phone, String dateTime, String operater) {
        this.num = num;
        this.name = name;
        this.carNum = carNum;
        this.phone = phone;
        this.dateTime = dateTime;
        this.operater = operater;
    }

    protected VerificationNotifyBean(Parcel in) {
        num = in.readInt();
        name = in.readString();
        carNum = in.readString();
        phone = in.readString();
        dateTime = in.readString();
        operater = in.readString();
        isConfirm = in.readBoolean();
    }

    public static final Creator<VerificationNotifyBean> CREATOR = new Creator<VerificationNotifyBean>() {
        @Override
        public VerificationNotifyBean createFromParcel(Parcel in) {
            return new VerificationNotifyBean(in);
        }

        @Override
        public VerificationNotifyBean[] newArray(int size) {
            return new VerificationNotifyBean[size];
        }
    };

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOperater() {
        return operater;
    }

    public void setOperater(String operater) {
        this.operater = operater;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(num);
        parcel.writeString(name);
        parcel.writeString(carNum);
        parcel.writeString(phone);
        parcel.writeString(dateTime);
        parcel.writeString(operater);
        parcel.writeBoolean(isConfirm);
    }
}
