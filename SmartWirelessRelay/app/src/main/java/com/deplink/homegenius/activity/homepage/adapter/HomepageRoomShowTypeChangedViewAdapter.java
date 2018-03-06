package com.deplink.homegenius.activity.homepage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.constant.RoomConstant;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
/*
*
 * Created by Administrator on 2017/11/11.
 */

public class HomepageRoomShowTypeChangedViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Room> listTop;
    public HomepageRoomShowTypeChangedViewAdapter(Context mContext, List<Room> mRooms) {
        this.mContext = mContext;
        listTop = mRooms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.homepage_roomchanged_item, null);
            viewHolder.textview_room_item = convertView
                    .findViewById(R.id.textview_room_item);
            viewHolder.device_number = convertView
                    .findViewById(R.id.device_number);
            viewHolder.imageview_room_type = convertView
                    .findViewById(R.id.imageview_room_type);
            viewHolder.view_line = convertView
                    .findViewById(R.id.view_line);
            viewHolder.layout_root = convertView
                    .findViewById(R.id.layout_root);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position==listTop.size()-1){
            viewHolder.view_line.setVisibility(View.INVISIBLE);
            viewHolder.layout_root.setBackgroundResource(R.drawable.halfrectangle_buttom_button_background);
        }else{
            viewHolder.layout_root.setBackgroundResource(R.drawable.button_delete_background);
            viewHolder.view_line.setVisibility(View.VISIBLE);
        }
        viewHolder.textview_room_item.setText(listTop.get(position).getRoomName());
        //网关设备加智能设备
        int devicesSize=listTop.get(position).getmDevices().size()+listTop.get(position).getmGetwayDevices().size();
        if( devicesSize==0){
            viewHolder.device_number.setBackground(mContext.getResources().getDrawable(R.drawable.homepage_roomselect_ovel_bg_device_size_0));
        }else{
            viewHolder.device_number.setBackground(mContext.getResources().getDrawable(R.drawable.homepage_roomselect_ovel_bg));
        }
        viewHolder.device_number.setText(""+devicesSize);
        setRoomTypeImageResource(position, viewHolder);
        return convertView;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return listTop.size();
    }
    /**
     * 设置房间类型对应的图片
     * @param position
     * @param viewHolder
     */
    private void setRoomTypeImageResource(int position, ViewHolder viewHolder) {
        if (listTop.get(position).getRoomType() == null) {
            //默认图片
            viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchlivingroom);
            return;
        }
        switch (listTop.get(position).getRoomType()) {
            case RoomConstant.ROOMTYPE.TYPE_LIVING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchlivingroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_BED:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchbedroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_DINING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchdiningroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_KITCHEN:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchkitchen);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STORAGE:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchstorageroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STUDY:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchstudy);
                break;
            case RoomConstant.ROOMTYPE.TYPE_TOILET:
                viewHolder.imageview_room_type.setImageResource(R.drawable.viewswitchtoilet);
                break;
        }
    }
    final static class ViewHolder {
        RelativeLayout layout_root;
        TextView textview_room_item;
        TextView device_number;
        ImageView imageview_room_type;
        View view_line;
    }
}
