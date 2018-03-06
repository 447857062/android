package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.doorbeel.DoorBellListener;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.WeakRefHandler;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenu;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenuCreator;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenuItem;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenuListView;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.json.homegenius.DoorBellItem;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class VistorHistoryActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "VistorHistoryActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private DoorbeelManager mDoorbeelManager;
    private SwipeMenuListView listview_vistor_list;
    private boolean isStartFromExperience;
    private List<DoorBellItem> visitorList;
    private List<Bitmap> visitorListImage;
    private DoorBellListener mDoorBellListener;
    private VisitorListAdapter mAdapter;
    private RelativeLayout layout_no_visitor;
    private Timer refreshTimer = null;
    private TimerTask refreshTask = null;
    private static final int TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS = 60000;
    private SDKManager manager;
    private EventCallback ec;
    private boolean isUserLogin;
    private DeleteDeviceDialog connectLostDialog;
    private TextView textview_visitor_loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistor_history);
        initViews();
        initDatas();
        initEvents();
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
                            mDoorbeelManager.getDoorbellHistory();
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

    @Override
    protected void onResume() {
        super.onResume();
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        mDoorbeelManager.addDeviceListener(mDoorBellListener);
        manager.addEventCallback(ec);
        if (!isStartFromExperience) {
            if (isUserLogin) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTimer();
                    }
                },500);
            } else {
                ToastSingleShow.showText(this, "未登录");
            }
        } else {
            layout_no_visitor.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDoorbeelManager.removeDeviceListener(mDoorBellListener);
        manager.removeEventCallback(ec);
        stopTimer();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        if (!isStartFromExperience) {
            listview_vistor_list.setAdapter(mAdapter);
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.delete_button)));
                    deleteItem.setWidth((int) Perfence.dp2px(VistorHistoryActivity.this, 70));
                    //  deleteItem.setBackground(R.layout.listview_deleteitem_layout);
                    // set item width
                    deleteItem.setTitle("删除");
                    deleteItem.setTitleSize(14);
                    // set item title font color
                    deleteItem.setTitleColor(Color.WHITE);
                    menu.addMenuItem(deleteItem);
                }
            };
            // set creator
            listview_vistor_list.setMenuCreator(creator);
            listview_vistor_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            DeleteDeviceDialog deleteDialog;
                            deleteDialog = new DeleteDeviceDialog(VistorHistoryActivity.this);
                            deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                                @Override
                                public void onSureBtnClicked() {
                                    if (!isStartFromExperience) {
                                        mDoorbeelManager.deleteDoorbellVistorImage(visitorList.get(position).getFile());
                                    }
                                }
                            });
                            deleteDialog.show();
                            deleteDialog.setTitleText("删除记录");
                            deleteDialog.setContentText("删除访问记录?");
                            break;
                    }
                    return false;
                }
            });
            //下拉刷星
            listview_vistor_list.setOnItemClickListener(deviceItemClickListener);

        }
    }
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_TOAST_DELETE_RECORD_RESULT:
                    if((boolean) msg.obj){
                        ToastSingleShow.showText(VistorHistoryActivity.this,"删除访客记录成功");
                    }else{
                        ToastSingleShow.showText(VistorHistoryActivity.this,"删除访客记录失败");
                    }

                    break;
            }
            return true;
        }
    };
    public static final int MSG_TOAST_DELETE_RECORD_RESULT=100;
    private Handler mHandler = new WeakRefHandler(mCallback);
    private AdapterView.OnItemClickListener deviceItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Intent intent = new Intent(VistorHistoryActivity.this, DoorbellLargeImage.class);
            intent.putExtra("file", visitorList.get(position).getFile());
            startActivity(intent);
        }
    };
    private void initDatas() {
        textview_title.setText("访客记录");
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        visitorList = new ArrayList<>();
        visitorListImage = new ArrayList<>();
        mDoorBellListener = new DoorBellListener() {
            @Override
            public void responseVisitorListResult(List<DoorBellItem> list) {
                super.responseVisitorListResult(list);
                if (list != null) {
                    Collections.sort(list, new Comparator<DoorBellItem>() {
                        @Override
                        public int compare(DoorBellItem o1, DoorBellItem o2) {
                            //compareTo就是比较两个值，如果前者大于后者，返回1，等于返回0，小于返回-1
                            if (o1.getTimestamp() == o2.getTimestamp()) {
                                return 0;
                            }
                            if (o1.getTimestamp() > o2.getTimestamp()) {
                                return -1;
                            }
                            if (o1.getTimestamp() < o2.getTimestamp()) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    visitorList.clear();
                    visitorList.addAll(list);
                    visitorListImage.clear();

                    for (int i = 0; i < visitorList.size(); i++) {
                        mDoorbeelManager.getDoorbellVistorImage(list.get(i).getFile(), i);
                    }
                    if (visitorList.size() > 0) {
                        layout_no_visitor.setVisibility(View.GONE);
                    } else {
                        layout_no_visitor.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void responseVisitorImage(Bitmap bitmap, int count) {
                super.responseVisitorImage(bitmap, count);
                if (count < visitorList.size()) {
                    visitorListImage.add(bitmap);
                }
                textview_visitor_loading.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();

            }
            @Override
            public void responseDeleteRecordHistory(boolean success) {
                super.responseDeleteRecordHistory(success);
                Message msg=Message.obtain();
                msg.what=MSG_TOAST_DELETE_RECORD_RESULT;
                msg.obj=success;
                mHandler.sendMessage(msg);
            }
        };
        mAdapter = new VisitorListAdapter(this, visitorList, visitorListImage);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        connectLostDialog = new DeleteDeviceDialog(VistorHistoryActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(VistorHistoryActivity.this, LoginActivity.class));
            }
        });
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }

            @Override
            public void deviceOpSuccess(String op, final String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);

            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                mAdapter.notifyDataSetChanged();

                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
            }
        };
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        listview_vistor_list = findViewById(R.id.listview_vistor_list);
        layout_no_visitor = findViewById(R.id.layout_no_visitor);
        textview_visitor_loading = findViewById(R.id.textview_visitor_loading);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
