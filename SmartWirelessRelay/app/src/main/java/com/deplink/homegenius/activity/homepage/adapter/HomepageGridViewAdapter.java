package com.deplink.homegenius.activity.homepage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.constant.RoomConstant;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
/*
*
 * Created by Administrator on 2017/11/11.
 */

public class HomepageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Room> listTop;
    public HomepageGridViewAdapter(Context mContext, List<Room> mRooms) {
        this.mContext = mContext;
        listTop = mRooms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.homepage_room_item, null);
            viewHolder.textview_room_item = convertView
                    .findViewById(R.id.textview_room_item);
            viewHolder.imageview_room_type = convertView
                    .findViewById(R.id.imageview_room_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textview_room_item.setText(listTop.get(position).getRoomName());
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
            viewHolder.imageview_room_type.setImageResource(R.drawable.homelivingroom);
            return;
        }
        switch (listTop.get(position).getRoomType()) {
            case RoomConstant.ROOMTYPE.TYPE_LIVING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.homelivingroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_BED:
                viewHolder.imageview_room_type.setImageResource(R.drawable.homebedroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_DINING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.homediningroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_KITCHEN:
                viewHolder.imageview_room_type.setImageResource(R.drawable.homekitchen);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STORAGE:
                viewHolder.imageview_room_type.setImageResource(R.drawable.homestorageroom);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STUDY:
                viewHolder.imageview_room_type.setImageResource(R.drawable.homestudy);
                break;
            case RoomConstant.ROOMTYPE.TYPE_TOILET:
                viewHolder.imageview_room_type.setImageResource(R.drawable.hometoilet);
                break;
        }
    }
    final static class ViewHolder {
        TextView textview_room_item;
        ImageView imageview_room_type;
    }
}
