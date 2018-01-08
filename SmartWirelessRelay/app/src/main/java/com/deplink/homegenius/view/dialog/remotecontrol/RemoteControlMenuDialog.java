package com.deplink.homegenius.view.dialog.remotecontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.deplink.homegenius.activity.device.remoteControl.EditRemoteDevicesActivity;
import com.deplink.homegenius.activity.device.remoteControl.airContorl.add.AirconditionChooseBandActivity;
import com.deplink.homegenius.activity.device.remoteControl.topBox.AddTopBoxActivity;
import com.deplink.homegenius.activity.device.remoteControl.tv.AddTvDeviceActivity;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.util.Perfence;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/7/25.
 */
public class RemoteControlMenuDialog extends Dialog implements View.OnClickListener {
    private static final String TAG="RemoteControlMenuDialog";
    public static final int TYPE_AIRCONDITION = 1;
    public static final int TYPE_TVBOX = 2;
    public static final int TYPE_TV = 3;
    private Context mContext;

    private View view_mode_menu;
    private TextView textview_edit;
    private TextView textview_quick_learn;
    private TextView textview_hand_learn;
    private int currentType;

    public RemoteControlMenuDialog(Context context, int type) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
        this.currentType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        p.width = (int) Perfence.dp2px(mContext, 120);
        View view = LayoutInflater.from(mContext).inflate(R.layout.aircondition_menu_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
    }


    private void initView() {
        view_mode_menu = findViewById(R.id.view_mode_menu);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
        textview_quick_learn = (TextView) findViewById(R.id.textview_quick_learn);
        textview_hand_learn = (TextView) findViewById(R.id.textview_hand_learn);

    }


    private void initEvent() {
        view_mode_menu.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        textview_quick_learn.setOnClickListener(this);
        textview_hand_learn.setOnClickListener(this);
    }

    private onLearnHandClickListener mLearnHandClickListener;

    public void setmLearnHandClickListener(onLearnHandClickListener mLearnHandClickListener) {
        Log.i(TAG,"setmLearnHandClickListener");
        this.mLearnHandClickListener = mLearnHandClickListener;
    }

    public interface onLearnHandClickListener {
        void onLearnHandBtnClicked();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_mode_menu:
                this.dismiss();
                break;
            case R.id.textview_hand_learn:
                mLearnHandClickListener.onLearnHandBtnClicked();
                this.dismiss();
                break;
            case R.id.textview_quick_learn:
                this.dismiss();
                Intent intent;
                RemoteControlManager.getInstance().setCurrentActionIsAddDevice(false);
                switch (currentType) {
                    case TYPE_AIRCONDITION:
                        intent = new Intent(mContext, AirconditionChooseBandActivity.class);
                        intent.putExtra("type","KT");
                        mContext.startActivity(intent);
                        break;
                    case TYPE_TVBOX:
                        intent = new Intent(mContext, AddTopBoxActivity.class);
                        intent.putExtra("type","智能机顶盒遥控");
                        mContext.startActivity(intent);
                        break;
                    case TYPE_TV:
                        intent = new Intent(mContext, AddTvDeviceActivity.class);
                        intent.putExtra("type","TV");
                        mContext.startActivity(intent);
                        break;
                }

                break;
            case R.id.textview_edit:
                this.dismiss();
                intent = new Intent(mContext, EditRemoteDevicesActivity.class);
                switch (currentType){
                    case TYPE_AIRCONDITION:
                        intent.putExtra("deviceType", DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL);
                        break;
                    case TYPE_TVBOX:
                        intent.putExtra("deviceType", DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL);
                        break;
                    case TYPE_TV:
                        intent.putExtra("deviceType", DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL);
                        break;
                }

                mContext.startActivity(intent);
                break;
        }
    }


    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        super.show();

    }

}
