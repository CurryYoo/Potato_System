package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.SpaceConsumer;
import com.example.kerne.potato.Fragment.InShackFragment;
import com.example.kerne.potato.Fragment.OutShackFragment;
import com.example.kerne.potato.Util.FarmPlanView;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kerne.potato.Util.ChangeStatusBar.setStatusBarColor;
import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class FarmPlanActivity extends AppCompatActivity {

    public static Fragment outShackFragment;
    public static Fragment inShackFragment;
    private static final int DATA_OK = 0;
    public String titles[] = new String[]{"棚外", "棚内"};
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.right_one_button)
    ImageView rightOneButton;
    @BindView(R.id.right_one_layout)
    LinearLayout rightOneLayout;
    @BindView(R.id.plan_indicator)
    TabPageIndicator planIndicator;
    @BindView(R.id.plan_viewPager)
    ViewPager planViewPager;
    @BindView(R.id.title_bar)
    LinearLayout titleBar;
    @BindView(R.id.edit_image)
    ImageView editImage;
    @BindView(R.id.edit_planA)
    LinearLayout editPlanA;
    @BindView(R.id.zoom_out_controls)
    LinearLayout zoomOutControls;
    @BindView(R.id.zoom_in_controls)
    LinearLayout zoomInControls;
    @BindView(R.id.out_farm)
    RelativeLayout outFarm;
    @BindView(R.id.in_farm)
    RelativeLayout inFarm;
    @BindView(R.id.main_farm)
    LinearLayout mainFarm;
    @BindView(R.id.border_farm)
    LinearLayout borderFarm;
    @BindView(R.id.h_scroll)
    LinearLayout hScroll;
    @BindView(R.id.v_scroll)
    LinearLayout vScroll;
    @BindView(R.id.swipe_layout)
    RelativeLayout swipeLayout;
    @BindView(R.id.farm_planA)
    RelativeLayout farmPlanA;
    @BindView(R.id.farm_planB)
    LinearLayout farmPlanB;
    @BindView(R.id.base_farm)
    RelativeLayout baseFarm;
    @BindView(R.id.cover_view)
    View coverView;
    private TabPageIndicatorAdapter mAdpter;
    private int currentPage = 0;
    private int currentPlan = 0;//0表示规划方式为planA，1表示规划方式为planB
    private int firstInit = 0;//设置第一次加载视图，之后不再重新加载
    private float size = 1f;
    private Boolean flag = false;//开始时处于不可编辑状态

    private Context self;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase db;
    private List<JSONObject> mBigFarmList = new ArrayList<>();
    private List<JSONObject> mOutShackList = new ArrayList<>();
    private List<JSONObject> mInShackList = new ArrayList<>();
    private String bigfarmId;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    if (currentPlan == 0) {
                        farmPlanA.setVisibility(View.GONE);
                        farmPlanB.setVisibility(View.VISIBLE);
                        if (firstInit == 0) {
                            initViewB();
                            firstInit = 1;
                        }
                        currentPlan = 1;
                    } else {
                        farmPlanB.setVisibility(View.GONE);
                        farmPlanA.setVisibility(View.VISIBLE);
                        currentPlan = 0;
                    }
                    break;
                case R.id.zoom_out_controls:
                    if (size > 1) {
                        size = size - 0.5f;
                        mainFarm.setScaleX(size);
                        mainFarm.setScaleY(size);
                        if (size == 1) {
                            baseFarm.scrollTo(0, 0);
                            hScroll.setVisibility(View.GONE);
                            vScroll.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.zoom_in_controls:
                    if (size < 5) {
                        hScroll.setVisibility(View.VISIBLE);
                        vScroll.setVisibility(View.VISIBLE);
                        size = size + 0.5f;
                        mainFarm.setScaleX(size);
                        mainFarm.setScaleY(size);
                    }
                    break;
                case R.id.edit_planA:
                    if (!flag) {
                        coverView.setVisibility(View.INVISIBLE);
                        editImage.setBackgroundResource(R.drawable.no_save);
                        flag = true;
                    } else {
                        coverView.setVisibility(View.VISIBLE);
                        editImage.setBackgroundResource(R.drawable.edit);
                        flag = false;
                        editor.putBoolean("upload_data", true);
                        editor.apply();
                        showShortToast(self, "保存完成");
                        //保存图片
//                        savePlanImage();
                        //保存数据
                        updateData();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        int lastX, lastY;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (v.getId()) {
                case R.id.h_scroll:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = (int) event.getRawX();
                            hScroll.setElevation(5);
                        case MotionEvent.ACTION_MOVE:
                            int dx = (int) (event.getRawX() - lastX);
                            lastX = (int) event.getRawX();
                            baseFarm.scrollBy(-dx, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            hScroll.setElevation(0);
                            if (baseFarm.getScrollX() < -baseFarm.getWidth() * (size - 1) / 2) {
                                baseFarm.scrollTo((int) (-baseFarm.getWidth() * (size - 1) / 2), baseFarm.getScrollY());
                            }
                            if (baseFarm.getScrollX() > baseFarm.getWidth() * (size - 1) / 2) {
                                baseFarm.scrollTo((int) (baseFarm.getWidth() * (size - 1) / 2), baseFarm.getScrollY());
                            }
                            break;
                    }
                    break;
                case R.id.v_scroll:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastY = (int) event.getRawY();
                            vScroll.setElevation(5);
                        case MotionEvent.ACTION_MOVE:
                            int dy = (int) (event.getRawY() - lastY);
                            lastY = (int) event.getRawY();
                            baseFarm.scrollBy(0, -dy);
                            break;
                        case MotionEvent.ACTION_UP:
                            vScroll.setElevation(0);
                            if (baseFarm.getScrollY() < -baseFarm.getHeight() * (size - 1) / 2) {
                                baseFarm.scrollTo(baseFarm.getScrollX(), (int) -(baseFarm.getHeight() * (size - 1) / 2));
                            }
                            if (baseFarm.getScrollY() > baseFarm.getHeight() * (size - 1) / 2) {
                                baseFarm.scrollTo(baseFarm.getScrollX(), (int) (baseFarm.getHeight() * (size - 1) / 2));
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.primary_background);
        setContentView(R.layout.activity_farm_plan);
        ButterKnife.bind(this);
        self = this;
        sp = self.getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();
        bigfarmId = getIntent().getStringExtra("bigfarmId");
        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 14);
        db = dbHelper.getWritableDatabase();
        sqLiteDatabase = dbHelper.getWritableDatabase();
        initToolBar();
        initViewA();
    }

    private void initToolBar() {
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.change);
        titleText.setText(getString(R.string.farm_plan));

        rightOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        leftOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        rightOneLayout.setOnClickListener(onClickListener);
        leftOneLayout.setOnClickListener(onClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            leftOneLayout.setTooltipText(getString(R.string.back_left));
            rightOneLayout.setTooltipText("切换");
        }
    }

    private void initViewA() {
        editImage.setBackgroundResource(R.drawable.edit);
        editPlanA.setOnClickListener(onClickListener);
        zoomInControls.setOnClickListener(onClickListener);
        zoomOutControls.setOnClickListener(onClickListener);
        vScroll.setOnTouchListener(onTouchListener);
        hScroll.setOnTouchListener(onTouchListener);
        coverView.setOnClickListener(null);
        //仿iOS下拉留白
        SmartSwipe.wrap(swipeLayout)
                .addConsumer(new SpaceConsumer())
                .enableVertical();
        //延迟加载视图
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                initView();
            }
        }, 50); //延迟ms
    }

    private void initFieldData() {
        //获取棚外数据
        mOutShackList.clear();
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
                    mOutShackList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor1.moveToNext());
        }
        cursor1.close();

        //获取大棚区域
        mInShackList.clear();
        Cursor cursor2 = db.query("LocalField", null, "bigfarmId=? and type=?", new String[]{bigfarmId, "greenhouse"}, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("fieldId", cursor2.getString(cursor2.getColumnIndex("id")));
                    jsonObject0.put("bigfarmId", cursor2.getString(cursor2.getColumnIndex("bigfarmId")));
                    jsonObject0.put("type", "greenhouse");
                    jsonObject0.put("expType", cursor2.getString(cursor2.getColumnIndex("expType")));
                    jsonObject0.put("num", cursor2.getInt(cursor2.getColumnIndex("num")));
                    jsonObject0.put("rows", cursor2.getInt(cursor2.getColumnIndex("rows")));
                    jsonObject0.put("x", cursor2.getInt(cursor2.getColumnIndex("moveX")));
                    jsonObject0.put("y", cursor2.getInt(cursor2.getColumnIndex("moveY")));
                    jsonObject0.put("name", cursor2.getString(cursor2.getColumnIndex("name")));
                    jsonObject0.put("description", cursor2.getString(cursor2.getColumnIndex("description")));
                    mInShackList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor2.moveToNext());
        }
        cursor2.close();
    }

    private void initView() {
        if (bigfarmId == null) {
            return;
        }
        initFieldData();
        if (outFarm != null && inFarm != null) {
            //加载棚外,设置farm大小
            outFarm.removeAllViews();
            FarmPlanView farmPlanView = new FarmPlanView(this, outFarm, outFarm.getWidth(), outFarm.getHeight(), mOutShackList);
            farmPlanView.createRoad("common");
            farmPlanView.createField("common", FarmPlanView.DRAG_EVENT);
            inFarm.removeAllViews();
            FarmPlanView farmPlanView2 = new FarmPlanView(this, inFarm, inFarm.getWidth(), inFarm.getHeight(), mInShackList);
            farmPlanView2.createRoad("greenhouse");
            farmPlanView2.createField("greenhouse", FarmPlanView.DRAG_EVENT);
        }
    }

    private void updateData() {
        List<JSONObject> mFarmList = new ArrayList<>();
        mFarmList.addAll(mOutShackList);
        mFarmList.addAll(mInShackList);
        List<ContentValues> contentValuesList = assembleData(mFarmList);
        for (int i = 0; i < contentValuesList.size(); i++) {
            db.update("LocalField", contentValuesList.get(i), "id=?", new String[]{contentValuesList.get(i).getAsString("id")});
        }
    }

    //组装数据
    private List<ContentValues> assembleData(List<JSONObject> jsonObjectList) {
        List<ContentValues> contentValuesList = new ArrayList<>();

        for (int i = 0; i < jsonObjectList.size(); i++) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", jsonObjectList.get(i).getString("fieldId"));
                contentValues.put("moveX", jsonObjectList.get(i).getInt("x"));
                contentValues.put("moveY", jsonObjectList.get(i).getInt("y"));
                contentValues.put("moveX1", jsonObjectList.get(i).getInt("x1"));
                contentValues.put("moveY1", jsonObjectList.get(i).getInt("y1"));
                contentValues.put("isUpdate", 0);
                contentValuesList.add(contentValues);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return contentValuesList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        dbHelper.close();
    }

    //加载视图
    private void initViewB() {
        outShackFragment = OutShackFragment.newInstance();
        inShackFragment = InShackFragment.newInstance();
        //给ViewPager设置Adapter
        mAdpter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        //与ViewPager绑在一起（核心步骤）
        planViewPager.setAdapter(mAdpter);
        planIndicator.setViewPager(planViewPager);
        planIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    //adapter
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        Bundle args = new Bundle();

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    args.putString("arg", titles[position]);
                    outShackFragment.setArguments(args);
                    return outShackFragment;
                case 1:
                    args.putString("arg", titles[position]);
                    inShackFragment.setArguments(args);
                    return inShackFragment;
            }
            return outShackFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position % titles.length];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //不销毁fragment
//            super.destroyItem(container, position, object);
        }
    }
}
