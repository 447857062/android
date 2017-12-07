package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class LearnByHandActivity extends Activity implements View.OnClickListener{
    private Button button_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_by_hand);
        initViews();
        initEvents();
    }

    private void initEvents() {
        button_cancel.setOnClickListener(this);
    }

    private void initViews() {
        button_cancel= (Button) findViewById(R.id.button_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cancel:
                this.finish();
                break;
        }
    }
}
