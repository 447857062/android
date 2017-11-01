package deplink.com.smartwirelessrelay.homegenius.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * Created by Administrator on 2017/10/31.
 */
public class MyExpandablelistviewAdapter extends BaseExpandableListAdapter{
    private Context context  ;
    private List<Device>groups;
    private List<List<SmartDev>>childs;
    //引入一个字段context，方便Activity实例化MyExpandablelistviewAdapter
    public MyExpandablelistviewAdapter(Context context,List<Device>devices, List<List<SmartDev>>smartDevs) {
        this.context = context;
        this.groups=devices;
        this.childs=smartDevs;
    }
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            /**
             * LayoutInflater是一个抽象类，它的inflate方法可以把一个xml文件转化为View对象
             * 对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入
             * 刚刚说明了：LayoutInflater是一个抽象类,要获取LayoutInflater的实例;
             * 获得 LayoutInflater 实例的三种方式:
             *  1.LayoutInflater inflater = getLayoutInflater();  //调用Activity的getLayoutInflater()
             *
             *  2.LayoutInflater localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             *
             *  3. LayoutInflater inflater = LayoutInflater.from(context);
             *  上面三种方法的本质是一样的
             */
            LayoutInflater minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.groupitem,null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.textview_device_uid);
        tv.setText(groups.get(groupPosition).getUid());
        TextView tvstatu = (TextView) convertView.findViewById(R.id.textview_device_statu);
        tvstatu.setText(groups.get(groupPosition).getStatus());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = minflater.inflate(R.layout.childitem,null);
        }
        TextView iv = (TextView) convertView.findViewById(R.id.textview_device_uid);
        TextView tv = (TextView) convertView.findViewById(R.id.textview_device_statu);
        TextView dev = (TextView) convertView.findViewById(R.id.textview_smart_dev);
        TextView statu = (TextView) convertView.findViewById(R.id.textview_smart_statu);



        tv.setText("ctrluid="+childs.get(groupPosition).get(childPosition).getCtrUid());
        tv.setText("statu="+childs.get(groupPosition).get(childPosition).getStatus());
        tv.setText("devuid="+childs.get(groupPosition).get(childPosition).getDevUid());
        tv.setText("type="+childs.get(groupPosition).get(childPosition).getType());
        //记得return convertView
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /*供外界更新数据的方法*/
    public void refresh(ExpandableListView expandableListView, int groupPosition){
        handler.sendMessage(new Message());
        //必须重新伸缩之后才能更新数据
        expandableListView.collapseGroup(groupPosition);
        expandableListView.expandGroup(groupPosition);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();//更新数据
            super.handleMessage(msg);
        }
    };
}

