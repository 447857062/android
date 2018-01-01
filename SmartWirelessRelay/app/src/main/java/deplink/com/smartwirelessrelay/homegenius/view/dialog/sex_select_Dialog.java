package deplink.com.smartwirelessrelay.homegenius.view.dialog;

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
public class Sex_select_Dialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout layout_male;
    private RelativeLayout layout_fmale;

    private RelativeLayout layout_cancel;


    public Sex_select_Dialog(Context context) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_sex_selector, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }


    private void initView() {
        layout_male= (RelativeLayout) findViewById(R.id.layout_male);
        layout_fmale= (RelativeLayout) findViewById(R.id.layout_fmale);

        layout_cancel= (RelativeLayout) findViewById(R.id.layout_cancel);

    }


    private void initEvent() {
        layout_male.setOnClickListener(this);
        layout_fmale.setOnClickListener(this);

        layout_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_male:
                mOnSexSelectClickListener.onSexSelect("男");
                this.dismiss();
                break;
            case R.id.layout_fmale:
                mOnSexSelectClickListener.onSexSelect("女");
                this.dismiss();
                break;

            case R.id.layout_cancel:
                this.dismiss();
                break;
        }
    }

    private onSexSelectClickListener mOnSexSelectClickListener;

    public void setmOnSexSelectClickListener(onSexSelectClickListener mOnSexSelectClickListener) {
        this.mOnSexSelectClickListener = mOnSexSelectClickListener;
    }

    public interface onSexSelectClickListener {
        void onSexSelect(String selectMode);
    }
    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        super.show();

    }

}
