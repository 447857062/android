package com.deplink.homegenius.view.dialog.smartlock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.constant.SmartLockConstant;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.edittext.SecurityPasswordEditText;

import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 授权操作dialog
 */
public class AuthoriseDialog extends Dialog implements View.OnClickListener, TextView.OnEditorActionListener {
    private static final String TAG = "AuthoriseDialog";
    private Context mContext;
    private RelativeLayout linelayout_select_auth_type;
    private RelativeLayout linelayout_auth_type_time_limit;

    private GetDialogAuthtTypeTimeListener mGetDialogAuthtTypeTimeListener;

    private RelativeLayout layout_content_one;
    private RelativeLayout layout_content_two;
    private RelativeLayout layout_content_three;

    /**
     * 单次授权的线性布局
     */
    private RelativeLayout linelayout_auth_type_once;
    /**
     * 永久授权的线性布局
     */
    private RelativeLayout linelayout_auth_type_permanent;
    /**
     * 对话框标题
     */
    private TextView textview_title;
    /**
     * 对话框右上角删除按钮（不显示对话框）
     */
    private ImageView iamgeview_delete;

    /**
     * 当前选择的授权方式
     */
    private TextView textview_current_auth_type;


    private RelativeLayout linelayout_time_limit_2_hours;
    private RelativeLayout linelayout_time_limit_12_hours;
    private RelativeLayout linelayout_time_limit_custom;
    private String currentAuthType;
    private String timeLimitAuthTypeTime;

    private EditText edittext_time_limit_custom;

