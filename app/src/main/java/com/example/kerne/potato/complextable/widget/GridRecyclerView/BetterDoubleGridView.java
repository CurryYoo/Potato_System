package com.example.kerne.potato.complextable.widget.GridRecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.GeneralActivity;
import com.example.kerne.potato.R;
import com.example.kerne.potato.TableActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

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


    private String farmlandId;
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
        mContext=context;
    }

    public BetterDoubleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        mContext=context;
    }

    public BetterDoubleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mContext=context;
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
        if(position<mTopGridData.size()){
            try {
                farmlandId = mTopGridData.get(position).getString("farmlandId");
                type = mTopGridData.get(position).getString("type");
                name = mTopGridData.get(position).getString("name");
                bigFarmName = mTopGridData.get(position).getString("bigFarmName");
                year = mTopGridData.get(position).getInt("year");
                length = mTopGridData.get(position).getInt("length");
                width = mTopGridData.get(position).getInt("width");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent = new Intent(mContext, GeneralActivity.class);
            intent.putExtra("length", length);
            intent.putExtra("width", width);
            intent.putExtra("farmlandId", farmlandId);
            intent.putExtra("type", type);
            intent.putExtra("farmName", name);
            intent.putExtra("bigFarmName", bigFarmName);
            intent.putExtra("year", year);
            mContext.startActivity(intent);
//            showShortToast(mContext, mContext.getText(R.string.farm) + "：" + name);
        }else {


            try {
                farmlandId = mBottomGridList.get(position-mTopGridData.size()).getString("farmlandId");
                type = mBottomGridList.get(position-mTopGridData.size()).getString("type");
                bigFarmName = mBottomGridList.get(position-mTopGridData.size()).getString("bigFarmName");
                year = mBottomGridList.get(position-mTopGridData.size()).getInt("year");
                expType = mBottomGridList.get(position-mTopGridData.size()).getString("expType");
                fieldId = mBottomGridList.get(position-mTopGridData.size()).getString("fieldId");
                num = mBottomGridList.get(position-mTopGridData.size()).getInt("num");
                rows = mBottomGridList.get(position-mTopGridData.size()).getInt("rows");
                name = mBottomGridList.get(position-mTopGridData.size()).getString("name");
                Log.d("pengnei2", num + "," + rows);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent = new Intent(mContext, TableActivity.class);

            final Intent finalIntent = intent;
            finalIntent.putExtra("expType", expType);
            finalIntent.putExtra("fieldId", fieldId);
            finalIntent.putExtra("num", num);
            finalIntent.putExtra("rows", rows);
            finalIntent.putExtra("farmlandId", farmlandId);
            finalIntent.putExtra("type", type);
            finalIntent.putExtra("farmName", name);
            finalIntent.putExtra("bigFarmName", bigFarmName);
            finalIntent.putExtra("year", year);
            mContext.startActivity(finalIntent);
//            showShortToast(mContext, mContext.getText(R.string.farm) + "：" + name);
        }
    }
}
