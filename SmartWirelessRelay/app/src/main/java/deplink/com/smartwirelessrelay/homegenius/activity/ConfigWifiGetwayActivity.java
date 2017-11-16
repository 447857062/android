package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ConfigWifiGetwayActivity extends Activity implements View.OnClickListener{
    private static final String TAG="ConfigWifiGetwayActivity";
    private ImageView image_back;
    private Button button_connect_right_now;
    private EditText edittext_input_wifi_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_wifi_getway);
        initViews();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_connect_right_now.setOnClickListener(this);
    }

    private void initViews() {
        image_back= (ImageView) findViewById(R.id.image_back);
        button_connect_right_now= (Button) findViewById(R.id.button_connect_right_now);
        edittext_input_wifi_password= (EditText) findViewById(R.id.edittext_input_wifi_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_connect_right_now:

                break;
        }
    }
}
