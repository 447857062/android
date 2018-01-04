package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

public class ModifyRoomNameActivity extends Activity implements View.OnClickListener {
    private ClearEditText clearEditText;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_room_name);
        initViews();
        initDatas();
        initEvents();
    }

    String orgRoomName;

    @Override
    protected void onResume() {
        super.onResume();
        orgRoomName = RoomManager.getInstance().getCurrentSelectedRoom().getRoomName();
        clearEditText.setText(orgRoomName);
        clearEditText.setSelection(orgRoomName.length());
    }

    private void initDatas() {
        textview_title.setText("修改房间名称");
        textview_edit.setText("完成");

    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
        clearEditText = findViewById(R.id.clear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                String roomName = clearEditText.getText().toString();
                if (!roomName.equalsIgnoreCase("")&& !roomName.equals(orgRoomName)) {
                    int result=RoomManager.getInstance().updateRoomName(orgRoomName, roomName);
                    if(result!=1){
                        Toast.makeText(this,"修改房间名称失败",Toast.LENGTH_SHORT).show();
                    }else{
                        finish();
                    }
                }

                break;
        }
    }
}
