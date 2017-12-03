package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

public class ModifyRoomNameActivity extends Activity implements View.OnClickListener {
    private ClearEditText clearEditText;
    private String roomName;
    private TextView textview_title;
    private TextView textview_edit;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_room_name);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("修改房间名称");
        textview_edit.setText("完成");
        String hintRoomName= RoomManager.getInstance().getCurrentSelectedRoom().getRoomName();
        clearEditText.setHint(hintRoomName);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        image_back= (ImageView) findViewById(R.id.image_back);
        clearEditText = (ClearEditText) findViewById(R.id.clear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                roomName = clearEditText.getText().toString();
                if (!roomName.equalsIgnoreCase("")) {
                    Intent i = new Intent();
                    i.putExtra("roomName", roomName);
                    setResult(RESULT_OK, i);
                }
                finish();
                break;
        }
    }
}
