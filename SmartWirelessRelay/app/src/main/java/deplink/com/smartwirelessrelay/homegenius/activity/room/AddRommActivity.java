package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.SmartGetwayActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.adapter.GridViewRommTypeAdapter;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class AddRommActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AddRommActivity";
    private TextView textview_add_room_complement;
    private ImageView image_back;
    private RelativeLayout layout_getway;
    private ClearEditText edittext_room_name;
    private GridView gridview_room_type;
    private GridViewRommTypeAdapter mGridViewRommTypeAdapter;
    private RoomManager roomManager;
    private List<String> listTop = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_romm);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        roomManager = RoomManager.getInstance();
        mGridViewRommTypeAdapter = new GridViewRommTypeAdapter(this);
        listTop.add(AppConstant.ROOMTYPE.TYPE_LIVING);
        listTop.add(AppConstant.ROOMTYPE.TYPE_BED);
        listTop.add(AppConstant.ROOMTYPE.TYPE_KITCHEN);
        listTop.add(AppConstant.ROOMTYPE.TYPE_STUDY);
        listTop.add(AppConstant.ROOMTYPE.TYPE_STORAGE);
        listTop.add(AppConstant.ROOMTYPE.TYPE_TOILET);
        listTop.add(AppConstant.ROOMTYPE.TYPE_DINING);
        roomType = AppConstant.ROOMTYPE.TYPE_LIVING;
        edittext_room_name.setText(roomType);
        edittext_room_name.setSelection(roomType.length());
    }

    private void initEvents() {
        textview_add_room_complement.setOnClickListener(this);
        image_back.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
        gridview_room_type.setAdapter(mGridViewRommTypeAdapter);
        gridview_room_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomType = listTop.get(position);
                mGridViewRommTypeAdapter.setSelectedPosition(position);
                mGridViewRommTypeAdapter.notifyDataSetInvalidated();
                edittext_room_name.setText(roomType);
                edittext_room_name.setSelection(roomType.length());
            }
        });

    }

    private void initViews() {
        textview_add_room_complement = (TextView) findViewById(R.id.textview_add_room_complement);
        image_back = (ImageView) findViewById(R.id.image_back);
        layout_getway = (RelativeLayout) findViewById(R.id.layout_getway);
        edittext_room_name = (ClearEditText) findViewById(R.id.edittext_room_name);
        gridview_room_type = (GridView) findViewById(R.id.gridview_room_type);
    }

    private String roomType;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //完成
            case R.id.textview_add_room_complement:
                String roomName = edittext_room_name.getText().toString();
                if (!roomName.equals("")) {
                    roomManager.addRoom(roomType, roomName, new Observer() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Object o) {
                            Log.i(TAG, "add room react onNext=" + (boolean) o);
                            if ((boolean) o) {
                                Toast.makeText(AddRommActivity.this, "添加房间成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddRommActivity.this, "添加房间失败", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                    finish();

                } else {
                    Toast.makeText(this, "请输入房间名称", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_getway:
                startActivity(new Intent(AddRommActivity.this, SmartGetwayActivity.class));
                break;
        }
    }
}
