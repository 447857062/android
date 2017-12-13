package deplink.com.smartwirelessrelay.homegenius.activity.device.getway.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;

/**
 * Created by Administrator on 2017/11/22.
 */
public class GetwayListDevicesAdapter extends BaseAdapter {
    private static final String TAG="GetwayAdapter";
    private Context mContext;
    private List<Device> mDev;
    private List<Device> mBinedDevs;

    public GetwayListDevicesAdapter(Context mContext, List<Device> dev, List<Device> bindDevs) {
        this.mContext = mContext;
        this.mBinedDevs = bindDevs;
        this.mDev = dev;
    }

    @Override
    public int getCount() {
        return mDev.size();
    }

    @Override
    public Object getItem(int position) {
        return mDev.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.getwaydevlistitem, null);
            vh.textview_device_name = (TextView) convertView.findViewById(R.id.textview_device_name);
            vh.textview_device_ip = (TextView) convertView.findViewById(R.id.textview_device_ip);
            vh.textview_device_bindstatus = (TextView) convertView.findViewById(R.id.textview_device_bindstatus);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        String deviceName = (mDev.get(position).getName());
        if (deviceName == null || deviceName.equals("")) {
            deviceName = "未绑定的网关设备";
        }

        vh.textview_device_ip.setText(mDev.get(position).getIpAddress());
        //为绑定的设备设置为绑定状态
        boolean deviceBinded=false;
        int bindDevicesIndex = 0;
        for(int i=0;i<mBinedDevs.size();i++){
            if(mDev.get(position).getUid().trim().equals(mBinedDevs.get(i).getUid().trim())){
                bindDevicesIndex=i;
                deviceBinded=true;
            }
        }
        Log.i(TAG,"deviceBinded="+deviceBinded);
        if(deviceBinded){
            vh.textview_device_bindstatus.setText("已绑定");
            vh.textview_device_name.setText(mBinedDevs.get(bindDevicesIndex).getName());
        }else{
            vh.textview_device_bindstatus.setText("未绑定");
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView textview_device_name;
        TextView textview_device_ip;
        TextView textview_device_bindstatus;
    }
}
