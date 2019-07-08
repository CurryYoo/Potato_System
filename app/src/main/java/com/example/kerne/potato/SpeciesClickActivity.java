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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

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

public class SpeciesClickActivity extends AppCompatActivity implements SpeciesClickAdapter.OnItemClickListener {

    private static final String TAG = SpeciesClickActivity.class.getSimpleName();
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

    private String fieldId;
    public static String expType;

    private String userRole;
    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    Intent intent = new Intent(SpeciesClickActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.species_click_activity);
        ButterKnife.bind(this);

        initToolBar();
        Intent intent = getIntent();
        fieldId = intent.getStringExtra("fieldId");
        expType = intent.getStringExtra("expType");
//        userRole = intent.getStringExtra("userRole");

        initData();

        //initView();
    }

    private void initToolBar() {
        titleText.setText("");
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.ic_menu_home);
        rightTwoButton.setBackgroundResource(R.drawable.ic_menu_map);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
        rightOneLayout.setOnClickListener(toolBarOnClickListener);
    }

    private void initData() {
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("SpeciesList", null, "fieldId=?", new String[]{fieldId}, null, null, null);
        if (cursor.moveToFirst()) {
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
        } else {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    userRole = data.getStringExtra("userRole");
//                    Log.d("userRole", userRole);
                }
                break;
            default:
                break;
        }
    }

}
