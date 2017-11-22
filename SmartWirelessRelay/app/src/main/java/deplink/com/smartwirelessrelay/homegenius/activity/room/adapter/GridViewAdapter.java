package deplink.com.smartwirelessrelay.homegenius.activity.room.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
/*
*
 * Created by Administrator on 2017/11/11.
 */

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;

    private List<Room>listTop;

    public GridViewAdapter(Context mContext,List<Room>mRooms) {
        this.mContext = mContext;
        listTop=mRooms;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();


                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.room_item, null);
                viewHolder.textview_room_item = (TextView) convertView
                        .findViewById(R.id.textview_room_item);



            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

            viewHolder.textview_room_item.setText(listTop.get(position).getRoomName());

        return convertView;
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

    }
}
