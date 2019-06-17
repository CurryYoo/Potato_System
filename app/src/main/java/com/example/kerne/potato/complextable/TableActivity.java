package com.example.kerne.potato.complextable;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.R;
import com.example.kerne.potato.SpeciesClickActivity;
import com.example.kerne.potato.complextable.base.RefreshParams;
import com.example.kerne.potato.complextable.base.adapter.AbsCommonAdapter;
import com.example.kerne.potato.complextable.base.adapter.AbsViewHolder;
import com.example.kerne.potato.complextable.bean.OnlineSaleBean;
import com.example.kerne.potato.complextable.bean.TableModel;
import com.example.kerne.potato.complextable.utils.WeakHandler;
import com.example.kerne.potato.complextable.widget.SyncHorizontalScrollView;
import com.example.kerne.potato.complextable.widget.pullrefresh.AbPullToRefreshView;
import com.example.kerne.potato.temporarystorage.SaveDataActivity;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TableActivity extends AppCompatActivity {

    /**
     * 用于存放标题的id,与textview引用
     */
    private SparseArray<TextView> mTitleTvArray;
    //表格部分
    private TextView tv_table_title_left;
    private LinearLayout right_title_container;
    private ListView leftListView;
    private ListView rightListView;
    private AbsCommonAdapter<TableModel> mLeftAdapter, mRightAdapter;
    private SyncHorizontalScrollView titleHorScv;
    private SyncHorizontalScrollView contentHorScv;
    private AbPullToRefreshView pulltorefreshview;
    private int pageNo = 0;
    private WeakHandler mHandler = new WeakHandler();
    private Context mContext;

    private List<TextView> textViews = new ArrayList<>();

    public static final int STATUS_EDIT = 0;
    public static final int STATUS_READ = 1;
    public static final int STATUS_INIT = 2;
    public static final int STATUS_UPDATE = 3;
    private int status = STATUS_EDIT;
    private JSONObject fields_json;
    private String fieldId;
    private String expType;
    private JSONArray rows = new JSONArray();
    private JSONArray jsonArray = new JSONArray();

    private String[][] str = null;

    private int maxRows; //最大行数
    private int maxColumns; //最大列数

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_layout);

        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 9);
        db = dbHelper.getWritableDatabase();

        status = getIntent().getIntExtra("status", STATUS_EDIT);
        fieldId = getIntent().getStringExtra("fieldId");
        expType = getIntent().getStringExtra("expType");
        String farmlandId;
        farmlandId = getIntent().getStringExtra("farmlandId");

        try {
            fields_json = new JSONObject(getIntent().getStringExtra("fields_json"));
//            maxRows = fields_json.getInt("rows");
//            maxColumns = fields_json.getInt("columns");
//            ids = fields_json.getJSONArray("ids");

            Cursor cursor = db.query("ExperimentField", null, "farmlandId=? and expType=?",
                    new String[]{farmlandId, expType}, null, null, "moveX");
            maxColumns = cursor.getCount();
            if (maxColumns >0){
                cursor.moveToFirst();
                for (int i = 0; i < maxColumns; i++){
                    jsonArray.put(i, cursor.getString(cursor.getColumnIndex("id")));
                    int num = cursor.getInt(cursor.getColumnIndex("num"));
                    rows.put(i, num);
                    maxRows = (num > maxRows ? num : maxRows);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        str = new String[maxRows][maxColumns];

        String sql = "select ExperimentField.*, SpeciesList.* from ExperimentField, SpeciesList " +
                "where ExperimentField.id=SpeciesList.fieldId and ExperimentField.expType='" + expType +
                "' order by ExperimentField.moveX";
        Cursor cursor0 = db.rawQuery(sql, null);
        if (cursor0.moveToFirst()) {
            String fieldId = "";
            int x = 0, y = 0;
            int columns = 0;
            fieldId = cursor0.getString(cursor0.getColumnIndex("id"));
            do {
                if (!fieldId.equals(cursor0.getString(cursor0.getColumnIndex("id")))) {
                    columns++;
                    fieldId = cursor0.getString(cursor0.getColumnIndex("id"));
                }
                x = cursor0.getInt(cursor0.getColumnIndex("x")) + columns * 1 - 1; //从0开始
                y = cursor0.getInt(cursor0.getColumnIndex("y")) - 1; //从0开始
                str[y][x] = cursor0.getString(cursor0.getColumnIndex("speciesId"));
            } while (cursor0.moveToNext());
        }
        else {
//            List<ContentValues> contentValuesList = assembleData(str, STATUS_INIT);
//            for(int i = 0; i < maxColumns; i++){
//                db.insert("SpeciesSequence", null, contentValuesList.get(i));
//            }
//            Log.d("str_", "111");
//            Toast.makeText(TableActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
        }
        cursor0.close();

//        Cursor cursor = db.query("SpeciesSequence", null, "fieldId=?",
//                new String[]{fieldId}, null, null, "id");
//        if(cursor.getCount() > 0){
//            cursor.moveToFirst();
//            for(int i = 0; i < cursor.getCount(); i++){
//                int NumofRows = cursor.getInt(cursor.getColumnIndex("NumofRows"));
//                String ContentofColumn = cursor.getString(cursor.getColumnIndex("ContentofColumn"));
//                Log.d("str__", NumofRows + "," + ContentofColumn);
//                JSONObject jsonObject0 = null;
//                try {
//                    jsonObject0 = new JSONObject(ContentofColumn);
//                    for(int j = 0; j < NumofRows; j++){
//                        str[j][i] = jsonObject0.getString("row_" + j);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                cursor.moveToNext();
//            }
//            Log.d("str_init", str[0][0] + " | count=" + cursor.getCount());
//        }
//        else {
//            List<ContentValues> contentValuesList = assembleData(str, STATUS_INIT);
//            for(int i = 0; i < maxColumns; i++){
//                db.insert("SpeciesSequence", null, contentValuesList.get(i));
//            }
//            Log.d("str_", "111");
//            Toast.makeText(TableActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
//        }
//        cursor.close();

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        if (status == STATUS_UPDATE) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sequence, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
//            case R.id.species_list:
//                Intent intent= new Intent(TableActivity.this, SpeciesClickActivity.class);
//                intent.putExtra("fieldId", fieldId);
//                startActivity(intent);
//                break;
            case R.id.save_off_seq:
                //保存操作 sqlite
                List<ContentValues> contentValuesList = assembleData(str, STATUS_UPDATE);
                Log.d("contentvaluesList", contentValuesList.toString());
//                db.delete("SpeciesList", "fieldId=?", new String[]{fieldId});
                for (int i = 0; i < jsonArray.length(); i++) {
                    int rows_num = 0;
                    try {
                        rows_num = rows.getInt(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < rows_num; j++) {
                        db.update("SpeciesList", contentValuesList.get(j), "fieldId=? and x=? and y=?",
                                new String[]{contentValuesList.get(j).getAsString("fieldId"), contentValuesList.get(j).getAsString("x"), contentValuesList.get(j).getAsString("y")});
                    }
                }
//                for(int i = 0; i < maxRows; i++){
//                    db.update("SpeciesList", contentValuesList.get(i), "fieldId=? and x=? and y=?", new String[]{fieldId, contentValuesList.get(i).getAsString("x"), contentValuesList.get(i).getAsString("y")});
////                    db.insert("SpeciesList", null, contentValuesList.get(i));
//                }
                Log.d("str_content", contentValuesList.get(0).getAsString("ContentofColumn") + "");
                Toast.makeText(TableActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

//        startActivity(new Intent(this,AboutMeActivity.class));
        return super.onOptionsItemSelected(item);
    }

    //组装数据
    private List<ContentValues> assembleData(String[][] str, int status){
        Log.d("str_save", str[0][0] + "");
        List<ContentValues> contentValuesList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                int rows_num = rows.getInt(i);
                for (int j = 0; j < rows_num; j++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("fieldId", jsonArray.getString(i));
                    contentValues.put("speciesId", str[j][i]);
                    contentValues.put("x", i + 1);
                    contentValues.put("y", j + 1);
                    Log.d("contentValues", contentValues.toString());
                    contentValuesList.add(contentValues);
                    Log.d("contentvaluesList0", contentValuesList.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        for(int i = 0; i < maxColumns; i++){
//            for (int j = 0; j < maxRows; j++) {
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("fieldId", fieldId);
//                contentValues.put("speciesId", str[j][i]);
//                contentValues.put("x", i + 1);
//                contentValues.put("y", j + 1);
//                Log.d("contentValues", contentValues.toString());
//                contentValuesList.add(contentValues);
//                Log.d("contentvaluesList0", contentValuesList.toString());
//            }
//
////            contentValues.put("NumofRows", maxRows);
////            if(status == STATUS_INIT){
////                contentValues.put("ContentofColumn", "");
////            }
////            else if(status == STATUS_UPDATE){
////                //将一行的所有数据存为json格式
////                JSONObject jsonObject = new JSONObject();
////                for (int j = 0; j < maxRows; j++){
////                    try {
////                        if(str[j][i] != null){
////                            jsonObject.put("row_" + j, str[j][i]);
////                        }
////                        else {
////                            jsonObject.put("row_" + j, "");
////                        }
////
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////                contentValues.put("ContentofColumn", jsonObject.toString());
////            }
//        }

        return contentValuesList;
    }

    public void init() {
        mContext = getApplicationContext();
        findByid();
        setListener();
        setData();
    }

    public void findByid() {
        pulltorefreshview = (AbPullToRefreshView) findViewById(R.id.pulltorefreshview);
//        pulltorefreshview.setPullRefreshEnable(false);
        tv_table_title_left = (TextView) findViewById(R.id.tv_table_title_left);
        tv_table_title_left.setText(expType);
        tv_table_title_left.setTextColor(Color.RED);
        leftListView = (ListView) findViewById(R.id.left_container_listview);
        rightListView = (ListView) findViewById(R.id.right_container_listview);
        right_title_container = (LinearLayout) findViewById(R.id.right_title_container);

        //right_title_container.addView(textView);

        getLayoutInflater().inflate(R.layout.table_right_title, right_title_container);
        for (int i = 0; i < maxColumns; i++) {
            View view = right_title_container.getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }

        titleHorScv = (SyncHorizontalScrollView) findViewById(R.id.title_horsv);
        contentHorScv = (SyncHorizontalScrollView) findViewById(R.id.content_horsv);
        // 设置两个水平控件的联动
        titleHorScv.setScrollView(contentHorScv);
        contentHorScv.setScrollView(titleHorScv);
        findTitleTextViewIds();
        initTableView();
    }

    /**
     * 初始化标题的TextView的item引用
     */
    private void findTitleTextViewIds() {
        mTitleTvArray = new SparseArray<>();
        for (int i = 0; i <= 20; i++) {
            try {
                Field field = R.id.class.getField("tv_table_title_" + 0);
                int key = field.getInt(new R.id());
                TextView textView = (TextView) findViewById(key);
                mTitleTvArray.put(key, textView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initTableView() {
        mLeftAdapter = new AbsCommonAdapter<TableModel>(mContext, R.layout.table_left_item) {
            @Override
            public void convert(AbsViewHolder helper, TableModel item, int pos) {
                TextView tv_table_content_left = helper.getView(R.id.tv_table_content_item_left);
                tv_table_content_left.setText(item.getLeftTitle());

            }
        };

//        TextView textView = new TextView(TableActivity.this);
//        textView.setId(R.id.tv_test1);
//        textView.setText("test");
//        textView.setWidth(80);
//        textView.setHeight(40);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextSize(12);
//        textView.setPadding(4, 0, 4, 0);
//        textView.setMaxLines(2);
//        textView.setTextColor(Color.parseColor("#000000"));
//        textView.setBackgroundColor(Color.RED);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(80,40);
//        textView.setLayoutParams(lp);
//        View view1 = LayoutInflater.from(TableActivity.this).inflate(R.layout.table_right_item, null);
//        LinearLayout linearLayout = view1.findViewById(R.id.linear_right_item);
//        linearLayout.addView(textView);
////        view1.setId(R.layout.ttt);

        mRightAdapter = new AbsCommonAdapter<TableModel>(mContext, R.layout.table_right_item) {
            @Override
            public void convert(AbsViewHolder helper, TableModel item, final int pos) {

                final TextView tv_table_content_right_item0 = helper.getView(R.id.tv_table_content_right_item0);
                textViews.add(tv_table_content_right_item0);
                final TextView tv_table_content_right_item1 = helper.getView(R.id.tv_table_content_right_item1);
                textViews.add(tv_table_content_right_item1);
                TextView tv_table_content_right_item2 = helper.getView(R.id.tv_table_content_right_item2);
                textViews.add(tv_table_content_right_item2);
                TextView tv_table_content_right_item3 = helper.getView(R.id.tv_table_content_right_item3);
                textViews.add(tv_table_content_right_item3);
                TextView tv_table_content_right_item4 = helper.getView(R.id.tv_table_content_right_item4);
                textViews.add(tv_table_content_right_item4);
                TextView tv_table_content_right_item5 = helper.getView(R.id.tv_table_content_right_item5);
                textViews.add(tv_table_content_right_item5);
                TextView tv_table_content_right_item6 = helper.getView(R.id.tv_table_content_right_item6);
                textViews.add(tv_table_content_right_item6);
                TextView tv_table_content_right_item7 = helper.getView(R.id.tv_table_content_right_item7);
                textViews.add(tv_table_content_right_item7);
                TextView tv_table_content_right_item8 = helper.getView(R.id.tv_table_content_right_item8);
                textViews.add(tv_table_content_right_item8);
                TextView tv_table_content_right_item9 = helper.getView(R.id.tv_table_content_right_item9);
                textViews.add(tv_table_content_right_item9);
                TextView tv_table_content_right_item10 = helper.getView(R.id.tv_table_content_right_item10);
                textViews.add(tv_table_content_right_item10);
                TextView tv_table_content_right_item11 = helper.getView(R.id.tv_table_content_right_item11);
                textViews.add(tv_table_content_right_item11);
                TextView tv_table_content_right_item12 = helper.getView(R.id.tv_table_content_right_item12);
                textViews.add(tv_table_content_right_item12);
//                TextView tv_table_content_right_item13 = helper.getView(R.id.tv_table_content_right_item13);
//                TextView tv_table_content_right_item14 = helper.getView(R.id.tv_table_content_right_item14);

//                tv_table_content_right_item0.setText(item.getText0());
//                tv_table_content_right_item1.setText(item.getText1());
//                tv_table_content_right_item2.setText(item.getText2());
//                tv_table_content_right_item3.setText(item.getText3());
//                tv_table_content_right_item4.setText(item.getText4());
//                tv_table_content_right_item5.setText(item.getText5());
//                tv_table_content_right_item6.setText(item.getText6());
//                tv_table_content_right_item7.setText(item.getText7());
//                tv_table_content_right_item8.setText(item.getText8());
//                tv_table_content_right_item9.setText(item.getText9());
//                tv_table_content_right_item10.setText(item.getText10());
//                tv_table_content_right_item11.setText(item.getText11());
//                tv_table_content_right_item12.setText(item.getText12());
//                tv_table_content_right_item13.setText(item.getText13());
//                tv_table_content_right_item14.setText(item.getText14());

                for(int i = 0; i < maxColumns; i++){
                    final int finalI = i;
                    final TextView tv = textViews.get(i);
                    tv.setText(item.getText(i));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(status == STATUS_EDIT){
                                final EditText editText = new EditText(TableActivity.this);
                                editText.setGravity(Gravity.CENTER); //文字居中
                                AlertDialog.Builder inputDialog = new AlertDialog.Builder(TableActivity.this);
                                inputDialog.setTitle(finalI+1 + "-" + pos+1 + ":请输入品种编号").setView(editText);
                                inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tv.setText(editText.getText().toString());
                                        str[pos][finalI] = editText.getText().toString();
                                        Toast.makeText(TableActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                            }
                            else if(status == STATUS_READ) {
                                String blockId = null;
                                String speciesId = tv.getText().toString();
                                Cursor c = db.query("SpeciesList", null, "speciesId=? and fieldId=?",
                                        new String[]{speciesId, fieldId}, null, null, null);
                                if(c.moveToFirst()){
                                    blockId = c.getString(c.getColumnIndex("blockId"));
                                }

                                Intent intent = new Intent(TableActivity.this, SaveDataActivity.class);
                                intent.putExtra("speciesId", speciesId);
                                intent.putExtra("expType", expType);
                                if(blockId != null){
                                    intent.putExtra("blockId", blockId);
                                }
                                else {
                                    intent.putExtra("blockId", "test");
                                }

                                startActivity(intent);
                                Toast.makeText(TableActivity.this, "点击的品种编号：" + tv.getText(), Toast.LENGTH_SHORT).show();
                                c.close();
                            }
                            else {
                                Toast.makeText(TableActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

//                tv_table_content_right_item0.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(status == STATUS_EDIT){
//                            final EditText editText = new EditText(TableActivity.this);
//                            editText.setGravity(Gravity.CENTER); //文字居中
//                            AlertDialog.Builder inputDialog = new AlertDialog.Builder(TableActivity.this);
//                            inputDialog.setTitle("我是一个输入Dialog" + pos).setView(editText);
//                            inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    tv_table_content_right_item0.setText(editText.getText().toString());
//                                    str[pos][0] = editText.getText().toString();
//                                    Toast.makeText(TableActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            }).show();
//                        }
//                        else if(status == STATUS_READ) {
//                            Toast.makeText(TableActivity.this, "next--->" + tv_table_content_right_item0.getText(), Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(TableActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });

//                //部分行设置颜色凸显
//                item.setTextColor(tv_table_content_right_item0, item.getText0());
//                item.setTextColor(tv_table_content_right_item5, item.getText5());
//                item.setTextColor(tv_table_content_right_item10, item.getText10());
//                item.setTextColor(tv_table_content_right_item14, item.getText14());

                for (int i = 0; i < maxColumns; i++) {
                    View view = ((LinearLayout) helper.getConvertView()).getChildAt(i);
                    view.setVisibility(View.VISIBLE);

//                    Log.d("width" + i, view.getX() + "");
                }
                textViews.clear();
            }
        };
        leftListView.setAdapter(mLeftAdapter);
        rightListView.setAdapter(mRightAdapter);
    }


    public void setListener() {
//        pulltorefreshview.setOnHeaderRefreshListener(new AbPullToRefreshView.OnHeaderRefreshListener() {
//            @Override
//            public void onHeaderRefresh(AbPullToRefreshView view) {
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        pageNo = 0;
//                        doGetDatas(0, RefreshParams.REFRESH_DATA);
//                    }
//                }, 1000);
//            }
//        });
//        pulltorefreshview.setOnFooterLoadListener(new AbPullToRefreshView.OnFooterLoadListener() {
//            @Override
//            public void onFooterLoad(AbPullToRefreshView view) {
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        doGetDatas(pageNo, RefreshParams.LOAD_DATA);
//                    }
//                }, 1000);
//            }
//
//        });
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转界面
                Toast.makeText(TableActivity.this, "打开某条记录的单独详情", Toast.LENGTH_SHORT).show();
            }
        });
//        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d("TestItemClick", view.getMatrix().toString() + " || " + i + " || " + l);
////                view.setVisibility(View.INVISIBLE);
//            }
//        });
    }

    public void setData(){
        doGetDatas(0, RefreshParams.REFRESH_DATA);
    }

    //模拟网络请求
    public void doGetDatas(int pageno, int state) {
        List<OnlineSaleBean> onlineSaleBeanList = new ArrayList<>();
        for(int i = 0 + pageno * maxRows; i < maxRows * (pageno + 1); i++){
            onlineSaleBeanList.add(new OnlineSaleBean("品种行"+ (i + 1)));
        }
//        if(state == RefreshParams.REFRESH_DATA){
//            pulltorefreshview.onHeaderRefreshFinish();
//        }else{
//            pulltorefreshview.onFooterLoadFinish();
//        }
        setDatas(onlineSaleBeanList, state);
    }

    private void setDatas(List<OnlineSaleBean> onlineSaleBeanList, int type) {
        if (onlineSaleBeanList.size() > 0) {
            List<TableModel> mDatas = new ArrayList<>();
            for (int i = 0; i < onlineSaleBeanList.size(); i++) {
                OnlineSaleBean onlineSaleBean = onlineSaleBeanList.get(i);
                TableModel tableMode = new TableModel();
                tableMode.setOrgCode(onlineSaleBean.getOrgCode());
                tableMode.setLeftTitle(onlineSaleBean.getCompanyName());

                for(int j = 0; j < maxColumns; j++){
                    if(str[i][j] == null){
                        tableMode.setText("", j);
                    }
                    else {
                        tableMode.setText(str[i][j], j);
                    }
                }

//                tableMode.setText0(str[i][0] + "");//列0内容
//                tableMode.setText1(str[i][1] + "");//列1内容
//                tableMode.setText2(str[i][2] + "");//列2内容
//                tableMode.setText3(onlineSaleBean.getSaleAllOneNow() + "");
//                tableMode.setText4(onlineSaleBean.getSaleAllLast() + "");
//                tableMode.setText5(onlineSaleBean.getSaleAllOneNowLast() + "");//
//                tableMode.setText6(onlineSaleBean.getSaleAllRate() + "");//
//                tableMode.setText7(onlineSaleBean.getSaleAllOneNowRate() + "");//
//                tableMode.setText8(onlineSaleBean.getRetailSale() + "");//
//                tableMode.setText9(onlineSaleBean.getRetailSaleOneNow() + "");//
//                tableMode.setText10(onlineSaleBean.getRetailSaleLast() + "");//
//                tableMode.setText11(onlineSaleBean.getRetailSaleOneNowLast() + "");//
//                tableMode.setText12(onlineSaleBean.getRetailSaleRate() + "");//
//                tableMode.setText13(onlineSaleBean.getRetailSaleOneNowRate() + "");//
//                tableMode.setText14(onlineSaleBean.getOnlineSale() + "");//
                mDatas.add(tableMode);
            }
            boolean isMore;
            if (type == RefreshParams.LOAD_DATA) {
                isMore = true;
            } else {
                isMore = false;
            }
            mLeftAdapter.addData(mDatas, isMore);
            mRightAdapter.addData(mDatas, isMore);
            //加载数据成功，增加页数
            pageNo++;
//            if (mDatas.size() < 20) {
//                pulltorefreshview.setLoadMoreEnable(false);
//            }
            mDatas.clear();
        } else {
            //数据为null
            if (type == RefreshParams.REFRESH_DATA) {
                mLeftAdapter.clearData(true);
                mRightAdapter.clearData(true);
                //显示数据为空的视图
                //                mEmpty.setShowErrorAndPic(getString(R.string.empty_null), 0);
            } else if (type == RefreshParams.LOAD_DATA) {
                Toast.makeText(mContext, "请求json失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
