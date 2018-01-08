package com.deplink.homegenius.activity.device.remoteControl.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.http.QueryBandResponse;
import com.deplink.homegenius.manager.connect.remote.https.RestfulTools;
import com.deplink.homegenius.view.listview.sortlistview.SortAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

import com.deplink.homegenius.activity.device.remoteControl.AddRemoteControlActivity;
import com.deplink.homegenius.view.edittext.ClearEditText;
import com.deplink.homegenius.view.listview.sortlistview.CharacterParser;
import com.deplink.homegenius.view.listview.sortlistview.PinyinComparator;
import com.deplink.homegenius.view.listview.sortlistview.SideBar;
import com.deplink.homegenius.view.listview.sortlistview.SortModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTvDeviceActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static final String TAG="AddTvDeviceActivity";
    private ListView listview_band;
    private TextView textview_title;
    private FrameLayout image_back;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText edittext_band_name;
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList = new ArrayList<>();
    private PinyinComparator pinyinComparator;
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
                if(response.body().getValue().size()>0){
                        SourceDateList.clear();
                        SourceDateList.addAll(filledData(response.body().getValue()));
                        Collections.sort(SourceDateList, pinyinComparator);
                        adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<QueryBandResponse> call, Throwable t) {

            }
        });
    }

    private void initEvents() {
        listview_band.setOnItemClickListener(this);
        image_back.setOnClickListener(this);
        edittext_band_name = findViewById(R.id.edittext_band_name);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {

                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listview_band.setSelection(position);
                }

            }
        });
        listview_band.setAdapter(adapter);
        edittext_band_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initDatas() {
        textview_title.setText("品牌选择");
        sideBar.setTextView(dialog);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        adapter = new SortAdapter(this, SourceDateList);


    }

    private void initViews() {
        listview_band= findViewById(R.id.listview_band);
        textview_title= findViewById(R.id.textview_title);
        image_back= findViewById(R.id.image_back);
        sideBar = findViewById(R.id.sidrbar);
        dialog = findViewById(R.id.dialog);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(AddTvDeviceActivity.this,AddRemoteControlActivity.class);
        intent.putExtra("bandname",SourceDateList.get(position).getName());
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
    private List<SortModel> filledData(List<String> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i));
            String pinyin = characterParser.getSelling(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }


    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)) {
                    filterDateList.add(sortModel);
                }
            }
        }
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }
}
