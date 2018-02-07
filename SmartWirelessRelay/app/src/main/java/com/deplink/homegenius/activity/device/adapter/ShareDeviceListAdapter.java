package com.deplink.homegenius.activity.device.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.device.share.UserShareInfo;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.imageview.CircleImageView;

import java.util.HashMap;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 */
public class ShareDeviceListAdapter extends BaseAdapter {
    private static final String TAG = "ShareDeviceListAdapter";
    private List<UserShareInfo> listTop = null;
    private Context mContext;
    private HashMap<String,Bitmap> userimage;
    public ShareDeviceListAdapter(Context mContext, List<UserShareInfo> list,HashMap<String,Bitmap> userimage) {
        this.mContext = mContext;
        this.listTop = list;
        this.userimage=userimage;
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
                    R.layout.share_user_list_item, null);
            viewHolder.user_portrait = convertView
                    .findViewById(R.id.user_portrait);
            viewHolder.user_name = convertView
                    .findViewById(R.id.user_name);
            viewHolder.textview_share_user_bind_state = convertView
                    .findViewById(R.id.textview_share_user_bind_state);
            viewHolder.layout_mamager = convertView
                    .findViewById(R.id.layout_mamager);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(userimage.get(listTop.get(position).getUsername())==null){
            viewHolder.user_portrait.setImageDrawable(mContext.getResources().getDrawable(R.drawable.defaultavatar));
        }else{
            viewHolder.user_portrait.setImageBitmap(userimage.get(listTop.get(position).getUsername()));
        }
       String selfusername= Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if(selfusername.equalsIgnoreCase(listTop.get(position).getUsername())){
            viewHolder.user_name.setText(listTop.get(position).getUsername()+"(我自己)");
        }else{
            viewHolder.user_name.setText(listTop.get(position).getUsername());
        }

        if(listTop.get(position).getIssuper()==1){
            viewHolder.layout_mamager.setVisibility(View.VISIBLE);
            viewHolder.textview_share_user_bind_state.setVisibility(View.GONE);
        }else{
            viewHolder.layout_mamager.setVisibility(View.GONE);
            viewHolder.textview_share_user_bind_state.setVisibility(View.VISIBLE);
            if(listTop.get(position).getStatus()==1){
                viewHolder.textview_share_user_bind_state.setBackgroundResource(R.drawable.radius10_bg_huise_layout_background);
                viewHolder.textview_share_user_bind_state.setTextColor(mContext.getResources().getColor(R.color.huise));
                viewHolder.textview_share_user_bind_state.setText("未绑定");
            }else if(listTop.get(position).getStatus()==2){
                viewHolder.textview_share_user_bind_state.setBackgroundResource(R.drawable.radius10_bg_blue_layout_background);
                viewHolder.textview_share_user_bind_state.setTextColor(mContext.getResources().getColor(R.color.title_blue_bg));
                viewHolder.textview_share_user_bind_state.setText("已绑定");
            }
        }
        return convertView;
    }
    final static class ViewHolder {
        CircleImageView user_portrait;
        TextView user_name;
        TextView textview_share_user_bind_state;
        RelativeLayout layout_mamager;
    }

}
