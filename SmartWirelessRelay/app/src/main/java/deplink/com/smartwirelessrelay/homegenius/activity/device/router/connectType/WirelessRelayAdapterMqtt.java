package deplink.com.smartwirelessrelay.homegenius.activity.device.router.connectType;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.json.SSIDList;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/8/29.
 */
public class WirelessRelayAdapterMqtt extends BaseAdapter {
    private static final String TAG = "AdapterForWirelessRelay";
    private Context mContext;
    private List<SSIDList> mData;
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


    public WirelessRelayAdapterMqtt(Context context, List<SSIDList> list) {
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
        encryption=mData.get(position).getEncryption();
        if(mData.get(position).getQuality().contains("/")){
            quality=Integer.parseInt(mData.get(position).getQuality().substring(0,mData.get(position).getQuality().indexOf("/")));
            Log.i(TAG,"quality="+quality);
        }

        //设只wifi信号图片
        if (!encryption.equalsIgnoreCase("none")) {
            vh.image.setImageResource(R.drawable.wifi_signal_encryption);
        } else {
            vh.image.setImageResource(R.drawable.wifi_signal_no_encryption);
        }
        if (quality < 33) {
            vh.image.setImageLevel(0);
        } else if (quality >= 33 && quality < 66) {
            vh.image.setImageLevel(1);
        } else if (quality >= 66) {
            vh.image.setImageLevel(2);
        }
        vh.tv.setText(mData.get(position).getSSID());
        vh.encryption_type.setText(mData.get(position).getEncryption());
        return convertView;
    }



    private static class ViewHolder {
        TextView tv;
        TextView encryption_type;
        ImageView image;
    }

}
