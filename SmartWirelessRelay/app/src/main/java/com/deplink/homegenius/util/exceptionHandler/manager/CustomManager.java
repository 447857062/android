package com.deplink.homegenius.util.exceptionHandler.manager;

import android.content.Context;

/**
 * Created by Administrator on 2017/11/17.
 */


public class CustomManager {
    private static CustomManager sInstance;
    private Context mContext;

    private CustomManager(Context context) {
        mContext = context;
    }

    public static CustomManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CustomManager(context);
        }

        return sInstance;
    }

}

