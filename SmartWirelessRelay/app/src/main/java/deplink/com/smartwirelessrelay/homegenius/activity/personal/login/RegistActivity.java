package deplink.com.smartwirelessrelay.homegenius.activity.personal.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class RegistActivity extends Activity implements View.OnClickListener{


    private TextView textview_title;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("注册");
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
