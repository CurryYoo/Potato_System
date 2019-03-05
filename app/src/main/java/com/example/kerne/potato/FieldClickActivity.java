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

public class FieldClickActivity extends AppCompatActivity implements FieldClickAdapter.OnItemClickListener {

    private static final String TAG = FieldClickActivity.class.getSimpleName();

    private List<JSONObject> mList = new ArrayList<>();

    private String fieldId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.field_click_activity);

        Intent intent = getIntent();
        fieldId = intent.getStringExtra("fieldId");

        initData();

        //initView();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run(){
//                HttpRequest.HttpRequest_field(fieldId, FieldClickActivity.this, new HttpRequest.HttpCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        try {
//                            JSONArray rows = new JSONArray();
//                            rows = result.getJSONArray("rows");
//                            int total = result.getInt("total");
//                            for(int i = 0; i < total; i++){
//                                JSONObject jsonObject0 = rows.getJSONObject(i);
//                                mList.add(jsonObject0);
//                            }
//                            Log.d("ShotJsonList", mList.toString());
//                            initView();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
            }
        }.start();
    }

    private void initView() {
        FieldClickAdapter adapter = new FieldClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(String content) {
        Toast.makeText(this, "你点击的是：" + content, Toast.LENGTH_SHORT).show();
    }
}
