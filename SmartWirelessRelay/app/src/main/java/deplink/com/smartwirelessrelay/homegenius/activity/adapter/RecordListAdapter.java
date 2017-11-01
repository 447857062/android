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
        //  vh.device_mac.setText(String.format(mContext.getResources().getString(R.string.device_mac),mListData.get(position).getMac()));
      /*  switch (position%4){
            case 0:
                vh.device_type.setImageResource(R.drawable.unbind_device_blue);
                break;
            case 1:
                vh.device_type.setImageResource(R.drawable.unbind_device_green);
                break;
            case 2:
                vh.device_type.setImageResource(R.drawable.unbind_device_origon);
                break;
            case 3:
                vh.device_type.setImageResource(R.drawable.unbind_device_red);
                break;
        }*/

        // vh.device_key.setText(String.format(mContext.getResources().getString(R.string.device_key),mListData.get(position).getDevice_key()));
        // vh.device_manufacturer.setText(String.format(mContext.getResources().getString(R.string.device_manufacturer),mListData.get(position).getManufacturer()));
        // vh.device_product.setText(String.format(mContext.getResources().getString(R.string.device_product),mListData.get(position).getProduct()));
        return convertView;
    }

    private static class ViewHolder{
        TextView device_uid;
        TextView device_statu;

    }
}
