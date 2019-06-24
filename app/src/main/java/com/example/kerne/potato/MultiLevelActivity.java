package com.example.kerne.potato;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kerne.potato.complextable.widget.multilevellist.TreeAdapter;
import com.example.kerne.potato.complextable.widget.multilevellist.TreePoint;
import com.example.kerne.potato.complextable.widget.multilevellist.TreeUtils;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MultiLevelActivity extends AppCompatActivity {

    private TreeAdapter adapter;
    private ListView listView;
    private EditText et_filter;
    private List<TreePoint> pointList = new ArrayList<>();
    private HashMap<String, TreePoint> pointMap = new HashMap<>();

    private List<JSONObject> mBigFarmList = new ArrayList<>();
    private List<JSONObject> mFarmList = new ArrayList<>();
    private List<JSONObject> mfiledList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_mutlilevel);
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addListener();
    }

    public void init() throws JSONException {
        adapter = new TreeAdapter(this, pointList, pointMap);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        et_filter = findViewById(R.id.et_filter);
        getData();
        initData();
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
//            pointList.add(new TreePoint("" + id, "分类" + i, "" + parentId, "0", i));
            pointList.add(new TreePoint("" + id, mBigFarmList.get(i).getString("name"), "" + parentId, "0", i));
            for (int j = 0; j < mFarmList.size(); j++) {
                if (j == 0) {
                    parentId2 = id;
                }
                id++;
                if (mFarmList.get(j).getString("bigfarmId").equals(mBigFarmList.get(i).getString("bigfarmId"))) {
                    TreePoint treePoint=new TreePoint("" + id, mFarmList.get(i).getString("name"), "" + parentId2, "1", j);
                    treePoint.setFarmlandID(mFarmList.get(j).getString("farmlandId"));
                    treePoint.setLength(mFarmList.get(j).getInt("length"));
                    treePoint.setWidth(mFarmList.get(j).getInt("width"));
                    treePoint.setType(mFarmList.get(j).getString("type"));
                    pointList.add(treePoint);
                }
//                for (int k = 1; k < 5; k++) {
//                    if (k == 1) {
//                        parentId3 = id;
//                    }
//                    id++;
//                    pointList.add(new TreePoint("" + id, "分类" + i + "_" + j + "_" + k, "" + parentId3, "0", k));
//                    for (int l = 1; l < 5; l++) {
//                        if (l == 1) {
//                            parentId4 = id;
//                        }
//                        id++;
//                        pointList.add(new TreePoint("" + id, "分类" + i + "_" + j + "_" + k + "_" + l, "" + parentId4, "1", l));
//                    }
//                }
            }
        }
        //打乱集合中的数据
        Collections.shuffle(pointList);
        //对集合中的数据重新排序
        updateData();
    }


    public void addListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.onItemClick(position);
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

    //对数据排序 深度优先
    private void updateData() {
        for (TreePoint treePoint : pointList) {
            pointMap.put(treePoint.getID(), treePoint);
        }
        Collections.sort(pointList, new Comparator<TreePoint>() {
            @Override
            public int compare(TreePoint lhs, TreePoint rhs) {
                int llevel = TreeUtils.getLevel(lhs, pointMap);
                int rlevel = TreeUtils.getLevel(rhs, pointMap);
                if (llevel == rlevel) {
                    if (lhs.getPARENTID().equals(rhs.getPARENTID())) {  //左边小
                        return lhs.getDISPLAY_ORDER() > rhs.getDISPLAY_ORDER() ? 1 : -1;
                    } else {  //如果父辈id不相等
                        //同一级别，不同父辈
                        TreePoint ltreePoint = TreeUtils.getTreePoint(lhs.getPARENTID(), pointMap);
                        TreePoint rtreePoint = TreeUtils.getTreePoint(rhs.getPARENTID(), pointMap);
                        return compare(ltreePoint, rtreePoint);  //父辈
                    }
                } else {  //不同级别
                    if (llevel > rlevel) {   //左边级别大       左边小
                        if (lhs.getPARENTID().equals(rhs.getID())) {
                            return 1;
                        } else {
                            TreePoint lreasonTreePoint = TreeUtils.getTreePoint(lhs.getPARENTID(), pointMap);
                            return compare(lreasonTreePoint, rhs);
                        }
                    } else {   //右边级别大   右边小
                        if (rhs.getPARENTID().equals(lhs.getID())) {
                            return -1;
                        }
                        TreePoint rreasonTreePoint = TreeUtils.getTreePoint(rhs.getPARENTID(), pointMap);
                        return compare(lhs, rreasonTreePoint);
                    }
                }
            }
        });
        adapter.notifyDataSetChanged();
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
