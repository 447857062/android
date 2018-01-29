package com.deplink.homegenius.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.deplink.homegenius.activity.device.router.connectType.DialConnectActivity;
import com.deplink.homegenius.activity.device.router.connectType.DynamicsActivity;
import com.deplink.homegenius.activity.device.router.connectType.StaticConnectActivity;
import com.deplink.homegenius.activity.device.router.connectType.WirelessRelayActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.Perfence;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class SelectConnectTypeDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout layout_ppop;
    private RelativeLayout layout_dynamics_ip;
    private RelativeLayout layout_static_ip;
    private RelativeLayout layout_wireless_relay;
    private RelativeLayout layout_cancel;
    public SelectConnectTypeDialog(Context context) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.connecttype_local_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
    }


    private void initView() {
        layout_ppop = findViewById(R.id.layout_ppop);
        layout_dynamics_ip = findViewById(R.id.layout_dynamics_ip);
        layout_static_ip = findViewById(R.id.layout_static_ip);
        layout_wireless_relay = findViewById(R.id.layout_wireless_relay);
        layout_cancel = findViewById(R.id.layout_cancel);

    }


    private void initEvent() {
        layout_ppop.setOnClickListener(this);
        layout_dynamics_ip.setOnClickListener(this);
        layout_static_ip.setOnClickListener(this);
        layout_wireless_relay.setOnClickListener(this);
        layout_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_ppop:
                mContext.startActivity(new Intent(mContext, DialConnectActivity.class));
                this.dismiss();
                break;
            case R.id.layout_dynamics_ip:
                mContext.startActivity(new Intent(mContext, DynamicsActivity.class));
                this.dismiss();
                break;
            case R.id.layout_static_ip:
                Intent staticIPIntent = new Intent(mContext, StaticConnectActivity.class);
                staticIPIntent.putExtra(AppConstant.OPERATION_TYPE, AppConstant.OPERATION_TYPE_WEB);
                mContext.startActivity(staticIPIntent);
                this.dismiss();
                break;
            case R.id.layout_wireless_relay:
                Intent wirelessRelayIntent = new Intent(mContext, WirelessRelayActivity.class);
                wirelessRelayIntent.putExtra(AppConstant.OPERATION_TYPE, AppConstant.OPERATION_TYPE_WEB);
                mContext.startActivity(wirelessRelayIntent);
                this.dismiss();
                break;
            case R.id.layout_cancel:
                this.dismiss();
                break;
        }
    }


    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        super.show();
    }

}
