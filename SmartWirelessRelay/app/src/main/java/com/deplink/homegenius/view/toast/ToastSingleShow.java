package com.deplink.homegenius.view.toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * toast显示太过频繁
 * Created by Administrator on 2017/9/1.
 */
public class ToastSingleShow {
    // 构造方法私有化 不允许new对象
    private ToastSingleShow() {
    }

    // Toast对象
    private static Toast toast = null;

    /**
     * 显示Toast
     */
    public static void showText(Context context, String text) {
        View v = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView textView = (TextView) v.findViewById(R.id.textview_toast);
        if (toast == null) {
            textView.setText(text);
            toast = new Toast(context);
            toast.setView(v);
        }
        textView.setText(text);
        toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
