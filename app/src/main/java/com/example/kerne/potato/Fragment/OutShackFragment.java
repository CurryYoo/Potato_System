package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.SpaceConsumer;
import com.example.kerne.potato.R;
import com.example.kerne.potato.Util.FarmPlanView;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class OutShackFragment extends Fragment {
    private static final int DATA_OK = 0;
    @BindView(R.id.save_plan)
    LinearLayout savePlan;
    @BindView(R.id.out_shack_farm)
    RelativeLayout outShackFarm;
    Unbinder unbinder;
    @BindView(R.id.out_image)
    ImageView outImage;
    @BindView(R.id.cover_view)
    View coverView;
    @BindView(R.id.base_farm)
    RelativeLayout baseFarm;
    @BindView(R.id.swipe_layout)
    RelativeLayout swipeLayout;

    private View view;
    private Context self;
    private String bigfarmId;

    SpeciesDBHelper dbHelper;
    SQLiteDatabase db;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private Boolean flag = false;//开始时处于不可编辑状态
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_plan:
                    if (!flag) {
                        coverView.setVisibility(View.INVISIBLE);
                        outImage.setBackgroundResource(R.drawable.no_save);
                        if (road != null) {
                            road.setText("编辑模式");
                        }
                        flag = true;
                    } else {
                        coverView.setVisibility(View.VISIBLE);
                        outImage.setBackgroundResource(R.drawable.edit);
                        if (road != null) {
                            road.setText("路");
                        }
                        flag = false;
                        editor.putBoolean("upload_data", true);
                        editor.apply();
                        showShortToast(self, "保存完成");
                        //保存
                        updateData();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private List<JSONObject> mFieldList = new ArrayList<>();
    private TextView road;
    private List<View> viewList;

    public static OutShackFragment newInstance() {
        OutShackFragment fragment = new OutShackFragment();
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_OK:
                    initView();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_out_shack, container, false);
        self = getContext();
        sp = self.getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();
        unbinder = ButterKnife.bind(this, view);
        outImage.setBackgroundResource(R.drawable.edit);

        //仿iOS下拉留白
        SmartSwipe.wrap(swipeLayout)
                .addConsumer(new SpaceConsumer())
                .enableVertical();

        coverView.setOnClickListener(null);
        savePlan.setOnClickListener(onClickListener);

        dbHelper = new SpeciesDBHelper(self, "SpeciesTable.db", null, 13);
        db = dbHelper.getWritableDatabase();

        initData();
        return view;
    }

    private void initData() {
        bigfarmId = getActivity().getIntent().getStringExtra("bigfarmId");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取棚外数据
                mFieldList.clear();
                Cursor cursor1 = db.query("LocalField", null, "bigfarmId=? and type=?", new String[]{bigfarmId, "common"}, null, null, null);
                if (cursor1.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("fieldId", cursor1.getString(cursor1.getColumnIndex("id")));
                            jsonObject0.put("bigfarmId", cursor1.getString(cursor1.getColumnIndex("bigfarmId")));
                            jsonObject0.put("type", "common");
                            jsonObject0.put("expType", cursor1.getString(cursor1.getColumnIndex("expType")));
                            jsonObject0.put("num", cursor1.getInt(cursor1.getColumnIndex("num")));
                            jsonObject0.put("rows", cursor1.getInt(cursor1.getColumnIndex("rows")));
                            jsonObject0.put("x", cursor1.getInt(cursor1.getColumnIndex("moveX")));
                            jsonObject0.put("y", cursor1.getInt(cursor1.getColumnIndex("moveY")));
                            jsonObject0.put("name", cursor1.getString(cursor1.getColumnIndex("name")));
                            jsonObject0.put("description", cursor1.getString(cursor1.getColumnIndex("description")));
                            mFieldList.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (cursor1.moveToNext());
                }
                cursor1.close();

                Message msg = new Message();
                msg.what = DATA_OK;
                myHandler.sendMessage(msg);
                Looper.loop();
            }
        }).start();
    }

    private void initView() {
        if (outShackFarm != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) outShackFarm.getLayoutParams();
            layoutParams.width = baseFarm.getWidth();
            layoutParams.height = (int) (0.95 * baseFarm.getWidth());
            outShackFarm.setLayoutParams(layoutParams);
            FarmPlanView farmPlanView = new FarmPlanView(getContext(), outShackFarm, baseFarm.getWidth(), (int) (0.95 * baseFarm.getWidth()), mFieldList);
            road = farmPlanView.createRoad("common");
            farmPlanView.createField("common", FarmPlanView.DRAG_EVENT);
        }
    }

    private void updateData() {
        List<ContentValues> contentValuesList = assembleData(mFieldList);
        for (int i = 0; i < contentValuesList.size(); i++) {
            db.update("LocalField", contentValuesList.get(i), "id=?", new String[]{contentValuesList.get(i).getAsString("id")});
        }
    }

    //组装数据
    private List<ContentValues> assembleData(List<JSONObject> jsonObjectList) {
        List<ContentValues> contentValuesList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonObjectList.size(); i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", jsonObjectList.get(i).getString("fieldId"));
                contentValues.put("moveX", jsonObjectList.get(i).getInt("x"));
                contentValues.put("moveY", jsonObjectList.get(i).getInt("y"));
                contentValuesList.add(contentValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentValuesList;
    }

    //fragment可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {//可见
            // 相当于Fragment的onResume
        } else {
            // 相当于Fragment的onPause
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        db.close();
        dbHelper.close();
    }
}
