package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.gridview.DragGridView;

public class RoomActivity extends Activity implements View.OnClickListener{
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;

    private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initViews();
        initEvents();

        DragGridView mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
        for (int i = 0; i < 9; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
            itemHashMap.put("item_image",R.drawable.com_tencent_open_notice_msg_icon_big);
            itemHashMap.put("item_text", "拖拽 " + Integer.toString(i));
            dataSourceList.add(itemHashMap);
        }

        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, dataSourceList,
                R.layout.grid_item, new String[] { "item_image", "item_text" },
                new int[] { R.id.item_image, R.id.item_text });

        mDragGridView.setAdapter(mSimpleAdapter);

        mDragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
                HashMap<String, Object> temp = dataSourceList.get(from);
                //这里的处理需要注意下
                if(from < to){
                    for(int i=from; i<to; i++){
                        Collections.swap(dataSourceList, i, i+1);
                    }
                }else if(from > to){
                    for(int i=from; i>to; i--){
                        Collections.swap(dataSourceList, i, i-1);
                    }
                }
                dataSourceList.set(to, temp);
                mSimpleAdapter.notifyDataSetChanged();
            }
        });
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RoomActivity.this,"onclick position="+position,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initEvents() {
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
    }

    private void initViews() {
        layout_home_page= (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices= (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms= (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center= (LinearLayout) findViewById(R.id.layout_personal_center);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_home_page:
                startActivity(new Intent(this,SmartHomeMainActivity.class));
                break;
            case R.id.layout_devices:
                startActivity(new Intent(this,DevicesActivity.class));
                break;
            case R.id.layout_rooms:
              //  startActivity(new Intent(this,RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this,PersonalCenterActivity.class));
                break;
        }
    }
}
