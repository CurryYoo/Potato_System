package com.example.kerne.potato.complextable.widget.GridRecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.R;
import com.example.kerne.potato.TableActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * auther: baiiu
 * time: 16/6/5 05 23:03
 * description:
 */
public class BetterDoubleGridView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<JSONObject> mTopGridData;
    private List<JSONObject> mBottomGridList;
    private Context mContext;


    private String bigfarmId;
    private String name;
    private String bigFarmName;

    //棚外特有属性
    private int length;
    private int width;

    private String type;
    private int year;

    //棚内特有属性
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


    public BetterDoubleGridView setmTopGridData(List<JSONObject> mTopGridData) {
        this.mTopGridData = mTopGridData;
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
                if (position == 0 || position == mTopGridData.size() + 1) {
                    return 4;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new DoubleGridAdapter(getContext(), mTopGridData, mBottomGridList, this));

        return this;
    }

    @Override
    public void onClick(View v) {

        TextView textView = (TextView) v;
        int position = (int) textView.getTag();

        Intent intent;
        try {
            if (position < mTopGridData.size()) {
                fieldId = mTopGridData.get(position).getString("fieldId");
                bigfarmId = mTopGridData.get(position).getString("bigfarmId");
                type = mTopGridData.get(position).getString("type");
                expType = mTopGridData.get(position).getString("expType");
                num = mTopGridData.get(position).getInt("num");
                rows = mTopGridData.get(position).getInt("rows");
                name = mTopGridData.get(position).getString("name");
            } else {
                fieldId = mBottomGridList.get(position - mTopGridData.size()).getString("fieldId");
                bigfarmId = mBottomGridList.get(position - mTopGridData.size()).getString("bigfarmId");
                type = mBottomGridList.get(position - mTopGridData.size()).getString("type");
                expType = mBottomGridList.get(position - mTopGridData.size()).getString("expType");
                num = mBottomGridList.get(position - mTopGridData.size()).getInt("num");
                rows = mBottomGridList.get(position - mTopGridData.size()).getInt("rows");
                name = mBottomGridList.get(position - mTopGridData.size()).getString("name");
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
