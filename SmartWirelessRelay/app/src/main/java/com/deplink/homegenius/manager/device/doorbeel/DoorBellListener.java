package com.deplink.homegenius.manager.device.doorbeel;

import android.graphics.Bitmap;

import com.deplink.sdk.android.sdk.json.homegenius.DoorBellItem;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
public abstract class DoorBellListener {

    public  void responseVisitorListResult(List<DoorBellItem> list){

    };
    public  void responseVisitorImage(Bitmap bitmap,int count){

    };
    public  void responseDeleteRecordHistory(boolean success){

    };
}
