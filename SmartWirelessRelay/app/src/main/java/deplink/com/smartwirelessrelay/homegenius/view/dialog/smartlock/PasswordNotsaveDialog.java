package deplink.com.smartwirelessrelay.homegenius.view.dialog.smartlock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 授权操作dialog
 */
public class PasswordNotsaveDialog extends Dialog implements View.OnClickListener{
    private static final String TAG = "PasswordNotsaveDialog";
    private Context mContext;
    private PasswordNotsaveSureListener mOnSureClick;
    private Button button_cancel;
    private Button button_sure;
    public PasswordNotsaveDialog(Context context) {
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

        view = LayoutInflater.from(mContext).inflate(R.layout.password_notsave_dialog, null);
        setContentView(view, p);
        //初始化界面控件
        initView();
        initDatas();
        //初始化界面控件的事件
        initEvents();
    }

    private void initDatas() {

    }

    private void initEvents() {
        button_cancel.setOnClickListener(this);
        button_sure.setOnClickListener(this);

    }


    private void initView() {
        button_cancel= (Button) findViewById(R.id.button_cancel);
        button_sure= (Button) findViewById(R.id.button_sure);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_cancel:

               this.dismiss();
                break;
            case R.id.button_sure:
                mOnSureClick.onSureClick();
                this.dismiss();
                break;

        }
    }




    public interface PasswordNotsaveSureListener {
        void onSureClick();
    }

    public void setmOnSureClick(PasswordNotsaveSureListener mOnSureClick) {
        this.mOnSureClick = mOnSureClick;
    }

    @Override
    public void show() {
        super.show();

    }

}
