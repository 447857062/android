package deplink.com.smartwirelessrelay.homegenius.view.dialog.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
/*
*
 * Created by Administrator on 2017/11/11.
 */

public class DeviceRoomTypeDialogAdapter extends BaseAdapter {
    private Context mContext;

    private List<String> listTop;

    public DeviceRoomTypeDialogAdapter(Context mContext, List<String> mRooms) {
        this.mContext = mContext;
        listTop = mRooms;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.dialog_device_room_type_item, null);
            viewHolder.textview_room_type = convertView
                    .findViewById(R.id.textview_room_type);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textview_room_type.setText(listTop.get(position));
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
        TextView textview_room_type;
    }
}
