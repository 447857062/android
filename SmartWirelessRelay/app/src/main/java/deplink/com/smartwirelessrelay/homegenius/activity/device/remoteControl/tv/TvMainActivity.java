package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.RemoteControlMenuDialog;

public class TvMainActivity extends Activity implements View.OnClickListener {
    private ImageView image_back;
    private RelativeLayout layout_control_base;
    private RelativeLayout layout_control_number;
    private TextView textview_title;
    private ImageView image_setting;
    private RelativeLayout layout_title_control_base;
    private RelativeLayout layout_title_control_number;
    private View view_control_base;
    private View view_control_number;
    private TextView textview_control_base;
    private TextView textview_control_number;
    private RemoteControlMenuDialog menu_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_main);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("电视遥控");
        menu_dialog=new RemoteControlMenuDialog(this,RemoteControlMenuDialog.TYPE_TV);
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        image_setting.setOnClickListener(this);
        layout_title_control_base.setOnClickListener(this);
        layout_title_control_number.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_setting= (ImageView) findViewById(R.id.image_setting);
        image_back = (ImageView) findViewById(R.id.image_back);
        layout_title_control_base = (RelativeLayout) findViewById(R.id.layout_title_control_base);
        layout_title_control_number = (RelativeLayout) findViewById(R.id.layout_title_control_number);
        layout_control_base = (RelativeLayout) findViewById(R.id.layout_control_base);
        layout_control_number = (RelativeLayout) findViewById(R.id.layout_control_number);
        view_control_base =  findViewById(R.id.view_control_base);
        view_control_number =  findViewById(R.id.view_control_number);
        textview_control_base = (TextView) findViewById(R.id.textview_control_base);
        textview_control_number = (TextView) findViewById(R.id.textview_control_number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.image_setting:
                menu_dialog.show();
                break;
            case R.id.layout_title_control_base:
                layout_control_base.setVisibility(View.VISIBLE);
                layout_control_number.setVisibility(View.GONE);
                view_control_base.setVisibility(View.VISIBLE);
                view_control_number.setVisibility(View.GONE);
                textview_control_base.setTextColor(getResources().getColor(R.color.title_blue_bg));
                textview_control_number.setTextColor(getResources().getColor(R.color.huise));
                break;
            case R.id.layout_title_control_number:
                layout_control_base.setVisibility(View.GONE);
                layout_control_number.setVisibility(View.VISIBLE);
                view_control_base.setVisibility(View.GONE);
                view_control_number.setVisibility(View.VISIBLE);
                textview_control_base.setTextColor(getResources().getColor(R.color.huise));
                textview_control_number.setTextColor(getResources().getColor(R.color.title_blue_bg));
                break;
        }
    }
}
