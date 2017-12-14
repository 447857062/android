package deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;


/**
 * Created by Administrator on 2017/8/10.
 * 登录时出现的转圈圈的动画，背景蓝色，中间显示圆形进入条。
 */
public class DialogThreeBounce {
    private static Dialog dialogLoading;
    private static Context mContext;


    public static void setmContext(Context mContext) {
        DialogThreeBounce.mContext = mContext;
    }

    public static void showLoading(Context context) {
        mContext = context;
        if (dialogLoading == null) {
            dialogLoading = new Dialog(context, R.style.DialogRadius);
        }
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_threebounce, null);
        dialogLoading.setContentView(view);
        dialogLoading.setCanceledOnTouchOutside(false);
        if (context != null && context instanceof Activity
                && !((Activity) context).isFinishing()) {
            dialogLoading.show();
        }
    }

    public static void hideLoading() {
        if (mContext != null && mContext instanceof Activity
                && !((Activity) mContext).isFinishing()) {
            if (null != dialogLoading) {
                dialogLoading.dismiss();
                dialogLoading = null;
            }
        }
    }

}
