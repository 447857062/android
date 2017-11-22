package deplink.com.smartwirelessrelay.homegenius.activity.device.getway.adapter;

import android.content.Context;
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
    private Context mContext;
    private List<Device> mDev;
    public GetwayListDevicesAdapter(Context mContext, List<Device>dev) {
        this.mContext=mContext;

        this.mDev=dev;
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
        if(convertView==null){
            vh=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.getwaydevlistitem,null);
            vh.textview_device_uid= (TextView) convertView.findViewById(R.id.textview_device_uid);
            vh.textview_device_statu= (TextView) convertView.findViewById(R.id.textview_device_statu);
            vh.textview_ipaddress= (TextView) convertView.findViewById(R.id.textview_ipaddress);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        vh.textview_device_uid.setText("设备识别码:"+mDev.get(position).getUid());
        vh.textview_device_statu.setText("状态："+mDev.get(position).getStatus());
        vh.textview_ipaddress.setText("网关IP地址："+mDev.get(position).getIpAddress());

        return convertView;
    }

    private static class ViewHolder{
        TextView textview_device_uid;
        TextView textview_device_statu;
        TextView textview_ipaddress;
    }
}
