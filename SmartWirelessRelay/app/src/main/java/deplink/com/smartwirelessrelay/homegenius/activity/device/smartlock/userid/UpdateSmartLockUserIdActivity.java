package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.userid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class UpdateSmartLockUserIdActivity extends Activity implements View.OnClickListener{
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private ListView listview_update_ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_smart_lock_user_id);
        initViews();
        initDatas();
        initEvents();
    }
    private UserIdAdapter mUserIdAdapter;
    private List<String>mIds;
    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        listview_update_ids.setAdapter(mUserIdAdapter);
    }

    private void initDatas() {
        textview_title.setText("修改ID名称");

        textview_edit.setText("完成");
        mIds=new ArrayList<>();
        mIds.add("001");
        mIds.add("002");
        mIds.add("003");
        mUserIdAdapter=new UserIdAdapter(this,mIds);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        listview_update_ids= (ListView) findViewById(R.id.listview_update_ids);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:


                break;


        }
    }
}
