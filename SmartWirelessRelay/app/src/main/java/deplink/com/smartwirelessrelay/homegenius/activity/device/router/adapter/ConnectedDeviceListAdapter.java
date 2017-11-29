package deplink.com.smartwirelessrelay.homegenius.activity.device.router.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.json.DevicesOnline;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.swipemenulistview.BaseSwipListAdapter;

/**
 * Created by Administrator on 2017/8/28.
 */
public class ConnectedDeviceListAdapter extends BaseSwipListAdapter {
    private static final String TAG ="DeviceListAdapter";
    private Context mContext;
    private List<DevicesOnline> mListData;
    public ConnectedDeviceListAdapter(Context context, List<DevicesOnline> listData) {
        this.mContext = context;
        this.mListData = listData;
    }

    @Override
    public int getCount() {

        return mListData.size();
    }
    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.connected_devicelist_item,null);
            vh.device_name= (TextView) convertView.findViewById(R.id.textview_device_name);
            vh.device_mac = (TextView) convertView.findViewById(R.id.textview_device_time);
            vh.tx= (TextView) convertView.findViewById(R.id.textview_uploadspeed);
            vh.rx= (TextView) convertView.findViewById(R.id.textview_downloadspeed);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.device_name.setText(mListData.get(position).getDeviceName());
        vh.device_mac.setText("mac:"+mListData.get(position).getMAC());
        String tx=mListData.get(position).getDataSpeedTx();
        String rx=mListData.get(position).getDataSpeedRx();
        if(tx.contains(".")){
           tx= tx.substring(0,tx.indexOf(".")+2)+"KB/s";
            Log.i(TAG,tx);
        }
        if(rx.contains(".")){
            rx= rx.substring(0,rx.indexOf(".")+2)+"KB/s";
            Log.i(TAG,rx);
        }

        vh.tx.setText(tx);
        vh.rx.setText(rx);

       return convertView;
    }

    private static class ViewHolder{
        TextView device_name;
        TextView device_mac;
        TextView rx;
        TextView tx;
    }
    @Override
    public boolean getSwipEnableByPosition(int position) {        //设置条目是否可以滑动

        return true;
    }
}
