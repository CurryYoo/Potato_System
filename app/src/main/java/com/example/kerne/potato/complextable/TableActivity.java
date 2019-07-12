package com.example.kerne.potato.complextable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.MainActivity;
import com.example.kerne.potato.R;
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
import com.hb.dialog.myDialog.MyAlertInputDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kerne.potato.Util.ShowKeyBoard.delayShowSoftKeyBoard;

public class TableActivity extends AppCompatActivity {

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
    @BindView(R.id.table_description)
    EditText tableDescription;
    @BindView(R.id.table_info)
    TextView tableInfo;
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

    private List<String> speciesNames = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();

    public static final int STATUS_EDIT = 0;
    public static final int STATUS_READ = 1;
    public static final int STATUS_INIT = 2;
    public static final int STATUS_UPDATE = 3;
    private int status = STATUS_READ;
    private JSONObject fields_json;
    private String fieldId;
    private String expType;
    private String type;
    private String bigFarmName;
    private String farmName;
    private String farmlandId;
    private int year;
    private String description;

    private int column;
    private JSONArray rows = new JSONArray();
    private JSONArray fieldArray = new JSONArray();

    private int column_num;
    private int row_num;

    private String[][] str = null;

    private int maxRows; //最大行数
    private int maxColumns; //最大列数

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase db;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private MenuItem editItem;

    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    Intent intent = new Intent(TableActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.right_two_layout:
                    if (status == STATUS_READ) {
                        status = STATUS_EDIT;
                        rightTwoButton.setBackgroundResource(R.drawable.ic_menu_no_save);
                        titleText.setText("品种规划");
                        tableDescription.setEnabled(true);
                        Toast.makeText(TableActivity.this, "进入品种规划", Toast.LENGTH_SHORT).show();
                    } else {

                        //保存操作 sqlite
                        List<ContentValues> contentValuesList = assembleData(str);

//                db.delete("SpeciesList", "fieldId=?", new String[]{fieldId});
                        try {
                            //更新备注description
                            Log.d("fieldArray", fieldArray.toString());
                            description = tableDescription.getText().toString();
                            Log.d("description", description);
                            for (int k = 0; k < fieldArray.length(); k++) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("description", description);

                                db.update("ExperimentField", contentValues, "id=?", new String[]{fieldArray.getString(k)});
                            }
                            //更新品种块block
                            int lastRows = 0; //把之前列的行数叠加起来，确定contentvalueslist中的位置
                            for (int i = 0; i < fieldArray.length() * column; i++) {

                                int rows_num = rows.getInt(i / column);
                                if (i > 0) {
                                    lastRows += rows.getInt((i - 1) / column);
                                }

                                for (int j = 0; j < rows_num; j++) {
                                    int id = lastRows + j;
                                    db.update("SpeciesList", contentValuesList.get(id), "fieldId=? and x=? and y=?",
                                            new String[]{contentValuesList.get(id).getAsString("fieldId"), contentValuesList.get(id).getAsString("x"), contentValuesList.get(id).getAsString("y")});
//                                    Log.d("fieldId,x,y", contentValuesList.get(id).getAsString("fieldId") + "," + contentValuesList.get(id).getAsString("x") + "," + contentValuesList.get(id).getAsString("y"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                for(int i = 0; i < maxRows; i++){
//                    db.update("SpeciesList", contentValuesList.get(i), "fieldId=? and x=? and y=?", new String[]{fieldId, contentValuesList.get(i).getAsString("x"), contentValuesList.get(i).getAsString("y")});
////                    db.insert("SpeciesList", null, contentValuesList.get(i));
//                }
                        Log.d("str_content", contentValuesList.get(0).getAsString("ContentofColumn") + "");
                        status = STATUS_READ;
                        rightTwoButton.setBackgroundResource(R.drawable.ic_menu_plan);
                        titleText.setText("品种种植");
                        tableDescription.setEnabled(false);
                        Toast.makeText(TableActivity.this, "保存成功，退出品种规划", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("update_location_data", true);
                        editor.apply();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        ButterKnife.bind(this);
        sp = getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();

        initToolBar();

        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        db = dbHelper.getWritableDatabase();

        fieldId = getIntent().getStringExtra("fieldId");
        expType = getIntent().getStringExtra("expType");
        type = getIntent().getStringExtra("type");
        bigFarmName = getIntent().getStringExtra("bigFarmName");
        farmName = getIntent().getStringExtra("farmName");
        year = getIntent().getIntExtra("year", 1970);
        farmlandId = getIntent().getStringExtra("farmlandId");
        Log.d("farmlandId", farmlandId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (type.equals("common")) {
                    try {
                        Cursor cursor = db.query("ExperimentField", null, "farmlandId=? and expType=?",
                                new String[]{farmlandId, expType}, null, null, "moveX");
                        maxColumns = cursor.getCount();
                        if (maxColumns > 0) {
                            cursor.moveToFirst();
                            for (int i = 0; i < maxColumns; i++) {
                                fieldArray.put(i, cursor.getString(cursor.getColumnIndex("id")));
                                int num = cursor.getInt(cursor.getColumnIndex("num"));
                                rows.put(i, num);
                                maxRows = (num > maxRows ? num : maxRows);
                                column = 1;
                                cursor.moveToNext();
                            }
                        }
                        cursor.close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    str = new String[maxRows][maxColumns];
                } else {
                    column = getIntent().getIntExtra("rows", 2);
                    maxRows = getIntent().getIntExtra("num", 0) / column;
                    maxColumns = getIntent().getIntExtra("rows", 0);
                    Log.d("num,rows", maxRows + "," + maxColumns);
                    str = new String[maxRows][maxColumns];

                    try {
                        fieldArray.put(0, fieldId);
                        rows.put(0, maxRows);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Cursor cursor = db.query("LocalSpecies", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        speciesNames.add(cursor.getString(cursor.getColumnIndex("name")));
                    } while (cursor.moveToNext());
                } else {
                    Toast.makeText(TableActivity.this, "species null", Toast.LENGTH_SHORT).show();
                }
                cursor.close();

                String sql;
                if (type.equals("common")) {
                    sql = "select ExperimentField.*, SpeciesList.* from ExperimentField, SpeciesList " +
                            "where ExperimentField.id=SpeciesList.fieldId and ExperimentField.expType='" + expType +
                            "' and ExperimentField.farmlandId='" + farmlandId + "' order by ExperimentField.moveX";
                } else {
                    sql = "select ExperimentField.*, SpeciesList.* from ExperimentField, SpeciesList " +
                            "where ExperimentField.id=SpeciesList.fieldId and ExperimentField.expType='" + expType +
                            "' and ExperimentField.id='" + fieldId + "' order by ExperimentField.moveX";
                }

                Cursor cursor0 = db.rawQuery(sql, null);
                if (cursor0.moveToFirst()) {
                    String fieldId = "";
                    int x = 0, y = 0;
                    int columns = 0;
                    description = cursor0.getString(cursor0.getColumnIndex("description"));
                    fieldId = cursor0.getString(cursor0.getColumnIndex("id"));
                    do {
                        if (!fieldId.equals(cursor0.getString(cursor0.getColumnIndex("id")))) {
                            Log.d("columns,fieldId,id", columns + "," + fieldId + "," + cursor0.getString(cursor0.getColumnIndex("id")));
                            columns++;
                            fieldId = cursor0.getString(cursor0.getColumnIndex("id"));
                        }
                        Log.d("x,y", x + "," + y + "," + columns + "," + column);
                        x = cursor0.getInt(cursor0.getColumnIndex("x")) + columns * column - 1; //从0开始
                        y = cursor0.getInt(cursor0.getColumnIndex("y")) - 1; //从0开始

                        str[y][x] = cursor0.getString(cursor0.getColumnIndex("speciesId"));
//                Log.d("x,y,str", x + "," + y + "," + str[y][x]);
                    } while (cursor0.moveToNext());
                } else {
//            List<ContentValues> contentValuesList = assembleData(str, STATUS_INIT);
//            for(int i = 0; i < maxColumns; i++){
//                db.insert("SpeciesSequence", null, contentValuesList.get(i));
//            }
//            Log.d("str_", "111");
//            Toast.makeText(TableActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                }
                cursor0.close();
            }
        }).start();

        Message msg = new Message();
        msg.what = 1;
        uiHandler.sendMessage(msg);

//        init();
    }

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i("TableActivity", "init data ok");
                    init();
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initToolBar() {
        titleText.setText("品种种植");
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.ic_menu_home);
        rightTwoButton.setBackgroundResource(R.drawable.ic_menu_plan);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
        rightOneLayout.setOnClickListener(toolBarOnClickListener);
        rightTwoLayout.setOnClickListener(toolBarOnClickListener);
    }

    //组装数据
    private List<ContentValues> assembleData(String[][] str) {
        Log.d("str_save", str[0][0] + "");
        List<ContentValues> contentValuesList = new ArrayList<>();
        try {
            for (int i = 0; i < fieldArray.length() * column; i++) {
                int rows_num = rows.getInt(i / column);
                for (int j = 0; j < rows_num; j++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("fieldId", fieldArray.getString(i / column));
                    contentValues.put("speciesId", str[j][i]);

                    contentValues.put("x", i % column + 1);
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
        tableInfo.setText("试验基地：" + bigFarmName+"   试验田：" + farmName+"   年份：" + year);
        tableDescription.setText(description);
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

                for (int i = 0; i < maxColumns; i++) {
                    final int finalI = i;
                    final TextView tv = textViews.get(i);
                    tv.setText(item.getText(i));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (status == STATUS_EDIT) {
                                int x = pos + 1, y = finalI + 1;
                                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(TableActivity.this).builder()
                                        .setTitle(x + "-" + y + ":请输入品种编号")
                                        .setEditText("");
                                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String species = myAlertInputDialog.getResult();
                                        boolean isExist = false;
                                        for (int j = 0; j < speciesNames.size(); j++) {
                                            if (species.equals(speciesNames.get(j))) {
                                                isExist = true;
                                            }
                                        }
                                        if (isExist) {
                                            tv.setText(species);
                                            str[pos][finalI] = species;
                                            Toast.makeText(TableActivity.this, species, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(TableActivity.this, "该品种不存在", Toast.LENGTH_SHORT).show();
                                        }
                                        myAlertInputDialog.dismiss();
                                    }
                                }).setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //showMsg("取消");
                                        myAlertInputDialog.dismiss();
                                    }
                                });
                                myAlertInputDialog.show();
                                //弹出软键盘
                                delayShowSoftKeyBoard(myAlertInputDialog.getContentEditText());

//                                AlertDialog.Builder inputDialog = new AlertDialog.Builder(TableActivity.this);
//                                inputDialog.setTitle(finalI+1 + "-" + pos+1 + ":请输入品种编号").setView(editText);
//                                inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String species = editText.getText().toString();
//                                        boolean isExist = false;
//                                        for (int j = 0; j < speciesNames.size(); j++) {
//                                            if (species.equals(speciesNames.get(j))) {
//                                                isExist = true;
//                                            }
//                                        }
//                                        if (isExist) {
//                                            tv.setText(species);
//                                            str[pos][finalI] = species;
//                                            Toast.makeText(TableActivity.this, species, Toast.LENGTH_SHORT).show();
//                                        }
//                                        else {
//                                            Toast.makeText(TableActivity.this, "该品种不存在", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }).show();
                            } else if (status == STATUS_READ) {
                                String blockId = null;
                                String speciesId = tv.getText().toString();
                                if (speciesId.equals("")) {
                                    Toast.makeText(TableActivity.this, "请填写品种名称", Toast.LENGTH_SHORT).show();
                                } else {
                                    Cursor c = db.query("SpeciesList", null, "speciesId=? and fieldId=?",
                                            new String[]{speciesId, fieldId}, null, null, null);
                                    if (c.moveToFirst()) {
                                        blockId = c.getString(c.getColumnIndex("blockId"));
                                    }

                                    Intent intent = new Intent(TableActivity.this, SaveDataActivity.class);
                                    intent.putExtra("speciesId", speciesId);
                                    intent.putExtra("expType", expType);
                                    if (blockId != null) {
                                        intent.putExtra("blockId", blockId);
                                    } else {
                                        intent.putExtra("blockId", "test");
                                    }

                                    startActivity(intent);
                                    Toast.makeText(TableActivity.this, "点击的品种编号：" + tv.getText(), Toast.LENGTH_SHORT).show();
                                    c.close();
                                }

                            } else {
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

    public void setData() {
        doGetDatas(0, RefreshParams.REFRESH_DATA);
    }

    //模拟网络请求
    public void doGetDatas(int pageno, int state) {
        List<OnlineSaleBean> onlineSaleBeanList = new ArrayList<>();
        for (int i = 0 + pageno * maxRows; i < maxRows * (pageno + 1); i++) {
            onlineSaleBeanList.add(new OnlineSaleBean("品种行" + (i + 1)));
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

                for (int j = 0; j < maxColumns; j++) {
                    if (str[i][j] == null) {
                        tableMode.setText("", j);
                    } else {
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
                Toast.makeText(mContext, "请求json失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
    }

}
