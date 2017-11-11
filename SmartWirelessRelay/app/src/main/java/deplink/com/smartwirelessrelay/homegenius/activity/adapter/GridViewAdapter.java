/*
package deplink.com.smartwirelessrelay.homegenius.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

*/
/**
 * Created by Administrator on 2017/11/11.
 *//*

public class GridViewAdapter extends BaseAdapter{
    private final int ROOM_ITEM = 0, ADD_ITEM = 1, TYPE_COUNT = 2;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            if (getItemViewType(position) == ROOM_ITEM) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.devicelist_device_item, null);
                viewHolder.textview_device_uid = (TextView) convertView
                        .findViewById(R.id.textview_device_uid);
                viewHolder.textview_device_status = (TextView) convertView
                        .findViewById(R.id.textview_device_status);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.devicelist_smartdevice_item, null);
                viewHolder.textview_device_uid = (TextView) convertView
                        .findViewById(R.id.textview_device_uid);
                viewHolder.textview_device_status = (TextView) convertView
                        .findViewById(R.id.textview_device_status);
                viewHolder.textview_device_name = (TextView) convertView
                        .findViewById(R.id.textview_device_name);

            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position < TopCount) {
            viewHolder.textview_device_uid.setText(listTop.get(position).getUid());
            viewHolder.textview_device_status.setText("状态:"+listTop.get(position).getStatus());
        } else {
            String deviceType=listBottom.get(position - TopCount).getType();
            String deviceStatu=listBottom.get(position - TopCount).getStatus();
            viewHolder.textview_device_name.setText(deviceType);
            viewHolder.textview_device_status.setText("状态:"+deviceStatu);

        }
        return convertView;
    }

    */
/*
     * 功能：获得当前选项的ID
     *
     * @see android.widget.Adapter#getItemId(int)
     *//*

    @Override
    public long getItemId(int position) {
        //System.out.println("getItemId = " + position);
        return position;
    }

    */
/*
     * 功能：获得当前选项
     *
     * @see android.widget.Adapter#getItem(int)
     *//*

    @Override
    public Object getItem(int position) {
        return position;
    }

    */
/*
     * 获得数量
     *
     * @see android.widget.Adapter#getCount()
     *//*

    @Override
    public int getCount() {
      return 0;
    }
    */
/**
     * 获取当前需要显示布局的类型
     * return TOP_ITEM则表示上面半部分列表
     * return BOTTOM_ITEM则表示下半部分列表
     * **//*

    @Override
    public int getItemViewType(int position) {

        if (position < TopCount)
            return ROOM_ITEM;
        else
            return ADD_ITEM;
    }
    */
/** 该方法返回多少个不同的布局 *//*

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }
    final static class ViewHolder {
        TextView textview_device_uid;
        TextView textview_device_status;
        TextView textview_device_name;
    }
}
*/
