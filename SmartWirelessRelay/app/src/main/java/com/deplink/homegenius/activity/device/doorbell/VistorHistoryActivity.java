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
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.doorbeel.DoorBellListener;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.dialog.doorbeel.Doorbeel_menu_Dialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenu;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenuCreator;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenuItem;
import com.deplink.homegenius.view.listview.swipemenulistview.SwipeMenuListView;
import com.deplink.sdk.android.sdk.json.homegenius.DoorBellItem;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class VistorHistoryActivity extends Activity implements View.OnClickListener {
    private static final String TAG="VistorHistoryActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private FrameLayout frame_setting;
    private DoorbeelManager mDoorbeelManager;
    private ImageView image_setting;
    private Doorbeel_menu_Dialog doorbeelMenuDialog;
    private SwipeMenuListView listview_vistor_list;
    private boolean isStartFromExperience;
    private List<DoorBellItem> visitorList;
    private List<Bitmap> visitorListImage;
    private DoorBellListener mDoorBellListener;
    private VisitorListAdapter mAdapter;
    private String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistor_history);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        mDoorbeelManager.addDeviceListener(mDoorBellListener);
        if(!isStartFromExperience){
            mDoorbeelManager.getDoorbellHistory();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDoorbeelManager.removeDeviceListener(mDoorBellListener);
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        if (!isStartFromExperience) {
         //   listview_vistor_list.setAdapter(mAdapter);
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
            MakeSureDialog dialog = new MakeSureDialog(VistorHistoryActivity.this);
            dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                @Override
                public void onSureBtnClicked() {
                    startActivity(new Intent(VistorHistoryActivity.this,DoorbeelMainActivity.class));
                }
            });
            dialog.show();
            dialog.setMsg("删除访问记录?");
        }
    };
    private void initDatas() {
        textview_title.setText("访客记录");
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        image_setting.setImageResource(R.drawable.menuicon);
        doorbeelMenuDialog = new Doorbeel_menu_Dialog(this);
        visitorList = new ArrayList<>();
        visitorListImage=new ArrayList<>();
        mDoorBellListener = new DoorBellListener() {
            @Override
            public void responseVisitorListResult(List<DoorBellItem> list) {
                super.responseVisitorListResult(list);
                if(list!=null){
                    visitorList.clear();
                    visitorList.addAll(list);
                    for(int i=0;i<visitorList.size();i++){
                        mDoorbeelManager.getDoorbellVistorImage("2059824611e309f0",i);
                    }
                }
            }

            @Override
            public void responseVisitorImage(Bitmap bitmap, int count) {
                super.responseVisitorImage(bitmap, count);
                if(count<visitorList.size()){
                    visitorListImage.add(count,bitmap);
                }
                if(count==visitorList.size()){
                  //  mAdapter.notifyDataSetChanged();
                }
            }
        };
        mAdapter=new VisitorListAdapter(this,visitorList,visitorListImage);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        image_setting = findViewById(R.id.image_setting);
        frame_setting = findViewById(R.id.frame_setting);
        listview_vistor_list = findViewById(R.id.listview_vistor_list);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.frame_setting:
                doorbeelMenuDialog.show();
                break;
        }
    }
}
