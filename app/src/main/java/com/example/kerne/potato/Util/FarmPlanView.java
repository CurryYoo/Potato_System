package com.example.kerne.potato.Util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
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
    public static final double SHACK_FARM_ROW = 580D;//棚行数，包括中间道路的行数
    public static final double SHACK_FARM_COLUMN = 14D;//棚列数
    public static final double ROAD_ROW = 10D;//道路行数
    public static final double SHACK_ROAD_ROW = 13.5D;//棚道路行数
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


    public void createRoad(String type) {
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
                Log.d("cheatGZ road greenhouse", "" + layoutParams.height + "," + layoutParams.width + "," + layoutParams.topMargin + "," + layoutParams.leftMargin);
                break;
            default:
                break;
        }
        roadTextView.setLayoutParams(layoutParams);
        roadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorDarkGrey));
        roadTextView.setText("田间小路");
        roadTextView.setTextColor(Color.WHITE);
        roadTextView.setTextSize(10);
        roadTextView.setGravity(Gravity.CENTER);
    }

    public List<TextView> createField(String type) {
        textViewList = new ArrayList<>();
        try {
            switch (type) {
                case "common":
                    for (int i = 0; i < mJsonList.size(); i++) {
                        TextView textView = new TextView(mContext);
                        double btn_width = (double) mJsonList.get(i).getInt("num") / mJsonList.get(i).getInt("row");
                        double btn_height = (double) mJsonList.get(i).getInt("row");
                        double btn_x = Double.valueOf(mJsonList.get(i).get("x").toString());
                        double btn_y = Double.valueOf(mJsonList.get(i).get("y").toString());

                        mRelativeLayout.addView(textView);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                        layoutParams.height = (int) ((btn_height / FARM_ROW) * (farmHeight - 20));
                        layoutParams.width = (int) ((btn_width / FARM_COLUMN) * (farmWidth - 20));

                        layoutParams.topMargin = (int) (btn_y * farmHeight);
                        layoutParams.leftMargin = (int) (btn_x * farmWidth);
                        Log.d("cheatGZ common field", "" + layoutParams.height + "," + layoutParams.width + "," + layoutParams.topMargin + "," + layoutParams.leftMargin);
                        textView.setLayoutParams(layoutParams);
                        textView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorDarkGrey));
                        textView.setText("加工鉴定");
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(10);
                        textView.setGravity(Gravity.CENTER);
                        textViewList.add(textView);
                    }
                    break;
                case "greenhouse":
                    for (int i = 0; i < mJsonList.size(); i++) {
                        TextView textView = new TextView(mContext);
                        double btn_width = (double) mJsonList.get(i).getInt("num") / mJsonList.get(i).getInt("row");
                        double btn_height = (double) mJsonList.get(i).getInt("row");
                        double btn_x = Double.valueOf(mJsonList.get(i).get("x").toString());
                        double btn_y = Double.valueOf(mJsonList.get(i).get("y").toString());

                        mRelativeLayout.addView(textView);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                        layoutParams.height = (int) ((btn_height / SHACK_FARM_ROW) * (farmHeight - 20));
                        layoutParams.width = (int) ((btn_width / SHACK_FARM_COLUMN) * (farmWidth - 20));

                        layoutParams.topMargin = (int) (btn_y * farmHeight);
                        layoutParams.leftMargin = (int) (btn_x * farmWidth);
                        Log.d("cheatGZ gh field", "" + layoutParams.height + "," + layoutParams.width + "," + layoutParams.topMargin + "," + layoutParams.leftMargin);
                        textView.setLayoutParams(layoutParams);
                        textView.setBackgroundColor(mContext.getResources().getColor(R.color.ColorDarkGrey));
                        textView.setText("加工鉴定");
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(10);
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
