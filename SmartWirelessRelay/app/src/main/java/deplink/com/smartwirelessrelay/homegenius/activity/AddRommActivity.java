package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class AddRommActivity extends Activity implements View.OnClickListener{
    private TextView textview_add_room_complement;
    private ImageView image_back;
    private RelativeLayout layout_getway;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_romm);
        initViews();
        initEvents();
    }

    private void initEvents() {
        textview_add_room_complement.setOnClickListener(this);
        image_back.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
    }

    private void initViews() {
        textview_add_room_complement= (TextView) findViewById(R.id.textview_add_room_complement);
        image_back= (ImageView) findViewById(R.id.image_back);
        layout_getway= (RelativeLayout) findViewById(R.id.layout_getway);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //完成
            case  R.id.textview_add_room_complement:
                onBackPressed();
                break;
            case  R.id.image_back:
                onBackPressed();
                break;
            case  R.id.layout_getway:
                startActivity(new Intent(AddRommActivity.this,SmartGetwayActivity.class));
                break;
        }
    }
}
