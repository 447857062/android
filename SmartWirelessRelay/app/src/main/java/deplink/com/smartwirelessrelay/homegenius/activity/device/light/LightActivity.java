package deplink.com.smartwirelessrelay.homegenius.activity.device.light;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.light.SmartLightListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.light.SmartLightManager;

public class LightActivity extends Activity implements View.OnClickListener, SmartLightListener {
    private static final String TAG = "LightActivity";
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private ImageView button_switch_light;
    private ImageView imageview_switch_bg;
    private SmartLightManager mSmartLightManager;
    private boolean switchStatus;
    private SeekBar progressBarLightYellow;
    private int lightColorProgress;
    private SeekBar progressBarLightWhite;
    private int lightBrightnessProgress;
    private ImageView imageview_lightyellow_reduce;
    private ImageView imageview_lightyellow_plus;
    private ImageView imageview_lightwhite_reduce;
    private ImageView imageview_lightwhite_plus;
    private ImageView iamgeview_switch;
    private TextView textview_switch_tips;
    private RelativeLayout layout_lightcolor_control;
    private RelativeLayout layout_brightness_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_switch_light.setOnClickListener(this);
        imageview_lightyellow_reduce.setOnClickListener(this);
        imageview_lightyellow_plus.setOnClickListener(this);
        imageview_lightwhite_reduce.setOnClickListener(this);
        imageview_lightwhite_plus.setOnClickListener(this);
        progressBarLightYellow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lightColorProgress = progress * 2;
                Log.i(TAG, "lightColorProgress=" + lightColorProgress + "lightBrightnessProgress=" + lightBrightnessProgress);
                if (isStartFromExperience) {
                    button_switch_light.setBackgroundResource(R.drawable.lightyellowlight);
                    float alpha = (float) (lightColorProgress / 200.0);
                    Log.i(TAG, "alpha=" + alpha);
                    button_switch_light.setAlpha(alpha);
                } else {
                    mSmartLightManager.setSmartLightParamas("regulation", lightColorProgress, lightBrightnessProgress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        progressBarLightWhite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lightBrightnessProgress = progress * 2;
                Log.i(TAG, "lightColorProgress=" + lightColorProgress + "lightBrightnessProgress=" + lightBrightnessProgress);
                if (isStartFromExperience) {
                    float alpha = (float) (lightBrightnessProgress / 200.0);
                    Log.i(TAG, "alpha=" + alpha);
                    imageview_switch_bg.setAlpha(alpha);
                } else {
                    mSmartLightManager.setSmartLightParamas("regulation", lightColorProgress, lightBrightnessProgress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initDatas() {
        textview_title.setText("智能灯泡");
        textview_edit.setText("编辑");
        mSmartLightManager = SmartLightManager.getInstance();
        mSmartLightManager.InitSmartLightManager(this);
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        button_switch_light = findViewById(R.id.button_switch_light);
        progressBarLightYellow = findViewById(R.id.lightColorProgressBar);
        progressBarLightWhite = findViewById(R.id.progressBar_brightness);
        imageview_lightyellow_reduce = findViewById(R.id.imageview_lightyellow_reduce);
        imageview_lightyellow_plus = findViewById(R.id.imageview_lightyellow_plus);
        imageview_lightwhite_reduce = findViewById(R.id.imageview_lightwhite_reduce);
        imageview_lightwhite_plus = findViewById(R.id.imageview_lightwhite_plus);
        iamgeview_switch = findViewById(R.id.iamgeview_switch);
        textview_switch_tips = findViewById(R.id.textview_switch_tips);
        imageview_switch_bg = findViewById(R.id.imageview_switch_bg);
        layout_brightness_control = findViewById(R.id.layout_brightness_control);
        layout_lightcolor_control = findViewById(R.id.layout_lightcolor_control);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartLightManager.releaswSmartManager();
    }

    private boolean isOnResume;
    private boolean isStartFromExperience;

    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        if (isStartFromExperience) {
            layout_lightcolor_control.setVisibility(View.GONE);
            layout_brightness_control.setVisibility(View.GONE);
        } else {
            mSmartLightManager.queryLightStatus();
            mSmartLightManager.addSmartLightListener(this);
        }
        isOnResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStartFromExperience) {

        } else {
            mSmartLightManager.removeSmartLightListener(this);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.imageview_lightyellow_reduce:
                lightColorProgress -= 20;
                if (lightColorProgress < 0) {
                    lightColorProgress = 200;
                }
                if (isStartFromExperience) {
                    progressBarLightYellow.setProgress(lightColorProgress);
                } else {
                    mSmartLightManager.setSmartLightParamas("regulation", lightColorProgress, lightBrightnessProgress);
                }

                break;
            case R.id.imageview_lightyellow_plus:
                lightColorProgress += 20;
                if (lightColorProgress > 200) {
                    lightColorProgress = 0;
                }
                if (isStartFromExperience) {
                    progressBarLightYellow.setProgress(lightColorProgress);
                } else {
                    mSmartLightManager.setSmartLightParamas("regulation", lightColorProgress, lightBrightnessProgress);
                }

                break;
            case R.id.imageview_lightwhite_reduce:
                lightBrightnessProgress -= 20;
                if (lightBrightnessProgress < 0) {
                    lightBrightnessProgress = 200;
                }
                if (isStartFromExperience) {
                    progressBarLightWhite.setProgress(lightBrightnessProgress);
                } else {
                    mSmartLightManager.setSmartLightParamas("regulation", lightColorProgress, lightBrightnessProgress);
                }

                break;
            case R.id.imageview_lightwhite_plus:
                lightBrightnessProgress += 20;
                if (lightBrightnessProgress > 200) {
                    lightBrightnessProgress = 0;
                }
                if (isStartFromExperience) {
                    progressBarLightWhite.setProgress(lightBrightnessProgress);
                } else {
                    mSmartLightManager.setSmartLightParamas("regulation", lightColorProgress, lightBrightnessProgress);
                }

                break;
            case R.id.button_switch_light:
                if (isStartFromExperience) {
                    if (switchStatus) {
                        switchStatus = false;
                        iamgeview_switch.setBackgroundResource(R.drawable.ovel_110_bg);
                        imageview_switch_bg.setBackgroundResource(R.color.room_type_text);
                        textview_switch_tips.setText("点击开启");
                        layout_lightcolor_control.setVisibility(View.GONE);
                        layout_brightness_control.setVisibility(View.GONE);
                    } else {
                        switchStatus = true;
                        iamgeview_switch.setBackgroundResource(R.drawable.radius110_bg_white_background);
                        imageview_switch_bg.setBackgroundResource(R.drawable.lightglowoutside);
                        textview_switch_tips.setText("点击关闭");
                        layout_lightcolor_control.setVisibility(View.VISIBLE);
                        layout_brightness_control.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (switchStatus) {
                        switchStatus = false;
                        mSmartLightManager.setSmartLightSwitch("close");
                    } else {
                        switchStatus = true;
                        mSmartLightManager.setSmartLightSwitch("open");
                    }
                }


                break;
            case R.id.textview_edit:
                startActivity(new Intent(this, LightEditActivity.class));
                break;
        }
    }

    private static final int MSG_GET_LIGHT_RESULT = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_LIGHT_RESULT:
                    QueryOptions resultObj = (QueryOptions) msg.obj;
                    if (resultObj.getOpen() == 1) {
                        iamgeview_switch.setBackgroundResource(R.drawable.radius110_bg_white_background);
                        imageview_switch_bg.setBackgroundResource(R.drawable.lightglowoutside);
                        textview_switch_tips.setText("点击关闭");
                        layout_lightcolor_control.setVisibility(View.VISIBLE);
                        layout_brightness_control.setVisibility(View.VISIBLE);
                    } else if (resultObj.getOpen() == 2) {
                        iamgeview_switch.setBackgroundResource(R.drawable.ovel_110_bg);
                        imageview_switch_bg.setBackgroundResource(R.color.room_type_text);
                        textview_switch_tips.setText("点击开启");
                        layout_lightcolor_control.setVisibility(View.GONE);
                        layout_brightness_control.setVisibility(View.GONE);
                    }
                    button_switch_light.setBackgroundResource(R.drawable.lightwhitelight);
                    if (resultObj.getYellow() != 0) {
                        button_switch_light.setBackgroundResource(R.drawable.lightyellowlight);
                        float alpha = (float) (resultObj.getYellow() / 200.0);
                        Log.i(TAG, "alpha=" + alpha);
                        button_switch_light.setAlpha(alpha);
                        if (isOnResume) {
                            progressBarLightYellow.setProgress(resultObj.getYellow() / 2);
                        }

                    }
                    if (resultObj.getWhite() != 0) {
                        float alpha = (float) (resultObj.getWhite() / 200.0);
                        Log.i(TAG, "alpha=" + alpha);
                        imageview_switch_bg.setAlpha(alpha);
                        if (isOnResume) {
                            progressBarLightWhite.setProgress(resultObj.getWhite() / 2);
                        }

                    }
                    isOnResume = false;
                    break;
            }
        }
    };

    @Override
    public void responseSetResult(String result) {
        Log.i(TAG, "responseSetResult=" + result);
        Gson gson = new Gson();
        QueryOptions resultObj = gson.fromJson(result, QueryOptions.class);
        Message msg = Message.obtain();
        msg.obj = resultObj;
        msg.what = MSG_GET_LIGHT_RESULT;
        mHandler.sendMessage(msg);
    }
}
