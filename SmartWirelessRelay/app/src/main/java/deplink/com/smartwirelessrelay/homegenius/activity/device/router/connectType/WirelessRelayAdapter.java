package deplink.com.smartwirelessrelay.homegenius.activity.device.router.connectType;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/8/29.
 * 本地接口的无线中继界面适配器
 */
public class WirelessRelayAdapter extends BaseAdapter {
    private static final String TAG = "AdapterForWirelessRelay";
    private Context mContext;
    private List<wifiScanRoot> mData;

    //链接参数
    private String crypt = "";
    private String encryption = "";

    private int quality = 0;

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {

        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public WirelessRelayAdapter(Context context, List<wifiScanRoot> list) {
        this.mContext = context;
        this.mData = list;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        Log.i("notifyDataSetChanged", "" + mData.size());
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_wireless_relay, null);
            vh.tv = (TextView) convertView.findViewById(R.id.list_text_show_name);
            vh.image = (ImageView) convertView.findViewById(R.id.iamge_item);
            vh.encryption_type = (TextView) convertView.findViewById(R.id.encryption_type);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        getConnectParams(position);
        Log.i(TAG,"mData.get(position).getSsid()="+mData.get(position).getSsid());
        vh.tv.setText(mData.get(position).getSsid());
        vh.encryption_type.setText(encryption);//加密方式
        //设只wifi信号图片
        if (!encryption.equalsIgnoreCase("OPEN")) {
            // vh.image.setImageResource(R.drawable.wifi_signal_encryption);

            vh.image.setImageResource(R.drawable.wifi_signal_strength);
        }
        if (quality < 33) {
            vh.image.setImageLevel(0);
        } else if (quality >= 33 && quality < 66) {
            vh.image.setImageLevel(1);
        } else if (quality >= 66) {
            vh.image.setImageLevel(2);
        }


        return convertView;
    }

    private void getConnectParams(int position) {
        int getGroupCiphersSize = 0;
        if (mData.get(position).getencryption().getGroupCiphers() != null) {
            getGroupCiphersSize = mData.get(position).getencryption().getGroupCiphers().size();
        }
        if (getGroupCiphersSize == 1) {
            if (mData.get(position).getencryption().getWep()) {
                crypt = "WEP";
            } else {

                crypt = "";
            }
        } else{
            for (int i = 0; i < getGroupCiphersSize; i++) {
                crypt += mData.get(position).getencryption().getGroupCiphers().get(i);
            }

        }
        encryption = mData.get(position).getencryption().getDescription();
        boolean wep = mData.get(position).getencryption().getWep();
        int wpa = mData.get(position).getencryption().getWpa();
        boolean enable = mData.get(position).getencryption().getEnabled();
        if (wep) {
            encryption = "WEP";
        } else if (wpa > 0) {
            if (wpa == 2 || wpa == 3) {

                String temp = "";
                for (int i = 0; i < mData.get(position).getencryption().getAuthSuites().size(); i++) {
                    temp += mData.get(position).getencryption().getAuthSuites().get(i);
                }
                encryption = "WPA2" + temp;
            } else {
                String temp = "";
                for (int i = 0; i < mData.get(position).getencryption().getAuthSuites().size(); i++) {
                    temp += mData.get(position).getencryption().getAuthSuites().get(i);
                }
                encryption += "WPA" + temp;
            }
        } else if (enable) {
            encryption = "UNKNOWN";
        } else {
            encryption = "OPEN";
        }
        int channel = mData.get(position).getChannel();
        String userName = mData.get(position).getSsid();


        quality = mData.get(position).getQuality();
        String password = "";
        Log.i(TAG, "crypt=" + crypt + "encryption=" + encryption + "channel=" + channel + "userName=" + userName + "password=" + password);
    }

    private static class ViewHolder {
        TextView tv;
        TextView encryption_type;
        ImageView image;
    }

}
