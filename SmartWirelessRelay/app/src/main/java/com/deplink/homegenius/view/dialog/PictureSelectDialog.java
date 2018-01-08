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
import android.widget.RelativeLayout;

import com.deplink.homegenius.util.Perfence;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class PictureSelectDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout layout_from_album;
    private RelativeLayout layout_picture;
    private RelativeLayout layout_cancel;
    public PictureSelectDialog(Context context) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        p.width = (int) Perfence.dp2px(mContext,283);
        View view = LayoutInflater.from(mContext).inflate(R.layout.picture_select_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
    }
    private void initView() {
        layout_from_album= findViewById(R.id.layout_from_album);
        layout_picture= findViewById(R.id.layout_picture);
        layout_cancel= findViewById(R.id.layout_cancel);

    }


    private void initEvent() {
        layout_from_album.setOnClickListener(this);
        layout_picture.setOnClickListener(this);
        layout_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_picture:
                mOnModeSelectClickListener.onModeSelect("picture");
                this.dismiss();
                break;
            case R.id.layout_from_album:
                mOnModeSelectClickListener.onModeSelect("from_album");
                this.dismiss();
                break;

            case R.id.layout_cancel:
                this.dismiss();
                break;
        }
    }

    private onModeSelectClickListener mOnModeSelectClickListener;

    public void setmOnModeSelectClickListener(onModeSelectClickListener mOnModeSelectClickListener) {
        this.mOnModeSelectClickListener = mOnModeSelectClickListener;
    }

    public interface onModeSelectClickListener {
        void onModeSelect(String action);
    }
    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        super.show();

    }

}
