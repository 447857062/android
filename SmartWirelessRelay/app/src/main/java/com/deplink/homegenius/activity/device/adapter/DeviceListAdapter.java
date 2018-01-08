package com.deplink.homegenius.activity.device.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.device.getway.Device;
import com.deplink.homegenius.constant.DeviceTypeConstant;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.Protocol.json.device.SmartDev;

/**
 */
public class DeviceListAdapter extends BaseAdapter {
    private static final String TAG = "DeviceListAdapter";
    private List<Device> listTop = null;
    private List<SmartDev> listBottom = null;
    private Context mContext;
    private final int TOP_ITEM = 0, BOTTOM_ITEM = 1, TYPE_COUNT = 2;
    /**
     * 头部列表数据的大小
     */
    private int TopCount = 0;

    public DeviceListAdapter(Context mContext, List<Device> list,
                             List<SmartDev> datasOther) {
        this.mContext = mContext;
        this.listTop = list;
        this.listBottom = datasOther;
        TopCount = listTop.size();
    }

    /**
     * 设置Item显示的数据集合
     *
     * @param list
     */
    public void setTopList(List<Device> list) {
        this.listTop = list;
        TopCount = listTop.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.i(TAG, "头部列表=" + listTop.size());
    }

    /**
     * 设置Item显示的数据集合
     *
     * @param list
     */
    public void setBottomList(List<SmartDev> list) {
        this.listBottom = list;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (listTop != null && listBottom != null) {
            count = TopCount + listBottom.size();
        }
        if (listTop != null && listBottom == null) {
            count = TopCount;
        }
        if (listBottom != null && listTop == null) {
            count = listBottom.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < TopCount) {
            return listTop.get(position);
        }

        if (position > TopCount) {
            return listBottom.get(position - TopCount);
        }

        if (position <= 1) {
            return null;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 该方法返回多少个不同的布局
     */
    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    /**
     * 获取当前需要显示布局的类型
     * return TOP_ITEM则表示上面半部分列表
     * return BOTTOM_ITEM则表示下半部分列表
     **/
    @Override
    public int getItemViewType(int position) {

        if (position < TopCount)
            return TOP_ITEM;
        else
            return BOTTOM_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (getItemViewType(position) == TOP_ITEM) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.devicelist_device_item, null);
                viewHolder.textview_device_status = convertView
                        .findViewById(R.id.textview_device_status);
                viewHolder.imageview_device_type = convertView
                        .findViewById(R.id.imageview_device_type);
                viewHolder.textview_device_name = convertView
                        .findViewById(R.id.textview_device_name);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.devicelist_smartdevice_item, null);
                viewHolder.textview_device_status = convertView
                        .findViewById(R.id.textview_device_status);
                viewHolder.textview_device_name = convertView
                        .findViewById(R.id.textview_device_name);
                viewHolder.imageview_device_type = convertView
                        .findViewById(R.id.imageview_device_type);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //智能网关
        if (position < TopCount) {
            String statu = listTop.get(position).getStatus();
            if (statu != null) {
                switch (statu){
                    case "on":
                        statu = "在线";
                        break;
                    case "off":
                        statu = "离线";
                        break;
                }
            } else {
                statu = "离线";
            }
            viewHolder.textview_device_status.setText(statu);
            viewHolder.imageview_device_type.setImageResource(R.drawable.gatewayicon);
            String deviceName = listTop.get(position).getName();
            viewHolder.textview_device_name.setText(deviceName);
        } else {
            String deviceType = listBottom.get(position - TopCount).getType();
            String deviceName = listBottom.get(position - TopCount).getName();


            String deviceStatu = listBottom.get(position - TopCount).getStatus();
            Log.i(TAG,"deviceStatu="+deviceStatu);
            if (deviceStatu != null) {
                switch (deviceStatu){
                    case "on":
                        deviceStatu = "在线";
                        break;
                    case "off":
                        deviceStatu = "离线";
                        break;
                }
            }else{
                deviceStatu = "离线";
            }
            if ("SMART_LOCK".equals(deviceType)) {
                deviceType = DeviceTypeConstant.TYPE.TYPE_LOCK;
                deviceStatu = "在线";
            }
            if ("IRMOTE_V2".equals(deviceType)) {
                deviceType = DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL;
            }
            viewHolder.textview_device_name.setText(deviceName);
            viewHolder.textview_device_status.setText(deviceStatu);
            getDeviceTypeImage(viewHolder, deviceType, position);
        }
        return convertView;
    }
    private void getDeviceTypeImage(ViewHolder viewHolder, String deviceType, int position) {
        switch (deviceType) {
            case DeviceTypeConstant.TYPE.TYPE_ROUTER:
                viewHolder.imageview_device_type.setImageResource(R.drawable.routericon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_LOCK:

                viewHolder.imageview_device_type.setImageResource(R.drawable.doorlockicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_MENLING:
                viewHolder.imageview_device_type.setImageResource(R.drawable.doorbellicon);
                break;
           case DeviceTypeConstant.TYPE.TYPE_SWITCH:
               String deviceSubType;
               deviceSubType = listBottom.get(position - TopCount).getSubType();
               Log.i(TAG,"deviceSubType="+deviceSubType);
               if(deviceSubType==null){
                   return;
               }
               switch (deviceSubType){
                   case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_ONEWAY:
                       viewHolder.imageview_device_type.setImageResource(R.drawable.switchalltheway);
                       break;
                   case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_TWOWAY:
                       viewHolder.imageview_device_type.setImageResource(R.drawable.roadswitch);
                       break;
                   case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_THREEWAY:
                       viewHolder.imageview_device_type.setImageResource(R.drawable.threewayswitch);
                       break;
                   case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_FOURWAY:
                       viewHolder.imageview_device_type.setImageResource(R.drawable.fourwayswitch);
                       break;
               }
               break;
            case DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL:
                viewHolder.imageview_device_type.setImageResource(R.drawable.infraredremotecontrolicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL:
            case "智能电视":
                viewHolder.imageview_device_type.setImageResource(R.drawable.tvicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL:
            case "智能空调":
                viewHolder.imageview_device_type.setImageResource(R.drawable.airconditioningicon);
                break;
            case DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL:
            case "智能机顶盒遥控":
                viewHolder.imageview_device_type.setImageResource(R.drawable.settopboxesicon);
                break;
            case  DeviceTypeConstant.TYPE.TYPE_LIGHT:
                viewHolder.imageview_device_type.setImageResource(R.drawable.equipmentlight);
                break;

        }
    }

    final static class ViewHolder {
        TextView textview_device_status;
        TextView textview_device_name;
        ImageView imageview_device_type;
    }

}
