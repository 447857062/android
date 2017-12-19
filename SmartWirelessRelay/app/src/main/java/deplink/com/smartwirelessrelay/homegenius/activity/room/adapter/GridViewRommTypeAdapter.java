package deplink.com.smartwirelessrelay.homegenius.activity.room.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
/*
*
 * Created by Administrator on 2017/11/11.
 */

public class GridViewRommTypeAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> listTop = new ArrayList<>();

    public GridViewRommTypeAdapter(Context mContext) {
        this.mContext = mContext;
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_LIVING);
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_BED);
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_KITCHEN);
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STUDY);
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STORAGE);
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_TOILET);
        listTop.add(deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_DINING);
    }

    private int selectedPosition = 0;// 选中的位置

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.roomtype_item, null);
            viewHolder.textview_room_item = (TextView) convertView
                    .findViewById(R.id.textview_room_item);
            viewHolder.imageview_room_type = (ImageView) convertView
                    .findViewById(R.id.imageview_room_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textview_room_item.setText(listTop.get(position));
        if (selectedPosition == position) {
            viewHolder.textview_room_item.setTextColor(mContext.getResources().getColor(R.color.room_type_text));
            setSelectRoomTypeImageResource(position, viewHolder);
        } else {
            viewHolder.textview_room_item.setTextColor(mContext.getResources().getColor(R.color.huise));
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
        if(listTop.get(position)==null){
            return;
        }
        switch (listTop.get(position)){
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_LIVING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroomlivingroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_BED:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroombedroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_DINING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroomdiningroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_KITCHEN:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroomkitchen);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STORAGE:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroomstorageroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STUDY:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroomstudy);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_TOILET:
                viewHolder.imageview_room_type.setImageResource(R.drawable.noaddroomtoilet);
                break;
        }
    }
    /**
     * 设置房间类型对应的图片
     * @param position
     * @param viewHolder
     */
    private void setSelectRoomTypeImageResource(int position, ViewHolder viewHolder) {
        if(listTop.get(position)==null){
            return;
        }
        switch (listTop.get(position)){
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_LIVING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroomlivingroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_BED:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroombedroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_DINING:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroomdiningroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_KITCHEN:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroomkitchen);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STORAGE:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroomstorageroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STUDY:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroomstudy);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_TOILET:
                viewHolder.imageview_room_type.setImageResource(R.drawable.addroomtoilet);
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
        return listTop.size();
    }

    final static class ViewHolder {
        TextView textview_room_item;
        ImageView imageview_room_type;

    }
}
