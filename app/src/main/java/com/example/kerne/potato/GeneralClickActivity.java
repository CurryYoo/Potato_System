package com.example.kerne.potato;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.example.kerne.potato.temporarystorage.Util;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 点击对应的 Activity
 *
 * Created by Xie.
 */

public class GeneralClickActivity extends AppCompatActivity implements GeneralClickAdapter.OnItemClickListener {

    private static final String TAG = GeneralClickActivity.class.getSimpleName();

    private List<JSONObject> mList = new ArrayList<>();

    private String bigfarmId = null;
    private String img;
    private String uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.general_click_activity);

        bigfarmId = getIntent().getStringExtra("bigfarmId");
        img = getIntent().getStringExtra("img");
        uri = getIntent().getStringExtra("uri");
        initData();

        //initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
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
            case R.id.back_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.map_bigfarm:
                Log.d("uri", Uri.parse(uri).toString());
                Util.watchOnlineLargePhoto(GeneralClickActivity.this, Uri.parse(uri));
                break;
            default:
        }
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.search:
//                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(this).builder()
//                        .setTitle("请输入名称：")
//                        .setEditText("");
//                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //showMsg(myAlertInputDialog.getResult());
//                        name = myAlertInputDialog.getResult();
//                        mList.clear();
//                        initData();
//                        myAlertInputDialog.dismiss();
//                    }
//                }).setNegativeButton("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //showMsg("取消");
//                        myAlertInputDialog.dismiss();
//                    }
//                });
//                myAlertInputDialog.show();
//                Toast.makeText(this,"Search item " + name, Toast.LENGTH_SHORT).show();
//                break;
//            case android.R.id.home:
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            default:
//        }
//        return true;
//    }

    private void initData() {
        //获取服务器中数据
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //获取棚外区域
        Cursor cursor = db.query("FarmList", null, "bigfarmId=?", new String[]{bigfarmId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("farmlandId", cursor.getString(cursor.getColumnIndex("farmlandId")));
                    jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject0.put("length", cursor.getInt(cursor.getColumnIndex("length")));
                    jsonObject0.put("width", cursor.getInt(cursor.getColumnIndex("width")));
                    jsonObject0.put("type", cursor.getString(cursor.getColumnIndex("type")));
                    jsonObject0.put("bigfarmId", cursor.getString(cursor.getColumnIndex("bigfarmId")));

//                    jsonObject0.put("userRole", userRole);
                    mList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("farmlandId_error", cursor.getString(cursor.getColumnIndex("farmlandId")));
                }
            } while (cursor.moveToNext());
        }
        else {
            Toast.makeText(GeneralClickActivity.this, "FarmList null", Toast.LENGTH_SHORT).show();
        }

        //获取大棚区域
        cursor = db.query("ExperimentField", null, "farmlandId=?", new String[]{bigfarmId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("fieldId", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject0.put("expType", cursor.getString(cursor.getColumnIndex("expType")));
                    jsonObject0.put("num", cursor.getInt(cursor.getColumnIndex("num")));
                    jsonObject0.put("farmlandId", cursor.getString(cursor.getColumnIndex("farmlandId")));
                    jsonObject0.put("rows", cursor.getInt(cursor.getColumnIndex("rows")));
                    jsonObject0.put("type", "greenhouse");
//                    jsonObject0.put("userRole", userRole);
                    Log.d("pengnei1", jsonObject0.toString());
                    mList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("greenhouse_error", cursor.getString(cursor.getColumnIndex("farmlandId")));
                }
            } while (cursor.moveToNext());
        }
        else {
            Toast.makeText(GeneralClickActivity.this, "FarmList null", Toast.LENGTH_SHORT).show();
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
        GeneralClickAdapter adapter = new GeneralClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(final String content) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(GeneralClickActivity.this);
//        //builder.setIcon(R.drawable.ic_launcher_background);
//        builder.setTitle("选择一个操作");
//        //    指定下拉列表的显示数据
//        final String[] options = {"进入", "删除"};
//        //    设置一个下拉的列表选择项
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(which == 0){
//                    Intent intent = new Intent(GeneralClickActivity.this, GeneralActivity.class);
//                    intent.putExtra("option", content);
//                    startActivity(intent);
//                }
//                else{
//                    Toast.makeText(GeneralClickActivity.this, "删除 " + content, Toast.LENGTH_SHORT).show();
//                }
//                //Toast.makeText(GeneralClickActivity.this, "选择的城市为：" + options[which], Toast.LENGTH_SHORT).show();
//            }
//        });
//        builder.show();
//
//        //Toast.makeText(this, "你点击的是：" + content, Toast.LENGTH_SHORT).show();
    }



}
