package deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition;

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
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class AirconditionModeSelectDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout layout_mode_hot;
    private RelativeLayout layout_mode_code;
    private RelativeLayout layout_mode_dehumidification;
    private RelativeLayout layout_mode_weend;
    private RelativeLayout layout_mode_auto;
    private RelativeLayout layout_cancel;


    public AirconditionModeSelectDialog(Context context) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.aircondition_mode_select_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }


    private void initView() {
        layout_mode_hot= (RelativeLayout) findViewById(R.id.layout_mode_hot);
        layout_mode_code= (RelativeLayout) findViewById(R.id.layout_mode_code);
        layout_mode_dehumidification= (RelativeLayout) findViewById(R.id.layout_mode_dehumidification);
        layout_mode_weend= (RelativeLayout) findViewById(R.id.layout_mode_weend);
        layout_mode_auto= (RelativeLayout) findViewById(R.id.layout_mode_auto);
        layout_cancel= (RelativeLayout) findViewById(R.id.layout_cancel);

    }


    private void initEvent() {
        layout_mode_hot.setOnClickListener(this);
        layout_mode_code.setOnClickListener(this);
        layout_mode_dehumidification.setOnClickListener(this);
        layout_mode_weend.setOnClickListener(this);
        layout_mode_auto.setOnClickListener(this);
        layout_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_mode_hot:
                mOnModeSelectClickListener.onModeSelect("制热模式");
                this.dismiss();
                break;
            case R.id.layout_mode_code:
                mOnModeSelectClickListener.onModeSelect("制冷模式");
                this.dismiss();
                break;
            case R.id.layout_mode_dehumidification:
                mOnModeSelectClickListener.onModeSelect("除湿模式");
                this.dismiss();
                break;
            case R.id.layout_mode_weend:
                mOnModeSelectClickListener.onModeSelect("送风模式");
                this.dismiss();
                break;
            case R.id.layout_mode_auto:
                mOnModeSelectClickListener.onModeSelect("自动模式");
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
