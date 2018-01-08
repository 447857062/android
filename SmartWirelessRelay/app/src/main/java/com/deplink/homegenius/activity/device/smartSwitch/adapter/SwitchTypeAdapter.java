package com.deplink.homegenius.activity.device.smartSwitch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.constant.DeviceTypeConstant;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/11/22.
 */
public class SwitchTypeAdapter extends BaseAdapter {
    private List<String> mTypeNames;
    private Context mContext;

    public SwitchTypeAdapter(Context mContext, List<String> typeNames) {
        this.mTypeNames = typeNames;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mTypeNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mTypeNames.get(position);
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
                    R.layout.switch_type_list_item, null);
            viewHolder.textview_band_name = (TextView) convertView
                    .findViewById(R.id.textview_band_name);
            viewHolder.imageview_type = (ImageView) convertView
                    .findViewById(R.id.imageview_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textview_band_name.setText(mTypeNames.get(position));
        switch (mTypeNames.get(position)){
            case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_ONEWAY:
                viewHolder.imageview_type.setImageResource(R.drawable.switchalltheway);
                break;
            case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_TWOWAY:
                viewHolder.imageview_type.setImageResource(R.drawable.roadswitch);
                break;
            case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_THREEWAY:
                viewHolder.imageview_type.setImageResource(R.drawable.threewayswitch);
                break;
            case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_FOURWAY:
                viewHolder.imageview_type.setImageResource(R.drawable.fourwayswitch);
                break;
        }

        return convertView;
    }

    final static class ViewHolder {
        TextView textview_band_name;
        ImageView imageview_type;
    }
}
