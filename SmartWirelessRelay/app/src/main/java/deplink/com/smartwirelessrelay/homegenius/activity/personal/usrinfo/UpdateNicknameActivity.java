package deplink.com.smartwirelessrelay.homegenius.activity.personal.usrinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

public class UpdateNicknameActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "UpdateNicknameActivity";
    private TextView textview_complement;
    private ClearEditText edittext_update_nickname;
    private ImageView imageview_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        initViews();
        initEvents();
    }

    private void initEvents() {
        textview_complement.setOnClickListener(this);
        imageview_back.setOnClickListener(this);
    }

    private void initViews() {
        textview_complement = (TextView) findViewById(R.id.textview_complement);
        edittext_update_nickname = (ClearEditText) findViewById(R.id.edittext_update_nickname);
        imageview_back = (ImageView) findViewById(R.id.imageview_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_complement:
                onBackPressed();
                break;
            case R.id.imageview_back:
                onBackPressed();
                break;
        }
    }
}
