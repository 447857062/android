package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.deplink.sdk.android.sdk.rest.ConverterFactory.CheckResponse;
import com.deplink.sdk.android.sdk.rest.RestfulToolsRouter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter.BlackListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter.ConnectedDeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureWithInputDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenu;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuCreator;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuItem;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.SwipeMenuListView;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private TextView textview_title;
    private FrameLayout image_back;
    private ImageView image_setting;
    /**
     * 已连接设备
     */
    private SwipeMenuListView listview_device_list;
    private List<DevicesOnline> mConnectedDevices;
    private ConnectedDeviceListAdapter mAdapter;
    private SwipeMenuListView listview_black_list;
    private List<BLACKLIST> mBlackListDatas;
    private BlackListAdapter mBlackListAdapter;
    private RelativeLayout layout_blak_list;
    private RelativeLayout layout_connected_devices;
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
    private View view_line_blak_list;
    private View view_line_connected_devices;
    private TextView textview_connected_devices;
    private TextView textview_blak_list;
    private FrameLayout frame_setting;
    /**
     * 检查使用本地接口的本地路由器连接情况
     */
    private boolean isConnectLocalRouter = false;
    private MakeSureWithInputDialog connetWifiDialog;

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
        isStartFromExperience = mRouterManager.isStartFromExperience();
        if (isStartFromExperience) {

        } else {
            manager.addEventCallback(ec);
            isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
            frame_blacklist_content.setVisibility(View.GONE);
            routerDevice = mRouterManager.getRouterDevice();
            Log.i(TAG, "routerDevice=" + (routerDevice != null));
            startTimer();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        manager.removeEventCallback(ec);
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
            Log.i(TAG, "设备离线了");
            routerDevice.setOnline(false);
            mRouterManager.getCurrentSelectedRouter().setStatus("离线");
            ContentValues values = new ContentValues();
            values.put("Status", "离线");
            final int affectColumn = DataSupport.updateAll(SmartDev.class, values, "Uid=?", mRouterManager.getCurrentSelectedRouter().getUid());

            Log.i(TAG, "更新设备在线状态 :离线 affectColumn=" + affectColumn);
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

    private boolean isStartFromExperience;

    private void initDatas() {
        textview_title.setText("路由器");
        image_setting.setImageResource(R.drawable.settingicon);

        mConnectedDevices = new ArrayList<>();
        mBlackListDatas = new ArrayList<>();
        mAdapter = new ConnectedDeviceListAdapter(this, mConnectedDevices);
        mBlackListAdapter = new BlackListAdapter(this, mBlackListDatas);
        mRouterManager = RouterManager.getInstance();
        // mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        isStartFromExperience = mRouterManager.isStartFromExperience();
        if (isStartFromExperience) {

        } else {
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


                            break;
                        case RouterDevice.OP_GET_REPORT:
                            mRouterManager.getCurrentSelectedRouter().setStatus("在线");
                            ContentValues values = new ContentValues();
                            values.put("Status", "在线");
                            final int affectColumn = DataSupport.updateAll(SmartDev.class, values, "Uid=?", mRouterManager.getCurrentSelectedRouter().getUid());
                            updatePerformance();

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

    }

    private void updatePerformance() {
        Log.i(TAG, "updatePerformance");
        refreshCount = 0;
        //这里不用设置设备上线的字段，在routerdevice类里面设置了
        String mem = routerDevice.getPerformance().getDevice().getMEM();
        textview_memory_use.setText(mem);
        String cpu = routerDevice.getPerformance().getDevice().getCPU();
        textview_cpu_use.setText(cpu);

        textview_upload_speed.setText("" + String.format(getResources().getString(R.string.rate_format), routerDevice.getUpRate()));

        textview_download_speend.setText("" + String.format(getResources().getString(R.string.rate_format), routerDevice.getDownRate()));

    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        layout_connected_devices.setOnClickListener(this);
        layout_blak_list.setOnClickListener(this);
        if (isStartFromExperience) {

        } else {
            listview_device_list.setAdapter(mAdapter);
        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.delete_button)));
                deleteItem.setWidth((int) Perfence.dp2px(RouterMainActivity.this, 70));
                //  deleteItem.setBackground(R.layout.listview_deleteitem_layout);
                // set item width
                deleteItem.setTitle("拉黑");
                deleteItem.setTitleSize(14);
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
                                    if (isStartFromExperience) {

                                    } else {
                                        routerDevice.setDeviceControl(control);
                                    }

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
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.delete_button)));
                // set item width
                openItem.setWidth((int) Perfence.dp2px(RouterMainActivity.this, 70));
                // set item title
                openItem.setTitle("恢复上网");
                // set item title fontsize
                openItem.setTitleSize(14);
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
        frame_setting = (FrameLayout) findViewById(R.id.frame_setting);
        textview_connected_devices = (TextView) findViewById(R.id.textview_connected_devices);
        textview_blak_list = (TextView) findViewById(R.id.textview_blak_list);
        view_line_connected_devices = findViewById(R.id.view_line_connected_devices);
        view_line_blak_list = findViewById(R.id.view_line_blak_list);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        image_setting = (ImageView) findViewById(R.id.image_setting);
        textview_title = (TextView) findViewById(R.id.textview_title);
        listview_device_list = (SwipeMenuListView) findViewById(R.id.listview_device_list);
        layout_connected_devices = (RelativeLayout) findViewById(R.id.layout_connected_devices);
        layout_blak_list = (RelativeLayout) findViewById(R.id.layout_blak_list);
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
            case R.id.layout_connected_devices:
                frame_blacklist_content.setVisibility(View.GONE);
                frame_devicelist_content_content.setVisibility(View.VISIBLE);
                view_line_blak_list.setVisibility(View.GONE);
                view_line_connected_devices.setVisibility(View.VISIBLE);
                textview_connected_devices.setTextColor(getResources().getColor(R.color.title_blue_bg));
                textview_blak_list.setTextColor(getResources().getColor(R.color.room_type_text));
                showQueryingDialog();
                break;
            case R.id.layout_blak_list:
                frame_devicelist_content_content.setVisibility(View.GONE);
                frame_blacklist_content.setVisibility(View.VISIBLE);
                view_line_blak_list.setVisibility(View.VISIBLE);
                view_line_connected_devices.setVisibility(View.GONE);
                textview_connected_devices.setTextColor(getResources().getColor(R.color.room_type_text));
                textview_blak_list.setTextColor(getResources().getColor(R.color.title_blue_bg));
                showQueryingDialog();
                break;
            case R.id.frame_setting:
                if (isStartFromExperience) {
                    startActivity(new Intent(this, RouterSettingActivity.class));
                } else {
                    if (mRouterManager.getCurrentSelectedRouter().getStatus().equals("在线")) {
                        startActivity(new Intent(this, RouterSettingActivity.class));
                    } else {
                        //本地配置先连路由器
                        checkRouter();
                    }
                }


                break;
        }
    }

    /**
     * 显示检查本地路由器是否连接上的加载中的dialog,超时就取消显示
     */
    private void showCheckRouterLoadingDialog() {
        Log.i(TAG, "showCheckRouterLoadingDialog");
        mHandler.postDelayed(connectStatus, 3000);
        DialogThreeBounce.showLoading(this);
    }


    private Runnable connectStatus = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "connectStatus isConnectLocalRouter=" + isConnectLocalRouter);
            if (!isConnectLocalRouter) {
                connetWifiDialog = new MakeSureWithInputDialog(RouterMainActivity.this, MakeSureWithInputDialog.DIALOG_TYPE_WIFI_CONNECTED);
                connetWifiDialog.setSureBtnClickListener(new MakeSureWithInputDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked(String password) {
                        DialogThreeBounce.hideLoading();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                connetWifiDialog.show();
            }
            DialogThreeBounce.hideLoading();
        }
    };

    /**
     * 检查是否连接本地路由器，（成功连接本地路由器后）选择上网方式
     */
    private void checkRouter() {
        MakeSureWithInputDialog dialog = new MakeSureWithInputDialog(this, MakeSureWithInputDialog.DIALOG_TYPE_BINDED_CHANGE_ROUTER_NAME);
        dialog.setSureBtnClickListener(new MakeSureWithInputDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked(String password) {

                if (!password.equals("")) {
                    showCheckRouterLoadingDialog();
                    isConnectLocalRouter = false;
                    RestfulToolsRouter.getSingleton(getApplicationContext()).checkRouter(password, new Callback<CheckResponse>() {
                        @Override
                        public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                            Log.i(TAG, "checkRouter " + response.body().toString());
                            CheckResponse result = response.body();
                            String token = response.headers().get("Set-Cookie");
                            int preferenceMode;
                            preferenceMode = MODE_PRIVATE;
                            SharedPreferences sp = getSharedPreferences("user", preferenceMode);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", token);
                            editor.apply();

                            if (!result.getLink().equalsIgnoreCase("")) {
                                RestfulToolsRouter.getSingleton(getApplicationContext()).setLink(result.getLink());
                                isConnectLocalRouter = true;
                                mHandler.removeCallbacks(connectStatus);
                                try {
                                    connetWifiDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(RouterMainActivity.this, RouterSettingActivity.class));
                                DialogThreeBounce.hideLoading();


                            }
                        }

                        @Override
                        public void onFailure(Call<CheckResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }

            }
        });
        dialog.show();
        dialog.setTitleText("输入管理员密码");
        dialog.setEditText("admin");
    }
}