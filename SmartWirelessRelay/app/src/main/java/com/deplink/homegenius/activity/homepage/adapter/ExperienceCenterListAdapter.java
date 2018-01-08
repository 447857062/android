package com.deplink.homegenius.activity.homepage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.Protocol.json.device.ExperienceCenterDevice;
import com.deplink.homegenius.constant.DeviceTypeConstant;

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
            viewHolder.image_device_type = convertView
                    .findViewById(R.id.image_device_type);
            viewHolder.textview_device_name = convertView
                    .findViewById(R.id.textview_device_name);
            viewHolder.iamgeview_setting = convertView
                    .findViewById(R.id.iamgeview_setting);
            viewHolder.layout_root = convertView
                    .findViewById(R.id.layout_root);
            viewHolder.view_line =  convertView
                    .findViewById(R.id.view_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position==mDatas.size()-1){
            viewHolder.view_line.setVisibility(View.INVISIBLE);
            viewHolder.layout_root.setBackgroundResource(R.drawable.halfrectangle_buttom_button_background);
        }else{
            viewHolder.layout_root.setBackgroundResource(R.drawable.button_delete_background);
            viewHolder.view_line.setVisibility(View.VISIBLE);
        }

        viewHolder.textview_device_name.setText(mDatas.get(position).getDeviceName());
        switch (mDatas.get(position).getDeviceName()){
            case DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY:
                viewHolder.image_device_type.setImageResource(R.drawable.gatewayicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_ROUTER:
                viewHolder.image_device_type.setImageResource(R.drawable.routericon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_LOCK:
            case "智能门锁":
                viewHolder.image_device_type.setImageResource(R.drawable.doorlockicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_MENLING:
                viewHolder.image_device_type.setImageResource(R.drawable.doorbellicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_SWITCH:
                viewHolder.image_device_type.setImageResource(R.drawable.switchicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.infraredremotecontrolicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.tvicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.airconditioningicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL:
                viewHolder.image_device_type.setImageResource(R.drawable.settopboxesicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_LIGHT:
                viewHolder.image_device_type.setImageResource(R.drawable.equipmentlight);
                break;

        }
        return convertView;
    }

    final static class ViewHolder {
        RelativeLayout layout_root;
        ImageView image_device_type;
        TextView textview_device_name;
        ImageView iamgeview_setting;
        View view_line;
    }

}
