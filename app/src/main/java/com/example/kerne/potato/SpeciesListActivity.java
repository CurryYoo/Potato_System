package com.example.kerne.potato;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 点击对应的 Activity
 *
 * Created by Tnno Wu on 2018/03/05.
 */

public class SpeciesListActivity extends AppCompatActivity implements SpeciesListAdapter.OnItemClickListener {

    private static final String TAG = SpeciesListActivity.class.getSimpleName();

    private List<JSONObject> mList = new ArrayList<>();
    private List<JSONObject> jList = new ArrayList<>();

    private String fieldId;
    public static String expType;

    private String speciesId;
    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.species_list_activity);

        Intent intent = getIntent();
        speciesId = intent.getStringExtra("speciesId");
//        userRole = intent.getStringExtra("userRole");

        initData();

        //initView();
    }

    private void initData() {
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 9);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("SpeciesList", null, "speciesId=?", new String[]{speciesId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("blockId", cursor.getString(cursor.getColumnIndex("blockId")));
                    fieldId = cursor.getString(cursor.getColumnIndex("fieldId"));
                    jsonObject0.put("fieldId", fieldId);
                    jsonObject0.put("speciesId", cursor.getString(cursor.getColumnIndex("speciesId")));
//                    jsonObject0.put("userRole", userRole);
                    Cursor cursor1 = db.query("ExperimentField", null, "id=?", new String[]{fieldId}, null, null, null);
                    if(cursor1.moveToFirst()){
                        jsonObject0.put("expType", cursor1.getString(cursor1.getColumnIndex("expType")));
                    }
                    else{
                        Toast.makeText(SpeciesListActivity.this, "Do not have the fieldId '" + fieldId + "' in ExperimentField", Toast.LENGTH_SHORT).show();
                    }
                    cursor1.close();
                    mList.add(jsonObject0);
//                    Log.d("mList.jsonObject", jsonObject0.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("speciesId_error", cursor.getString(cursor.getColumnIndex("speciesId")));
                }
            } while (cursor.moveToNext());
        }
        else {
            Toast.makeText(SpeciesListActivity.this, "该品种不存在！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SpeciesListActivity.this, MainActivity.class);
            startActivity(intent);
        }

        if(fieldId != null){
            cursor = db.query("ExperimentField", null, "id=?", new String[]{fieldId}, null, null, null);
            if(cursor.moveToFirst()){
                expType = cursor.getString(cursor.getColumnIndex("expType"));
            }
            else{
                Toast.makeText(SpeciesListActivity.this, "Do not have the fieldId '" + fieldId + "' in ExperimentField", Toast.LENGTH_SHORT).show();
            }
        }

        cursor.close();
        db.close();
        dbHelper.close();
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
        SpeciesListAdapter adapter = new SpeciesListAdapter(this, this);

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

}
