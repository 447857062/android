package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.air;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ChooseBandActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{
    private BandListAdapter mBandListAdapter;
    private ListView listview_band;
    private List<String>bands;
    private ImageView imageview_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_band);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        listview_band.setAdapter(mBandListAdapter);
        listview_band.setOnItemClickListener(this);
        imageview_back.setOnClickListener(this);
    }

    private void initDatas() {
        bands=new ArrayList<>();
        bands.add("美的");
        bands.add("艾美特");
        bands.add("格力");
        bands.add("先锋");
        bands.add("松下");
        mBandListAdapter=new BandListAdapter(this,bands);


    }

    private void initViews() {
        listview_band= (ListView) findViewById(R.id.listview_band);
        imageview_back= (ImageView) findViewById(R.id.imageview_back);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(ChooseBandActivity.this,addRemoteControlActivity.class);
        intent.putExtra("bandname",bands.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageview_back:
                onBackPressed();
                break;
        }
    }
}
