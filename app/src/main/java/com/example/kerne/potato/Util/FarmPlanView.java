package com.example.kerne.potato.Util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kerne.potato.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FarmPlanView {

    public static final double FARM_ROW = 410D;//棚外试验田行，包括中间道路的行数
    public static final double FARM_COLUMN = 40D;//棚外试验田列数
    public static final double SHACK_FARM_ROW = 551D;//棚行数，包括中间道路的行数
    public static final double SHACK_FARM_COLUMN = 7D;//棚列数
    public static final double ROAD_ROW = 10D;//道路行数
    public static final double SHACK_ROAD_ROW = 11D;//棚道路行数
    public static final int RATIO = 1000000;
    public List<TextView> textViewList;//试验区域块
    public TextView roadTextView;//道路
    private Context mContext;
    private RelativeLayout mRelativeLayout;//试验田
    private List<JSONObject> mJsonList;//试验区域数据
    private int farmWidth;//试验田宽
    private int farmHeight;//试验田长

    public FarmPlanView(Context context, RelativeLayout relativeLayout, int farmWidth, int farmHeight, List<JSONObject> list) {
        this.mContext = context;
        this.mRelativeLayout = relativeLayout;
        this.mJsonList = list;
        this.farmWidth = farmWidth;
        this.farmHeight = farmHeight;
    }


    public TextView createRoad(String type) {
        //type==0,棚外
        RelativeLayout.LayoutParams layoutParams;
        roadTextView = new TextView(mContext);
        mRelativeLayout.addView(roadTextView);
        layoutParams = (RelativeLayout.LayoutParams) roadTextView.getLayoutParams();
        switch (type) {
            case "common":
                layoutParams.height = (int) ((ROAD_ROW / FARM_ROW) * farmHeight);
                layoutParams.width = farmWidth;
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = (int) ((((FARM_ROW - ROAD_ROW) / 2) / FARM_ROW) * farmHeight);
                break;
            case "greenhouse":
                layoutParams.height = (int) ((SHACK_ROAD_ROW / SHACK_FARM_ROW) * farmHeight);
                layoutParams.width = farmWidth;
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = (int) ((((SHACK_FARM_ROW - SHACK_ROAD_ROW) / 2) / SHACK_FARM_ROW) * farmHeight);
                break;
            default:
                break;
        }
        roadTextView.setLayoutParams(layoutParams);
        roadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorDarkGrey));
        roadTextView.setText("田间小路");
        roadTextView.setTextColor(Color.WHITE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            roadTextView.setAutoSizeTextTypeUniformWithConfiguration(8,16,1, TypedValue.COMPLEX_UNIT_SP);
        }else {
            roadTextView.setTextSize(8);
        }
        roadTextView.setGravity(Gravity.CENTER);

        return roadTextView;
    }

    public List<TextView> createField(String type) {
        textViewList = new ArrayList<>();
        try {
            switch (type) {
                case "common":
                    for (int i = 0; i < mJsonList.size(); i++) {
                        TextView textView = new TextView(mContext);
                        double btn_row = (double) mJsonList.get(i).getInt("num") / mJsonList.get(i).getInt("rows");
                        double btn_column = (double) mJsonList.get(i).getInt("rows");
                        double btn_x = Double.valueOf(mJsonList.get(i).get("x").toString()) / RATIO;
                        double btn_y = Double.valueOf(mJsonList.get(i).get("y").toString()) / RATIO;

                        mRelativeLayout.addView(textView);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                        layoutParams.height = (int) ((btn_row / FARM_ROW) * (farmHeight - 20));
                        layoutParams.width = (int) ((btn_column / FARM_COLUMN) * (farmWidth - 20));

                        layoutParams.topMargin = (int) (btn_y * farmHeight);
                        layoutParams.leftMargin = (int) (btn_x * farmWidth);
                        textView.setLayoutParams(layoutParams);
                        textView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorDarkGrey));
                        textView.setText(mJsonList.get(i).getString("name"));
                        textView.setTextColor(Color.WHITE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            textView.setAutoSizeTextTypeUniformWithConfiguration(8,15,1, TypedValue.COMPLEX_UNIT_SP);
                        }else {
                            textView.setTextSize(9);
                        }
                        textView.setGravity(Gravity.CENTER);
                        textViewList.add(textView);
                    }
                    break;
                case "greenhouse":
                    for (int i = 0; i < mJsonList.size(); i++) {
                        TextView textView = new TextView(mContext);
                        double btn_row = (double) mJsonList.get(i).getInt("num") / mJsonList.get(i).getInt("rows");
                        double btn_column = (double) mJsonList.get(i).getInt("rows");
                        double btn_x = Double.valueOf(mJsonList.get(i).get("x").toString()) / RATIO;
                        double btn_y = Double.valueOf(mJsonList.get(i).get("y").toString()) / RATIO;

                        mRelativeLayout.addView(textView);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                        layoutParams.width = (int) ((btn_column / SHACK_FARM_COLUMN) * (farmWidth - 20));
                        layoutParams.height = (int) ((btn_row / SHACK_FARM_ROW) * (farmHeight - 20));

                        layoutParams.topMargin = (int) (btn_y * farmHeight);
                        layoutParams.leftMargin = (int) (btn_x * farmWidth);
                        textView.setLayoutParams(layoutParams);
                        textView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorDarkGrey));
                        textView.setText(mJsonList.get(i).getString("expType"));
                        textView.setTextColor(Color.WHITE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            textView.setAutoSizeTextTypeUniformWithConfiguration(8,15,1, TypedValue.COMPLEX_UNIT_SP);
                        }else {
                            textView.setTextSize(9);
                        }
                        textView.setGravity(Gravity.CENTER);
                        textViewList.add(textView);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textViewList;
    }
}
