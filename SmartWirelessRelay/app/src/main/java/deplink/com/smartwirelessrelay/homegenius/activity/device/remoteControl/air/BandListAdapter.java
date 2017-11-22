package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.air;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/11/22.
 */
public class BandListAdapter extends BaseAdapter {
    private List<String> bands;
    private Context mContext;

    public BandListAdapter( Context mContext,List<String> bands) {
        this.bands = bands;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return bands.size();
    }

    @Override
    public Object getItem(int position) {
        return bands.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();


            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.air_band_list_item, null);
            viewHolder.textview_band_name = (TextView) convertView
                    .findViewById(R.id.textview_band_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textview_band_name.setText(bands.get(position));


        return convertView;
    }

    final static class ViewHolder {
        TextView textview_band_name;
    }
}
