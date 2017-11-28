package deplink.com.smartwirelessrelay.homegenius.activity.room.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private List<String> listTop=new ArrayList<>();

    public GridViewRommTypeAdapter(Context mContext) {
        this.mContext = mContext;
        listTop.add("客厅");
        listTop.add("卧室");
        listTop.add("厨房");
        listTop.add("书房");
        listTop.add("储物室");
        listTop.add("洗手间");
        listTop.add("饭厅");
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textview_room_item.setText(listTop.get(position));
        if (selectedPosition == position) {
            convertView.setBackgroundColor(Color.RED);
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.gray_919090));

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
        return listTop.size();
    }
    final static class ViewHolder {
        TextView textview_room_item;

    }
}
