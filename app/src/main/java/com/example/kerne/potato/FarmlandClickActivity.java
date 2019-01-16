package com.example.kerne.potato;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 点击对应的 Activity
 *
 * Created by Tnno Wu on 2018/03/05.
 */

public class FarmlandClickActivity extends AppCompatActivity implements FarmlandClickAdapter.OnItemClickListener {

    private static final String TAG = FarmlandClickActivity.class.getSimpleName();

    private List<JSONObject> mList = new ArrayList<>();

    private String farmlandId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmland_click_activity);

        Intent intent = getIntent();
        farmlandId = intent.getStringExtra("farmlandId");
        Log.d("farmlandId", farmlandId);

        initData();

        //initView();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run(){
                HttpRequest.HttpRequest_farmland(farmlandId, FarmlandClickActivity.this, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            JSONArray rows = new JSONArray();
                            rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            for(int i = 0; i < total; i++){
                                JSONObject jsonObject0 = rows.getJSONObject(i);
                                mList.add(jsonObject0);
                            }
                            Log.d("FarmlandJsonList", mList.toString());
                            initView();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();

    }

    private void initView() {
        FarmlandClickAdapter adapter = new FarmlandClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(String content) {
        //Toast.makeText(this, "你点击的是：" + content, Toast.LENGTH_SHORT).show();
    }
}
