package deplink.com.smartwirelessrelay.homegenius.view.viewselector;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.viewselector.Utils.ScreenUtil;
import deplink.com.smartwirelessrelay.homegenius.view.viewselector.view.PickerView;

/**
 * Created by liuli on 2015/11/27.
 */
public class SexSelector {
    private static final String TAG="SexSelector";
    public interface ResultHandler {
        void handle(String sex);
    }
    private ResultHandler handler;
    private Context context;
    private Dialog seletorDialog;
    private PickerView sex_pv;
    private TextView tv_cancle;
    private TextView tv_select;

    public SexSelector(Context context, ResultHandler resultHandler) {
        this.context = context;
        this.handler = resultHandler;
        initDialog();
        initView();
    }

    private ArrayList<String> sex;
    public void show() {

        addListener();
        sex_pv.setData(sex);
        seletorDialog.show();
    }

    private void initDialog() {
        if (seletorDialog == null) {
            seletorDialog = new Dialog(context, R.style.time_dialog);
            seletorDialog.setCancelable(false);
            seletorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            seletorDialog.setContentView(R.layout.dialog_sex_selector);
            Window window = seletorDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            int width = ScreenUtil.getInstance(context).getScreenWidth();
            lp.width = width;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        sex_pv = (PickerView) seletorDialog.findViewById(R.id.sex_pv);
        tv_cancle = (TextView) seletorDialog.findViewById(R.id.tv_cancle);
        tv_select = (TextView) seletorDialog.findViewById(R.id.tv_select);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seletorDialog.dismiss();
            }
        });
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 当前选择的性别
                Log.i(TAG,"当前选择的性别:"+currentSelectSex);
                handler.handle(currentSelectSex);
                seletorDialog.dismiss();
            }
        });
        sex=new ArrayList<>();
        sex.add("女");
        sex.add("男");
        sex_pv.setSelected(0);
    }
    private String currentSelectSex;

    private void addListener() {
        sex_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                Log.i(TAG,"当前选择的性别(setOnSelectListener):"+currentSelectSex);
                currentSelectSex=text;
            }
        });


    }

}
