package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.SpaceConsumer;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.kerne.potato.Util.ChangeStatusBar.setStatusBarColor;
import static com.example.kerne.potato.Util.CustomToast.showShortToast;
import static com.example.kerne.potato.Util.ShowKeyBoard.delayShowSoftKeyBoard;

public class TableActivity extends AppCompatActivity {

    public static final int STATUS_EDIT = 0;
    public static final int STATUS_READ = 1;
    public static final int INIT = 2;
    public static final int INIT_TABLE = 3;
    public static final int UPDATE = 4;
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
    @BindView(R.id.plan_column)
    EditText planColumn;
    @BindView(R.id.plan_row)
    EditText planRow;
    @BindView(R.id.confirm_view)
    View confirmView;
    @BindView(R.id.confirm_button)
    TextView confirmButton;
    @BindView(R.id.swipe_layout)
    LinearLayout swipeLayout;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
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
    private int status = STATUS_READ;
    private JSONObject fields_json;
    private String fieldId;
    private String expType;
    private String type;
    private String bigfarmId;
    private String description;
    private int column = 1;
    private JSONArray rows = new JSONArray();
    private JSONArray fieldArray = new JSONArray();
    private int column_num;
    private int row_num;
    private String[][] str = null;
    private int maxRows; //最大行数
    private int maxColumns; //最大列数
    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase db;
    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    if (status == STATUS_READ) {
                        status = STATUS_EDIT;
                        rightOneButton.setBackgroundResource(R.drawable.no_save);
                        titleText.setText(getText(R.string.species_data_plan));
                        titleText.setTextColor(getResources().getColor(R.color.color_red));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            rightOneLayout.setTooltipText(getResources().getText(R.string.save_data));
                        }
                        tableDescription.setEnabled(true);
                        showShortToast(TableActivity.this, mContext.getString(R.string.enter_species_plan_mode));
                    } else {
                        rightOneButton.setBackgroundResource(R.drawable.edit);
                        titleText.setText(expType);
                        titleText.setTextColor(getResources().getColor(R.color.primary_text));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            rightOneLayout.setTooltipText(getText(R.string.species_data_plan));
                        }
                        tableDescription.setEnabled(false);

                        status = STATUS_READ;
                        showShortToast(TableActivity.this, mContext.getString(R.string.exit_species_plan_mode));
                        editor.putBoolean("upload_data", true);
                        editor.apply();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                //保存操作 sqlite
                                List<ContentValues> contentValuesList = assembleData(str);
                                Log.d("contentvaluelist", contentValuesList.toString());

                                try {
                                    //更新备注description
                                    description = tableDescription.getText().toString();
                                    for (int k = 0; k < fieldArray.length(); k++) {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("description", description);

                                        db.update("LocalField", contentValues, "id=?", new String[]{fieldArray.getString(k)});
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
                                            db.update("LocalBlock", contentValuesList.get(id), "fieldId=? and x=? and y=?",
                                                    new String[]{contentValuesList.get(id).getAsString("fieldId"), contentValuesList.get(id).getAsString("x"), contentValuesList.get(id).getAsString("y")});
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }

                    break;
                case R.id.right_two_layout:
                    final ArrayList<Pair<Integer, Integer>>[] list = new ArrayList[10];
                    final SweetAlertDialog inputDialog = new SweetAlertDialog(TableActivity.this, SweetAlertDialog.NORMAL_TYPE);
                    LayoutInflater mlayoutInflater = LayoutInflater.from(TableActivity.this);
                    @SuppressLint("InflateParams") final View view = mlayoutInflater.inflate(R.layout.dialog_input, null);
                    final EditText dialog_input = view.findViewById(R.id.dialog_input);
                    dialog_input.setHint(getString(R.string.input_species_data));
                    inputDialog.addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    inputDialog.setCustomView(view);
                    inputDialog.setConfirmText("确定");
                    inputDialog.setCancelText("取消");
                    inputDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //TODO
                            list[0] = searchData(dialog_input.getText().toString());
                            inputDialog.dismiss();
                            showSearchData(list[0]);
                        }
                    });
                    inputDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    inputDialog.show();
                    delayShowSoftKeyBoard(dialog_input);
                    break;

                case R.id.confirm_button:
                    if (!planColumn.getText().toString().equals("") && !planRow.getText().toString().equals("")) {
                        if (Integer.parseInt(planColumn.getText().toString()) < 0 || Integer.parseInt(planRow.getText().toString()) < 0) {
                            showShortToast(getBaseContext(), getString(R.string.toast_input_error));
                        } else {
                            maxColumns = Integer.parseInt(planColumn.getText().toString());
                            maxRows = Integer.parseInt(planRow.getText().toString());
                            initTableView(maxColumns);
                            str = new String[maxRows][maxColumns];
                            column = maxColumns;
                            try {
                                rows.put(0, maxRows);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            setData();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initLocalField(fieldId, maxRows, maxColumns);
                                    initLocalBlock(fieldId, maxRows, maxColumns);
                                }
                            }).start();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT:
                    init();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.primary_background);
        setContentView(R.layout.activity_table);
        ButterKnife.bind(this);
        sp = getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();

        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 15);
        db = dbHelper.getWritableDatabase();

        fieldId = getIntent().getStringExtra("fieldId");
        bigfarmId = getIntent().getStringExtra("bigfarmId");
        type = getIntent().getStringExtra("type");
        expType = getIntent().getStringExtra("expType");

        //仿iOS下拉留白
        SmartSwipe.wrap(swipeLayout)
                .addConsumer(new SpaceConsumer())
                .enableVertical();

        initToolBar();

        //延迟加载视图
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                initTable();
            }
        }, 10); //延迟ms

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                initTable();
//            }
//        }).start();
    }

    private void initToolBar() {
        titleText.setText(expType);
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.edit);
        rightTwoButton.setBackgroundResource(R.drawable.ic_search);

        leftOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        rightOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        rightTwoLayout.setBackgroundResource(R.drawable.selector_trans_button);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
        rightOneLayout.setOnClickListener(toolBarOnClickListener);
        rightTwoLayout.setOnClickListener(toolBarOnClickListener);
        confirmButton.setOnClickListener(toolBarOnClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            leftOneLayout.setTooltipText(getText(R.string.back_left));
            rightOneLayout.setTooltipText(getText(R.string.species_data_plan));
            rightTwoLayout.setTooltipText(getText(R.string.search_species));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initTable() {
        boolean isCreated = false;
        Cursor cursor = db.query("LocalField", null, "id=?", new String[]{fieldId}, null, null, null);
        if (cursor.moveToFirst()) {
            maxColumns = cursor.getInt(cursor.getColumnIndex("rows"));
            column = maxColumns;
            int num = cursor.getInt(cursor.getColumnIndex("num"));
            maxRows = (maxColumns != 0) ? num / maxColumns : 0;
            isCreated = cursor.getInt(cursor.getColumnIndex("isCreated")) == 2;
        }
        cursor.close();

        str = new String[maxRows][maxColumns];

        //如果已填冲，自动填充行列数，并隐藏确认按钮
        if (isCreated) {
            planColumn.setText(maxColumns + "");
            planRow.setText(maxRows + "");
            planColumn.setEnabled(false);
            planRow.setEnabled(false);

            confirmView.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);
        }
        try {
            fieldArray.put(0, fieldId);
            rows.put(0, maxRows);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initData();
    }

    private void initData() {
        Cursor cursor = db.query("LocalSpecies", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                speciesNames.add(cursor.getString(cursor.getColumnIndex("name")));
            } while (cursor.moveToNext());
        } else {
            showShortToast(TableActivity.this, getString(R.string.toast_species_null_error));
        }
        cursor.close();


        Cursor cursor1 = db.query("LocalField", null, "id=?", new String[]{fieldId}, null, null, null);
        if (cursor1.moveToFirst()) {
            do {

            } while (cursor1.moveToNext());
        } else {
            showShortToast(TableActivity.this, "无localfield");
        }
        cursor1.close();

        String sql = "select LocalField.*, LocalBlock.* from LocalField, LocalBlock " +
                "where LocalField.id=LocalBlock.fieldId and LocalField.expType='" + expType +
                "' and LocalField.id='" + fieldId + "' order by LocalField.moveX";

        Cursor cursor0 = db.rawQuery(sql, null);
        if (cursor0.moveToFirst()) {
            String fieldId = "";
            int x = 0, y = 0;
            int columns = 0;
            description = cursor0.getString(cursor0.getColumnIndex("description"));
            fieldId = cursor0.getString(cursor0.getColumnIndex("id"));
            do {
                if (!fieldId.equals(cursor0.getString(cursor0.getColumnIndex("id")))) {
                    columns++;
                    fieldId = cursor0.getString(cursor0.getColumnIndex("id"));
                }
                x = cursor0.getInt(cursor0.getColumnIndex("x")) + columns * column - 1; //从0开始
                y = cursor0.getInt(cursor0.getColumnIndex("y")) - 1; //从0开始

                str[y][x] = cursor0.getString(cursor0.getColumnIndex("speciesId"));
            } while (cursor0.moveToNext());
        } else {

        }
        cursor0.close();
        tableDescription.setText(description);
        Message msg = new Message();
        msg.what = INIT;
        uiHandler.sendMessage(msg);
    }


    //组装数据
    private List<ContentValues> assembleData(String[][] str) {
        List<ContentValues> contentValuesList = new ArrayList<>();
        try {
            Log.d("fieldArray", fieldArray.toString());
            Log.d("column", column + "");
            Log.d("rows", rows.toString());
            for (int i = 0; i < fieldArray.length() * column; i++) {
                int rows_num = rows.getInt(i / column);
                for (int j = 0; j < rows_num; j++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("fieldId", fieldArray.getString(i / column));
                    contentValues.put("speciesId", str[j][i]);
                    contentValues.put("x", i % column + 1);
                    contentValues.put("y", j + 1);
                    contentValues.put("isUpdate", 0);
                    contentValuesList.add(contentValues);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contentValuesList;
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        findByid();
        setListener();
        setData();
    }

    public void findByid() {
        mContext = getApplicationContext();
        pulltorefreshview = findViewById(R.id.pulltorefreshview);
        tv_table_title_left = findViewById(R.id.tv_table_title_left);
        tv_table_title_left.setText(getString(R.string.species_list));
        tv_table_title_left.setTextColor(getResources().getColor(R.color.primary_text));
        leftListView = findViewById(R.id.left_container_listview);
        rightListView = findViewById(R.id.right_container_listview);
        right_title_container = findViewById(R.id.right_title_container);


        titleHorScv = findViewById(R.id.title_horsv);
        contentHorScv = findViewById(R.id.content_horsv);
        // 设置两个水平控件的联动
        titleHorScv.setScrollView(contentHorScv);
        contentHorScv.setScrollView(titleHorScv);
        findTitleTextViewIds();
        initTableView(maxColumns);
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

    //初始化列数
    private void initTableConfiguration(int column) {
        getLayoutInflater().inflate(R.layout.table_right_title, right_title_container);
        for (int i = 0; i < column; i++) {
            View view = right_title_container.getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }
    }

    public void initTableView(int column) {
        //初始化列数
        initTableConfiguration(column);

        mLeftAdapter = new AbsCommonAdapter<TableModel>(mContext, R.layout.table_left_item) {
            @Override
            public void convert(AbsViewHolder helper, TableModel item, int pos) {
                TextView tv_table_content_left = helper.getView(R.id.tv_table_content_item_left);
                tv_table_content_left.setText(item.getLeftTitle());

            }
        };

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
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item13));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item14));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item15));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item16));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item17));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item18));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item19));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item20));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item21));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item22));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item23));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item24));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item25));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item26));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item27));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item28));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item29));
                textViews.add((TextView)helper.getView(R.id.tv_table_content_right_item30));

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
                    final int x = finalI + 1, y = pos + 1;
                    final TextView tv = textViews.get(i);
                    tv.setText(item.getText(i));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (status == STATUS_EDIT) {

                                final SweetAlertDialog inputDialog = new SweetAlertDialog(TableActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                LayoutInflater mlayoutInflater = LayoutInflater.from(TableActivity.this);
                                @SuppressLint("InflateParams") final View view = mlayoutInflater.inflate(R.layout.dialog_input, null);
                                final EditText dialog_input = view.findViewById(R.id.dialog_input);
                                dialog_input.setHint(x + "-" + y + "  " + getString(R.string.input_species_data));
                                inputDialog.addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                inputDialog.setCustomView(view);
                                inputDialog.setConfirmText("确定");
                                inputDialog.setCancelText("取消");
                                inputDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        String species = dialog_input.getText().toString();
                                        boolean isExist = false;
                                        for (int j = 0; j < speciesNames.size(); j++) {
                                            if (species.equals(speciesNames.get(j))) {
                                                isExist = true;
                                            }
                                        }
                                        if (isExist) {
                                            tv.setText(species);
                                            str[pos][finalI] = species;
                                            showShortToast(TableActivity.this, species);
                                        } else {
                                            showShortToast(TableActivity.this, getString(R.string.toast_species_null_error));
                                        }
                                        inputDialog.dismiss();
                                    }
                                });
                                inputDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                                inputDialog.show();
                                delayShowSoftKeyBoard(dialog_input);
                            } else if (status == STATUS_READ) {
                                String blockId = null;
                                String speciesId = tv.getText().toString();
                                if (speciesId.equals("")) {
                                    showShortToast(TableActivity.this, mContext.getString(R.string.toast_species_click_tip));
                                } else {
                                    Cursor c = db.query("LocalBlock", null, "fieldId=? and x=? and y=?",
                                            new String[]{fieldId, x + "", y + ""}, null, null, null);
                                    if (c.moveToFirst()) {
                                        blockId = c.getString(c.getColumnIndex("blockId"));
                                    }

                                    Intent intent = new Intent(TableActivity.this, SaveDataActivity.class);
                                    intent.putExtra("speciesId", speciesId);
                                    intent.putExtra("expType", expType);
                                    Log.d("blockId", blockId);
                                    if (blockId != null) {
                                        intent.putExtra("blockId", blockId);
                                    } else {
                                        intent.putExtra("blockId", "test");
                                    }
                                    startActivity(intent);
                                    c.close();
                                }

                            } else {
                                showShortToast(TableActivity.this, getString(R.string.toast_null_error));
                            }
                        }
                    });
                }

                for (int i = 0; i < maxColumns; i++) {
                    View view = ((LinearLayout) helper.getConvertView()).getChildAt(i);
                    view.setVisibility(View.VISIBLE);
                }
                textViews.clear();
            }
        };
        leftListView.setAdapter(mLeftAdapter);
        rightListView.setAdapter(mRightAdapter);
    }


    public void setListener() {

        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转界面
                showShortToast(TableActivity.this, getString(R.string.toast_species_click_tip));
            }
        });
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
//                    tableMode.setText("test", j);
                }
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
                showShortToast(mContext, mContext.getString(R.string.toast_network_error));
            }
        }
    }

    private ArrayList<Pair<Integer, Integer>> searchData(String speciesName) {
        ArrayList<Pair<Integer, Integer>> list = new ArrayList<>();
        for(int i = 0; i < maxRows; i++) {
            for(int j = 0; j < maxColumns; j++) {
                if(speciesName.equals(str[i][j])) {
                    list.add(new Pair<>(i, j)); //行、列
                }
            }
        }
        return list;
    }

    private void showSearchData(List<Pair<Integer, Integer>> list) {
        final int x = list.get(0).first, y = list.get(0).second;
        Log.i("showSearchData_xy", x + "," + y);
        Log.i("showSearchData_top", leftListView.getChildAt(x).getTop() + "," + rightListView.getChildAt(x).getTop());
        leftListView.clearFocus();
        leftListView.post(new Runnable() {
            @Override
            public void run() {
//                mLeftAdapter.notifyDataSetChanged();
//                leftListView.requestFocusFromTouch();
//                leftListView.setSelection(leftListView.getChildAt(x).getTop());
                leftListView.setSelection(leftListView.getChildAt(x).getTop());
//                leftListView.smoothScrollToPosition(x);
            }
        });
        rightListView.clearFocus();
        rightListView.post(new Runnable() {
            @Override
            public void run() {
//                mRightAdapter.notifyDataSetChanged();
//                rightListView.requestFocusFromTouch();
//                rightListView.setSelection(rightListView.getChildAt(x).getTop());
                rightListView.setSelection(rightListView.getChildAt(x).getTop());
//                rightListView.smoothScrollToPosition(x);

            }
        });
//        leftListView.smoothScrollToPosition(leftListView.getChildAt(x).getTop());
//        rightListView.smoothScrollToPosition(rightListView.getChildAt(x).getTop());
//        leftListView.scrollTo(0, leftListView.getChildAt(x).getTop());
//        rightListView.scrollTo(0, rightListView.getChildAt(x).getTop());
//        mLeftAdapter.notifyDataSetInvalidated();
//        mRightAdapter.notifyDataSetInvalidated();
//        leftListView.setSelection(leftListView.getChildAt(x).getTop());
//        rightListView.setSelection(rightListView.getChildAt(x).getTop());
//        leftListView.smoothScrollToPositionFromTop(leftListView.getChildAt(x).getTop(), 0, 5000);
//        rightListView.smoothScrollToPositionFromTop(rightListView.getChildAt(x).getTop(), 0, 5000);
    }

    private void initLocalField(String fieldId, int row, int column) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("rows", column);
        contentValues.put("num", row * column);
        contentValues.put("isCreated", 1);
        db.update("LocalField", contentValues, "id=?", new String[]{fieldId});
    }

    private void initLocalBlock(String fieldId, int row, int column) {
        db.delete("LocalBlock", "fieldId=?", new String[]{fieldId});
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                contentValues.put("blockId", fieldId + "_" + j + "_" + i);
                contentValues.put("fieldId", fieldId);
                contentValues.put("speciesId", "");
                contentValues.put("x", j + 1);
                contentValues.put("y", i + 1);
                contentValues.put("isUpdate", 1);
                db.insert("LocalBlock", null, contentValues);
                contentValues.clear();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
    }

}
