package deplink.com.smartwirelessrelay.homegenius.activity.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/10/31.
 */
public class SmartDevListAdapter extends BaseAdapter{
    private Context mContext;
    private List<SmartDev>mSmartDev;
    public SmartDevListAdapter(Context mContext, List<SmartDev>smartDev) {
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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.smartdevlistitem,null);
            vh.textview_device_uid= (TextView) convertView.findViewById(R.id.textview_device_uid);
            vh.textview_device_statu= (TextView) convertView.findViewById(R.id.textview_device_statu);
            vh.textview_smart_dev= (TextView) convertView.findViewById(R.id.textview_smart_dev);
            vh.textview_smart_dev_statu= (TextView) convertView.findViewById(R.id.textview_smart_dev_statu);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        String deviceName=mSmartDev.get(position).getCtrUid();
        if(deviceName.equals("")){
            deviceName="未知";
        }
        vh.textview_device_uid.setText("ctrluid:"+deviceName);
        vh.textview_device_statu.setText("devuid："+mSmartDev.get(position).getUid());
        vh.textview_smart_dev.setText("智能设备statu："+mSmartDev.get(position).getStatus());
        vh.textview_smart_dev_statu.setText("智能设备type："+mSmartDev.get(position).getType());

        return convertView;
    }

    private static class ViewHolder{
        TextView textview_device_uid;
        TextView textview_device_statu;
        TextView textview_smart_dev;
        TextView textview_smart_dev_statu;

    }
}
