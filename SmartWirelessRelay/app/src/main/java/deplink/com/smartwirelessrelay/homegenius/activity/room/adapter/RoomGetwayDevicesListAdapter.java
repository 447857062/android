package deplink.com.smartwirelessrelay.homegenius.activity.room.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.Device;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

/**
 * Created by Administrator on 2017/10/31.
 * 每个房间管理的设备
 */
public class RoomGetwayDevicesListAdapter extends BaseAdapter {
    private static final String TAG = "GetwayDevicesAdapter";
    private Context mContext;
    private List<Device> mGetwayDev;
    private Room currentRoom;
    private DeviceManager mDeviceManager;
    public RoomGetwayDevicesListAdapter(Context mContext, List<Device> devices, Room room,DeviceManager deviceManager) {
        this.mContext = mContext;
        this.mGetwayDev = devices;
        this.currentRoom=room;
        this.mDeviceManager=deviceManager;
    }

    @Override
    public int getCount() {
        return mGetwayDev.size();
    }

    @Override
    public Object getItem(int position) {
        return mGetwayDev.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.room_manager_getway_devices_listitem, null);
            vh.imageview_device_type = (ImageView) convertView.findViewById(R.id.imageview_device_type);
            vh.textview_device_type = (TextView) convertView.findViewById(R.id.textview_device_type);
            vh.imageview_delete = (ImageView) convertView.findViewById(R.id.imageview_delete);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.textview_device_type.setText(mGetwayDev.get(position).getName());
        vh.imageview_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "imageview_delete onclick");
                //删除数据库中的数据
               mDeviceManager.deleteGetwayDeviceInWhatRoom(currentRoom,mGetwayDev.get(position).getUid());
                mGetwayDev.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageview_device_type;
        TextView textview_device_type;
        ImageView imageview_delete;
    }
}
