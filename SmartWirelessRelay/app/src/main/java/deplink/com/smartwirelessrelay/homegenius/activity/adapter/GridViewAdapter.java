package deplink.com.smartwirelessrelay.homegenius.activity.adapter;

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
    private final int ROOM_ITEM = 0, ADD_ITEM = 1, TYPE_COUNT = 2;
    private Context mContext;

    private List<Room>listTop;
    /**
     * 头部列表数据的大小
     * */
    private int TopCount = 0;
    public GridViewAdapter(Context mContext,List<Room>mRooms) {
        this.mContext = mContext;
        listTop=mRooms;
        TopCount = listTop.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            if (getItemViewType(position) == ROOM_ITEM) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.room_item, null);
                viewHolder.textview_room_item = (TextView) convertView
                        .findViewById(R.id.textview_room_item);

            } else {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.room_item_add, null);
                viewHolder.imageview_room_item_add = (TextView) convertView
                        .findViewById(R.id.textview_device_uid);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position < TopCount) {
            viewHolder.textview_room_item.setText(listTop.get(position).getRoomName());
        } else {
           //TODO 添加设备按钮

        }
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
        int count = 0;
        if (listTop != null ) {
            count = TopCount +1;
        }

        if ( listTop == null) {
            count = 1;
        }
        return count;
    }

    /*
         * 获取当前需要显示布局的类型
         * return TOP_ITEM则表示上面半部分列表
         * return BOTTOM_ITEM则表示下半部分列表
         * *
    */
    @Override
    public int getItemViewType(int position) {

        if (position < TopCount)
            return ROOM_ITEM;
        else
            return ADD_ITEM;
    }
/*
* 该方法返回多少个不同的布局
 */

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    final static class ViewHolder {
        TextView textview_room_item;
        TextView imageview_room_item_add;
    }
}
