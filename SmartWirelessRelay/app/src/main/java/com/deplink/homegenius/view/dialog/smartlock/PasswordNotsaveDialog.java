package com.deplink.homegenius.view.dialog.smartlock;

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
 * 授权操作dialog
 */
public class PasswordNotsaveDialog extends Dialog implements View.OnClickListener{
    private static final String TAG = "PasswordNotsaveDialog";
    private Context mContext;
    private PasswordNotsaveSureListener mOnSureClick;
    private TextView button_cancel;
    private TextView button_sure;
    public PasswordNotsaveDialog(Context context) {
        super(context, R.style.AuthoriseDialog);
        mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        p.width = (int) Perfence.dp2px(mContext,290);
        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.password_notsave_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        initDatas();
        //初始化界面控件的事件
        initEvents();
    }

    private void initDatas() {

    }

    private void initEvents() {
        button_cancel.setOnClickListener(this);
        button_sure.setOnClickListener(this);

    }


    private void initView() {
        button_cancel= (TextView) findViewById(R.id.button_cancel);
        button_sure= (TextView) findViewById(R.id.button_sure);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_cancel:

               this.dismiss();
                break;
            case R.id.button_sure:
                mOnSureClick.onSureClick();
                this.dismiss();
                break;

        }
    }




    public interface PasswordNotsaveSureListener {
        void onSureClick();
    }

    public void setmOnSureClick(PasswordNotsaveSureListener mOnSureClick) {
        this.mOnSureClick = mOnSureClick;
    }

    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        super.show();

    }

}
