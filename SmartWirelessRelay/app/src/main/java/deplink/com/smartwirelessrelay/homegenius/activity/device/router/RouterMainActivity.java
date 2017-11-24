package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.router.BLACKLIST;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.router.ConnectedDevices;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter.BlackListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter.ConnectedDeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.SpeedLimitDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenu;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuCreator;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuItem;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuListView;

public class RouterMainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RouterMainActivity";
    private ImageView image_back;
    private ImageView imageview_setting;
    /**
     * 已连接设备
     */
    private SwipeMenuListView listview_device_list;
    private List<ConnectedDevices> mConnectedDevices;
    private ConnectedDeviceListAdapter mAdapter;
    private SwipeMenuListView listview_black_list;
    private List<BLACKLIST>mBlackListDatas;
    private BlackListAdapter mBlackListAdapter;
    private Button button_connected_devices;
    private Button button_blak_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_main);
        initViews();
        initDatas();
        initEvents();
    }

    private AdapterView.OnItemClickListener deviceItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            SpeedLimitDialog speedLimitDialog = new SpeedLimitDialog(RouterMainActivity.this);
            speedLimitDialog.setSureBtnClickListener(new SpeedLimitDialog.onSureBtnClickListener() {

                @Override
                public void onSureBtnClicked(String rx, String tx) {
                    //TODO 限速
                }
            });
            speedLimitDialog.show();
        }
    };

    private void initDatas() {
        mConnectedDevices = new ArrayList<>();
        //调试
        ConnectedDevices temp = new ConnectedDevices();
        temp.setDeviceName("手机1");
        temp.setDataSpeedRx("23456743");
        temp.setDataSpeedTx("67890");
        temp.setMAC("wewqewqwqewqewqewq");
        mConnectedDevices.add(temp);
        temp = new ConnectedDevices();
        temp.setDeviceName("手机2");
        temp.setDataSpeedRx("23456743");
        temp.setDataSpeedTx("67890");
        temp.setMAC("wewqewqwqewqewqewq");
        mConnectedDevices.add(temp);
        temp = new ConnectedDevices();
        temp.setDeviceName("手机3");
        temp.setDataSpeedRx("23456743");
        temp.setDataSpeedTx("67890");
        temp.setMAC("wewqewqwqewqewqewq");
        mConnectedDevices.add(temp);

        mAdapter = new ConnectedDeviceListAdapter(this, mConnectedDevices);

        mBlackListDatas=new ArrayList<>();
        BLACKLIST blackTemp=new BLACKLIST();
        blackTemp.setMAC("21321312dasda");
        blackTemp.setDeviceName("手机1");
        blackTemp.setDeviceMac("rewrewff");
        mBlackListDatas.add(blackTemp);
         blackTemp=new BLACKLIST();
        blackTemp.setMAC("21321312dasda");
        blackTemp.setDeviceName("手机2");
        blackTemp.setDeviceMac("rewrewff");
        mBlackListDatas.add(blackTemp);
         blackTemp=new BLACKLIST();
        blackTemp.setMAC("21321312dasda");
        blackTemp.setDeviceName("手机3");
        blackTemp.setDeviceMac("rewrewff");
        mBlackListDatas.add(blackTemp);
        mBlackListAdapter=new BlackListAdapter(this,mBlackListDatas);
    }

    private float Yoffset = 0;
    private Handler mHandler = new Handler();

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_connected_devices.setOnClickListener(this);
        button_blak_list.setOnClickListener(this);
        imageview_setting.setOnClickListener(this);
        listview_device_list.setAdapter(mAdapter);


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.read_fc5551)));
                // set item width
                openItem.setWidth((int) Perfence.dp2px(RouterMainActivity.this, 80));
                // set item title
                openItem.setTitle("限速");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.black_3a3a3a)));
                deleteItem.setWidth((int) Perfence.dp2px(RouterMainActivity.this, 80));
                //  deleteItem.setBackground(R.layout.listview_deleteitem_layout);
                // set item width
                deleteItem.setTitle("拉黑");
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                // deleteItem.setIcon(R.drawable.arror_back);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listview_device_list.setMenuCreator(creator);
        listview_device_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                //   String item = mDatas.get(position);
                switch (index) {
                    case 0:
                        // open
                        SpeedLimitDialog speedLimitDialog = new SpeedLimitDialog(RouterMainActivity.this);
                        speedLimitDialog.setSureBtnClickListener(new SpeedLimitDialog.onSureBtnClickListener() {

                            @Override
                            public void onSureBtnClicked(String rx, String tx) {
                                //TODO
                            }
                        });
                        speedLimitDialog.show();
                        break;
                    case 1:
                        MakeSureDialog dialog = new MakeSureDialog(RouterMainActivity.this);
                        dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                            @Override
                            public void onSureBtnClicked() {

                            }
                        });
                        dialog.show();
                        dialog.setTitleText("确定将设备(" + /*mDatas.get(position).getDeviceName()*/  ")拉入黑名单");

                        break;
                }
                return false;
            }
        });

        //下拉刷星
        listview_device_list.setOnItemClickListener(deviceItemClickListener);
        listview_device_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Yoffset = event.getY();
                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "setOnTouchListener ACTION_MOVE" + (event.getY() - Yoffset));
                        if (event.getY() - Yoffset > HEIGHT_MARK_TO_REFRESH) {


                            DialogThreeBounce.showLoading(RouterMainActivity.this);


                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DialogThreeBounce.hideLoading();
                                }
                            }, 2000);
                        }
                }
                return false;
            }
        });


        listview_black_list.setAdapter(mBlackListAdapter);


        SwipeMenuCreator creatorBlackList = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.read_fc5551)));
                // set item width
                openItem.setWidth((int) Perfence.dp2px(RouterMainActivity.this, 80));
                // set item title
                openItem.setTitle("恢复上网");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

            }
        };
        // set creator
        listview_black_list.setMenuCreator(creatorBlackList);
        listview_device_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                //   String item = mDatas.get(position);
                switch (index) {
                    case 0:
                        // open
                        MakeSureDialog dialog = new MakeSureDialog(RouterMainActivity.this);
                        dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {

                            @Override
                            public void onSureBtnClicked() {
                               //TODO 从黑名单中移除
                            }
                        });

                            dialog.show();
                            dialog.setTitleText("确定将该设备从黑名单移除");

                        break;

                }
                return false;
            }
        });

        listview_black_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                MakeSureDialog dialog = new MakeSureDialog(RouterMainActivity.this);
                dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {

                    @Override
                    public void onSureBtnClicked() {
                        //TODO 恢复上网

                    }
                });
                dialog.show();
                dialog.setTitleText("确定将该设备从黑名单移除");
            }
        });

    }

    /**
     * 竖向滑动这么多距离就开始刷新
     */
    public static final int HEIGHT_MARK_TO_REFRESH = 250;

    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        imageview_setting = (ImageView) findViewById(R.id.imageview_setting);
        listview_device_list = (SwipeMenuListView) findViewById(R.id.listview_device_list);
        button_connected_devices = (Button) findViewById(R.id.button_connected_devices);
        button_blak_list = (Button) findViewById(R.id.button_blak_list);
        listview_black_list = (SwipeMenuListView) findViewById(R.id.listview_black_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_connected_devices:
                listview_black_list.setVisibility(View.GONE);
                listview_device_list.setVisibility(View.VISIBLE);

                break;
            case R.id.button_blak_list:
                listview_device_list.setVisibility(View.GONE);
                listview_black_list.setVisibility(View.VISIBLE);
                break;
            case R.id.imageview_setting:
                startActivity(new Intent(this, RouterSettingActivity.class));
                break;
        }
    }
}
