package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class InShackFragment extends Fragment {

    private static final String TAG = "CheatGZ";
    @BindView(R.id.in_shack_firm)
    RelativeLayout inShackFirm;
    @BindView(R.id.save_plan)
    LinearLayout savePlan;
    Unbinder unbinder;
    private View view;
    private Context self;
    private View.OnTouchListener moveTouchListenr = new View.OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int ea = event.getAction();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    lastY = (int) event.getRawY();
                    int x = 0, y = 0;
                    x = v.getLeft();
                    y = v.getTop();
                    Log.d(TAG + "初始位置", x + "," + y);
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
                    Log.d(TAG, "onTouch: " + l + "==" + t + "==" + r + "==" + b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
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
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_plan:
                    break;
                default:
                    break;
            }
        }
    };
    private List<JSONObject> mJsonList = null;
    private List<TextView> textViewList;


    public static InShackFragment newInstance() {
        InShackFragment fragment = new InShackFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_in_shack, container, false);
        self = getContext();
        unbinder = ButterKnife.bind(this, view);

        savePlan.setOnClickListener(onClickListener);
        initView();
        return view;
    }


    private void initView() {
        mJsonList = new ArrayList<>();
        try {
            for (int i = 0; i < 4; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("num", 200);
                jsonObject.put("row", 100);
                jsonObject.put("x", 0.2);
                jsonObject.put("y", 0.3);
                mJsonList.add(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                if (inShackFirm != null) {

                    Log.d("cheatGZ_main", inShackFirm.getWidth() + "," + inShackFirm.getHeight());
                    FarmPlanView farmPlanView = new FarmPlanView(getContext(), inShackFirm, inShackFirm.getWidth(), inShackFirm.getHeight(), mJsonList);
                    farmPlanView.createRoad("greenhouse");
                    textViewList = farmPlanView.createField("greenhouse");
                    for (int i = 0; i < textViewList.size(); i++) {
                        textViewList.get(i).setOnTouchListener(moveTouchListenr);
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
