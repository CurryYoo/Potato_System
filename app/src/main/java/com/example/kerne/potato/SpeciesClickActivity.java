package com.example.kerne.potato;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 点击对应的 Activity
 *
 * Created by Tnno Wu on 2018/03/05.
 */

public class SpeciesClickActivity extends AppCompatActivity implements SpeciesClickAdapter.OnItemClickListener {

    private static final String TAG = SpeciesClickActivity.class.getSimpleName();

    private List<JSONObject> mList = new ArrayList<>();

    private String fieldId;
    public static String expType;

    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.species_click_activity);

        Intent intent = getIntent();
        fieldId = intent.getStringExtra("fieldId");
        expType = intent.getStringExtra("expType");
//        userRole = intent.getStringExtra("userRole");

        initData();

        //initView();
    }

    private void initData() {
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 8);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("SpeciesList", null, "fieldId=?", new String[]{fieldId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("blockId", cursor.getString(cursor.getColumnIndex("blockId")));
                    jsonObject0.put("fieldId", cursor.getString(cursor.getColumnIndex("fieldId")));
                    jsonObject0.put("speciesId", cursor.getString(cursor.getColumnIndex("speciesId")));
//                    jsonObject0.put("userRole", userRole);
                    mList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("blockId_error", cursor.getString(cursor.getColumnIndex("blockId")));
                }
            } while (cursor.moveToNext());
        }
        else {
            Toast.makeText(SpeciesClickActivity.this, "SpeciesList null", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        //Log.d("mList.toString", mList.toString());
        initView();

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

//        JSONObject jsonObject0 = new JSONObject();
//        JSONObject jsonObject1 = new JSONObject();
//        try {
//            jsonObject0.put("plotId", "AA");
//            jsonObject0.put("speciesId", "品种1号");
//            jsonObject0.put("fieldId", fieldId);
//            jsonObject0.put("userRole", userRole);
//            jsonObject1.put("plotId", "BB");
//            jsonObject1.put("speciesId", "品种2号");
//            jsonObject1.put("fieldId", fieldId);
//            jsonObject1.put("userRole", userRole);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mList.add(jsonObject0);
//        mList.add(jsonObject1);
//
//        //!!!
//        initView();
    }

    private void initView() {
        SpeciesClickAdapter adapter = new SpeciesClickAdapter(this, this);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
//                    userRole = data.getStringExtra("userRole");
//                    Log.d("userRole", userRole);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            default:
        }
        return true;
    }

}
