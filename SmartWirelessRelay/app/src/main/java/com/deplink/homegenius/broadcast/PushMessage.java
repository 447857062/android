package com.deplink.homegenius.broadcast;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${kelijun} on 2018/1/30.
 */
public class PushMessage implements Parcelable{
    private String bell_uid;
    private String bell_name;
    private String bell_mac;
    private long timestamp;
    private String file;

    protected PushMessage(Parcel in) {
        bell_uid = in.readString();
        bell_name = in.readString();
        bell_mac = in.readString();
        timestamp = in.readLong();
        file = in.readString();
    }

    public static final Creator<PushMessage> CREATOR = new Creator<PushMessage>() {
        @Override
        public PushMessage createFromParcel(Parcel in) {
            return new PushMessage(in);
        }

        @Override
        public PushMessage[] newArray(int size) {
            return new PushMessage[size];
        }
    };

    public String getBell_uid() {
        return bell_uid;
    }

    public void setBell_uid(String bell_uid) {
        this.bell_uid = bell_uid;
    }

    public String getBell_name() {
        return bell_name;
    }

    public void setBell_name(String bell_name) {
        this.bell_name = bell_name;
    }

    public String getBell_mac() {
        return bell_mac;
    }

    public void setBell_mac(String bell_mac) {
        this.bell_mac = bell_mac;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bell_uid);
        parcel.writeString(bell_name);
        parcel.writeString(bell_mac);
        parcel.writeLong(timestamp);
        parcel.writeString(file);
    }
}
