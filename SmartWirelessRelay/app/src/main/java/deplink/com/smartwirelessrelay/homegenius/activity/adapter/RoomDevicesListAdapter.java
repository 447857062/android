package deplink.com.smartwirelessrelay.homegenius.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceNameTranslate;

/**
 * Created by Administrator on 2017/10/31.
 * 每个房间管理的设备
 */
public class RoomDevicesListAdapter extends BaseAdapter{
    private Context mContext;
    private List<SmartDev>mSmartDev;
    public RoomDevicesListAdapter(Context mContext, List<SmartDev>smartDev) {
        this.mContext=mContext;
        this.mSmartDev=smartDev;
    }

    @Override
    public int getCount() {
        return mSmartDev.size();
    }

    @Override
    public Object getItem(int position) {
        return mSmartDev.get(position);
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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.room_manager_devices_listitem,null);
            vh.imageview_device_type= (ImageView) convertView.findViewById(R.id.imageview_device_type);
            vh.textview_device_type= (TextView) convertView.findViewById(R.id.textview_device_type);
            vh.imageview_delete= (ImageView) convertView.findViewById(R.id.imageview_delete);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        String deviceName=mSmartDev.get(position).getCtrUid();
        if(deviceName.equals("")){
            deviceName="未知";
        }
        vh.textview_device_type.setText(DeviceNameTranslate.getDeviceTranslatedName(mSmartDev.get(position).getType()));
        return convertView;
    }

    private static class ViewHolder{
        ImageView imageview_device_type;
        TextView textview_device_type;
        ImageView imageview_delete;
    }
}
