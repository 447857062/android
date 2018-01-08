package com.deplink.homegenius.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by benond on 2017/2/7.
 */


public class SharedPreference {

    private static final String TAG = "SharedPreference";
    private Context mContext;
    private SharedPreferences share;
    private SharedPreferences.Editor editor;

    public SharedPreference(Context context, String name) {
        this.mContext = context;
        saveData(name);
    }

    private void saveData(String name) {
        share = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = share.edit();
    }

    public void clearSharedPreferences() {
        editor.clear().commit();
    }

    public void saveBoolean(String name, Boolean data) {
        editor.putBoolean(name, data);
        editor.commit();
    }

    public void saveString(String name, String data) {
        editor.putString(name, data);
        editor.commit();
    }

    public void saveInt(String name, int data) {
        editor.putInt(name, data);
        editor.commit();
    }

    public void clearData() {
        editor.clear().commit();
    }

    public String getString(String name) {
        return share.getString(name, null);
    }

    public Boolean getBoolean(String name) {
        return share.getBoolean(name, false);
    }

    public int getInt(String name) {
        return share.getInt(name, -1);
    }

}
