package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kerne.potato.R;
import com.example.kerne.potato.complextable.widget.GridRecyclerView.BetterDoubleGridView;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirmSurveyFragment extends Fragment {

    private static final int DATA_OK = 0;
    private static final int CHANGE_VIEW = 1;
    private static String bigfarmId;
    private View view;
    private Context self;
    private BetterDoubleGridView betterDoubleGridView;
    private List<JSONObject> mFieldList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_OK:
                    initRecyclerView();
                    break;
                case CHANGE_VIEW:
                    initView();
                    break;
                default:
                    break;
            }
        }
    };

    public static FirmSurveyFragment newInstance() {
        FirmSurveyFragment fragment = new FirmSurveyFragment();
        return fragment;
    }


    public void selectFarm(String bigFarmId) {
        FirmSurveyFragment.bigfarmId = bigFarmId;
        Message msg = new Message();
        msg.what = CHANGE_VIEW;
        myHandler.sendMessage(msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_firm_survey, container, false);
        self = getContext();
        initView();
        return view;
    }

    private void initView() {
        betterDoubleGridView = view.findViewById(R.id.expType_list);
        if (bigfarmId == null) {
            initRecyclerView();
        } else {
            initData();
        }
    }

    private void initData() {
        mFieldList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
                SpeciesDBHelper dbHelper = new SpeciesDBHelper(self, "SpeciesTable.db", null, 11);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

//                //获取棚外区域
//                Cursor cursor = db.query("FarmList", null, "bigfarmId=?", new String[]{bigfarmId}, null, null, null);
//                if (cursor.moveToFirst()) {
//                    do {
//                        JSONObject jsonObject0 = new JSONObject();
//                        try {
//                            jsonObject0.put("farmlandId", cursor.getString(cursor.getColumnIndex("farmlandId")));
//                            jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
//                            jsonObject0.put("length", cursor.getInt(cursor.getColumnIndex("length")));
//                            jsonObject0.put("width", cursor.getInt(cursor.getColumnIndex("width")));
//                            jsonObject0.put("type", cursor.getString(cursor.getColumnIndex("type")));
//                            jsonObject0.put("bigfarmId", cursor.getString(cursor.getColumnIndex("bigfarmId")));
////                            jsonObject0.put("bigFarmName", bigFarmName);
////                            jsonObject0.put("year", year);
//
//                            mFieldList.add(jsonObject0);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();


                //获取大棚区域
                Cursor cursor2 = db.query("ExperimentField", null, "bigfarmId=?", new String[]{bigfarmId}, null, null, null);
                if (cursor2.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("fieldId", cursor2.getString(cursor2.getColumnIndex("id")));
                            jsonObject0.put("name", cursor2.getString(cursor2.getColumnIndex("name")));
                            jsonObject0.put("expType", cursor2.getString(cursor2.getColumnIndex("expType")));
                            jsonObject0.put("num", cursor2.getInt(cursor2.getColumnIndex("num")));
                            jsonObject0.put("bigfarmId", cursor2.getString(cursor2.getColumnIndex("bigfarmId")));
                            jsonObject0.put("rows", cursor2.getInt(cursor2.getColumnIndex("rows")));
                            jsonObject0.put("type", cursor2.getString(cursor2.getColumnIndex("type")));

                            Log.d("typeeeeeeeeeee", cursor2.getString(cursor2.getColumnIndex("type")));
//                            jsonObject0.put("bigFarmName", bigFarmName);
//                            jsonObject0.put("year", year);
                            mFieldList.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (cursor2.moveToNext());
                }
                cursor2.close();

                db.close();
                dbHelper.close();

                Message msg = new Message();
                msg.what = DATA_OK;
                myHandler.sendMessage(msg);
            }
        }).start();
    }

    private void initRecyclerView() {
        List<JSONObject> outShack = new ArrayList<>();
        List<JSONObject> inShack = new ArrayList<>();
        if (mFieldList.size() > 0) {
            try {
                for (int i = 0; i < mFieldList.size(); ++i) {

                    if (mFieldList.get(i).getString("type").equals("common")) {
                        outShack.add(mFieldList.get(i));
                    } else {
                        inShack.add(mFieldList.get(i));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        betterDoubleGridView.setmTopGridData(outShack).setmBottomGridList(inShack).build();
    }

    //设置在前一页更新数据后，本页可以刷新数据
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            // 相当于Fragment的onResume
//            if (update_flag) {
//                initData();
//                update_flag = false;
//            }
//        } else {
//            // 相当于Fragment的onPause
//        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
