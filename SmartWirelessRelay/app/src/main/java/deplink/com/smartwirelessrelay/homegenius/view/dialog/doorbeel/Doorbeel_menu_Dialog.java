package deplink.com.smartwirelessrelay.homegenius.view.dialog.doorbeel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell.EditDoorbellActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell.VistorHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;


/**
 * Created by Administrator on 2017/7/25.
 */
public class Doorbeel_menu_Dialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View view_mode_menu;
    private TextView textview_edit;
    private TextView textview_record;
    public Doorbeel_menu_Dialog(Context context) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        p.width = (int) Perfence.dp2px(mContext,120);
        View view = LayoutInflater.from(mContext).inflate(R.layout.doorbeel_menu_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();


    }


    private void initView() {
        view_mode_menu=findViewById(R.id.view_mode_menu);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        textview_record= (TextView) findViewById(R.id.textview_record);

    }


    private void initEvent() {
        view_mode_menu.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        textview_record.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.view_mode_menu:
                this.dismiss();
                break;
            case R.id.textview_edit:
                this.dismiss();
             mContext.startActivity(new Intent(mContext, EditDoorbellActivity.class));

                break;
            case R.id.textview_record:
                this.dismiss();
                mContext.startActivity(new Intent(mContext, VistorHistoryActivity.class));
                break;
        }
    }


    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.RIGHT|Gravity.TOP);
        super.show();

    }

}
