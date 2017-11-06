package deplink.com.smartwirelessrelay.homegenius.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Record;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/10/31.
 */
public class RecordListAdapter extends BaseAdapter{
    private Context mContext;
    private List<Record>mDatas;
    public RecordListAdapter(Context mContext, List<Record>mDevices) {
        this.mContext=mContext;
        this.mDatas=mDevices;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            vh=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.devlistitem,null);
            vh.device_uid= (TextView) convertView.findViewById(R.id.textview_device_uid);
            vh.device_statu= (TextView) convertView.findViewById(R.id.textview_device_statu);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        String deviceName=mDatas.get(position).getTime();
        if(deviceName.equals("")){
            deviceName="未知";
        }
        vh.device_uid.setText("time:"+deviceName);
        vh.device_statu.setText("useid："+mDatas.get(position).getUserid());
        return convertView;
    }

    private static class ViewHolder{
        TextView device_uid;
        TextView device_statu;

    }
}
