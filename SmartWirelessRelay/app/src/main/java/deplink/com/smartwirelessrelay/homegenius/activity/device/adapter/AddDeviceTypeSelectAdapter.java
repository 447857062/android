package deplink.com.smartwirelessrelay.homegenius.activity.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/11/14.
 */
public class AddDeviceTypeSelectAdapter extends BaseAdapter{
    private Context mContext;
    private List<String>mDeviceType;
    public  AddDeviceTypeSelectAdapter(Context context, List<String>deviceType){
        this.mContext = context;
        this.mDeviceType=deviceType;
    }
    @Override
    public int getCount() {
        return mDeviceType.size();
    }

    @Override
    public Object getItem(int position) {
        return mDeviceType.get(position);
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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.adddevie_type_name_item,null);
            vh.textview_device_type_name= (TextView) convertView.findViewById(R.id.textview_device_type_name);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        String deviceTypeName=mDeviceType.get(position);

        vh.textview_device_type_name.setText(deviceTypeName);

        return convertView;
    }
    private static class ViewHolder{
        TextView textview_device_type_name;

    }
}
