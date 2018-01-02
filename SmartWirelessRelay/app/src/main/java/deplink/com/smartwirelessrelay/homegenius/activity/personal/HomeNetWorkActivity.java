package deplink.com.smartwirelessrelay.homegenius.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.adapter.HomeNetWorkAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.RouterMainActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;

/**
 * 家庭网络
 */
public class HomeNetWorkActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "HomeNetworkActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private ListView listviewNetworkDevices;
    private List<Device> mGatwayDevices;
    private HomeNetWorkAdapter mAdapter;
    private GetwayManager getwayManager;
    private List<SmartDev> mRouterDevice;
    private TextView textview_no_getway;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_check_list);
        initViews();
        initDatas();
        initEvents();
    }
    private void initDatas() {
        textview_title.setText("家庭网络");
        getwayManager = GetwayManager.getInstance();
        mRouterDevice = new ArrayList<>();
        mRouterDevice = RouterManager.getInstance().getAllRouterDevice();
        mGatwayDevices = new ArrayList<>();
        mGatwayDevices=getwayManager.getAllGetwayDevice();
        mAdapter = new HomeNetWorkAdapter(this, mGatwayDevices, mRouterDevice);
        listviewNetworkDevices.setAdapter(mAdapter);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        listviewNetworkDevices.setAdapter(mAdapter);
        listviewNetworkDevices.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRouterDevice = RouterManager.getInstance().getAllRouterDevice();
        mGatwayDevices=getwayManager.getAllGetwayDevice();
        mAdapter.setTopList(mGatwayDevices);
        mAdapter.setBottomList(mRouterDevice);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
        listviewNetworkDevices = (ListView) findViewById(R.id.listview_getway_devices);
        textview_no_getway = (TextView) findViewById(R.id.textview_no_getway);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mGatwayDevices.size() < (position + 1)) {
            RouterManager.getInstance().setCurrentSelectedRouter(mRouterDevice.get(position - mGatwayDevices.size()));
            startActivity(new Intent(HomeNetWorkActivity.this, RouterMainActivity.class));
        }else{
            GetwayManager.getInstance().setCurrentSelectGetwayDevice(mGatwayDevices.get(position));
            startActivity(new Intent(HomeNetWorkActivity.this, GetwayDeviceActivity.class));
        }
    }
}