package deplink.com.smartwirelessrelay.homegenius.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/8/9.
 * 用户头像上传时，选择获取用户头像获取方式的对话框
 */
public class UserImagePickerDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private TextView text_picker_from_galary;
    private TextView text_picker_from_camera;
    private TextView text_picker_cancel;
    private onFromGalaryClickListener mOnFromGalaryClickListener;
    private onFromCameraClickListener fromCameraClickListener;
    public UserImagePickerDialog(Context context){
        super(context, R.style.iamge_picker);
        mContext=context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        p.height = (int)(screenHeigh*0.25);
        p.width = (int)(screenWidth*0.9);

        View view= LayoutInflater.from(mContext).inflate(R.layout.user_image_picker_dialog,null);
        setContentView(view,p);
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }

    private void initView() {
        text_picker_from_galary= (TextView) findViewById(R.id.text_picker_from_galary);
        text_picker_from_camera= (TextView) findViewById(R.id.text_picker_from_camera);
        text_picker_cancel= (TextView) findViewById(R.id.text_picker_cancel);

    }

    private void initData() {
    }

    private void initEvent() {
        text_picker_from_galary.setOnClickListener(this);
        text_picker_from_camera.setOnClickListener(this);
        text_picker_cancel.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_picker_from_galary:
                mOnFromGalaryClickListener.onFromGalaryClicked();
                this.dismiss();
                break;
            case R.id.text_picker_from_camera:
                fromCameraClickListener.onFromCameraClicked();
                this.dismiss();
                break;
            case R.id.text_picker_cancel:
            this.dismiss();
                break;
        }
    }
    public interface onFromGalaryClickListener {
        public void onFromGalaryClicked();
    }
    public interface onFromCameraClickListener {
        public void onFromCameraClicked();
    }
    public void setOnFromGalaryClickListener(onFromGalaryClickListener onFromGalaryClickListener) {

        this.mOnFromGalaryClickListener = onFromGalaryClickListener;
    }
    public void setOnFromCameraClickListener(onFromCameraClickListener onFromCameraClickListener) {

        this.fromCameraClickListener = onFromCameraClickListener;
    }
}
