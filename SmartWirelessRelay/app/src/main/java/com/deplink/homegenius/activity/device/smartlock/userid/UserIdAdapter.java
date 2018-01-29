package com.deplink.homegenius.activity.device.smartlock.userid;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.edittext.ClearEditText;

import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/10/31.
 * 开锁记录适配器
 */
public class UserIdAdapter extends BaseAdapter {
    private static final String TAG = "UserIdAdapter";
    private Context mContext;
    private ArrayList<String> mDatas;

    public UserIdAdapter(Context mContext, ArrayList<String> mDevices) {
        this.mContext = mContext;
        this.mDatas = mDevices;
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

    private ViewHolder vh;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.useridlistitem, null);
            vh = new ViewHolder(convertView, position);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.textview_userid.setText(mDatas.get(position));
        vh.edittext_input_userid.setHint("请为" + (mDatas.get(position) + "命名"));
        return convertView;
    }

    private class ViewHolder {
        TextView textview_userid;
        ClearEditText edittext_input_userid;

        public ViewHolder(View view, int pisition) {
            textview_userid = view.findViewById(R.id.textview_userid);
            edittext_input_userid = view.findViewById(R.id.edittext_input_userid);
            edittext_input_userid.setTag(pisition);//存tag值
            edittext_input_userid.addTextChangedListener(new TextSwitcher(this));
        }
    }
    class TextSwitcher implements TextWatcher {
        private ViewHolder mHolder;

        public TextSwitcher(ViewHolder mHolder) {
            this.mHolder = mHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int position = (int) mHolder.edittext_input_userid.getTag();//取tag值
            Perfence.setPerfence(mDatas.get(position), s.toString());
            Log.i(TAG,"保存别名 key="+mDatas.get(position)+"value:"+s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
