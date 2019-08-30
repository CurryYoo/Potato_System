package com.example.kerne.potato.Util;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kerne.potato.R;
import com.example.kerne.potato.TableActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FarmPlanView {

    public static final double FARM_ROW = 410D;//棚外试验田行，包括中间道路的行数
    public static final double FARM_COLUMN = 40D;//棚外试验田列数
    public static final double SHACK_FARM_ROW = 552D;//棚行数，包括中间道路的行数
    public static final double SHACK_FARM_COLUMN = 14D;//棚列数
    public static final double ROAD_ROW = 10D;//道路行数
    public static final double SHACK_ROAD_ROW = 12D;//棚道路行数
    public static final int RATIO = 1000000;

    public static final int DRAG_EVENT = 0;
    public static final int CLICK_EVENT = 1;

    public List<View> viewList;//试验区域块
    public TextView roadTextView;//道路
    private Context mContext;
    private RelativeLayout mRelativeLayout;//试验田
    private List<JSONObject> mJsonList;//试验区域数据
    private int farmWidth;//试验田宽
    private int farmHeight;//试验田长
    private int endX = 0, endY = 0, endX1 = 0, endY1 = 0;//list中最终存储的x,y轴坐标
    private String expType = null;
    private int row = 0, column = 0;
    private String description = "备注";

    //监听事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, TableActivity.class);
            try {
                intent.putExtra("fieldId", mJsonList.get(Integer.parseInt(v.getTag().toString())).getString("fieldId"));
                intent.putExtra("bigfarmId", mJsonList.get(Integer.parseInt(v.getTag().toString())).getString("bigfarmId"));
                intent.putExtra("type", mJsonList.get(Integer.parseInt(v.getTag().toString())).getString("type"));
                intent.putExtra("expType", mJsonList.get(Integer.parseInt(v.getTag().toString())).getString("expType"));
                intent.putExtra("num", mJsonList.get(Integer.parseInt(v.getTag().toString())).getInt("num"));
                intent.putExtra("rows", mJsonList.get(Integer.parseInt(v.getTag().toString())).getInt("rows"));
                intent.putExtra("name", mJsonList.get(Integer.parseInt(v.getTag().toString())).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mContext.startActivity(intent);
        }
    };


    //监听事件
    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            onLongClickShowTip(v);
            return true;
        }
    };


    private View.OnTouchListener moveTouchListenr = new View.OnTouchListener() {
        int lastX, lastY;

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int ea = event.getAction();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
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
                    if (r > mRelativeLayout.getWidth()) {
                        r = mRelativeLayout.getWidth();
                        l = r - v.getWidth();
                    }
                    if (b > mRelativeLayout.getHeight()) {
                        b = mRelativeLayout.getHeight();
                        t = b - v.getHeight();
                    }
                    v.layout(l, t, r, b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    //x，y轴坐标
                    endX = (int) (((double) v.getLeft() / farmWidth) * RATIO);
                    endY = (int) (((double) v.getTop() / farmHeight) * RATIO);
                    endX1 = (int) (((double) (v.getLeft() + v.getWidth()) / farmWidth) * RATIO);
                    endY1 = (int) (((double) (v.getTop() + v.getHeight()) / farmHeight) * RATIO);
                    try {
                        mJsonList.get(Integer.parseInt(v.getTag().toString())).put("x", endX);
                        mJsonList.get(Integer.parseInt(v.getTag().toString())).put("y", endY);
                        mJsonList.get(Integer.parseInt(v.getTag().toString())).put("x1", endX1);
                        mJsonList.get(Integer.parseInt(v.getTag().toString())).put("y1", endY1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    };

    public FarmPlanView(Context context, RelativeLayout relativeLayout, int farmWidth, int farmHeight, List<JSONObject> list) {
        this.mContext = context;
        this.mRelativeLayout = relativeLayout;
        this.mJsonList = list;
        this.farmWidth = farmWidth;
        this.farmHeight = farmHeight;
    }

    @SuppressLint("SetTextI18n")
    private void onLongClickShowTip(View v) {
        try {
            row = mJsonList.get(Integer.parseInt(v.getTag().toString())).getInt("num") / mJsonList.get(Integer.parseInt(v.getTag().toString())).getInt("rows");
            column = mJsonList.get(Integer.parseInt(v.getTag().toString())).getInt("rows");
            description = mJsonList.get(Integer.parseInt(v.getTag().toString())).getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.layout_popupwindow, null, false);//引入弹窗布局
        TextView fieldData = vPopupWindow.findViewById(R.id.field_data);//popupwindow内容
        TextView fieldDescription = vPopupWindow.findViewById(R.id.field_description);//popupwindow内容
        SanJiaoView sjv = vPopupWindow.findViewById(R.id.sanjiaoView);//popupwindow三角突起
        LinearLayout mainLayout = vPopupWindow.findViewById(R.id.main_layout);//popupwindow主体布局

        fieldData.setText("列：" + column + "  " + "行：" + row);
        if (description != null && description.length() == 0) {
            fieldDescription.setVisibility(View.GONE);
        } else {
            fieldDescription.setText(description);
        }
        PopupWindow popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_trans));

        //设置三角突起的参数
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int left = v.getLeft();
        int top = v.getTop();
        int right = v.getRight();

        //获取手机的分辨率
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        int dWidth = display.getWidth();
        int dHeight = display.getHeight();

        //获取popupwindow布局的padding值
        int paddingLeft = vPopupWindow.getPaddingLeft();
        int paddingRight = vPopupWindow.getPaddingRight();
        vPopupWindow.measure(w, h);
        mainLayout.measure(w, h);

        //popupwindow主体的宽度
        int width1 = mainLayout.getMeasuredWidth();

        //popwindow带padding的宽度
        int widthP = width1 + paddingLeft + paddingRight;

        //popupwindow上方控件的宽度
        int parentWidth = right - left;

        //小三角的宽度
        sjv.measure(w, h);
        int width = sjv.getMeasuredWidth();

        //小三角最大的marginLeft值
        int maxMarginLeft = width1 - width;

        //控件需调整的margin值
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //判断使用那种计算方式来计算小三角的位移量
        int centerIndex;

        if (parentWidth > widthP) {
            centerIndex = maxMarginLeft / 2;
        } else if ((dWidth - left) > widthP) {
            centerIndex = parentWidth / 2 - width / 2 - paddingLeft;
        } else {
            int rWidth = dWidth - right;
            centerIndex = width1 - (parentWidth / 2 + rWidth - paddingRight);
        }

        if (centerIndex > maxMarginLeft) {
            centerIndex = maxMarginLeft;
        }
        params.setMargins(centerIndex, 0, 0, 0);
        sjv.setLayoutParams(params);
        popupWindow.showAsDropDown(v);
        CountTimer countTimer = new CountTimer(4000, 500, popupWindow);//定时消失
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
        roadTextView.setBackgroundResource(R.drawable.bg_road);
        roadTextView.setText("路");
        roadTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            roadTextView.setAutoSizeTextTypeUniformWithConfiguration(6, 12, 1, TypedValue.COMPLEX_UNIT_SP);
        } else {
            roadTextView.setTextSize(6);
        }
        roadTextView.setGravity(Gravity.CENTER);

        return roadTextView;
    }

    /**
     * @param type   生成区域的类型
     * @param action 生成区域的action event 监听事件,0代表可以拖动,1代表可以点击
     * @return
     */

    public List<View> createField(String type, int action) {
        viewList = new ArrayList<>();
        try {
            switch (type) {
                case "common":
                    for (int i = 0; i < mJsonList.size(); i++) {
                        double btn_row = (double) mJsonList.get(i).getInt("num") / mJsonList.get(i).getInt("rows");
                        double btn_column = (double) mJsonList.get(i).getInt("rows");
                        double btn_x = Double.valueOf(mJsonList.get(i).get("x").toString()) / RATIO;
                        double btn_y = Double.valueOf(mJsonList.get(i).get("y").toString()) / RATIO;

                        //映射行数
                        switch (mJsonList.get(i).getString("expType")) {
                            case "株系圃":
                                btn_row = btn_row * 1;
                                break;
                            case "株系选种圃":
                                btn_row = btn_row * 1;
                                break;
                            case "加工鉴定":
                                btn_row = btn_row * 2;
                                break;
                            case "早熟鉴定":
                                btn_row = btn_row * 2;
                                break;
                            case "晚熟鉴定":
                                btn_row = btn_row * 2;
                                break;
                            case "加工预备比":
                                btn_row = btn_row * 3;
                                break;
                            case "早熟预备比":
                                btn_row = btn_row * 3;
                                break;
                            case "晚熟预备比":
                                btn_row = btn_row * 3;
                                break;
                            case "彩色预备比":
                                btn_row = btn_row * 3;
                                break;
                            case "加工品比":
                                btn_row = btn_row * 5;
                                break;
                            case "早熟品比":
                                btn_row = btn_row * 5;
                                break;
                            case "晚熟品比":
                                btn_row = btn_row * 5;
                                break;
                            case "彩色品比":
                                btn_row = btn_row * 5;
                                break;
                            case "品系筛选":
                                btn_row = btn_row * 3;
                                break;
                            case "区域试验":
                                btn_row = btn_row * 5;
                                break;
                            default:
                                break;
                        }

                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.field_view, null, false);
                        TextView textView = view.findViewById(R.id.field_name);
                        textView.setText(mJsonList.get(i).getString("expType"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            textView.setAutoSizeTextTypeUniformWithConfiguration(6, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                        } else {
                            textView.setTextSize(8);
                        }
                        mRelativeLayout.addView(view);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.height = (int) ((btn_row / FARM_ROW) * (farmHeight - 20));
                        layoutParams.width = (int) ((btn_column / FARM_COLUMN) * (farmWidth - 20));
                        layoutParams.topMargin = (int) (btn_y * farmHeight);
                        layoutParams.leftMargin = (int) (btn_x * farmWidth);
                        view.setLayoutParams(layoutParams);
                        view.setTag(i);
                        if (action == DRAG_EVENT) {
                            view.setOnTouchListener(moveTouchListenr);
                        } else if (action == CLICK_EVENT) {
                            view.setOnClickListener(onClickListener);
                            view.setOnLongClickListener(onLongClickListener);
                        }
                        viewList.add(view);
                    }
                    break;
                case "greenhouse":
                    for (int i = 0; i < mJsonList.size(); i++) {
                        double btn_row = (double) mJsonList.get(i).getInt("num") / mJsonList.get(i).getInt("rows");
                        double btn_column = (double) mJsonList.get(i).getInt("rows");
                        double btn_x = Double.valueOf(mJsonList.get(i).get("x").toString()) / RATIO;
                        double btn_y = Double.valueOf(mJsonList.get(i).get("y").toString()) / RATIO;

                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.field_view, null, false);
                        TextView textView = view.findViewById(R.id.field_name);
                        textView.setText(mJsonList.get(i).getString("expType"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            textView.setAutoSizeTextTypeUniformWithConfiguration(6, 12, 1, TypedValue.COMPLEX_UNIT_SP);
                        } else {
                            textView.setTextSize(8);
                        }
                        mRelativeLayout.addView(view);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.width = (int) ((btn_column / SHACK_FARM_COLUMN) * (farmWidth - 20));
                        layoutParams.height = (int) ((btn_row / SHACK_FARM_ROW) * (farmHeight - 20));
                        layoutParams.leftMargin = (int) (btn_x * farmWidth);
                        layoutParams.topMargin = (int) (btn_y * farmHeight);
                        view.setLayoutParams(layoutParams);
                        view.setTag(i);
                        if (action == DRAG_EVENT) {
                            view.setOnTouchListener(moveTouchListenr);
                        } else if (action == CLICK_EVENT) {
                            view.setOnClickListener(onClickListener);
                            view.setOnLongClickListener(onLongClickListener);
                        }
                        viewList.add(view);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewList;
    }
}