    public AuthoriseDialog(Context context) {
        super(context, R.style.AuthoriseDialog);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        // p.height = (int) Perfence.dp2px(mContext,180);
        p.width = (int) Perfence.dp2px(mContext, 290);
        View view;

        view = LayoutInflater.from(mContext).inflate(R.layout.authorise_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        initDatas();
        //初始化界面控件的事件
        initEvents();
    }

    private void initDatas() {
        layout_content_one.setVisibility(View.VISIBLE);
        layout_content_two.setVisibility(View.GONE);
        layout_content_three.setVisibility(View.GONE);
        textview_current_auth_type.setText("单次授权");
        timeLimitAuthTypeTime = "2";
        currentAuthType = SmartLockConstant.AUTH_TYPE_ONCE;
    }

    private void initEvents() {
        edt_pwd.setSecurityEditCompleListener(new SecurityPasswordEditText.SecurityEditCompleListener() {

            @Override
            public void onNumCompleted(String num) {
                Log.i(TAG, "输入密码是num=" + num);
                if (mGetDialogAuthtTypeTimeListener != null) {
                    if (currentAuthType.equalsIgnoreCase(SmartLockConstant.AUTH_TYPE_TIME_LIMIT)) {
                        mGetDialogAuthtTypeTimeListener.onGetDialogAuthtTypeTime(SmartLockConstant.AUTH_TYPE_TIME_LIMIT, num, timeLimitAuthTypeTime);
                    } else {
                        mGetDialogAuthtTypeTimeListener.onGetDialogAuthtTypeTime(currentAuthType, num, null);
                    }

                    AuthoriseDialog.this.dismiss();
                } else {
                    Log.i(TAG, "没有给dialog设置数据监听接口");
                }

            }

            @Override
            public void unCompleted(String num) {

            }
        });
        linelayout_select_auth_type.setOnClickListener(this);
        linelayout_auth_type_time_limit.setOnClickListener(this);
        iamgeview_delete.setOnClickListener(this);
        linelayout_auth_type_once.setOnClickListener(this);
        linelayout_auth_type_permanent.setOnClickListener(this);
        linelayout_time_limit_2_hours.setOnClickListener(this);
        linelayout_time_limit_12_hours.setOnClickListener(this);
        linelayout_time_limit_custom.setOnClickListener(this);
        edittext_time_limit_custom.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edittext_time_limit_custom.setImeOptions(EditorInfo.IME_ACTION_GO);

    }

    private SecurityPasswordEditText edt_pwd;

    private void initView() {

        edt_pwd = findViewById(R.id.edt_pwd);

        linelayout_select_auth_type = findViewById(R.id.linelayout_select_auth_type);
        linelayout_auth_type_time_limit = findViewById(R.id.linelayout_auth_type_time_limit);
        layout_content_one = findViewById(R.id.layout_content_one);
        layout_content_two = findViewById(R.id.layout_content_two);
        layout_content_three = findViewById(R.id.layout_content_three);

        textview_title = findViewById(R.id.textview_title);
        iamgeview_delete = findViewById(R.id.iamgeview_delete);
        linelayout_auth_type_once = findViewById(R.id.linelayout_auth_type_once);
        linelayout_auth_type_permanent = findViewById(R.id.linelayout_auth_type_permanent);
        textview_current_auth_type = findViewById(R.id.textview_current_auth_type);
        linelayout_time_limit_2_hours = findViewById(R.id.linelayout_time_limit_2_hours);
        linelayout_time_limit_12_hours = findViewById(R.id.linelayout_time_limit_12_hours);
        linelayout_time_limit_custom = findViewById(R.id.linelayout_time_limit_custom);
        edittext_time_limit_custom = findViewById(R.id.edittext_time_limit_custom);
        edittext_time_limit_custom.setOnEditorActionListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linelayout_select_auth_type:
                layout_content_one.setVisibility(View.GONE);
                layout_content_two.setVisibility(View.VISIBLE);
                layout_content_three.setVisibility(View.GONE);
                textview_title.setText("请选择授权方式");
                break;
            case R.id.linelayout_auth_type_time_limit:
                layout_content_one.setVisibility(View.GONE);
                layout_content_two.setVisibility(View.GONE);
                layout_content_three.setVisibility(View.VISIBLE);
                textview_title.setText("请选择限时时间");
                break;
            case R.id.iamgeview_delete:
                this.dismiss();
                break;
            case R.id.linelayout_auth_type_once:

                textview_current_auth_type.setText("单次授权");
                layout_content_one.setVisibility(View.VISIBLE);
                layout_content_two.setVisibility(View.GONE);
                layout_content_three.setVisibility(View.GONE);
                currentAuthType = SmartLockConstant.AUTH_TYPE_ONCE;
                showInputmothed();
                //TODO
                break;
            case R.id.linelayout_auth_type_permanent:
                layout_content_one.setVisibility(View.VISIBLE);
                layout_content_two.setVisibility(View.GONE);
                layout_content_three.setVisibility(View.GONE);
                textview_current_auth_type.setText("永久授权");
                currentAuthType = SmartLockConstant.AUTH_TYPE_PERPETUAL;
                showInputmothed();
                break;
            case R.id.linelayout_time_limit_2_hours:
                layout_content_one.setVisibility(View.VISIBLE);
                layout_content_two.setVisibility(View.GONE);
                layout_content_three.setVisibility(View.GONE);
                textview_current_auth_type.setText("期限授权");
                timeLimitAuthTypeTime = "2";
                currentAuthType = SmartLockConstant.AUTH_TYPE_TIME_LIMIT;
                showInputmothed();
                break;
            case R.id.linelayout_time_limit_12_hours:
                layout_content_one.setVisibility(View.VISIBLE);
                layout_content_two.setVisibility(View.GONE);
                layout_content_three.setVisibility(View.GONE);
                textview_current_auth_type.setText("期限授权");
                currentAuthType = SmartLockConstant.AUTH_TYPE_TIME_LIMIT;
                timeLimitAuthTypeTime = "12";
                showInputmothed();
                break;
            case R.id.linelayout_time_limit_custom:
                showInputmothed();
                //TODO
                break;

        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {

            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            layout_content_one.setVisibility(View.VISIBLE);
            layout_content_two.setVisibility(View.GONE);
            layout_content_three.setVisibility(View.GONE);
            textview_current_auth_type.setText("期限授权");
            currentAuthType = SmartLockConstant.AUTH_TYPE_TIME_LIMIT;
            String tempLimitTime = edittext_time_limit_custom.getText().toString();
            if (tempLimitTime.equals("自定义") || tempLimitTime.equalsIgnoreCase("")) {
                timeLimitAuthTypeTime = "12";
            } else {
                timeLimitAuthTypeTime = edittext_time_limit_custom.getText().toString();
            }
        }
        return false;
    }


    public interface GetDialogAuthtTypeTimeListener {
        void onGetDialogAuthtTypeTime(String authType, String password, String limitTime);
    }


    @Override
    public void show() {
        super.show();
        /*弹出软件盘*/
        showInputmothed();
    }

    private void showInputmothed() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {

                               InputMethodManager inputManager =
                                       (InputMethodManager) edt_pwd.getSecurityEdit().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(edt_pwd.getSecurityEdit(), 0);
                           }
                       },
                500);
    }

    public void setGetDialogAuthtTypeTimeListener(GetDialogAuthtTypeTimeListener mGetDialogAuthtTypeTimeListener) {

        this.mGetDialogAuthtTypeTimeListener = mGetDialogAuthtTypeTimeListener;
    }

}
