package deplink.com.smartwirelessrelay.homegenius.activity.personal.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ForgetPasswordActivity extends Activity implements View.OnClickListener{
    private ImageView imageview_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initViews();
        initEvents();
    }

    private void initEvents() {
        imageview_back.setOnClickListener(this);
    }

    private void initViews() {
        imageview_back= (ImageView) findViewById(R.id.imageview_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageview_back:
                onBackPressed();
                break;
        }
    }
}
