package deplink.com.smartwirelessrelay.homegenius.activity.smartlock.alarmhistory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.util.DateUtil;

/**
 * Created by Administrator on 2017/10/31.
 * 开锁记录适配器
 */
public class AlarmHistoryAdapter extends BaseAdapter{
    private static final String TAG="AlarmHistoryAdapter";
    private Context mContext;
    private List<LOCK_ALARM>mDatas;
    public AlarmHistoryAdapter(Context mContext, List<LOCK_ALARM>mDevices) {
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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.alarmhistorylistitem,null);
            vh.textview_userid= (TextView) convertView.findViewById(R.id.textview_userid);
            vh.textview_data_year_mouth_day= (TextView) convertView.findViewById(R.id.textview_data_year_mouth_day);
            vh.textview_data_hour_minute_second= (TextView) convertView.findViewById(R.id.textview_data_hour_minute_second);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        String time=mDatas.get(position).getTime();
        Date date= DateUtil.transStringTodata(time);
        String yearMouthDay=DateUtil.getYearMothDayStringFromData(date);
        String hourMinuteSecond=DateUtil.getHourMinuteSecondStringFromData(date);
        Log.i(TAG,"yearMouthDay="+yearMouthDay+"hourMinuteSecond="+hourMinuteSecond);
        vh.textview_userid.setText(mDatas.get(position).getUserid());
        vh.textview_data_year_mouth_day.setText(yearMouthDay);
        vh.textview_data_hour_minute_second.setText(hourMinuteSecond);
        return convertView;
    }

    private static class ViewHolder{
        TextView textview_userid;
        TextView textview_data_year_mouth_day;
        TextView textview_data_hour_minute_second;

    }
}
