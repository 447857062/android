package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.os.Bundle;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class HomePageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }
}
