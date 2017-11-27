package deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.adapter.SwitchTypeAdapter;
import deplink.com.smartwirelessrelay.homegenius.qrcode.qrcodecapture.CaptureActivity;

public class SelectSwitchTypeActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ImageView image_back;
    private ListView listview_switch_type;
    private SwitchTypeAdapter mTypeAdapter;
    private List<String>typeNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_switch_type);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        typeNames=new ArrayList<>();
        typeNames.add("一路开关");
        typeNames.add("二路开关");
        typeNames.add("三路开关");
        typeNames.add("四路开关");
        mTypeAdapter=new SwitchTypeAdapter(this,typeNames);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        listview_switch_type.setAdapter(mTypeAdapter);
        listview_switch_type.setOnItemClickListener(this);
    }

    private void initViews() {
        image_back= (ImageView) findViewById(R.id.image_back);
        listview_switch_type= (ListView) findViewById(R.id.listview_switch_type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (typeNames.get(position)){
            case "一路开关":
//                break;
            case "二路开关":
//                break;
            case "三路开关":
//                break;
            case "四路开关":
                Intent intentQrcodeSn = new Intent();
                intentQrcodeSn.setClass(SelectSwitchTypeActivity.this, CaptureActivity.class);
                intentQrcodeSn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentQrcodeSn);
                break;
        }
    }
}
