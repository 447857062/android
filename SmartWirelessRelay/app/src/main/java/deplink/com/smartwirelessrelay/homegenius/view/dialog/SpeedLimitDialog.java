package deplink.com.smartwirelessrelay.homegenius.view.dialog;

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

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class SpeedLimitDialog extends Dialog implements View.OnClickListener {
    private Context mContext;


    private Button btn_sure;
    private Button btn_cancel;

    private TextView textview_unbind_device_type;
    private onSureBtnClickListener mOnSureBtnClickListener;
    private EditText edittext_download_speed;
    private EditText edittext_upload_speed;
    public SpeedLimitDialog(Context context) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        p.height = (int)(screenHeigh*0.28);
        p.width = (int)(screenWidth*0.9);
        View view = LayoutInflater.from(mContext).inflate(R.layout.speed_limit_dialog, null);
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
        edittext_download_speed = (EditText) findViewById(R.id.edittext_download_speed);
        edittext_upload_speed = (EditText) findViewById(R.id.edittext_upload_speed);
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

                String rx=edittext_download_speed.getText().toString().trim();
                if(rx.equals("")){
                    ToastSingleShow.showText(mContext,"请输入下行速度");
                    return;
                }
                String tx=edittext_upload_speed.getText().toString().trim();
                if(rx.equals("")){
                    ToastSingleShow.showText(mContext,"请输入上行速度");
                    return;
                }
                mOnSureBtnClickListener.onSureBtnClicked(rx,tx);
                this.dismiss();
                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }


    public interface onSureBtnClickListener {
        public void onSureBtnClicked(String rx, String tx);
    }

    @Override
    public void show() {
        super.show();

    }

    public void setSureBtnClickListener(onSureBtnClickListener mOnSureBtnClickListener) {

        this.mOnSureBtnClickListener = mOnSureBtnClickListener;
    }

}
