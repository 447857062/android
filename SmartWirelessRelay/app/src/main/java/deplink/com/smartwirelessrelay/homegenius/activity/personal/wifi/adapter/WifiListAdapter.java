package deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;

/**
 * Created by Administrator on 2017/8/29.
 */
public class WifiListAdapter extends BaseAdapter {
    private static final String TAG = "WifiListAdapter";
    private Context mContext;
    private List<SSIDList> mData;


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


    public WifiListAdapter(Context context, List<SSIDList> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wifi_list_item, null);
            vh.textview_wifi_name = (TextView) convertView.findViewById(R.id.textview_wifi_name);
            vh.textview_password = (TextView) convertView.findViewById(R.id.textview_password);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.textview_wifi_name.setText(mData.get(position).getSSID());
        vh.textview_password.setText(mData.get(position).getEncryption());
        return convertView;
    }



    private static class ViewHolder {
        TextView textview_wifi_name;
        TextView textview_password;
    }

}
