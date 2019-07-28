package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class InShackFragment extends Fragment {

    private static final String TAG = "CheatGZ";
    private static final int DATA_OK = 0;
    @BindView(R.id.in_shack_firm)
    RelativeLayout inShackFirm;
    @BindView(R.id.save_plan)
    LinearLayout savePlan;
    Unbinder unbinder;
    @BindView(R.id.in_image)
    ImageView inImage;
    @BindView(R.id.cover_view)
    View coverView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private View view;
    private Context self;
    private Boolean flag = false;//开始时处于不可编辑状态
    private String bigfarmId;
    private List<JSONObject> mFieldList = new ArrayList<>();
    private View.OnTouchListener moveTouchListenr = new View.OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int ea = event.getAction();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.bg_field_p);
                    v.setElevation(10);
                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    lastY = (int) event.getRawY();
                    int x = 0, y = 0;
                    x = v.getLeft();
                    y = v.getTop();
                case MotionEvent.ACTION_MOVE:
                    //event.getRawX();获得移动的位置
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int l = v.getLeft() + dx;
                    int b = v.getBottom() + dy;
                    int r = v.getRight() + dx;
                    int t = v.getTop() + dy;

                    //下面判断移动是否超出屏幕
                    if (l < 0) {
                        l = 0;
                        r = l + v.getWidth();
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + v.getHeight();
                    }
                    if (r > inShackFirm.getWidth()) {
                        r = inShackFirm.getWidth();
                        l = r - v.getWidth();
                    }
                    if (b > inShackFirm.getHeight()) {
                        b = inShackFirm.getHeight();
                        t = b - v.getHeight();
                    }
                    v.layout(l, t, r, b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundResource(R.drawable.bg_field);
                    v.setElevation(0);
                    int m = 0, n = 0, main_width = 0, main_height;
                    m = v.getLeft();
                    n = v.getTop();
                    main_width = inShackFirm.getWidth();
                    main_height = inShackFirm.getHeight();

                    Log.d(TAG + "结束位置", m + "," + n + "," + main_width + "," + main_height);
                    break;
            }
            return true;
        }
    };
    private List<JSONObject> mJsonList = null;
    private TextView road;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_plan:
                    if (!flag) {
                        coverView.setVisibility(View.GONE);
                        inImage.setBackgroundResource(R.drawable.no_save);
//                        savePlan.getBackground().setAlpha(50);
                        if (road != null) {
                            road.setText("编辑模式");
                        }
                        flag = true;
                    } else {
                        coverView.setVisibility(View.VISIBLE);
                        inImage.setBackgroundResource(R.drawable.edit);
//                        savePlan.getBackground().setAlpha(255);
                        if (road != null) {
                            road.setText("田间小路");
                        }
                        flag = false;
                        editor.putBoolean("upload_data", true);
                        editor.apply();
                        showShortToast(self, "保存完成");
                        //TODO 保存
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private List<TextView> textViewList;
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

    public static InShackFragment newInstance() {
        InShackFragment fragment = new InShackFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_in_shack, container, false);
        self = getContext();
        sp = self.getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();
        unbinder = ButterKnife.bind(this, view);
        inImage.setBackgroundResource(R.drawable.edit);
        savePlan.getBackground().setAlpha(100);

        coverView.setOnClickListener(null);
        savePlan.setOnClickListener(onClickListener);
        initData();
        initView();
        return view;
    }

    private void initData() {
        bigfarmId = getActivity().getIntent().getStringExtra("bigfarmId");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
                SpeciesDBHelper dbHelper = new SpeciesDBHelper(self, "SpeciesTable.db", null, 11);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                //获取大棚区域
                Cursor cursor = db.query("ExperimentField", null, "bigfarmId=?", new String[]{bigfarmId}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("fieldId", cursor.getString(cursor.getColumnIndex("id")));
                            jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
                            jsonObject0.put("expType", cursor.getString(cursor.getColumnIndex("expType")));
                            jsonObject0.put("num", cursor.getInt(cursor.getColumnIndex("num")));
                            jsonObject0.put("bigfarmId", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                            jsonObject0.put("rows", cursor.getInt(cursor.getColumnIndex("rows")));
                            jsonObject0.put("x", cursor.getInt(cursor.getColumnIndex("moveX")));
                            jsonObject0.put("y", cursor.getInt(cursor.getColumnIndex("moveY")));
                            jsonObject0.put("type", "greenhouse");
                            mFieldList.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                db.close();
                dbHelper.close();

                Message msg = new Message();
                msg.what = DATA_OK;
                myHandler.sendMessage(msg);
            }
        }).start();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        if (inShackFirm != null) {
            FarmPlanView farmPlanView = new FarmPlanView(getContext(), inShackFirm, inShackFirm.getWidth(), inShackFirm.getHeight(), mFieldList);
            road = farmPlanView.createRoad("greenhouse");
            textViewList = farmPlanView.createField("greenhouse");
            for (int i = 0; i < textViewList.size(); i++) {
                textViewList.get(i).setOnTouchListener(moveTouchListenr);
                textViewList.get(i).setBackgroundResource(R.drawable.bg_field);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
