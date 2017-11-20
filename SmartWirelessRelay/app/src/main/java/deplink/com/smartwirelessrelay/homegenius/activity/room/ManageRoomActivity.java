package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.SmartGetwayActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ManageRoomActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ManageRoomActivity";
    public static final int REQUEST_MODIFY_ROOM_NAME = 100;
    private ImageView image_back;
    private Button button_delete_room;
    private TextView textview_device_number;
    private TextView textview_room_name;
    private Bundle mBundle;
    private String mRoomName;
    private RelativeLayout layout_room_name;
    private RelativeLayout layout_device_number;
    private RelativeLayout layout_getway;
    private int roomOrdinalNumber;
    private RoomManager mRoomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_room);

        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mBundle = getIntent().getExtras();
        Log.i(TAG, "mBundle!=null " + (mBundle != null));
        if (mBundle != null) {
            mRoomName = mBundle.getString("roomName");
            Log.i(TAG, "当前编辑的房间名称= " + mRoomName);
            textview_room_name.setText(mRoomName);
        }
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_delete_room.setOnClickListener(this);
        layout_room_name.setOnClickListener(this);
        layout_device_number.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
    }

    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        button_delete_room = (Button) findViewById(R.id.button_delete_room);
        textview_room_name = (TextView) findViewById(R.id.textview_room_name);
        textview_device_number = (TextView) findViewById(R.id.textview_device_number);
        layout_room_name = (RelativeLayout) findViewById(R.id.layout_room_name);
        layout_device_number = (RelativeLayout) findViewById(R.id.layout_device_number);
        layout_getway = (RelativeLayout) findViewById(R.id.layout_getway);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_delete_room:
                //TODO 删除房间
                if (mRoomName != null) {
                    mRoomManager.deleteRoom(mRoomName, new Observer() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }
                        @Override
                        public void onNext(@NonNull Object o) {
                            Log.i(TAG, "删除房间，影响的行数=" + o);
                            if ((Integer)o == 1) {
                                finish();
                            } else {
                                Toast.makeText(ManageRoomActivity.this, "删除房间失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

                }

                break;
            case R.id.layout_room_name:
                intent = new Intent(this, ModifyRoomNameActivity.class);
                intent.putExtra("roomname", mRoomName);
                //创建一个Bundle，用来在Activity间传递数据
                startActivityForResult(intent, REQUEST_MODIFY_ROOM_NAME);
                break;
            case R.id.layout_device_number:
                intent = new Intent(this, DeviceNumberActivity.class);
                //创建一个Bundle，用来在Activity间传递数据
                intent.putExtra("roomname", mRoomName);
                startActivity(intent);
                break;
            case R.id.layout_getway:
                intent = new Intent(this, SmartGetwayActivity.class);
                //创建一个Bundle，用来在Activity间传递数据
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_MODIFY_ROOM_NAME) {
            mRoomName = data.getStringExtra("roomName");
            Log.i(TAG, "onActivityResult mRoomName=" + mRoomName);
            textview_room_name.setText(mRoomName);
        }
    }
}
