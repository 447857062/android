package deplink.com.smartwirelessrelay.homegenius.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
 * 带输入框的对话框，有些提示不一样，可以设置标题，输入框默认的字符
 */
public class MakeSureWithInputDialog extends Dialog implements View.OnClickListener {
    private static final String TAG="MakeSureWithInputDialog";
    private Context mContext;
    private Button btn_sure;
    private Button btn_cancel;
    private TextView textview_unbind_device_type;
    private TextView textview_dialog_info;
    private onSureBtnClickListener mOnSureBtnClickListener;
    private EditText edittext_input_password;
    public static final int DIALOG_TYPE_INPUT_WIFINAME = 1;
    public static final int DIALOG_TYPE_INPUT_DEVICENAME = 2;
    public static final int DIALOG_TYPE_WIFI_CONNECTED = 3;
    public static final int DIALOG_TYPE_ADD_CONTROLER = 4;
    public static final int DIALOG_TYPE_BINDED_CHANGE_ROUTER_NAME = 5;
    private int mDialogType;

    public MakeSureWithInputDialog(Context context, int dialogType) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
        mDialogType = dialogType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        p.height = (int) (screenHeigh * 0.25);
        p.width = (int) (screenWidth * 0.9);
        View view = null;
        switch (mDialogType) {
            case DIALOG_TYPE_INPUT_WIFINAME:
                view = LayoutInflater.from(mContext).inflate(R.layout.input_wifiname_dialog, null);
                break;
            case DIALOG_TYPE_BINDED_CHANGE_ROUTER_NAME:
                view = LayoutInflater.from(mContext).inflate(R.layout.change_router_name_dialog, null);
                break;
            case DIALOG_TYPE_INPUT_DEVICENAME:
                view = LayoutInflater.from(mContext).inflate(R.layout.input_devicename_dialog, null);
                break;
            case DIALOG_TYPE_WIFI_CONNECTED:
                view = LayoutInflater.from(mContext).inflate(R.layout.connectwifi_dialog, null);
                break;
            case DIALOG_TYPE_ADD_CONTROLER:
                view = LayoutInflater.from(mContext).inflate(R.layout.add_controler_dialog, null);
                break;
        }

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
        if(mDialogType==DIALOG_TYPE_WIFI_CONNECTED){
            textview_dialog_info= (TextView) findViewById(R.id.textview_dialog_info);
        }

    }

    public void setTitleText(String text) {
        textview_unbind_device_type.setText(text);
    }
    public void setDialogInfoText(String text) {
        if(mDialogType==DIALOG_TYPE_WIFI_CONNECTED){
            textview_dialog_info.setText(text);
        }
    }
    public void setDialogInfoButtonSureText(String text) {
        if(mDialogType==DIALOG_TYPE_WIFI_CONNECTED){
           btn_sure.setText(text);
        }
    }

    /**
     * 输入框显示默认的字符
     *
     * @param text
     */
    public void setEditText(String text) {
        if (!text.equals("")) {
            edittext_input_password.setText(text);
            if(text.length()>20){
                edittext_input_password.setSelection(20);
            }else{
                edittext_input_password.setSelection(text.length());
            }

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
                String password = "";
                if (edittext_input_password != null) {
                    password = edittext_input_password.getText().toString().trim();
                }

                switch (mDialogType) {
                    case DIALOG_TYPE_INPUT_WIFINAME:
                        if (password.length() < 8){
                            ToastSingleShow.showText(mContext," wifi密码不能小于8位");
                            return;
                        }
                        try {
                            mOnSureBtnClickListener.onSureBtnClicked(password);
                            this.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case DIALOG_TYPE_INPUT_DEVICENAME:
                        if (password.equals("")) {
                            ToastSingleShow.showText(mContext, "请输入路由器名字");
                            return;
                        }
                        try {
                            mOnSureBtnClickListener.onSureBtnClicked(password);
                            this.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case DIALOG_TYPE_WIFI_CONNECTED:
                        this.dismiss();
                        mOnSureBtnClickListener.onSureBtnClicked("");
                        break;
                    case DIALOG_TYPE_BINDED_CHANGE_ROUTER_NAME:
                        try {
                            password=edittext_input_password.getText().toString().trim();
                            if(password.equals("")){
                                ToastSingleShow.showText(mContext,"还没有输入设备名称");
                                return;
                            }
                            Log.i(TAG,"password="+password);
                            mOnSureBtnClickListener.onSureBtnClicked(password);
                            this.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case DIALOG_TYPE_ADD_CONTROLER:
                        try {
                            password = edittext_input_password.getText().toString().trim();
                            if (password.equals("")) {
                                ToastSingleShow.showText(mContext, "还没有输入管理员昵称");
                                return;
                            }
                            mOnSureBtnClickListener.onSureBtnClicked("");
                            this.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }


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
