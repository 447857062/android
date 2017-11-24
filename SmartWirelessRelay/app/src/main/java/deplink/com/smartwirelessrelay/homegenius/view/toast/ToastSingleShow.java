package deplink.com.smartwirelessrelay.homegenius.view.toast;

import android.content.Context;
import android.widget.Toast;

/**
 * toast显示太过频繁
 * Created by Administrator on 2017/9/1.
 */
public class ToastSingleShow {
    // 构造方法私有化 不允许new对象
    private ToastSingleShow() {
    }

    // Toast对象
    private static Toast toast = null;

    /**
     * 显示Toast
     */
    public static void showText(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
}
