package com.deplink.homegenius.view.dialog.remotecontrol.aircondition;

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

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.util.Perfence;


/**
 * Created by Administrator on 2017/7/25.
 *
 */
public class AirconditionWindSpeedSelectDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout layout_windspeed_hight;
    private RelativeLayout layout_windspeed_middle;
    private RelativeLayout layout_windspeed_low;
    private RelativeLayout layout_windspeed_auto;
    private RelativeLayout layout_cancel;


    public AirconditionWindSpeedSelectDialog(Context context) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.aircondition_windspeed_select_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }


    private void initView() {
        layout_windspeed_hight= (RelativeLayout) findViewById(R.id.layout_windspeed_hight);
        layout_windspeed_middle= (RelativeLayout) findViewById(R.id.layout_windspeed_middle);
        layout_windspeed_low= (RelativeLayout) findViewById(R.id.layout_windspeed_low);
        layout_windspeed_auto= (RelativeLayout) findViewById(R.id.layout_windspeed_auto);
        layout_cancel= (RelativeLayout) findViewById(R.id.layout_cancel);

    }


    private void initEvent() {
        layout_windspeed_hight.setOnClickListener(this);
        layout_windspeed_middle.setOnClickListener(this);
        layout_windspeed_low.setOnClickListener(this);
        layout_windspeed_auto.setOnClickListener(this);
        layout_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_windspeed_hight:
                mOnModeSelectClickListener.onModeSelect("高风");
                this.dismiss();
                break;
            case R.id.layout_windspeed_middle:
                mOnModeSelectClickListener.onModeSelect("中风");
                this.dismiss();
                break;
            case R.id.layout_windspeed_low:
                mOnModeSelectClickListener.onModeSelect("低风");
                this.dismiss();
                break;
            case R.id.layout_windspeed_auto:
                mOnModeSelectClickListener.onModeSelect("自动风速");
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
        void onModeSelect(String selectMode);
    }
    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        super.show();

    }

}
