package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class OutShackFragment extends Fragment {
    private static final String TAG = "CheatGZ";
    @BindView(R.id.save_plan)
    LinearLayout savePlan;
    @BindView(R.id.out_shack_firm)
    RelativeLayout outShackFirm;
    Unbinder unbinder;
    @BindView(R.id.out_image)
    ImageView outImage;
    @BindView(R.id.cover_view)
    View coverView;
    private View view;
    private Context self;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private Boolean flag = false;//开始时处于不可编辑状态
    private View.OnTouchListener moveTouchListenr = new View.OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int ea = event.getAction();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.bg_farm_p);
                    v.setElevation(10);
                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    lastY = (int) event.getRawY();
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
                    if (r > outShackFirm.getWidth()) {
                        r = outShackFirm.getWidth();
                        l = r - v.getWidth();
                    }
                    if (b > outShackFirm.getHeight()) {
                        b = outShackFirm.getHeight();
                        t = b - v.getHeight();
                    }
                    v.layout(l, t, r, b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundResource(R.drawable.bg_farm);
                    v.setElevation(0);
                    int m = 0, n = 0, main_width = 0, main_height;
                    m = v.getLeft();
                    n = v.getTop();
                    main_width = outShackFirm.getWidth();
                    main_height = outShackFirm.getHeight();

                    Log.d(TAG + "结束位置", m + "," + n + "," + main_width + "," + main_height);
                    break;
            }
            return true;
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_plan:
                    if (!flag) {
                        coverView.setVisibility(View.GONE);
                        outImage.setBackgroundResource(R.drawable.ic_menu_no_save);
                        savePlan.getBackground().setAlpha(50);
                        if(road!=null) {
                            road.setText("编辑模式");
                        }
                        flag = true;
                    } else {
                        coverView.setVisibility(View.VISIBLE);
                        outImage.setBackgroundResource(R.drawable.ic_menu_plan);
                        savePlan.getBackground().setAlpha(255);
                        if(road!=null) {
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
    private List<JSONObject> mJsonList = null;
    private TextView road;
    private List<TextView> textViewList;

    public static OutShackFragment newInstance() {
        OutShackFragment fragment = new OutShackFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_out_shack, container, false);
        self = getContext();
        sp = self.getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();
        unbinder = ButterKnife.bind(this, view);
        outImage.setBackgroundResource(R.drawable.ic_menu_plan);

        coverView.setOnClickListener(null);
        savePlan.setOnClickListener(onClickListener);
        initView();
        return view;
    }

    private void initView() {
        mJsonList = new ArrayList<>();
        try {
            for (int i = 0; i < 3; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("num", 50+i*10);
                jsonObject.put("rows", 1+i);
                jsonObject.put("x", 300000+i*100000);
                jsonObject.put("y", 300000+i*100000);
                jsonObject.put("name", "加工鉴定");
                mJsonList.add(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                if (outShackFirm != null) {
                    FarmPlanView farmPlanView = new FarmPlanView(getContext(), outShackFirm, outShackFirm.getWidth(), outShackFirm.getHeight(), mJsonList);
                    road=farmPlanView.createRoad("common");
                    textViewList = farmPlanView.createField("common");
                    for (int i = 0; i < textViewList.size(); i++) {
                        textViewList.get(i).setOnTouchListener(moveTouchListenr);
                        textViewList.get(i).setBackgroundResource(R.drawable.bg_farm);
                    }
                }
            }
        }, 1000); //延迟ms
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
