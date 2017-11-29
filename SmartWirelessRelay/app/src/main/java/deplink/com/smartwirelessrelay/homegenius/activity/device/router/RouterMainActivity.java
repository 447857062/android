package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.BLACKLIST;
import com.deplink.sdk.android.sdk.json.DeviceControl;
import com.deplink.sdk.android.sdk.json.DevicesOnline;
import com.deplink.sdk.android.sdk.json.WHITELIST;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter.BlackListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter.ConnectedDeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenu;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuCreator;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuItem;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuListView;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class RouterMainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RouterMainActivity";
    private static final int TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS = 3000;
    private int refreshCount = 0;
    /**
     * 设备上线下线监控，2次发送没有回数据就认为设备下线,所以是大于1
     */
    private static final int TIME_OUT_WATCHDOG_MAXCOUNT = 1;
    /**
     * 竖向滑动这么多距离就开始刷新
     */
    public static final int HEIGHT_MARK_TO_REFRESH = 250;
    private ImageView image_back;
    private ImageView imageview_setting;
    /**
     * 已连接设备
     */
    private SwipeMenuListView listview_device_list;
    private List<DevicesOnline> mConnectedDevices;
    private ConnectedDeviceListAdapter mAdapter;
    private SwipeMenuListView listview_black_list;
    private List<BLACKLIST> mBlackListDatas;
    private BlackListAdapter mBlackListAdapter;
    private Button button_connected_devices;
    private Button button_blak_list;
    private MakeSureDialog connectLostDialog;
    private TextView textview_show_query_device_result;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private boolean isUserLogin;
    private boolean deviceOnline = true;
    private boolean isSetBlackList = false;
    private boolean isRemoveBlackList = false;
    private Timer refreshTimer = null;
    private TimerTask refreshTask = null;
    private float Yoffset = 0;
    private Handler mHandler = new Handler();
    private TextView textview_show_blacklist_device_result;
    private boolean isMqttConnect = true;
    private FrameLayout frame_blacklist_content;
    private FrameLayout frame_devicelist_content_content;
    private TextView textview_cpu_use;
    private TextView textview_memory_use;
    private TextView textview_upload_speed;
    private TextView textview_download_speend;
    private RouterManager mRouterManager;
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
            MakeSureDialog dialog = new MakeSureDialog(RouterMainActivity.this);
            dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                @Override
                public void onSureBtnClicked() {
                    isSetBlackList = true;
                    String mac = mConnectedDevices.get(position).getMAC();
                    String name = mConnectedDevices.get(position).getDeviceName();
                    DeviceControl control = new DeviceControl();
                    com.deplink.sdk.android.sdk.json.BLACKLIST blacklist = new com.deplink.sdk.android.sdk.json.BLACKLIST();
                    blacklist.setDeviceMac(mac);
                    blacklist.setDeviceName(name);
                    control.setBLACKLIST(blacklist);
                    try {
                        routerDevice.setDeviceControl(control);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
            dialog.setTitleText("确定将设备(" + mConnectedDevices.get(position).getDeviceName() + ")拉入黑名单");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        routerDevice=mRouterManager.getRouterDevice();
        startTimer();
        if (isUserLogin) {
            showQueryingDialog();
        }
        frame_blacklist_content.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void stopTimer() {
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }
        if (refreshTimer != null) {
            refreshTimer.cancel();//到其他界面就不要发请求数据了
            refreshTimer = null;
        }
    }

    private void startTimer() {
        if (refreshTimer == null) {
            refreshTimer = new Timer();
        }
        if (refreshTask == null) {
            refreshTask = new TimerTask() {
                @Override
                public void run() {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            queryRouterInfo();
                        }
                    });
                }
            };
        }
        if (refreshTimer != null) {
            //3秒钟发一次查询的命令
            refreshTimer.schedule(refreshTask, 0, TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS);
        }
    }

    private void queryRouterInfo() {
        if (refreshCount > TIME_OUT_WATCHDOG_MAXCOUNT) {
            routerDevice.setOnline(false);
            textview_cpu_use.setText("--");
            textview_memory_use.setText("--");
            textview_upload_speed.setText("--");
            textview_download_speend.setText("--");
            if (routerDevice != null) {
                routerDevice.getReport();//这里只要查询设备有没有上线
                //下面几个也加上
                queryDevices();
                routerDevice.queryLan();
                routerDevice.queryWan();
            }
        } else {
            if (routerDevice != null) {
                refreshCount++;
                routerDevice.getReport();//进界面更新
                queryDevices();
                routerDevice.queryLan();
                routerDevice.queryWan();
            }
        }
    }

    /**
     * 查询已挂载到当前路由器的设备
     */
    private void queryDevices() {
        Log.i(TAG, "routerDevice == null" + (routerDevice == null) + "isUserLogin=" + isUserLogin + "isMqttConnect=" + isMqttConnect);
        if (NetUtil.isNetAvailable(RouterMainActivity.this)) {
            if (isUserLogin) {
                if (routerDevice != null) {
                    if (deviceOnline) {
                        routerDevice.queryDevices();
                    } else {
                        textview_show_query_device_result.setVisibility(View.VISIBLE);
                        textview_show_query_device_result.setText("设备不在线，无法读取设备信息");
                    }
                }
            } else {
                textview_show_query_device_result.setVisibility(View.VISIBLE);
                textview_show_query_device_result.setText("尚未登录，无法读取设备信息");
                mConnectedDevices.clear();
                mAdapter.notifyDataSetChanged();
            }
        } else {
            textview_show_query_device_result.setVisibility(View.VISIBLE);
            textview_show_query_device_result.setText("没有网络连接，无法读取设备信息");
            mConnectedDevices.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showQueryingDialog() {
        DialogThreeBounce.showLoading(this);
        mHandler.postDelayed(hideLoadingCallback, 1500);
    }

    private Runnable hideLoadingCallback = new Runnable() {
        @Override
        public void run() {
            DialogThreeBounce.hideLoading();
        }
    };


    private void initDatas() {
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        mConnectedDevices = new ArrayList<>();
        mAdapter = new ConnectedDeviceListAdapter(this, mConnectedDevices);
        mBlackListDatas = new ArrayList<>();
        mBlackListAdapter = new BlackListAdapter(this, mBlackListDatas);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        connectLostDialog = new MakeSureDialog(RouterMainActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(RouterMainActivity.this, LoginActivity.class));
            }
        });
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    default:
                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void deviceOpSuccess(String op, final String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                Log.i(TAG, "deviceOpSuccess op=" + op);
                switch (op) {
                    case RouterDevice.OP_GET_DEVICES:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DialogThreeBounce.hideLoading();
                                    if (frame_devicelist_content_content.getVisibility() == View.VISIBLE) {
                                        mConnectedDevices.clear();
                                        if (routerDevice != null && routerDevice.getDevicesOnlineRoot() != null) {
                                            mConnectedDevices.addAll(routerDevice.getDevicesOnlineRoot().getDevicesOnline());
                                        }
                                        Log.i(TAG, "设备界面：获取已连接设备列表:" + mConnectedDevices.size());
                                        if (mConnectedDevices.size() == 0) {
                                            textview_show_query_device_result.setVisibility(View.VISIBLE);
                                            textview_show_query_device_result.setText("没有设备连接当前的路由器");
                                        } else {
                                            textview_show_query_device_result.setVisibility(View.GONE);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    if (frame_blacklist_content.getVisibility() == View.VISIBLE) {
                                        if (routerDevice.getDevicesOnlineRoot().getBLACKLIST().size() == 0) {
                                            textview_show_blacklist_device_result.setVisibility(View.VISIBLE);
                                            textview_show_blacklist_device_result.setText("黑名单中没有添加设备");
                                        } else {
                                            textview_show_blacklist_device_result.setVisibility(View.GONE);
                                        }
                                        mBlackListDatas.clear();
                                        mBlackListDatas.addAll(routerDevice.getDevicesOnlineRoot().getBLACKLIST());
                                        mBlackListAdapter.notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        break;
                    case RouterDevice.OP_GET_REPORT:
                        try {
                            updatePerformance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RouterDevice.OP_SUCCESS:
                        if (frame_devicelist_content_content.getVisibility() == View.VISIBLE) {
                            if (isSetBlackList) {
                                ToastSingleShow.showText(RouterMainActivity.this, "加入黑名单成功");
                            }
                        }
                        if (frame_blacklist_content.getVisibility() == View.VISIBLE) {
                            if (isRemoveBlackList) {
                                ToastSingleShow.showText(RouterMainActivity.this, "恢复上网设置成功");
                            }
                        }

                        break;
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isMqttConnect = false;
                mConnectedDevices.clear();
                mAdapter.notifyDataSetChanged();
                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                switch (action) {
                    case GET_BINDING:
                        break;
                }
            }
        };
    }

    private void updatePerformance() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshCount = 0;
                //这里不用设置设备上线的字段，在routerdevice类里面设置了
                String mem = routerDevice.getPerformance().getDevice().getMEM();
                if (Integer.valueOf(mem) > 80) {
                    textview_memory_use.setTextColor(getResources().getColor(R.color.cpu_percent_text_color));
                } else {
                    textview_memory_use.setTextColor(getResources().getColor(R.color.memory_percent_text_color));
                }
                textview_memory_use.setText(mem + "%");
                String cpu = routerDevice.getPerformance().getDevice().getCPU();
                if (Integer.valueOf(cpu) > 80) {
                    textview_cpu_use.setTextColor(getResources().getColor(R.color.cpu_percent_text_color));
                } else {
                    textview_cpu_use.setTextColor(getResources().getColor(R.color.memory_percent_text_color));
                }
                textview_cpu_use.setText(cpu + "%");

                textview_upload_speed.setText("" + String.format(getResources().getString(R.string.rate_format), routerDevice.getUpRate()));

                textview_download_speend.setText("" + String.format(getResources().getString(R.string.rate_format), routerDevice.getDownRate()));

            }
        });
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        button_connected_devices.setOnClickListener(this);
        button_blak_list.setOnClickListener(this);
        imageview_setting.setOnClickListener(this);
        listview_device_list.setAdapter(mAdapter);


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listview_device_list.setMenuCreator(creator);
        listview_device_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        MakeSureDialog dialog = new MakeSureDialog(RouterMainActivity.this);
                        dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                            @Override
                            public void onSureBtnClicked() {
                                isSetBlackList = true;
                                String mac = mConnectedDevices.get(position).getMAC();
                                String name = mConnectedDevices.get(position).getDeviceName();
                                DeviceControl control = new DeviceControl();
                                BLACKLIST blacklist = new BLACKLIST();
                                blacklist.setDeviceMac(mac);
                                blacklist.setDeviceName(name);
                                control.setBLACKLIST(blacklist);
                                try {
                                    routerDevice.setDeviceControl(control);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog.show();
                        dialog.setTitleText("确定将设备(" + mConnectedDevices.get(position).getDeviceName() + ")拉入黑名单");

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
        listview_black_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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
                                isRemoveBlackList = true;
                                String mac = mBlackListDatas.get(position).getMAC();
                                DeviceControl control = new DeviceControl();
                                WHITELIST whitelist = new WHITELIST();
                                whitelist.setDeviceMac(mac);
                                control.setWHITELIST(whitelist);
                                if (routerDevice != null) {
                                    routerDevice.setDeviceControl(control);
                                }
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
                        isRemoveBlackList = true;
                        String mac = mBlackListDatas.get(position).getMAC();
                        DeviceControl control = new DeviceControl();
                        WHITELIST whitelist = new WHITELIST();
                        whitelist.setDeviceMac(mac);
                        control.setWHITELIST(whitelist);
                        if (routerDevice != null) {
                            routerDevice.setDeviceControl(control);
                        }
                    }
                });
                dialog.show();
                dialog.setTitleText("确定将该设备从黑名单移除");
            }
        });

    }


    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        imageview_setting = (ImageView) findViewById(R.id.imageview_setting);
        listview_device_list = (SwipeMenuListView) findViewById(R.id.listview_device_list);
        button_connected_devices = (Button) findViewById(R.id.button_connected_devices);
        button_blak_list = (Button) findViewById(R.id.button_blak_list);
        listview_black_list = (SwipeMenuListView) findViewById(R.id.listview_black_list);
        textview_show_query_device_result = (TextView) findViewById(R.id.textview_show_query_device_result);
        textview_show_blacklist_device_result = (TextView) findViewById(R.id.textview_show_blacklist_device_result);
        frame_devicelist_content_content = (FrameLayout) findViewById(R.id.frame_devicelist_content_content);
        frame_blacklist_content = (FrameLayout) findViewById(R.id.frame_blacklist_content);
        textview_cpu_use = (TextView) findViewById(R.id.textview_cpu_use);
        textview_memory_use = (TextView) findViewById(R.id.textview_memory_use);
        textview_upload_speed = (TextView) findViewById(R.id.textview_upload_speed);
        textview_download_speend = (TextView) findViewById(R.id.textview_download_speend);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_connected_devices:
                frame_blacklist_content.setVisibility(View.GONE);
                frame_devicelist_content_content.setVisibility(View.VISIBLE);
                showQueryingDialog();
                break;
            case R.id.button_blak_list:
                frame_devicelist_content_content.setVisibility(View.GONE);
                frame_blacklist_content.setVisibility(View.VISIBLE);
                showQueryingDialog();
                break;
            case R.id.imageview_setting:
                startActivity(new Intent(this, RouterSettingActivity.class));
                break;
        }
    }
}
