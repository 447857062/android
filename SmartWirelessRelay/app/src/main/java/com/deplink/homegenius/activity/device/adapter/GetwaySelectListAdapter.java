package com.deplink.homegenius.activity.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 */
public class GetwaySelectListAdapter extends BaseAdapter {
    private static final String TAG = "GetwaySelectListAdapter";
    private List<GatwayDevice> listTop = null;
    private Context mContext;

    public GetwaySelectListAdapter(Context mContext, List<GatwayDevice> list) {
        this.mContext = mContext;
        this.listTop = list;
    }

    @Override
    public int getCount() {

        return listTop.size();
    }

    @Override
    public Object getItem(int position) {

        return listTop.get(position);
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
                    R.layout.getway_select_list_item, null);
            viewHolder.textview_device_name = convertView
                    .findViewById(R.id.textview_device_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String deviceName = listTop.get(position).getName();
        viewHolder.textview_device_name.setText(deviceName);
        return convertView;
    }


    final static class ViewHolder {
        TextView textview_device_name;

    }

}
