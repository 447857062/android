package deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;

/**
 *
 */
public class ExperienceCenterListAdapter extends BaseAdapter {
    private static final String TAG = "CenterListAdapter";
    private List<ExperienceCenterDevice> mDatas;
    private Context mContext;

    public ExperienceCenterListAdapter(Context mContext, List<ExperienceCenterDevice> device) {
        this.mContext = mContext;
        this.mDatas = device;
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();


            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.person_page_experience_center_listitem, null);
            viewHolder.image_device_type = (ImageView) convertView
                    .findViewById(R.id.image_device_type);
            viewHolder.textview_device_name = (TextView) convertView
                    .findViewById(R.id.textview_device_name);
            viewHolder.textview_online_statu = (TextView) convertView
                    .findViewById(R.id.textview_online_statu);
            viewHolder.iamgeview_setting = (ImageView) convertView
                    .findViewById(R.id.iamgeview_setting);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.textview_device_name.setText(mDatas.get(position).getDeviceName());
        if (mDatas.get(position).isOnline()) {
            viewHolder.textview_online_statu.setText("在线");
        } else {
            viewHolder.textview_online_statu.setText("离线");
        }


        return convertView;
    }

    final static class ViewHolder {
        ImageView image_device_type;
        TextView textview_device_name;
        TextView textview_online_statu;
        ImageView iamgeview_setting;
    }

}
