package com.deplink.homegenius.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.deplink.homegenius.util.Perfence;

import java.lang.ref.WeakReference;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class MakeSureDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Button btn_sure;
    private Button btn_cancel;
    private TextView textview_unbind_device_type;
    private onSureBtnClickListener mOnSureBtnClickListener;

    private onCancelBtnClickListener mOnCancelBtnClickListener;
    private TextView textivew_msg;
    WeakReference<Activity> activityWeakRef;
    public MakeSureDialog(Context context) {
        super(context, R.style.MakeSureDialog);
        activityWeakRef = new WeakReference<>((Activity) context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        p.width = (int) Perfence.dp2px(mContext,283);
        p.height = (int) Perfence.dp2px(mContext,185);
        View view = LayoutInflater.from(mContext).inflate(R.layout.makesure_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
    }


    private void initView() {
        btn_sure = findViewById(R.id.btn_sure);
        btn_cancel = findViewById(R.id.btn_cancel);
        textview_unbind_device_type = findViewById(R.id.textview_unbind_device_type);
        textivew_msg = findViewById(R.id.textivew_msg);


    }

    public void setTitleText(String text){
        if (activityWeakRef != null  && !activityWeakRef.get().isFinishing()) {
            textview_unbind_device_type.setText(text);
        }

    }
    public void setMsg(String text){
        if (activityWeakRef != null  && !activityWeakRef.get().isFinishing()) {
            textivew_msg.setText(text);
        }

    }
    private void initEvent() {
        btn_sure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                this.dismiss();
                mOnSureBtnClickListener.onSureBtnClicked();
                break;
            case R.id.btn_cancel:
               if(mOnCancelBtnClickListener!=null){
                   mOnCancelBtnClickListener.onCancelBtnClicked();
               }
                this.dismiss();
                break;
        }
    }


    public interface onSureBtnClickListener {
         void onSureBtnClicked();
    }
    public interface onCancelBtnClickListener {
         void onCancelBtnClicked();
    }

    @Override
    public void show() {
        if (activityWeakRef != null  && !activityWeakRef.get().isFinishing()) {
            super.show();
        }
    }
    public void setSureBtnClickListener(onSureBtnClickListener mOnSureBtnClickListener) {

        this.mOnSureBtnClickListener = mOnSureBtnClickListener;
    }

}
