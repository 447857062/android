package com.deplink.sdk.android.sdk.json.homegenius;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${kelijun} on 2018/1/16.
 */
public class DoorBellResponseBody {
   private List<DoorBellItem>item=new ArrayList<>();
    private int  errcode;
    private String  msg;

    public List<DoorBellItem> getItem() {
        return item;
    }

    public void setItem(List<DoorBellItem> item) {
        this.item = item;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
