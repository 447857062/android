package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.constant.TvKeyNameConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;

public class LearnByHandActivity extends Activity implements View.OnClickListener,RemoteControlListener{
    private Button button_cancel;
    private RemoteControlManager mRemoteControlManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_by_hand);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRemoteControlManager.study();
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

    @Override
    public void responseQueryResult(String result) {
        if(result.contains("Study")){
            //学习成功
            int currentLearnByHand=mRemoteControlManager.getCurrentLearnByHandKeyName();
            switch (currentLearnByHand){
                case TvKeyNameConstant.KEYNAME.KEYNAME_CH_PLUS:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_CH_REDUCE:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_DOWN:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_HOME:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_LEFT:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_RIGHT:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_MUTE:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_VOL_PLUS:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_VOL_REDUCE:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_POWER:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_RETURN:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_SURE:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_0:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_1:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_2:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_3:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_4:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_5:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_6:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_7:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_8:
                    break;
                case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_9:
                    break;
            }
            LearnByHandActivity.this.finish();
        }
    }
}
