package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.complextable.widget.multilevellist.TreeAdapter;
import com.example.kerne.potato.complextable.widget.multilevellist.TreePoint;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiLevelActivity extends AppCompatActivity {

    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    private TreeAdapter adapter;
    private ListView listView;
    private EditText et_filter;
    private List<TreePoint> pointList = new ArrayList<>();
    private HashMap<String, TreePoint> pointMap = new HashMap<>();

    private List<JSONObject> mBigFarmList = new ArrayList<>();
    private List<JSONObject> mFarmList = new ArrayList<>();
    private List<JSONObject> mFieldList = new ArrayList<>();
    private List<JSONObject> mSpeciesList = new ArrayList<>();
    private static final int COMPLETED = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) adapter.notifyDataSetChanged();
        }
    };

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
        setContentView(R.layout.activity_mutlilevel);
        ButterKnife.bind(this);
        init();
        addListener();
    }

    private void initToolBar() {
        titleText.setText("实验田目录树");
        leftOneButton.setBackgroundResource(R.drawable.left_back);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
    }

    public void init() {
        adapter = new TreeAdapter(this, pointList, pointMap);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        et_filter = findViewById(R.id.et_filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                getData();
                try {
                    initData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = COMPLETED;
                handler.sendMessage(msg);
                Looper.loop();
            }
        }).start();
    }

    /*获取数据*/
    private void getData() {
        //获取服务器中数据
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //获取大田信息
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

//                    jsonObject0.put("userRole", userRole);
                    mBigFarmList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("bigfarmId_error", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                }
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(MultiLevelActivity.this, "BigfarmList null", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

        Cursor cursor2 = db.query("FarmList", null, null, null, null, null, null);
//        Cursor cursor = db.query("ExperimentField", null, "farmlandId=?", new String[]{farmlandId}, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                JSONObject jsonObject2 = new JSONObject();
                try {
                    jsonObject2.put("farmlandId", cursor2.getString(cursor2.getColumnIndex("farmlandId")));
                    jsonObject2.put("name", cursor2.getString(cursor2.getColumnIndex("name")));
                    jsonObject2.put("length", cursor2.getInt(cursor2.getColumnIndex("length")));
                    jsonObject2.put("width", cursor2.getInt(cursor2.getColumnIndex("width")));
                    jsonObject2.put("type", cursor2.getString(cursor2.getColumnIndex("type")));
                    jsonObject2.put("bigfarmId", cursor2.getString(cursor2.getColumnIndex("bigfarmId")));

                    mFarmList.add(jsonObject2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("fieldId_error", cursor.getString(cursor2.getColumnIndex("id")));
                }
            } while (cursor2.moveToNext());
        } else {
            Toast.makeText(MultiLevelActivity.this, "ExperimentField null", Toast.LENGTH_SHORT).show();
        }
        cursor2.close();

        Cursor cursor3 = db.query("ExperimentField", null, null, null, null, null, null);
        if (cursor3.moveToFirst()) {
            do {
                JSONObject jsonObject3 = new JSONObject();
                try {
                    jsonObject3.put("id", cursor3.getString(cursor3.getColumnIndex("id")));
                    jsonObject3.put("name", cursor3.getString(cursor3.getColumnIndex("name")));
                    jsonObject3.put("deleted", cursor3.getInt(cursor3.getColumnIndex("deleted")));
                    jsonObject3.put("expType", cursor3.getString(cursor3.getColumnIndex("expType")));
                    jsonObject3.put("moveX", cursor3.getInt(cursor3.getColumnIndex("moveX")));
                    jsonObject3.put("moveY", cursor3.getInt(cursor3.getColumnIndex("moveY")));
                    jsonObject3.put("moveX1", cursor3.getInt(cursor3.getColumnIndex("moveX1")));
                    jsonObject3.put("moveY1", cursor3.getInt(cursor3.getColumnIndex("moveY1")));
                    jsonObject3.put("num", cursor3.getString(cursor3.getColumnIndex("num")));
                    jsonObject3.put("color", cursor3.getString(cursor3.getColumnIndex("color")));
                    jsonObject3.put("farmlandId", cursor3.getString(cursor3.getColumnIndex("farmlandId")));
                    jsonObject3.put("rows", cursor3.getInt(cursor3.getColumnIndex("rows")));
                    jsonObject3.put("speciesList", cursor3.getString(cursor3.getColumnIndex("speciesList")));

                    mFieldList.add(jsonObject3);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("FieldId_error", cursor.getString(cursor3.getColumnIndex("id")));
                }
            } while (cursor3.moveToNext());
        } else {
            Toast.makeText(MultiLevelActivity.this, "ExperimentField null", Toast.LENGTH_SHORT).show();
        }
        cursor3.close();

        Cursor cursor4 = db.query("SpeciesList", null, null, null, null, null, null);
        if (cursor4.moveToFirst()) {
            do {
                JSONObject jsonObject4 = new JSONObject();
                try {
                    jsonObject4.put("blockId", cursor4.getString(cursor4.getColumnIndex("blockId")));
                    jsonObject4.put("fieldId", cursor4.getString(cursor4.getColumnIndex("fieldId")));
                    jsonObject4.put("speciesId", cursor4.getString(cursor4.getColumnIndex("speciesId")));
                    jsonObject4.put("x", cursor4.getInt(cursor4.getColumnIndex("x")));
                    jsonObject4.put("y", cursor4.getInt(cursor4.getColumnIndex("y")));

                    mSpeciesList.add(jsonObject4);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("SpeciesId_error", cursor.getString(cursor4.getColumnIndex("id")));
                }
            } while (cursor4.moveToNext());
        } else {
            Toast.makeText(MultiLevelActivity.this, "ExperimentField null", Toast.LENGTH_SHORT).show();
        }
        cursor4.close();

        db.close();
        dbHelper.close();
    }

    //初始化数据
    //数据特点：TreePoint 之间的关系特点   id是任意唯一的。    如果为根节点 PARENTID  为"0"   如果没有子节点，也就是本身是叶子节点的时候ISLEAF = "1"
    //  DISPLAY_ORDER 是同一级中 显示的顺序
    //如果需要做选中 单选或者多选，只需要给TreePoint增加一个选中的属性，在ReasonAdapter中处理就好了
    private void initData() throws JSONException {
        pointList.clear();
        int id = 1000;
        int parentId = 0;
        int parentId2 = 0;
        int parentId3 = 0;
        int parentId4 = 0;
        for (int i = 0; i < mBigFarmList.size(); i++) {
            id++;
            pointList.add(new TreePoint("" + id, "" + mBigFarmList.get(i).getString("name"), "" + parentId, "0", i));
            int order_i = 1;
            for (int j = 0; j < mFarmList.size(); j++) {
                if (j == 0) {
                    parentId2 = id;
                }
                if (mFarmList.get(j).getString("bigfarmId").equals(mBigFarmList.get(i).getString("bigfarmId"))) {
                    id++;
                    pointList.add(new TreePoint("" + id, "" + mFarmList.get(j).getString("name"), "" + parentId2, "0", order_i++));
                    int order_j = 1;
                    for (int k = 0; k < mFieldList.size(); k++) {
                        if (k == 0) {
                            parentId3 = id;
                        }
                        if (mFieldList.get(k).getString("farmlandId").equals(mFarmList.get(j).getString("farmlandId"))) {
                            id++;
                            pointList.add(new TreePoint("" + id, "" + mFieldList.get(k).getString("expType"), "" + parentId3, "0", order_j++));
                            int order_k = 1;
                            for (int l = 0; l < mSpeciesList.size(); l++) {
                                if (l == 0) {
                                    parentId4 = id;
                                }
                                if (mSpeciesList.get(l).getString("fieldId").equals(mFieldList.get(k).getString("id"))) {
                                    id++;
                                    TreePoint treePoint = new TreePoint("" + id, "" + mSpeciesList.get(l).getString("speciesId"), "" + parentId4, "1", order_k++);
                                    mSpeciesList.get(l).put("expType", mFieldList.get(k).getString("expType"));
                                    treePoint.setJsonObject(mSpeciesList.get(l));
                                    pointList.add(treePoint);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (TreePoint treePoint : pointList) {
            pointMap.put(treePoint.getID(), treePoint);
        }
    }


    public void addListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    adapter.onItemClick(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        et_filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchAdapter(s);
            }
        });
    }

    private void searchAdapter(Editable s) {
        adapter.setKeyword(s.toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
        }
        return true;
    }
}
