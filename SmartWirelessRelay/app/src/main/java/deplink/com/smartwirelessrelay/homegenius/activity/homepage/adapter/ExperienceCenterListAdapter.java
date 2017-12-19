package deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceType;

/**
 *
 */
public class ExperienceCenterListAdapter extends BaseAdapter {
    private static final String TAG = "ExperienceCenterAdapter";
    private List<ExperienceCenterDevice> mDatas;
    private Context mContext;

    public ExperienceCenterListAdapter(Context mContext, List<ExperienceCenterDevice> device) {
        this.mContext = mContext;
        this.mDatas = device;
    }

    @Override
    public int getCount() {

        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {

        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.experience_center_listitem, null);
            viewHolder.image_device_type = (ImageView) convertView
                    .findViewById(R.id.image_device_type);
            viewHolder.textview_device_name = (TextView) convertView
                    .findViewById(R.id.textview_device_name);
            viewHolder.iamgeview_setting = (ImageView) convertView
                    .findViewById(R.id.iamgeview_setting);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.textview_device_name.setText(mDatas.get(position).getDeviceName());
        switch (mDatas.get(position).getDeviceName()){
            case DeviceType.TYPE.TYPE_SMART_GETWAY:
                viewHolder.image_device_type.setImageResource(R.drawable.gatewayicon);
                break;
            case DeviceType.TYPE.TYPE_ROUTER:
                viewHolder.image_device_type.setImageResource(R.drawable.routericon);
                break;
            case DeviceType.TYPE.TYPE_LOCK:
            case "智能门锁":
                viewHolder.image_device_type.setImageResource(R.drawable.doorlockicon);
                break;
            case DeviceType.TYPE.TYPE_MENLING:
                viewHolder.image_device_type.setImageResource(R.drawable.doorbellicon);
                break;
            case DeviceType.TYPE.TYPE_SWITCH:
                viewHolder.image_device_type.setImageResource(R.drawable.switchicon);
                break;
            case DeviceType.TYPE.TYPE_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.infraredremotecontrolicon);
                break;
            case DeviceType.TYPE.TYPE_TV_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.tvicon);
                break;
            case DeviceType.TYPE.TYPE_AIR_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.airconditioningicon);
                break;
            case DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.settopboxesicon);
                break;

        }
        return convertView;
    }

    final static class ViewHolder {
        ImageView image_device_type;
        TextView textview_device_name;
        ImageView iamgeview_setting;
    }

}
