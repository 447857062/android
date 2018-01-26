package com.deplink.homegenius.activity.device.adapter;

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
 * 设备添加选择房间适配器
 */

public class AddDeviceGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Room> listTop;
    public AddDeviceGridViewAdapter(Context mContext, List<Room> mRooms) {
        this.mContext = mContext;
        listTop = mRooms;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if(position==listTop.size()){
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.room_item_add, null);
            }else{
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.room_item, null);
                viewHolder.textview_room_item = convertView
                        .findViewById(R.id.textview_room_item);
                viewHolder.imageview_room_type = convertView
                        .findViewById(R.id.imageview_room_type);
            }



            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position!=listTop.size()){
            viewHolder.textview_room_item.setText(listTop.get(position).getRoomName());
            setRoomTypeImageResource(position, viewHolder);
        }



        return convertView;
    }

    /**
     * 设置房间类型对应的图片
     * @param position
     * @param viewHolder
     */
    private void setRoomTypeImageResource(int position, ViewHolder viewHolder) {
        if(listTop.get(position).getRoomType()==null){
            return;
        }
        switch (listTop.get(position).getRoomType()){
            case RoomConstant.ROOMTYPE.TYPE_LIVING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_livingroom_background);
                break;
            case RoomConstant.ROOMTYPE.TYPE_BED:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_bedroom_background);
                break;
            case RoomConstant.ROOMTYPE.TYPE_DINING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_diningroom_background);
                break;
            case RoomConstant.ROOMTYPE.TYPE_KITCHEN:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_kitchroom_background);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STORAGE:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_storageroom_background);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STUDY:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_studyroom_background);
                break;
            case RoomConstant.ROOMTYPE.TYPE_TOILET:
                viewHolder.imageview_room_type.setImageResource(R.drawable.button_add_device_roomselect_toiletroom_background);
                break;
        }
    }
/*
     * 功能：获得当前选项的ID
     *
     * @see android.widget.Adapter#getItemId(int)
   */

    @Override
    public long getItemId(int position) {
        //System.out.println("getItemId = " + position);
        return position;
    }
/*
     * 功能：获得当前选项
     *
     * @see android.widget.Adapter#getItem(int)
 */

    @Override
    public Object getItem(int position) {
        return position;
    }
/*
     * 获得数量
     *
     * @see android.widget.Adapter#getCount()
  */
    @Override
    public int getCount() {

        return listTop.size()+1;
    }


    final static class ViewHolder {
        TextView textview_room_item;
        ImageView imageview_room_type;
    }
}
