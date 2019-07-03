package com.example.kerne.potato;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BigfarmClickActivity extends AppCompatActivity implements BigfarmClickAdapter.OnItemClickListener {

    private static final String TAG = BigfarmClickActivity.class.getSimpleName();

    private List<JSONObject> mList = new ArrayList<>();

    private String name = null;
    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.bigfarm_click_activity);

//        userRole = getIntent().getStringExtra("userRole");

        initData();

        //initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            default:
        }
        return true;
    }

    private void initData() {
        //获取服务器中数据
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("BigfarmList", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("bigfarmId", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                    jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject0.put("description", cursor.getString(cursor.getColumnIndex("description")));
                    jsonObject0.put("img", cursor.getString(cursor.getColumnIndex("img")));
                    jsonObject0.put("year", cursor.getInt(cursor.getColumnIndex("year")));
                    jsonObject0.put("uri", cursor.getString(cursor.getColumnIndex("uri")));

//                    jsonObject0.put("userRole", userRole);
                    mList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("bigfarmId_error", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                }
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(BigfarmClickActivity.this, "BigfarmList null", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
        dbHelper.close();
        //Log.d("mList.toString", mList.toString());
        initView();

//        new Thread(){
//            @Override
//            public void run(){
//                HttpRequest.HttpRequest_farm(name, GeneralClickActivity.this, new HttpRequest.HttpCallback() {
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

    private void initView() {
        BigfarmClickAdapter adapter = new BigfarmClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(final String content) {
    }
}
