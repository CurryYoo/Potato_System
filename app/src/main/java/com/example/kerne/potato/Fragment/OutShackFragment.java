package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_plan:
                    if (!flag) {
                        coverView.setVisibility(View.GONE);
                        outImage.setBackgroundResource(R.drawable.no_save);
                        if(road!=null) {
                            road.setText("编辑模式");
                        }
                        flag = true;
                    } else {
                        coverView.setVisibility(View.VISIBLE);
                        outImage.setBackgroundResource(R.drawable.edit);
                        if(road!=null) {
                            road.setText("田间小路");
                        }
                        flag = false;
                        editor.putBoolean("upload_data", true);
                        editor.apply();
                        showShortToast(self, "保存完成");
                        //TODO 保存,只需要更新数据库即可
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private List<JSONObject> mFieldList = null;
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
        outImage.setBackgroundResource(R.drawable.edit);
        savePlan.getBackground().setAlpha(100);

        coverView.setOnClickListener(null);
        savePlan.setOnClickListener(onClickListener);
        initView();
        return view;
    }

    private void initView() {
        mFieldList = new ArrayList<>();
        try {
            for (int i = 0; i < 3; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("num", 50+i*10);
                jsonObject.put("rows", 1+i);
                jsonObject.put("x", 300000+i*100000);
                jsonObject.put("y", 300000+i*100000);
                jsonObject.put("name", "加工鉴定");
                mFieldList.add(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                if (outShackFirm != null) {
                    FarmPlanView farmPlanView = new FarmPlanView(getContext(), outShackFirm, outShackFirm.getWidth(), outShackFirm.getHeight(), mFieldList);
                    road=farmPlanView.createRoad("common");
                    textViewList = farmPlanView.createField("common",FarmPlanView.DRAG_EVENT);
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
