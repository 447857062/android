package deplink.com.smartwirelessrelay.homegenius.view.dialog.devices;

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
import android.widget.ListView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;


/**
 * Created by Administrator on 2017/7/25.
 */
public class DeviceAtRoomDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    private View view_mode_menu;
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
        view_mode_menu=findViewById(R.id.view_device_menu);
        listview_room_types= (ListView) findViewById(R.id.listview_room_types);

    }


    private void initEvent() {
        view_mode_menu.setOnClickListener(this);
        mAdapter=new DeviceRoomTypeDialogAdapter(mContext,mRoomTypes);
        listview_room_types.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.view_mode_menu:
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

}
