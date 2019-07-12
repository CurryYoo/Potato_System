package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.example.kerne.potato.temporarystorage.Util;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Item 点击对应的 Activity
 * <p>
 * Created by Xie.
 */

public class GeneralClickActivity extends AppCompatActivity implements GeneralClickAdapter.OnItemClickListener {

    private static final String TAG = GeneralClickActivity.class.getSimpleName();
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.right_two_button)
    ImageView rightTwoButton;
    @BindView(R.id.right_two_layout)
    LinearLayout rightTwoLayout;
    @BindView(R.id.right_one_button)
    ImageView rightOneButton;
    @BindView(R.id.right_one_layout)
    LinearLayout rightOneLayout;

    private List<JSONObject> mList = new ArrayList<>();

    private String bigfarmId = null;
    private String img;
    private int year;
    private String uri;
    private String bigFarmName;

    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    Intent intent = new Intent(GeneralClickActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.right_two_layout:
                    Util.watchOnlineLargePhoto(GeneralClickActivity.this, Uri.parse(uri), "实验田示意图");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.general_click_activity);
        ButterKnife.bind(this);


        bigfarmId = getIntent().getStringExtra("bigfarmId");
        img = getIntent().getStringExtra("img");
        year = getIntent().getIntExtra("year", 1970);
        uri = getIntent().getStringExtra("uri");
        bigFarmName = getIntent().getStringExtra("name");
        initToolBar();
        initData();

        //initView();
    }

    private void initToolBar() {
        titleText.setText(bigFarmName);
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.ic_menu_home);
        rightTwoButton.setBackgroundResource(R.drawable.ic_menu_map);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
        rightOneLayout.setOnClickListener(toolBarOnClickListener);
        rightTwoLayout.setOnClickListener(toolBarOnClickListener);
    }


    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
                SpeciesDBHelper dbHelper = new SpeciesDBHelper(GeneralClickActivity.this, "SpeciesTable.db", null, 10);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                //获取棚外区域
                Cursor cursor = db.query("FarmList", null, "bigfarmId=?", new String[]{bigfarmId}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("farmlandId", cursor.getString(cursor.getColumnIndex("farmlandId")));
                            jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
                            jsonObject0.put("length", cursor.getInt(cursor.getColumnIndex("length")));
                            jsonObject0.put("width", cursor.getInt(cursor.getColumnIndex("width")));
                            jsonObject0.put("type", cursor.getString(cursor.getColumnIndex("type")));
                            jsonObject0.put("bigfarmId", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                            jsonObject0.put("bigFarmName", bigFarmName);
                            jsonObject0.put("year", year);

//                    jsonObject0.put("userRole", userRole);
                            mList.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("farmlandId_error", cursor.getString(cursor.getColumnIndex("farmlandId")));
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();


                //获取大棚区域
                Cursor cursor2 = db.query("ExperimentField", null, "farmlandId=?", new String[]{bigfarmId}, null, null, null);
                if (cursor2.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("fieldId", cursor2.getString(cursor2.getColumnIndex("id")));
                            jsonObject0.put("name", cursor2.getString(cursor2.getColumnIndex("name")));
                            jsonObject0.put("expType", cursor2.getString(cursor2.getColumnIndex("expType")));
                            jsonObject0.put("num", cursor2.getInt(cursor2.getColumnIndex("num")));
                            jsonObject0.put("farmlandId", cursor2.getString(cursor2.getColumnIndex("farmlandId")));
                            jsonObject0.put("rows", cursor2.getInt(cursor2.getColumnIndex("rows")));
                            jsonObject0.put("type", "greenhouse");
                            jsonObject0.put("bigFarmName", bigFarmName);
                            jsonObject0.put("year", year);
//                    jsonObject0.put("userRole", userRole);
                            mList.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("greenhouse_error", cursor2.getString(cursor2.getColumnIndex("farmlandId")));
                        }
                    } while (cursor2.moveToNext());
                }
                cursor2.close();

                db.close();
                dbHelper.close();

                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
                Looper.loop();
            }
        }).start();

//        initView();

//        new Thread(){
//            @Override
//            public void run(){
//                HttpRequest.HttpRequest_farm(bigFarmName, GeneralClickActivity.this, new HttpRequest.HttpCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        try {
//                            JSONArray rows = new JSONArray();
//                            rows = result.getJSONArray("rows");
//                            int total = result.getInt("total");
//                            for(int i = 0; i < total; i++){
//                                JSONObject jsonObject0 = rows.getJSONObject(i);
//                                jsonObject0.put("userRole", userRole);
//                                mList.add(jsonObject0);
//                            }
//                            Log.d("GeneralJsonList", mList.toString());
//
//                            initView();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        }.start();

    }

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i(GeneralClickActivity.TAG, "init data ok");
                    initView();
                    break;
            }
        }
    };

    private void initView() {
        GeneralClickAdapter adapter = new GeneralClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        if(mList.size()==0){
            Toast.makeText(this,R.string.farm_null_error,Toast.LENGTH_SHORT).show();
        }
        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(final String content) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
    }
}
