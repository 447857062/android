package deplink.com.smartwirelessrelay.homegenius.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.custom.widget.SecurityPasswordEditText;

/**
 * 授权操作dialog
 */
public class AuthoriseDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "AuthoriseDialog";
    private Context mContext;
    private LinearLayout linelayout_select_auth_type;
    private onSureBtnClickListener mOnSureBtnClickListener;

    private RelativeLayout layout_content_one;
    private RelativeLayout layout_content_two;
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

        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        p.height = (int) (screenHeigh * 0.35);
        p.width = (int) (screenWidth * 0.9);
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
    }

    private void initEvents() {
        edt_pwd.setSecurityEditCompleListener(new SecurityPasswordEditText.SecurityEditCompleListener(){

            @Override
            public void onNumCompleted(String num) {

            }

            @Override
            public void unCompleted(String num) {

            }
        });
        linelayout_select_auth_type.setOnClickListener(this);
    }

    private SecurityPasswordEditText edt_pwd;

    private void initView() {

        edt_pwd = (SecurityPasswordEditText) findViewById(R.id.edt_pwd);
        linelayout_select_auth_type = (LinearLayout) findViewById(R.id.linelayout_select_auth_type);
        layout_content_one = (RelativeLayout) findViewById(R.id.layout_content_one);
        layout_content_two = (RelativeLayout) findViewById(R.id.layout_content_two);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linelayout_select_auth_type:
                layout_content_one.setVisibility(View.GONE);
                layout_content_two.setVisibility(View.VISIBLE);
                break;

        }
    }


    public interface onSureBtnClickListener {
        void onSureBtnClicked(String password);
    }

    @Override
    public void show() {
        super.show();

    }

    public void setSureBtnClickListener(onSureBtnClickListener mOnSureBtnClickListener) {

        this.mOnSureBtnClickListener = mOnSureBtnClickListener;
    }

}
