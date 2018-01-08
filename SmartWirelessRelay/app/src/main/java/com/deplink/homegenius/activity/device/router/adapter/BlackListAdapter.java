package com.deplink.homegenius.activity.device.router.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.json.BLACKLIST;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.view.listview.swipemenulistview.BaseSwipListAdapter;

/**
 * Created by Administrator on 2017/8/28.
 */
public class BlackListAdapter extends BaseSwipListAdapter {
    private Context mContext;
    private List<BLACKLIST> mListData;

    public BlackListAdapter(Context context, List<BLACKLIST> listData) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.blacklist_item,null);
            vh.device_name= convertView.findViewById(R.id.textview_device_name);
            vh.device_time= convertView.findViewById(R.id.textview_device_time);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.device_name.setText(mListData.get(position).getDeviceName());
        vh.device_time.setText("MAC:"+mListData.get(position).getMAC());
        return convertView;
    }

    private static class ViewHolder{
        TextView device_name;
        TextView device_time;
    }
    @Override
    public boolean getSwipEnableByPosition(int position) {        //设置条目是否可以滑动
        return true;
    }
}
