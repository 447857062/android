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
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;


/**
 * Created by Administrator on 2017/7/25.
 * 长度限制 SN 20  MAC,序列号 12
 */
public class KeynotlearnDialog extends Dialog implements View.OnClickListener {
    private Context mContext;



    public KeynotlearnDialog(Context context) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        p.width = (int) Perfence.dp2px(mContext,290);
        View view = LayoutInflater.from(mContext).inflate(R.layout.key_notlearn_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }

    private TextView textview_center;
    private void initView() {
        textview_center= (TextView) findViewById(R.id.textview_center);

    }


    private void initEvent() {
        textview_center.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_center:
                this.dismiss();

                break;

        }
    }
    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
        super.show();

    }

}
