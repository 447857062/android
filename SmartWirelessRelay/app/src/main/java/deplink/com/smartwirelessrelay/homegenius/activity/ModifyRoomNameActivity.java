package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

public class ModifyRoomNameActivity extends Activity implements View.OnClickListener {
    private TextView textview_title_complement;
    private ClearEditText clearEditText;
    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_room_name);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        String hintRoomName=getIntent().getStringExtra("roomname");
        clearEditText.setHint(hintRoomName);
    }

    private void initEvents() {
        textview_title_complement.setOnClickListener(this);
    }

    private void initViews() {
        textview_title_complement = (TextView) findViewById(R.id.textview_title_complement);
        clearEditText = (ClearEditText) findViewById(R.id.clear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_title_complement:
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
