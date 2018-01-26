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
import android.widget.EditText;
import android.widget.TextView;

import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.toast.ToastSingleShow;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class WifiRelayInputDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Button btn_sure;
    private Button btn_cancel;
    private TextView textview_unbind_device_type;
    private onSureBtnClickListener mOnSureBtnClickListener;
    private EditText edittext_input_password;
    public WifiRelayInputDialog(Context context) {
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
        p.height = (int) Perfence.dp2px(mContext,185);
        View view = LayoutInflater.from(mContext).inflate(R.layout.wifi_relay_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }


    private void initView() {
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        textview_unbind_device_type = (TextView) findViewById(R.id.textview_unbind_device_type);
        edittext_input_password = (EditText) findViewById(R.id.edittext_input_password);


    }

    public void setTitleText(String text){
        textview_unbind_device_type.setText(text);
    }
    private void initEvent() {
        btn_sure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                String password=edittext_input_password.getText().toString().trim();
                if(password.length()<8){
                    ToastSingleShow.showText(mContext,"wifi密码位数不能小于8");
                    return;
                }
                mOnSureBtnClickListener.onSureBtnClicked(password);
                this.dismiss();
                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }


    public interface onSureBtnClickListener {
         void onSureBtnClicked(String password);
    }

    @Override
    public void show() {
        super.show();

    }

    public void setSureBtnClickListener(onSureBtnClickListener mOnSureBtnClickListener) {

        this.mOnSureBtnClickListener = mOnSureBtnClickListener;
    }

}
