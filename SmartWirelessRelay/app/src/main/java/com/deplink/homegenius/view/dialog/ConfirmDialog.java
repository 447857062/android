package com.deplink.homegenius.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.deplink.homegenius.util.Perfence;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    public ConfirmDialog(Context context) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        p.width = (int) Perfence.dp2px(mContext,290);
        View view = LayoutInflater.from(mContext).inflate(R.layout.confirm_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }

    private TextView textview_cancel;
    private TextView textview_center;
    private TextView textview_title;
    private TextView textview_makesure;
    private void initView() {
        textview_cancel= findViewById(R.id.textview_cancel);
        textview_center= findViewById(R.id.textview_center);
        textview_title= findViewById(R.id.textview_title);
        textview_makesure= findViewById(R.id.textview_makesure);

    }


    private void initEvent() {
        textview_cancel.setOnClickListener(this);
        textview_center.setOnClickListener(this);
    }
    public void setDialogTitleText(String text) {
        textview_title.setText(text);
    }
    public void setDialogMsgText(String text) {
        textview_makesure.setText(text);

    }


    private onSureBtnClickListener mOnSureBtnClickListener;
    public void setSureBtnClickListener(onSureBtnClickListener mOnSureBtnClickListener) {

        this.mOnSureBtnClickListener = mOnSureBtnClickListener;
    }
    public interface onSureBtnClickListener {
        void onSureBtnClicked();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_center:
                this.dismiss();
                mOnSureBtnClickListener.onSureBtnClicked();
                break;
            case R.id.textview_cancel:
                this.dismiss();
                break;
        }
    }
    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        super.show();

    }

}
