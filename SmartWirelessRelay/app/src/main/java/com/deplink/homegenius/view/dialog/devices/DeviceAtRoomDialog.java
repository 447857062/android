package com.deplink.homegenius.view.dialog.devices;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.deplink.homegenius.util.Perfence;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/7/25.
 */
public class DeviceAtRoomDialog extends Dialog implements View.OnClickListener ,AdapterView.OnItemClickListener{
    private Context mContext;

    private View view_device_menu;
    private List<String>mRoomTypes;
    private ListView listview_room_types;
    private DeviceRoomTypeDialogAdapter mAdapter;
    public DeviceAtRoomDialog(Context context,List<String>roomTypes) {
        super(context, R.style.MakeSureDialog);
        mContext = context;
        this.mRoomTypes=roomTypes;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        p.width = (int) Perfence.dp2px(mContext,120);
        View view = LayoutInflater.from(mContext).inflate(R.layout.device_at_room_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
    }
    private void initView() {
        view_device_menu=findViewById(R.id.view_device_menu);
        listview_room_types= (ListView) findViewById(R.id.listview_room_types);

    }


    private void initEvent() {
        view_device_menu.setOnClickListener(this);
        mAdapter=new DeviceRoomTypeDialogAdapter(mContext,mRoomTypes);
        listview_room_types.setAdapter(mAdapter);
        listview_room_types.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.view_device_menu:
                this.dismiss();
                break;
        }
    }


    @Override
    public void show() {
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity( Gravity.LEFT|Gravity.TOP);
        super.show();

    }
    private onItemClickListener mOnItemClickListener;
    public void setRoomTypeItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public interface onItemClickListener {
        void onItemClicked(int position);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClicked(position);
        }
    }
}
