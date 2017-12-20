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
 *
 */
public class AirconditionWindDirectionSelectDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout layout_winddirection_up;
    private RelativeLayout layout_winddirection_middle;
    private RelativeLayout layout_winddirection_down;
    private RelativeLayout layout_winddirection_auto;
    private RelativeLayout layout_cancel;


    public AirconditionWindDirectionSelectDialog(Context context) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.aircondition_winddirection_select_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }


    private void initView() {
        layout_winddirection_up= (RelativeLayout) findViewById(R.id.layout_winddirection_up);
        layout_winddirection_middle= (RelativeLayout) findViewById(R.id.layout_winddirection_middle);
        layout_winddirection_down= (RelativeLayout) findViewById(R.id.layout_winddirection_down);
        layout_winddirection_auto= (RelativeLayout) findViewById(R.id.layout_winddirection_auto);
        layout_cancel= (RelativeLayout) findViewById(R.id.layout_cancel);

    }


    private void initEvent() {
        layout_winddirection_up.setOnClickListener(this);
        layout_winddirection_middle.setOnClickListener(this);
        layout_winddirection_down.setOnClickListener(this);
        layout_winddirection_auto.setOnClickListener(this);
        layout_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_winddirection_up:
                mOnModeSelectClickListener.onModeSelect("风向向上");
                this.dismiss();
                break;
            case R.id.layout_winddirection_middle:
                mOnModeSelectClickListener.onModeSelect("风向向中");
                this.dismiss();
                break;
            case R.id.layout_winddirection_down:
                mOnModeSelectClickListener.onModeSelect("风向向下");
                this.dismiss();
                break;
            case R.id.layout_winddirection_auto:
                mOnModeSelectClickListener.onModeSelect("自动风向");
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
