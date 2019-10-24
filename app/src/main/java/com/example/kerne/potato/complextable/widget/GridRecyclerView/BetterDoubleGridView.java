package com.example.kerne.potato.complextable.widget.GridRecyclerView;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.R;
import com.example.kerne.potato.TableActivity;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

/**
 * auther: baiiu
 * time: 16/6/5 05 23:03
 * description:
 */
public class BetterDoubleGridView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<JSONObject> mTopGridDataList;
    private List<JSONObject> mBottomGridList;
    private Context mContext;
    private DoubleGridAdapter adapter;


    private String bigfarmId;
    private String name;
    private String bigFarmName;

    private String type;
    private String expType;
    private String fieldId;
    private int num;
    private int rows;


    public BetterDoubleGridView(Context context) {
        this(context, null);
        mContext = context;
    }

    public BetterDoubleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        mContext = context;
    }

    public BetterDoubleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BetterDoubleGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        inflate(context, R.layout.layout_recyclerview, this);
        ButterKnife.bind(this, this);
    }


    public BetterDoubleGridView setmTopGridDataList(List<JSONObject> mTopGridDataList) {
        this.mTopGridDataList = mTopGridDataList;
        return this;
    }

    public BetterDoubleGridView setmBottomGridList(List<JSONObject> mBottomGridList) {
        this.mBottomGridList = mBottomGridList;
        return this;
    }

    public BetterDoubleGridView build() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == mTopGridDataList.size() + 1) {
                    return 4;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new DoubleGridAdapter(getContext(), mTopGridDataList, mBottomGridList, this, this);
        recyclerView.setAdapter(adapter);

        return this;
    }

    @Override
    public void onClick(View v) {

        TextView textView = (TextView) v;
        String tag = (String) textView.getTag();
        try {
            bigfarmId = mTopGridDataList.get(0).getString("bigfarmId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (tag.equals("棚外 +")) {
            createNewType("common", mContext);
        } else if (tag.equals("棚内 +")) {
            createNewType("greenhouse", mContext);
        } else {
            Intent intent;
            try {
                int position = Integer.parseInt(tag);
                if (position < mTopGridDataList.size()) {
                    fieldId = mTopGridDataList.get(position).getString("fieldId");
                    bigfarmId = mTopGridDataList.get(position).getString("bigfarmId");
                    type = mTopGridDataList.get(position).getString("type");
                    expType = mTopGridDataList.get(position).getString("expType");
                    num = mTopGridDataList.get(position).getInt("num");
                    rows = mTopGridDataList.get(position).getInt("rows");
                    name = mTopGridDataList.get(position).getString("name");
                } else {
                    fieldId = mBottomGridList.get(position - mTopGridDataList.size()).getString("fieldId");
                    bigfarmId = mBottomGridList.get(position - mTopGridDataList.size()).getString("bigfarmId");
                    type = mBottomGridList.get(position - mTopGridDataList.size()).getString("type");
                    expType = mBottomGridList.get(position - mTopGridDataList.size()).getString("expType");
                    num = mBottomGridList.get(position - mTopGridDataList.size()).getInt("num");
                    rows = mBottomGridList.get(position - mTopGridDataList.size()).getInt("rows");
                    name = mBottomGridList.get(position - mTopGridDataList.size()).getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent = new Intent(mContext, TableActivity.class);
            intent.putExtra("bigfarmId", bigfarmId);
            intent.putExtra("fieldId", fieldId);
            intent.putExtra("type", type);
            intent.putExtra("expType", expType);
            intent.putExtra("num", num);
            intent.putExtra("rows", rows);
            intent.putExtra("name", name);
            mContext.startActivity(intent);
        }
    }

    private void createNewType(final String shack, final Context mContext) {
        final SweetAlertDialog createTypeDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                .setConfirmText("确定")
                .setCancelText("取消");
        LayoutInflater mlayoutInflater = LayoutInflater.from(getContext());
        View view = mlayoutInflater.inflate(R.layout.dialog_input, null);
        final EditText dialog_input = view.findViewById(R.id.dialog_input);
        dialog_input.setHint("增加新实验类型");
        createTypeDialog.addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        createTypeDialog.setCustomView(view);
        createTypeDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (dialog_input.getText().toString().length() == 0) {
                    showShortToast(mContext, "试验类型名称不能为空");
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    insertNewType(shack, dialog_input.getText().toString());
                    showShortToast(mContext, "试验类型添加成功");
                }
            }
        });
        createTypeDialog.show();
    }

    //新增一个试验类型
    private void insertNewType(String shack, String expType) {
        SpeciesDBHelper dbHelper;
        SQLiteDatabase db;
        dbHelper = new SpeciesDBHelper(getContext(), "SpeciesTable.db", null, 15);
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", bigfarmId + "_" + (mTopGridDataList.size() + mBottomGridList.size()));
        contentValues.put("name", expType);
        contentValues.put("expType", expType);
        contentValues.put("moveX", 0);
        contentValues.put("moveY", 0);
        contentValues.put("moveX1", 0);
        contentValues.put("moveY1", 0);
        contentValues.put("num", 0);
        contentValues.put("rows", 0);
        contentValues.put("bigfarmId", bigfarmId);
        contentValues.put("description", "");
        contentValues.put("type", shack);
        contentValues.put("isCreated", 0);
        db.insert("LocalField", null, contentValues);
        contentValues.clear();
        try {
            JSONObject jsonObject0 = new JSONObject();
            jsonObject0.put("fieldId", bigfarmId + "_" + (mTopGridDataList.size() + mBottomGridList.size()));
            jsonObject0.put("name", expType);
            jsonObject0.put("expType", expType);
            jsonObject0.put("num", 0);
            jsonObject0.put("bigfarmId", bigfarmId);
            jsonObject0.put("rows", 0);
            if (shack.equals("common")) {
                jsonObject0.put("type", "common");
                mTopGridDataList.add(jsonObject0);
                adapter.notifyDataSetChanged();
            } else {
                jsonObject0.put("type", "greenhouse");
                mBottomGridList.add(jsonObject0);
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();
        dbHelper.close();
    }

    @Override
    public boolean onLongClick(View v) {
        TextView textView = (TextView) v;
        String tag = (String) textView.getTag();
        try {
            bigfarmId = mTopGridDataList.get(0).getString("bigfarmId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (tag.equals("棚外 +")) {
        } else if (tag.equals("棚内 +")) {
        } else {
            int position = Integer.parseInt(tag);
            updateTypeName(position);
        }
        return true;
    }

    private void updateTypeName(final int position) {
        try {
            final SweetAlertDialog updateTypeNameDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                    .setConfirmText("确定")
                    .setCancelText("取消");
            LayoutInflater mlayoutInflater = LayoutInflater.from(getContext());
            View view = mlayoutInflater.inflate(R.layout.dialog_input, null);
            final EditText dialog_input = view.findViewById(R.id.dialog_input);
            if (position < mTopGridDataList.size()) {

                dialog_input.setText(mTopGridDataList.get(position).getString("name"));
            } else {
                dialog_input.setText(mBottomGridList.get(position - mTopGridDataList.size()).getString("name"));
            }
            updateTypeNameDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    if (dialog_input.getText().toString().length() == 0) {
                        showShortToast(mContext, "试验类型名称不能为空");
                    } else {
                        updateNewTypeName(position, dialog_input.getText().toString());
                        showShortToast(mContext, "试验类型修改成功");
                    }
                }
            });
            updateTypeNameDialog.addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            updateTypeNameDialog.setCustomView(view);
            updateTypeNameDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateNewTypeName(int position, String name) {
        try {
            String fieldId;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            if (position < mTopGridDataList.size()) {
                fieldId = mTopGridDataList.get(position).getString("fieldId");
                mTopGridDataList.set(position, jsonObject);
            } else {
                fieldId = mBottomGridList.get(position - mTopGridDataList.size()).getString("fieldId");
                mBottomGridList.set(position - mTopGridDataList.size(), jsonObject);
            }
            SpeciesDBHelper dbHelper;
            SQLiteDatabase db;
            dbHelper = new SpeciesDBHelper(getContext(), "SpeciesTable.db", null, 15);
            db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            db.update("LocalField", contentValues, "id=?", new String[]{fieldId});
            contentValues.clear();
            db.close();
            dbHelper.close();
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
