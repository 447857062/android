package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
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
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
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
    private static final int TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS = 5000;
    private SDKManager manager;
    private EventCallback ec;
    private boolean isUserLogin;
    private DeleteDeviceDialog connectLostDialog;
    private TextView textview_edit;
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
        isUserLogin=Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        mDoorbeelManager.addDeviceListener(mDoorBellListener);
        manager.addEventCallback(ec);
        if (!isStartFromExperience) {
            if(isUserLogin){
                startTimer();
            }else{
                ToastSingleShow.showText(this,"未登录");
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
        textview_edit.setOnClickListener(this);
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
            listview_vistor_list.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Yoffset = event.getY();
                        case MotionEvent.ACTION_MOVE:
                            if (event.getY() - Yoffset > HEIGHT_MARK_TO_REFRESH) {
                                DialogThreeBounce.showLoading(VistorHistoryActivity.this);
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
        }
    }

    private float Yoffset = 0;
    private Handler mHandler = new Handler();
    /**
     * 竖向滑动这么多距离就开始刷新
     */
    public static final int HEIGHT_MARK_TO_REFRESH = 250;
    private AdapterView.OnItemClickListener deviceItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
           // Intent intent = new Intent(VistorHistoryActivity.this, DoorbeelMainActivity.class);
           // intent.putExtra("file", visitorList.get(position).getFile());
           // startActivity(intent);
        }
    };
    private int count;
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

                mAdapter.notifyDataSetChanged();

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
        textview_edit = findViewById(R.id.textview_edit);
        listview_vistor_list = findViewById(R.id.listview_vistor_list);
        layout_no_visitor = findViewById(R.id.layout_no_visitor);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                //TODO 清除所有的记录

                break;
        }
    }
}
