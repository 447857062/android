package com.deplink.homegenius.activity.device.doorbell;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.util.DateUtil;
import com.deplink.homegenius.util.bitmap.BitmapHandler;
import com.deplink.homegenius.view.listview.swipemenulistview.BaseSwipListAdapter;
import com.deplink.sdk.android.sdk.json.homegenius.DoorBellItem;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/8/28.
 */
public class VisitorListAdapter extends BaseSwipListAdapter {
    private static final String TAG ="VisitorListAdapter";
    private Context mContext;
    private List<DoorBellItem> mListData;
    private List<Bitmap> mListDataImage;
    public VisitorListAdapter(Context context, List<DoorBellItem> listData,List<Bitmap> listDataImage) {
        this.mContext = context;
        this.mListData = listData;
        this.mListDataImage=listDataImage;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }
    @Override
    public Object getItem(int position) {
        return mListData.get(position);
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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.doorbell_visitor_list_item,null);
            vh.textview_file_name= convertView.findViewById(R.id.textview_file_name);
            vh.textview_timestamp = convertView.findViewById(R.id.textview_timestamp);
            vh.image_snap = convertView.findViewById(R.id.image_snap);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        try {
            if(mListDataImage.size()>0 && position-1<=mListDataImage.size()){
                BitmapDrawable bbb = new BitmapDrawable(BitmapHandler.toRoundCorner(mListDataImage.get(position), 30));
                vh.image_snap.setBackgroundDrawable(bbb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //这里是秒
        vh.textview_timestamp.setText(""+ DateUtil.format(mListData.get(position).getTimestamp()*1000));
       return convertView;
    }

    private static class ViewHolder{
        TextView textview_file_name;
        TextView textview_timestamp;
        ImageView image_snap;
    }
    @Override
    public boolean getSwipEnableByPosition(int position) {        //设置条目是否可以滑动
        return true;
    }
}
