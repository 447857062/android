package deplink.com.smartwirelessrelay.homegenius.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;

/**
 * @author frankLi
 *
 */
public class DeviceListAdapter extends BaseAdapter {
	private static final String TAG="DeviceListAdapter";
	private List<Device> listTop = null;
	private List<SmartDev> listBottom = null;
	private Context mContext;
	private final int TOP_ITEM = 0, BOTTOM_ITEM = 1, TYPE_COUNT = 2;
	/**
	 * 头部列表数据的大小
	 * */
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

	/** 该方法返回多少个不同的布局 */
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_COUNT;
	}

	/**
	 * 获取当前需要显示布局的类型 
	 * return TOP_ITEM则表示上面半部分列表
	 * return BOTTOM_ITEM则表示下半部分列表
	 * **/
	@Override
	public int getItemViewType(int position) {

		if (position < TopCount)
			return TOP_ITEM;
		else
			return BOTTOM_ITEM;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			if (getItemViewType(position) == TOP_ITEM) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.devicelist_device_item, null);
				viewHolder.textview_device_uid = (TextView) convertView
						.findViewById(R.id.textview_device_uid);
				viewHolder.textview_device_status = (TextView) convertView
						.findViewById(R.id.textview_device_status);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.devicelist_smartdevice_item, null);
				viewHolder.textview_device_uid = (TextView) convertView
						.findViewById(R.id.textview_device_uid);
				viewHolder.textview_device_status = (TextView) convertView
						.findViewById(R.id.textview_device_status);
				viewHolder.textview_device_name = (TextView) convertView
						.findViewById(R.id.textview_device_name);

			}

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position < TopCount) {
			viewHolder.textview_device_uid.setText(listTop.get(position).getUid());
			viewHolder.textview_device_status.setText("状态:"+listTop.get(position).getStatus());
		} else {
			String deviceType=listBottom.get(position - TopCount).getType();
			String deviceStatu=listBottom.get(position - TopCount).getStatus();
			Log.i(TAG,"deviceType="+deviceType+"deviceStatu="+deviceStatu);
			viewHolder.textview_device_name.setText(deviceType);
			viewHolder.textview_device_status.setText("状态:"+deviceStatu);

		}
		return convertView;
	}

	final static class ViewHolder {
		TextView textview_device_uid;
		TextView textview_device_status;
		TextView textview_device_name;
	}

}
