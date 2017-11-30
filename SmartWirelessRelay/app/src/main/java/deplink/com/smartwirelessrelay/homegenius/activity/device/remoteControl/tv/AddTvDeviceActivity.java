package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.http.QueryBandResponse;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.adapter.BandListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.add.addRemoteControlActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.RestfulTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTvDeviceActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static final String TAG="AddTvDeviceActivity";
    private BandListAdapter mBandListAdapter;
    private ListView listview_band;
    private List<String> bands;
    private TextView textview_title;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tv_device);
        initViews();
        initDatas();
        initEvents();
    }
    @Override
    protected void onResume() {
        super.onResume();
        RestfulTools.getSingleton(this).queryBand("TV", "cn", new Callback<QueryBandResponse>() {
            @Override
            public void onResponse(Call<QueryBandResponse> call, Response<QueryBandResponse> response) {
                Log.i(TAG,"获取电视品牌response="+response.message());
                if(response.body().getValue().size()>0){
                    for(int i=0;i<response.body().getValue().size();i++){
                        bands.clear();
                        bands.addAll(response.body().getValue());
                        mBandListAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<QueryBandResponse> call, Throwable t) {

            }
        });
    }

    private void initEvents() {
        listview_band.setAdapter(mBandListAdapter);
        listview_band.setOnItemClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("品牌选择");
        bands=new ArrayList<>();
        mBandListAdapter=new BandListAdapter(this,bands);


    }

    private void initViews() {
        listview_band= (ListView) findViewById(R.id.listview_band);
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(AddTvDeviceActivity.this,addRemoteControlActivity.class);
        intent.putExtra("bandname",bands.get(position));
        intent.putExtra("type","TV");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
