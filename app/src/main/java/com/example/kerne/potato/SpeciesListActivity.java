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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.example.kerne.potato.temporarystorage.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Item 点击对应的 Activity
 * <p>
 * Created by Tnno Wu on 2018/03/05.
 */

public class SpeciesListActivity extends AppCompatActivity implements SpeciesListAdapter.OnItemClickListener {

    private static final String TAG = SpeciesListActivity.class.getSimpleName();
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    private  Thread thread;

    private List<JSONObject> mList = new ArrayList<>();
    private List<JSONObject> jList = new ArrayList<>();

    private String fieldId;
    public static String expType;

    private String speciesId;
    private String userRole;
    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.species_list_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        speciesId = intent.getStringExtra("speciesId");
        initToolBar();
//        userRole = intent.getStringExtra("userRole");

        initData();
    }
    private void initToolBar() {
        titleText.setText(speciesId);
        leftOneButton.setBackgroundResource(R.drawable.left_back);
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                SpeciesDBHelper dbHelper = new SpeciesDBHelper(SpeciesListActivity.this, "SpeciesTable.db", null, 10);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

//        String sql = "select ExperimentField.*, SpeciesList.* from ExperimentField, SpeciesList " +
//                "where ExperimentField.id=SpeciesList.fieldId and ExperimentField.expType='" + expType +
//                "' order by ExperimentField.moveX";
//        db.rawQuery(sql, null);

                Cursor cursor = db.query("SpeciesList", null, "speciesId=?", new String[]{speciesId}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("blockId", cursor.getString(cursor.getColumnIndex("blockId")));
                            fieldId = cursor.getString(cursor.getColumnIndex("fieldId"));
                            jsonObject0.put("fieldId", fieldId);
                            jsonObject0.put("speciesId", cursor.getString(cursor.getColumnIndex("speciesId")));
//                    jsonObject0.put("userRole", userRole);
                            Cursor cursor1 = db.query("ExperimentField", null, "id=?", new String[]{fieldId}, null, null, null);
                            if (cursor1.moveToFirst()) {
                                jsonObject0.put("expType", cursor1.getString(cursor1.getColumnIndex("expType")));
                            } else {
                                Looper.prepare();
                                Toast.makeText(SpeciesListActivity.this, "Do not have the fieldId '" + fieldId + "' in ExperimentField", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            cursor1.close();
                            mList.add(jsonObject0);
//                    Log.d("mList.jsonObject", jsonObject0.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("speciesId_error", cursor.getString(cursor.getColumnIndex("speciesId")));
                        }
                    } while (cursor.moveToNext());
                } else {
                    Toast.makeText(SpeciesListActivity.this, "品种不存在", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SpeciesListActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                if (fieldId != null) {
                    cursor = db.query("ExperimentField", null, "id=?", new String[]{fieldId}, null, null, null);
                    if (cursor.moveToFirst()) {
                        expType = cursor.getString(cursor.getColumnIndex("expType"));
                    } else {
                        Toast.makeText(SpeciesListActivity.this, "实验田中无 " + fieldId +" 相关信息", Toast.LENGTH_SHORT).show();
                    }
                }

                cursor.close();
                db.close();
                dbHelper.close();
                //Log.d("mList.toString", mList.toString());

                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);

            }
        }).start();

//        initView();

//        new Thread(){
//            @Override
//            public void run(){
//                HttpRequest.HttpRequest_species(fieldId, SpeciesClickActivity.this, new HttpRequest.HttpCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        try {
//                            JSONObject data = new JSONObject();
//                            data = result.getJSONObject("data");
//                            int num = Integer.parseInt(data.getString("num"));
//                            for(int i = 0; i < num; i++){
//                                JSONArray jsonArray0 = data.getJSONArray("speciesId");
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("speciesId", jsonArray0.getString(i));
//                                jsonObject.put("userRole", userRole);
//                                mList.add(jsonObject);
//                            }
//                            Log.d("ShotJsonList", mList.toString());
//
//                            initView(); //!!!
//
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
                    Log.i(SpeciesListActivity.TAG, "init data ok");
                    initView();
                    break;
            }
        }
    };

    private void initView() {
        SpeciesListAdapter adapter = new SpeciesListAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(String content) {
        Toast.makeText(this, "品种编号：" + content, Toast.LENGTH_SHORT).show();
    }
}
