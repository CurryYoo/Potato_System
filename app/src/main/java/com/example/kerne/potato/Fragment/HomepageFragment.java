package com.example.kerne.potato.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.SpaceConsumer;
import com.billy.android.swipe.listener.SwipeListener;
import com.example.kerne.potato.FarmPlanActivity;
import com.example.kerne.potato.LoginActivity;
import com.example.kerne.potato.MainActivity;
import com.example.kerne.potato.R;
import com.example.kerne.potato.Util.FarmPlanView;
import com.example.kerne.potato.Util.HttpRequest;
import com.example.kerne.potato.Util.UserRole;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.facebook.stetho.Stetho;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.app.Activity.RESULT_OK;
import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class HomepageFragment extends Fragment {

    private static final int BIGFARMLIST_OK = 0;
    private static final int FARMLIST_OK = 1;
    private static final int LOCALFIELD_OK = 2;
    private static final int LOCALBLOCK_OK = 3;
    private static final int LOCALSPECIES_OK = 4;
    private static final int DATA_OK = 5;
    private static final int FIELD_DATA_OK = 6;
    private static final int CREATE_BIGFARM_OK = 10;
    private static final int UPDATE_BIGFARMIMG_OK = 11;
    private static final int CREATE_FIELD_OK = 12;
    private static final int DOWNLOAD_FIELD_OK = 13;
    private static final int DOWNLOAD_BLOCK_OK = 14;
    private static final int UPLOAD_FIELD_OK = 15;
    private static final int UPLOAD_BLOCK_OK = 16;
    private static final int UPLOAD_DESCRIPTION_OK = 17;
    private static final int UPLOAD_SURVEY_OK = 18;
    private static int downloadSuccess_Num = 0;
    private static int request_Num = 4;
    private static int uploadSuccess_Num = 0;
    private static int upload_Num = 2;

    private static final int UPLOAD_SURVEY_WORDS = 20;
    private static final int UPLOAD_SURVEY_IMG1 = 21;
    private static final int UPLOAD_SURVEY_IMG2 = 22;
    private static final int UPLOAD_SURVEY_IMG3 = 23;
    private static final int UPLOAD_SURVEY_IMG4 = 24;
    private static final int UPLOAD_SURVEY_IMG5 = 25;
    private static final int[] request_num = {0, 5}; //请求成功的次数，总请求次数
    private static boolean isOnline = false;
    private SweetAlertDialog downloadDataDialog;
    private SweetAlertDialog updateDialog;
    String img1;
    String img2;
    String img3;
    String img4;
    String img5;
    Badge badge;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    //用户角色字段
    String userRole = "farmer";
    private LinearLayout baseFarm;
    private View view;
    private Context self;
    private RelativeLayout swipeLayout;
    private LinearLayout createNewFarm;
    private LinearLayout btnDownload;
    private ImageView uploadIcon;
    private LinearLayout btnUpload;
    private Spinner homepageYears;
    private LinearLayout planFarm;
    private RelativeLayout outFarm;
    private RelativeLayout inFarm;
    private LinearLayout mainFarm;
    private LinearLayout zoomOut;
    private LinearLayout zoomIn;
    private LinearLayout hScroll;
    private LinearLayout vScroll;

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase db;
    private String Fid[] = new String[5000];
    private List<JSONObject> mBigFarmList = new ArrayList<>();
    private List<JSONObject> mOutShackList = new ArrayList<>();
    private List<JSONObject> mInShackList = new ArrayList<>();
    private List<View> mOutList = new ArrayList<>();
    private List<View> mInList = new ArrayList<>();
    private List<String> mYears = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private String bigfarmId;

    private float size = 1;//田地放大倍数

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @SuppressLint("HandlerLeak")
    private Handler childHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_SURVEY_WORDS:
                    request_num[0]++;
                    break;
                case UPLOAD_SURVEY_IMG1:
                    request_num[0]++;
                    break;
                case UPLOAD_SURVEY_IMG2:
                    request_num[0]++;
                    break;
                case UPLOAD_SURVEY_IMG3:
                    request_num[0]++;
                    break;
                case UPLOAD_SURVEY_IMG4:
                    request_num[0]++;
                    break;
                case UPLOAD_SURVEY_IMG5:
                    request_num[0]++;
                    break;
                default:
                    break;
            }
            if (request_num[0] == request_num[1]) {
//                Message message = new Message();
//                message.what = UPLOAD_SURVEY_OK;
//                mHandler.sendMessage(message);
                Log.i("childHandler", "upload img ok");
            }

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BIGFARMLIST_OK:
                    downloadSuccess_Num++;
                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_farm_exp_type));
                    break;
//                case FARMLIST_OK:
//                    downloadSuccess_Num++;
//                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_farm_plan_data));
//                    break;
                case LOCALFIELD_OK:
                    downloadSuccess_Num++;
                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_species_data));
                    break;
                case LOCALBLOCK_OK:
                    downloadSuccess_Num++;
                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_localspecies_data));
                    break;
                case LOCALSPECIES_OK:
                    downloadSuccess_Num++;
                    break;
                case DATA_OK:
                    if (mBigFarmList.size() > 0) {
                        //spinner加载
                        try {
                            bigfarmId = mBigFarmList.get(0).getString("bigfarmId");
                            mYears.clear();
                            for (int i = 0; i < mBigFarmList.size(); i++) {
                                mYears.add(mBigFarmList.get(i).getString("year"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Collections.sort(mYears, Collections.<String>reverseOrder());
                        spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner_textview, mYears);
                        homepageYears.setAdapter(spinnerAdapter);
                        initView();
                    }
                    break;
                default:
                    break;
            }
            if (downloadSuccess_Num == request_Num) {
                downloadDataDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                downloadDataDialog.setCancelable(true);
                downloadDataDialog.setTitleText(getContext().getString(R.string.download_complete));
                downloadDataDialog.setContentText(null);
                downloadSuccess_Num = 0;
                initData();
                MainActivity mainActivity = new MainActivity();
                mainActivity.selectFarm(bigfarmId);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CREATE_BIGFARM_OK: //创建bigfarm完成
                    uploadBigfarmImage();
                    CreateField();
                    mBigFarmList.clear();
                    mYears.clear();
                    initData();
                    break;
                case CREATE_FIELD_OK: //创建field完成
                    downloadFieldAndBlock();
                    break;
                case DOWNLOAD_FIELD_OK: //下载field数据完成
                    updateFieldData();
                    break;
                case DOWNLOAD_BLOCK_OK: //下载block数据完成
                    uploadPlanData();
                    break;
                case UPLOAD_FIELD_OK:
                    break;
                case UPLOAD_BLOCK_OK: //上传block数据完成
                    uploadSurveyData();
                    uploadSuccess_Num++;
                    break;
                case UPLOAD_DESCRIPTION_OK:
                    uploadSuccess_Num++;
                    break;
                case UPLOAD_SURVEY_OK: //上传调查数据完成
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.selectFarm(bigfarmId);
                    updateDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    updateDialog.setCancelable(true);
                    updateDialog.setTitleText(getContext().getString(R.string.update_complete));
                    updateDialog.setContentText(null);
                    showShortToast(self, getString(R.string.toast_upload_data_complete));
                    break;
                default:
                    break;
            }
            if (uploadSuccess_Num == upload_Num) {
                //
            }
        }
    };
    //监听事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.create_new_farm:
                    userRole = UserRole.getUserRole();
                    if (!userRole.equals("farmer")) {
                        final SweetAlertDialog createFarmDialog = new SweetAlertDialog(self, SweetAlertDialog.NORMAL_TYPE)
                                .setContentText(getString(R.string.createFarm))
                                .setConfirmText("确定")
                                .setCancelText("取消");
                        LayoutInflater mlayoutInflater = LayoutInflater.from(getContext());
                        View view = mlayoutInflater.inflate(R.layout.dialog_input, null);
                        final EditText dialog_input = view.findViewById(R.id.dialog_input);
                        dialog_input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        dialog_input.setHint("增加新一年的实验田数据");
                        createFarmDialog.addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        createFarmDialog.setCustomView(view);
                        createFarmDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (dialog_input.getText().toString().length() == 0) {
                                    showShortToast(getContext(), "年份为空,输入无效");
                                } else if (checkYears(dialog_input.getText().toString(), mYears)) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //初始化本地bigfarm数据
                                            String bigfarmId_ = initLocalBigfarm(dialog_input.getText().toString());
                                            //初始化本地localfield数据
                                            initLocalField(bigfarmId_);

                                            mBigFarmList.clear();
                                            mYears.clear();
                                            initData();

                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showShortToast(self, "添加成功");
                                                }
                                            });

                                            editor.putBoolean("upload_data", true);
                                            editor.apply();
//                                        MainActivity mainActivity = new MainActivity();
//                                        mainActivity.selectFarm(bigfarmId);
                                        }
                                    }).start();

                                } else {
                                    showShortToast(getContext(), "年份重复,输入无效");
                                }
                            }
                        });

                        createFarmDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                        createFarmDialog.show();
                    } else {
                        showShortToast(self, getString(R.string.toast_log_in));
                        Intent intent = new Intent(self, LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }

                    break;
                case R.id.btn_download:
                    //检查网络状况
                    if (!isOnline) {
                        showShortToast(self, getString(R.string.toast_check_network_state));
                        break;
                    }
                    if (sp.getBoolean("upload_data", false)) {
                        final SweetAlertDialog downloadWarnDialog = new SweetAlertDialog(self, SweetAlertDialog.NORMAL_TYPE)
                                .setContentText(getString(R.string.download_data_warn))
                                .setConfirmText("确定")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        editor.putBoolean("upload_data", false);
                                        editor.apply();
                                        if (badge != null) {
                                            badge.hide(false);
                                        }
                                        mBigFarmList.clear();
                                        mYears.clear();
                                        downloadData();
                                    }
                                });
                        downloadWarnDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                        downloadWarnDialog.show();
                    } else {
                        mBigFarmList.clear();
                        downloadData();
                    }
                    break;
                case R.id.btn_upload:
                    if (bigfarmId == null) {
                        showShortToast(getContext(), getString(R.string.toast_database_null));
                        break;
                    } else {
                        //检查网络状况
                        if (!isOnline) {
                            showShortToast(self, getString(R.string.toast_check_network_state));
                            break;
                        }
                        //将暂存的数据从数据库取出并提交到远程服务器
                        userRole = UserRole.getUserRole();
                        if (!userRole.equals("farmer")) {
                            if (badge != null) {
                                badge.hide(false);
                            }
                            editor.putBoolean("upload_data", false);
                            editor.apply();
                            updateDialog = new SweetAlertDialog(self, SweetAlertDialog.PROGRESS_TYPE);
                            updateDialog.setTitleText(getString(R.string.update_data));
                            updateDialog.setContentText("上传本地数据耗时较长，请勿中途退出，以免造成数据缺失");
                            updateDialog.setCancelable(false);
                            updateDialog.show();

                            CreateBigfarm();
//                            uploadPlanData();
//                            uploadSurveyData();

                        } else {
                            showShortToast(self, getString(R.string.toast_log_in));
                            Intent intent = new Intent(self, LoginActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    }
                    break;
                case R.id.plan_farm:
                    if (bigfarmId == null) {
                        showShortToast(getContext(), getString(R.string.toast_database_null));
                        break;
                    } else {
                        Intent intent = new Intent(self, FarmPlanActivity.class);
                        intent.putExtra("bigfarmId", bigfarmId);
                        self.startActivity(intent);
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

    //初始化年份
    private String initLocalBigfarm(String year) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("bigfarmId", year);
        contentValues.put("name", year);
        contentValues.put("year", year);
        contentValues.put("isCreated", 0);
        db.insert("BigfarmList", null, contentValues);
        contentValues.clear();
        return year;
    }

    //初始化本地field
    private void initLocalField(String mBigfarmId) {
        String[] expTypes = {"株系圃", "株系选种圃", "加工鉴定", "早熟鉴定", "晚熟鉴定", "加工预备比",
                "早熟预备比", "晚熟预备比", "彩色预备比", "加工品比", "早熟品比", "晚熟品比", "彩色品比", "品系筛选", "区域试验", //以上为棚外试验田
                "抗旱棚", "杂交棚1", "杂交棚2", "杂交棚3", "单株棚1", "单株棚2", "单株棚3", "单株棚4",//以下为棚内试验田
                "单株棚5", "单株棚6", "单株棚7"};
        String[] expTypes_out = {"株系圃", "株系选种圃", "加工鉴定", "早熟鉴定", "晚熟鉴定", "加工预备比",
                "早熟预备比", "晚熟预备比", "彩色预备比", "加工品比", "早熟品比", "晚熟品比", "彩色品比", "品系筛选", "区域试验"};
        //以下棚内试验田
        String[] expTypes_in = {"抗旱棚", "杂交棚1", "杂交棚2", "杂交棚3", "单株棚1", "单株棚2", "单株棚3", "单株棚4",
                "单株棚5", "单株棚6", "单株棚7"};
//        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < expTypes.length; i++) {
            contentValues.put("id", mBigfarmId + "_" + i);
            contentValues.put("name", expTypes[i]);
            contentValues.put("expType", expTypes[i]);
            contentValues.put("moveX", 0);
            contentValues.put("moveY", 0);
            contentValues.put("moveX1", 0);
            contentValues.put("moveY1", 0);
            contentValues.put("num", 0);
            contentValues.put("rows", 0);
            contentValues.put("bigfarmId", mBigfarmId);
            contentValues.put("description", "");
            contentValues.put("type", i > 14 ? "greenhouse" : "common");
            contentValues.put("isCreated", 0);
            db.insert("LocalField", null, contentValues);
            contentValues.clear();
        }

    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                bigfarmId = mBigFarmList.get(position).getString("bigfarmId");
                initView();
                MainActivity mainActivity = new MainActivity();
                mainActivity.selectFarm(bigfarmId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    public static HomepageFragment newInstance() {
        return new HomepageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage, container, false);
        self = getContext();
        Stetho.initializeWithDefaults(self);

        init();
        return view;
    }

    private Boolean checkYears(String y, List<String> ys) {
        for (int i = 0; i < ys.size(); i++) {
            if (ys.get(i).equals(y)) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        sp = self.getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();

        swipeLayout = view.findViewById(R.id.swipe_layout);
        createNewFarm = view.findViewById(R.id.create_new_farm);
        btnDownload = view.findViewById(R.id.btn_download);
        btnUpload = view.findViewById(R.id.btn_upload);
        uploadIcon = view.findViewById(R.id.upload_icon);
        homepageYears = view.findViewById(R.id.homepage_years);
        planFarm = view.findViewById(R.id.plan_farm);
        outFarm = view.findViewById(R.id.out_farm);
        inFarm = view.findViewById(R.id.in_farm);
        zoomOut = view.findViewById(R.id.zoom_out_controls);
        zoomIn = view.findViewById(R.id.zoom_in_controls);
        mainFarm = view.findViewById(R.id.main_farm);
        hScroll = view.findViewById(R.id.h_scroll);
        vScroll = view.findViewById(R.id.v_scroll);
        baseFarm = view.findViewById(R.id.base_farm);

        homepageYears.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down_dark);


        createNewFarm.setOnClickListener(onClickListener);
        btnDownload.setOnClickListener(onClickListener);
        btnUpload.setOnClickListener(onClickListener);
        planFarm.setOnClickListener(onClickListener);
        zoomOut.setOnClickListener(onClickListener);
        zoomIn.setOnClickListener(onClickListener);

        homepageYears.setOnItemSelectedListener(onItemSelectedListener);

        hScroll.setOnTouchListener(onTouchListener);
        vScroll.setOnTouchListener(onTouchListener);

        dbHelper = new SpeciesDBHelper(getContext(), "SpeciesTable.db", null, 15);
        db = dbHelper.getWritableDatabase();
        sqLiteDatabase = dbHelper.getWritableDatabase();

        //仿iOS下拉留白
        SmartSwipe.wrap(swipeLayout)
                .addConsumer(new SpaceConsumer())
                .enableVertical()
        .addListener(new SwipeListener() {
            @Override
            public void onConsumerAttachedToWrapper(SmartSwipeWrapper wrapper, SwipeConsumer consumer) {

            }

            @Override
            public void onConsumerDetachedFromWrapper(SmartSwipeWrapper wrapper, SwipeConsumer consumer) {

            }

            @Override
            public void onSwipeStateChanged(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int state, int direction, float progress) {

            }

            @Override
            public void onSwipeStart(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {

            }

            @Override
            public void onSwipeProcess(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction, boolean settling, float progress) {

            }

            @Override
            public void onSwipeRelease(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction, float progress, float xVelocity, float yVelocity) {
                initView();
            }

            @Override
            public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {

            }

            @Override
            public void onSwipeClosed(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {

            }
        });
        initData();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        self.registerReceiver(networkChangeReceiver, intentFilter);
    }


    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
//                dbHelper = new SpeciesDBHelper(getContext(), "SpeciesTable.db", null, 14);
//                db = dbHelper.getReadableDatabase();

                Cursor cursor = db.query("BigfarmList", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("bigfarmId", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                            jsonObject0.put("name", cursor.getString(cursor.getColumnIndex("name")));
                            jsonObject0.put("description", cursor.getString(cursor.getColumnIndex("description")));
                            jsonObject0.put("img", cursor.getString(cursor.getColumnIndex("img")));
                            jsonObject0.put("year", cursor.getInt(cursor.getColumnIndex("year")));
                            jsonObject0.put("uri", cursor.getString(cursor.getColumnIndex("uri")));
                            mBigFarmList.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                Collections.reverse(mBigFarmList);//对于年份倒序显示
                cursor.close();
//                dbHelper.close();
//                db.close();

                Message msg = new Message();
                msg.what = DATA_OK;
                myHandler.sendMessage(msg);
                Looper.loop();
            }
        }).start();
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

        if (outFarm != null) {
            //加载棚外,设置farm大小
//                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) outFarm.getLayoutParams();
//                    layoutParams.width = baseFarm.getWidth();
//                    layoutParams.height = (int) (0.95 * baseFarm.getWidth());
//                    outFarm.setLayoutParams(layoutParams);
            outFarm.removeAllViews();
            FarmPlanView farmPlanView = new FarmPlanView(getContext(), outFarm, outFarm.getWidth(), outFarm.getHeight(), mOutShackList);
            farmPlanView.createRoad("common");
            farmPlanView.createField("common", FarmPlanView.CLICK_EVENT);
        }

        if (inFarm != null) {
            //加载棚内
//                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) inFarm.getLayoutParams();
//                    layoutParams2.width = (int) (0.6 * baseFarm.getHeight());
//                    layoutParams2.height = baseFarm.getHeight();
//                    inFarm.setLayoutParams(layoutParams2);
            inFarm.removeAllViews();
            FarmPlanView farmPlanView = new FarmPlanView(getContext(), inFarm, inFarm.getWidth(), inFarm.getHeight(), mInShackList);
            farmPlanView.createRoad("greenhouse");
            farmPlanView.createField("greenhouse", FarmPlanView.CLICK_EVENT);
        }
    }

    private void downloadData() {
        downloadDataDialog = new SweetAlertDialog(self, SweetAlertDialog.PROGRESS_TYPE);
        downloadDataDialog.setTitleText(getString(R.string.download_farm_data));
        downloadDataDialog.setContentText("下载云端数据耗时较长，请勿中途退出，以免造成数据缺失");
        downloadDataDialog.setCancelable(false);
        downloadDataDialog.show();

        HttpRequest.HttpRequest_bigfarm(null, self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("BigfarmList", null, null);
                        File cache = new File(self.getExternalCacheDir(), "cache"); //缓存图片的文件夹目录
                        if (!cache.exists()) {
                            cache.mkdirs();
                        }
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            JSONObject jsonObject0 = new JSONObject();
                            ContentValues contentValues = new ContentValues();
                            for (int i = 0; i < total; i++) {
                                jsonObject0 = rows.getJSONObject(i);

                                contentValues.put("bigfarmId", jsonObject0.getString("id"));
                                contentValues.put("name", jsonObject0.getString("name"));
                                contentValues.put("description", jsonObject0.getString("description"));
                                contentValues.put("img", jsonObject0.getString("img"));
                                if (jsonObject0.get("year") != null) {
                                    contentValues.put("year", jsonObject0.getInt("year"));
                                }
                                if (jsonObject0.get("width") != null) {
                                    contentValues.put("width", jsonObject0.getInt("width"));
                                }
                                if (jsonObject0.get("length") != null) {
                                    contentValues.put("length", jsonObject0.getInt("length"));
                                }
                                contentValues.put("isCreated", 1);

//                                Uri uri = getImageURI(HttpRequest.serverUrl + jsonObject0.getString("img"), cache);
//                                contentValues.put("uri", uri.toString());

                                db.insert("BigfarmList", null, contentValues);
                                contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = BIGFARMLIST_OK;
                        myHandler.sendMessage(msg);
                    }
                }).start();

            }
        });

//        HttpRequest.HttpRequest_farm(null, self, new HttpRequest.HttpCallback() {
//            @Override
//            public void onSuccess(final JSONObject result) { //获取FarmList信息
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        db.delete("FarmList", null, null);
//                        try {
//                            JSONArray rows = new JSONArray();
//                            rows = result.getJSONArray("rows");
//                            int total = result.getInt("total");
//                            JSONObject jsonObject0;
//                            for (int i = 0; i < total; i++) {
//                                jsonObject0 = rows.getJSONObject(i);
//                                ContentValues contentValues = new ContentValues();
//                                contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
//                                if (jsonObject0.getBoolean("deleted")) {
//                                    contentValues.put("deleted", "true");
//                                } else {
//                                    contentValues.put("deleted", "false");
//                                }
//                                contentValues.put("name", jsonObject0.getString("name"));
//                                if (jsonObject0.get("length") != null) {
//                                    contentValues.put("length", jsonObject0.getInt("length"));
//                                }
//                                if (jsonObject0.get("width") != null) {
//                                    contentValues.put("width", jsonObject0.getInt("width"));
//                                }
//                                contentValues.put("type", jsonObject0.getString("type"));
//                                contentValues.put("bigfarmId", jsonObject0.getString("bigfarmId"));
//
//                                db.insert("FarmList", null, contentValues);
//                                contentValues.clear();
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        Message msg = new Message();
//                        msg.what = FARMLIST_OK;
//                        myHandler.sendMessage(msg);
//                    }
//                }).start();
//
//            }
//        });

        HttpRequest.HttpRequest_map(null, self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) { //获取ExperimentField信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("LocalField", null, null);
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            ContentValues contentValues = new ContentValues();
                            JSONObject jsonObject0;
                            for (int i = 0; i < total; i++) {
                                jsonObject0 = rows.getJSONObject(i);

                                contentValues.put("id", jsonObject0.getString("id"));
                                contentValues.put("name", jsonObject0.getString("name"));
                                if (jsonObject0.getBoolean("deleted")) {
                                    contentValues.put("deleted", "true");
                                } else {
                                    contentValues.put("deleted", "false");
                                }
                                contentValues.put("expType", jsonObject0.getString("expType"));
                                if (jsonObject0.get("moveX") != null) {
                                    if (!jsonObject0.getString("moveX").equals("null")) {
                                        contentValues.put("moveX", jsonObject0.getInt("moveX"));
                                    }
                                }
                                if (jsonObject0.get("moveY") != null) {
                                    if (!jsonObject0.getString("moveY").equals("null")) {
                                        contentValues.put("moveY", jsonObject0.getInt("moveY"));
                                    }
                                }
                                if (jsonObject0.get("moveX1") != null) {
                                    if (!jsonObject0.getString("moveX1").equals("null")) {
                                        contentValues.put("moveX1", jsonObject0.getInt("moveX1"));
                                    }
                                }
                                if (jsonObject0.get("moveY1") != null) {
                                    if (!jsonObject0.getString("moveY1").equals("null")) {
                                        contentValues.put("moveY1", jsonObject0.getInt("moveY1"));
                                    }
                                }
                                if (jsonObject0.get("num") != null) {
                                    contentValues.put("num", jsonObject0.getInt("num"));
                                }
                                contentValues.put("color", jsonObject0.getString("color"));
                                contentValues.put("bigfarmId", jsonObject0.getString("bigfarmId"));
                                if (jsonObject0.get("rows") != null) {
                                    contentValues.put("rows", jsonObject0.getInt("rows"));
                                }
                                if (jsonObject0.get("length") != null) {
                                    contentValues.put("length", jsonObject0.getInt("length"));
                                }
                                if (jsonObject0.get("width") != null) {
                                    contentValues.put("width", jsonObject0.getInt("width"));
                                }
                                contentValues.put("description", jsonObject0.getString("description"));
                                contentValues.put("type", jsonObject0.getString("type"));
                                contentValues.put("speciesList", jsonObject0.getString("speciesList"));
                                contentValues.put("isCreated", 2);
                                contentValues.put("isUpdate", 1);

                                db.insert("LocalField", null, contentValues);
                                contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = LOCALFIELD_OK;
                        myHandler.sendMessage(msg);
                    }
                }).start();

            }
        });

        HttpRequest.HttpRequest_Species(self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("LocalBlock", null, null);
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            ContentValues contentValues = new ContentValues();
                            JSONObject jsonObject0;
                            for (int i = 0; i < total; i++) {
                                jsonObject0 = rows.getJSONObject(i);

                                contentValues.put("blockId", jsonObject0.getString("id"));
                                contentValues.put("fieldId", jsonObject0.getString("fieldId"));
                                contentValues.put("speciesId", jsonObject0.getString("speciesId"));

                                if (jsonObject0.get("x") != null) {
                                    if (!jsonObject0.getString("x").equals("null")) {
                                        contentValues.put("x", jsonObject0.getInt("x"));
                                    }
                                }
                                if (jsonObject0.get("y") != null) {
                                    if (!jsonObject0.getString("y").equals("null")) {
                                        contentValues.put("y", jsonObject0.getInt("y"));
                                    }
                                }
                                contentValues.put("isUpdate", 1);

                                db.insert("LocalBlock", null, contentValues);
//                                        updateSqlite("SpeciesList", "blockId", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = LOCALBLOCK_OK;
                        myHandler.sendMessage(msg);
                    }
                }).start();

            }
        });

        HttpRequest.HttpRequest_LocalSpecies(new JSONObject(), self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("LocalSpecies", null, null);
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            ContentValues contentValues = new ContentValues();
                            JSONObject jsonObject0;
                            for (int i = 0; i < total; i++) {
                                jsonObject0 = rows.getJSONObject(i);
                                contentValues.put("speciesid", jsonObject0.getString("speciesId"));
                                contentValues.put("name", jsonObject0.getString("name"));

                                db.insert("LocalSpecies", null, contentValues);

                                contentValues.clear();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = LOCALSPECIES_OK;
                        myHandler.sendMessage(msg);
                    }
                }).start();

            }
        });
    }

    //获取bigfarm图片uri
    public Uri getImageURI(String path, File cache) throws Exception {
        String name = path.substring(path.lastIndexOf("/") + 1);
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.code() == 200) {
                InputStream is = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    //创建bigfarm
    private void CreateBigfarm() {
//        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("BigfarmList", null, null, null, null, null, null);
        final int[] num = {cursor.getCount()};
        if (cursor.moveToFirst()) {
            do {
                int isCreated = cursor.getInt(cursor.getColumnIndex("isCreated"));
                if (isCreated != 0) {
                    num[0]--;
                    continue;
                }
                final String bigfarmId = cursor.getString(cursor.getColumnIndex("bigfarmId"));
                final String name = cursor.getString(cursor.getColumnIndex("name"));
                final String year = cursor.getString(cursor.getColumnIndex("year"));

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", name);
                    jsonObject.put("year", year);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpRequest.HttpRequest_CreateBigfarm(jsonObject, self, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            if (result.getBoolean("success")) {
                                JSONObject data = result.getJSONObject("data");
                                String id = data.getString("id");
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("bigfarmId", id);
                                contentValues.put("isCreated", 1);
                                sqLiteDatabase.update("BigfarmList", contentValues, "name=? and year=?",
                                        new String[]{name, year});
                                contentValues.clear();
                                contentValues.put("bigfarmId", id);
                                sqLiteDatabase.update("LocalField", contentValues,
                                        "bigfarmId=?", new String[]{bigfarmId});
                                num[0]--;

                                if (num[0] == 0) {
                                    Message msg = new Message();
                                    msg.what = CREATE_BIGFARM_OK;
                                    mHandler.sendMessage(msg);
                                }
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        updateDialog.setCancelable(true);
                                        updateDialog.setTitleText("试验田" + name + "创建失败");
                                        updateDialog.setContentText(null);
                                        showShortToast(self, "试验田" + name + "创建失败");
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } while (cursor.moveToNext());
        } else {
            showShortToast(self, "没有创建过大田");
        }
        if (num[0] == 0) {
            Message msg = new Message();
            msg.what = CREATE_BIGFARM_OK;
            mHandler.sendMessage(msg);
        }
        cursor.close();

    }

    //D/UpdateBigfarmImg: onResponse: {"timestamp":"2019-08-30T03:00:11.786+0000","status":500,"error":"Internal Server Error","message":"Failed to parse multipart servlet request; nested exception is java.io.IOException: The temporary upload location [/tmp/tomcat.8168872527897146995.9527/work/Tomcat/localhost/ROOT] is not valid","path":"/bigfarm/updateBigfarmImg"}
    private void uploadBigfarmImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = sqLiteDatabase.query("BigfarmList", null, "isUpdate=?", new String[]{"0"}, null, null, null);
                final int[] num = {cursor.getCount()};
                if (cursor.moveToFirst()) {
                    do {
                        final String bigfarmId = cursor.getString(cursor.getColumnIndex("bigfarmId"));
                        String picPath = cursor.getString(cursor.getColumnIndex("uri"));
                        HttpRequest.OkHttp_UpdateBigfarmImg(bigfarmId, picPath, self);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("isUpdate", 1);
                        db.update("BigfarmList", contentValues, "bigfarmId=?", new String[]{bigfarmId});
                        num[0]--;
                        if (num[0] == 0) {
                            Message msg = new Message();
                            msg.what = UPDATE_BIGFARMIMG_OK;
                            mHandler.sendMessage(msg);
                        }
                    } while (cursor.moveToNext());
                }
                if (num[0] == 0) {
                    Message msg = new Message();
                    msg.what = UPDATE_BIGFARMIMG_OK;
                    mHandler.sendMessage(msg);
                }
                cursor.close();
            }
        }).start();

    }

    //创建field
    private void CreateField() {
        final JSONArray jsonArray = new JSONArray();
//        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("LocalField", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int isCreated = cursor.getInt(cursor.getColumnIndex("isCreated"));
                if (isCreated != 1) {
                    continue;
                }
//                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String expType = cursor.getString(cursor.getColumnIndex("expType"));
                int moveX = cursor.getInt(cursor.getColumnIndex("moveX"));
                int moveY = cursor.getInt(cursor.getColumnIndex("moveY"));
                int moveX1 = cursor.getInt(cursor.getColumnIndex("moveX1"));
                int moveY1 = cursor.getInt(cursor.getColumnIndex("moveY1"));
                int num = cursor.getInt(cursor.getColumnIndex("num"));
                int rows = cursor.getInt(cursor.getColumnIndex("rows"));
                if (num == 0 || rows == 0) {
                    continue;
                }
                String bigfarmId = cursor.getString(cursor.getColumnIndex("bigfarmId"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                JSONObject jsonObject0 = new JSONObject();
                try {
//                    jsonObject0.put("id", id);
                    jsonObject0.put("name", name);
                    jsonObject0.put("expType", expType);
                    jsonObject0.put("moveX", moveX);
                    jsonObject0.put("moveY", moveY);
                    jsonObject0.put("moveX1", moveX1);
                    jsonObject0.put("moveY1", moveY1);
                    jsonObject0.put("num", num);
                    jsonObject0.put("rows", rows);
                    jsonObject0.put("bigfarmId", bigfarmId);
                    jsonObject0.put("description", description);
                    jsonObject0.put("type", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (jsonArray.length() == 0) {
            Message msg = new Message();
            msg.what = CREATE_FIELD_OK;
            mHandler.sendMessage(msg);
            return;
        }
        HttpRequest.HttpRequest_CreateField(jsonArray, self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (result.getBoolean("success")) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("isCreated", 2);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            db.update("LocalField", contentValues, "name=? and bigfarmId=?",
                                    new String[]{jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("bigfarmId")});
                        }
                        Message msg = new Message();
                        msg.what = CREATE_FIELD_OK;
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                updateDialog.setCancelable(true);
                                updateDialog.setTitleText("试验区域创建失败");
                                updateDialog.setContentText(null);
                                showShortToast(self, "试验区域创建失败");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //下载field和block数据
    private void downloadFieldAndBlock() {
        final Handler childHandler0 = new Handler();
        final HashMap<String, String> fieldmap = new HashMap<>();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpRequest.HttpRequest_Species(self, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(final JSONObject result) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                db.delete("LocalBlock", null, null);
                                try {
                                    JSONArray rows = result.getJSONArray("rows");
                                    int total = result.getInt("total");
                                    ContentValues contentValues = new ContentValues();
                                    JSONObject jsonObject0;
                                    for (int i = 0; i < total; i++) {
                                        jsonObject0 = rows.getJSONObject(i);

                                        contentValues.put("blockId", jsonObject0.getString("id"));
                                        contentValues.put("fieldId", jsonObject0.getString("fieldId"));
//                                        contentValues.put("speciesId", jsonObject0.getString("speciesId"));

                                        if (jsonObject0.get("x") != null) {
                                            if (!jsonObject0.getString("x").equals("null")) {
                                                contentValues.put("x", jsonObject0.getInt("x"));
                                            }
                                        }
                                        if (jsonObject0.get("y") != null) {
                                            if (!jsonObject0.getString("y").equals("null")) {
                                                contentValues.put("y", jsonObject0.getInt("y"));
                                            }
                                        }

                                        db.update("LocalBlock", contentValues, "fieldId=? and x=? and y=?",
                                                new String[]{jsonObject0.getString("fieldId"), jsonObject0.getString("x"), jsonObject0.getString("y")});
//                                db.insert("LocalBlock", null, contentValues);
//                                        updateSqlite("SpeciesList", "blockId", contentValues); //缓存数据到本地sqlite
                                        contentValues.clear();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Message msg = new Message();
                                msg.what = DOWNLOAD_BLOCK_OK;
                                mHandler.sendMessage(msg);
                            }
                        }).start();

                    }
                });
            }
        };

        HttpRequest.HttpRequest_map(null, self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) { //获取ExperimentField信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        db.delete("LocalField", null, null);
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            ContentValues contentValues = new ContentValues();
                            ContentValues contentValues1 = new ContentValues();
                            JSONObject jsonObject0;
                            for (int i = 0; i < total; i++) {
                                jsonObject0 = rows.getJSONObject(i);

                                //获取服务器的fieldId与本地的fieldId之间的映射关系
                                Cursor cursor0 = db.query("LocalField", null, "name=? and bigfarmId=?",
                                        new String[]{jsonObject0.getString("name"), jsonObject0.getString("bigfarmId")}, null, null, null);
                                if (cursor0.moveToFirst()) {
//                                    if (cursor0.getInt(cursor0.getColumnIndex("isCreated")) != 0) {
//                                        cursor0.close();
//                                        continue;
//                                    }
                                    fieldmap.put(jsonObject0.getString("id"), cursor0.getString(cursor0.getColumnIndex("id")));
                                }
                                cursor0.close();

                                contentValues.put("id", jsonObject0.getString("id"));
                                contentValues.put("name", jsonObject0.getString("name"));
                                if (jsonObject0.getBoolean("deleted")) {
                                    contentValues.put("deleted", "true");
                                } else {
                                    contentValues.put("deleted", "false");
                                }
                                contentValues.put("expType", jsonObject0.getString("expType"));
//                                if (jsonObject0.get("moveX") != null) {
//                                    if (!jsonObject0.getString("moveX").equals("null")) {
//                                        contentValues.put("moveX", jsonObject0.getInt("moveX"));
//                                    }
//                                }
//                                if (jsonObject0.get("moveY") != null) {
//                                    if (!jsonObject0.getString("moveY").equals("null")) {
//                                        contentValues.put("moveY", jsonObject0.getInt("moveY"));
//                                    }
//                                }
//                                if (jsonObject0.get("moveX1") != null) {
//                                    if (!jsonObject0.getString("moveX1").equals("null")) {
//                                        contentValues.put("moveX1", jsonObject0.getInt("moveX1"));
//                                    }
//                                }
//                                if (jsonObject0.get("moveY1") != null) {
//                                    if (!jsonObject0.getString("moveY1").equals("null")) {
//                                        contentValues.put("moveY1", jsonObject0.getInt("moveY1"));
//                                    }
//                                }
//                                if (jsonObject0.get("num") != null) {
//                                    contentValues.put("num", jsonObject0.getInt("num"));
//                                }
//                                contentValues.put("color", jsonObject0.getString("color"));
//                                contentValues.put("bigfarmId", jsonObject0.getString("bigfarmId"));
//                                if (jsonObject0.get("rows") != null) {
//                                    contentValues.put("rows", jsonObject0.getInt("rows"));
//                                }
//                                if (jsonObject0.get("length") != null) {
//                                    contentValues.put("length", jsonObject0.getInt("length"));
//                                }
//                                if (jsonObject0.get("width") != null) {
//                                    contentValues.put("width", jsonObject0.getInt("width"));
//                                }
//                                contentValues.put("description", jsonObject0.getString("description"));
//                                contentValues.put("type", jsonObject0.getString("type"));
//                                contentValues.put("speciesList", jsonObject0.getString("speciesList"));
                                contentValues.put("isCreated", 2);
//                                contentValues.put("isUpdate", 1);

                                db.update("LocalField", contentValues, "name=? and bigfarmId=?",
                                        new String[]{jsonObject0.getString("name"), jsonObject0.getString("bigfarmId")});
                                contentValues.clear();

                                contentValues1.put("fieldId", jsonObject0.getString("id"));
                                db.update("LocalBlock", contentValues1, "fieldId=?",
                                        new String[]{fieldmap.get(jsonObject0.getString("id"))});
                                contentValues1.clear();
//                                db.insert("ExperimentField", null, contentValues);

                            }

                            Message message = new Message();
                            message.what = DOWNLOAD_FIELD_OK;
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        childHandler0.post(runnable);
                    }
                }).start();
            }
        });

    }

    //更新field数据（坐标）
    private void updateFieldData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final JSONArray jsonArray = new JSONArray();
//                sqLiteDatabase = dbHelper.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.query("LocalField", null, "isUpdate=?", new String[]{"0"}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex("id"));
                        int moveX = cursor.getInt(cursor.getColumnIndex("moveX"));
                        int moveY = cursor.getInt(cursor.getColumnIndex("moveY"));
                        int moveX1 = cursor.getInt(cursor.getColumnIndex("moveX1"));
                        int moveY1 = cursor.getInt(cursor.getColumnIndex("moveY1"));
                        String description = cursor.getString(cursor.getColumnIndex("description"));
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("id", id);
                            jsonObject0.put("moveX", moveX);
                            jsonObject0.put("moveY", moveY);
                            jsonObject0.put("moveX1", moveX1);
                            jsonObject0.put("moveY1", moveY1);
                            jsonObject0.put("description", description);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(jsonObject0);
                    } while (cursor.moveToNext());
                    HttpRequest.HttpRequest_UpdateFields(jsonArray, self, new HttpRequest.HttpCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                if (result.getBoolean("success")) {
                                    Message message = new Message();
                                    message.what = UPLOAD_FIELD_OK;
                                    mHandler.sendMessage(message);
                                    ContentValues contentValues = new ContentValues();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        contentValues.put("isUpdate", 1);
                                        db.update("LocalField", contentValues, "id=?", new String[]{jsonArray.getJSONObject(i).getString("id")});
                                        contentValues.clear();
                                    }
                                } else {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            updateDialog.setCancelable(true);
                                            updateDialog.setTitleText("试验区域数据更新失败");
                                            updateDialog.setContentText(null);
                                            showShortToast(self, "试验区域数据更新失败");
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Message message = new Message();
                    message.what = UPLOAD_FIELD_OK;
                    mHandler.sendMessage(message);
                }
                cursor.close();
                Looper.loop();
            }
        }).start();

    }

    //上传种植图信息
    private void uploadPlanData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONArray jsonArray = new JSONArray();
//                sqLiteDatabase = dbHelper.getReadableDatabase();
                Cursor cursor0 = sqLiteDatabase.query("LocalBlock", null, null, null, null, null, null);
                if (cursor0.moveToFirst()) {
                    do {
                        String blockId = cursor0.getString(cursor0.getColumnIndex("blockId"));
                        String fieldId = cursor0.getString(cursor0.getColumnIndex("fieldId"));
                        String speciesId = cursor0.getString(cursor0.getColumnIndex("speciesId"));
                        int x = cursor0.getInt(cursor0.getColumnIndex("x"));
                        int y = cursor0.getInt(cursor0.getColumnIndex("y"));
                        int isUpdate = cursor0.getInt(cursor0.getColumnIndex("isUpdate"));
                        if (isUpdate != 0) {
                            continue;
                        }
                        JSONObject jsonObject0 = new JSONObject();
                        try {
                            jsonObject0.put("id", blockId);
                            jsonObject0.put("fieldId", fieldId);
                            jsonObject0.put("speciesId", speciesId);
                            jsonObject0.put("x", x);
                            jsonObject0.put("y", y);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(jsonObject0);
                    } while (cursor0.moveToNext());
                    if (jsonArray.length() != 0) {
                        HttpRequest.HttpRequest_SpeciesList(jsonArray, self, new HttpRequest.HttpCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    if (result.getBoolean("success")) {
                                        Message msg = new Message();
                                        msg.what = UPLOAD_BLOCK_OK;
                                        mHandler.sendMessage(msg);

                                        ContentValues contentValues = new ContentValues();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            contentValues.put("isUpdate", 1);
                                            db.update("LocalBlock", contentValues, "blockId=?",
                                                    new String[]{jsonArray.getJSONObject(i).getString("id")});
                                            contentValues.clear();
                                        }

                                    } else {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                                updateDialog.setCancelable(true);
                                                updateDialog.setTitleText("种植图信息更新失败");
                                                updateDialog.setContentText(null);
                                                showShortToast(self, "种植图信息更新失败");
                                            }
                                        });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Message msg = new Message();
                        msg.what = UPLOAD_BLOCK_OK;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = UPLOAD_BLOCK_OK;
                    mHandler.sendMessage(msg);
                    showShortToast(self, getString(R.string.toast_null_plan_data));
                }
//                cursor0 = sqLiteDatabase.query("LocalField", null, null, null, null, null, null);
//                final int[] nums = {cursor0.getCount()};
//                if (cursor0.moveToFirst()) {
//                    do {
//                        String experimentFieldId = cursor0.getString(cursor0.getColumnIndex("id"));
//                        String description = cursor0.getString(cursor0.getColumnIndex("description"));
//                        int num = cursor0.getInt(cursor0.getColumnIndex("num"));
//                        int rows = cursor0.getInt(cursor0.getColumnIndex("rows"));
//                        if (num == 0 || rows == 0) {
//                            nums[0]--;
//                            continue;
//                        }
//                        HttpRequest.HttpRequest_description(experimentFieldId, description, self, new HttpRequest.HttpCallback() {
//                            @Override
//                            public void onSuccess(JSONObject result) {
//                                try {
//                                    if (result.getBoolean("success")) {
//                                        nums[0]--;
//                                    } else {
//                                        myHandler.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                showShortToast(self, "备注数据上传失败");
//                                            }
//                                        });
//                                    }
//                                    if (nums[0] == 0) {
//                                        Message msg = new Message();
//                                        msg.what = UPLOAD_DESCRIPTION_OK;
//                                        mHandler.sendMessage(msg);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    } while (cursor0.moveToNext());
//                }
//                if (nums[0] == 0) {
//                    Message msg = new Message();
//                    msg.what = UPLOAD_DESCRIPTION_OK;
//                    mHandler.sendMessage(msg);
//                }
                cursor0.close();
            }
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private void uploadSurveyData() {
//        final int UPLOAD_SURVEY_WORDS = 20;
//        final int UPLOAD_SURVEY_IMG1 = 21;
//        final int UPLOAD_SURVEY_IMG2 = 22;
//        final int UPLOAD_SURVEY_IMG3 = 23;
//        final int UPLOAD_SURVEY_IMG4 = 24;
//        final int UPLOAD_SURVEY_IMG5 = 25;
//        final int[] request_num = {0, 6}; //请求成功的次数，总请求次数

        String sql = "select SpeciesTable.*, LocalSpecies.* from SpeciesTable, LocalSpecies " +
                "where SpeciesTable.speciesId=LocalSpecies.name";
//        sqLiteDatabase = dbHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        final int[] nums = {cursor.getCount(), cursor.getCount(), cursor.getCount(),
                cursor.getCount(), cursor.getCount(), cursor.getCount()}; //每个请求的最大数量
        if (cursor.moveToFirst()) {
            do {
                int isUpdate = cursor.getInt(cursor.getColumnIndex("isUpdate"));
                if (isUpdate != 0) {
                    for (int i = 0; i < 6; i++) {
                        nums[i]--;
                    }
                    continue;
                }
                final String speciesId = cursor.getString(cursor.getColumnIndex("speciesid"));
                final String name = cursor.getString(cursor.getColumnIndex("name")); //++++++++++++++
                final String blockId = cursor.getString(cursor.getColumnIndex("blockId")); //+++++++++++++
                String experimentType = cursor.getString(cursor.getColumnIndex("experimentType"));
                String plantingDate = cursor.getString(cursor.getColumnIndex("plantingDate"));
                String emergenceDate = cursor.getString(cursor.getColumnIndex("emergenceDate"));
                int sproutRate = cursor.getInt(cursor.getColumnIndex("sproutRate"));
                String squaringStage = cursor.getString(cursor.getColumnIndex("squaringStage"));
                String blooming = cursor.getString(cursor.getColumnIndex("blooming"));
                String leafColour = cursor.getString(cursor.getColumnIndex("leafColour"));
                String corollaColour = cursor.getString(cursor.getColumnIndex("corollaColour"));
                String flowering = cursor.getString(cursor.getColumnIndex("flowering"));
                String stemColour = cursor.getString(cursor.getColumnIndex("stemColour"));
                String openpollinated = cursor.getString(cursor.getColumnIndex("openpollinated"));
                String maturingStage = cursor.getString(cursor.getColumnIndex("maturingStage"));
                int growingPeriod = cursor.getInt(cursor.getColumnIndex("growingPeriod"));
                String uniformityOfTuberSize = cursor.getString(cursor.getColumnIndex("uniformityOfTuberSize"));
                String tuberShape = cursor.getString(cursor.getColumnIndex("tuberShape"));
                String skinSmoothness = cursor.getString(cursor.getColumnIndex("skinSmoothness"));
                String eyeDepth = cursor.getString(cursor.getColumnIndex("eyeDepth"));
                String skinColour = cursor.getString(cursor.getColumnIndex("skinColour"));
                String fleshColour = cursor.getString(cursor.getColumnIndex("fleshColour"));
                String isChoozen = cursor.getString(cursor.getColumnIndex("isChoozen"));
                final String remark = cursor.getString(cursor.getColumnIndex("remark"));
                int harvestNum = cursor.getInt(cursor.getColumnIndex("harvestNum"));
                int lmNum = cursor.getInt(cursor.getColumnIndex("lmNum"));
                int lmWeight = cursor.getInt(cursor.getColumnIndex("lmWeight"));
                int sNum = cursor.getInt(cursor.getColumnIndex("sNum"));
                int sWeight = cursor.getInt(cursor.getColumnIndex("sWeight"));
                float commercialRate = cursor.getFloat(cursor.getColumnIndex("commercialRate"));
                float plotYield1 = cursor.getFloat(cursor.getColumnIndex("plotYield1"));
                float plotYield2 = cursor.getFloat(cursor.getColumnIndex("plotYield2"));
                float plotYield3 = cursor.getFloat(cursor.getColumnIndex("plotYield3"));
                float acreYield = cursor.getFloat(cursor.getColumnIndex("acreYield"));
                float bigPlantHeight1 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight1"));
                float bigPlantHeight2 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight2"));
                float bigPlantHeight3 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight3"));
                float bigPlantHeight4 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight4"));
                float bigPlantHeight5 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight5"));
                float bigPlantHeight6 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight6"));
                float bigPlantHeight7 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight7"));
                float bigPlantHeight8 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight8"));
                float bigPlantHeight9 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight9"));
                float bigPlantHeight10 = cursor.getFloat(cursor.getColumnIndex("bigPlantHeight10"));
                float plantHeightAvg = cursor.getFloat(cursor.getColumnIndex("plantHeightAvg"));
                int bigBranchNumber1 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber1"));
                int bigBranchNumber2 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber2"));
                int bigBranchNumber3 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber3"));
                int bigBranchNumber4 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber4"));
                int bigBranchNumber5 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber5"));
                int bigBranchNumber6 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber6"));
                int bigBranchNumber7 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber7"));
                int bigBranchNumber8 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber8"));
                int bigBranchNumber9 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber9"));
                int bigBranchNumber10 = cursor.getInt(cursor.getColumnIndex("bigBranchNumber10"));
                float branchNumberAvg = cursor.getFloat(cursor.getColumnIndex("branchNumberAvg"));
                float bigYield1 = cursor.getFloat(cursor.getColumnIndex("bigYield1"));
                float bigYield2 = cursor.getFloat(cursor.getColumnIndex("bigYield2"));
                float bigYield3 = cursor.getFloat(cursor.getColumnIndex("bigYield3"));
                float bigYield4 = cursor.getFloat(cursor.getColumnIndex("bigYield4"));
                float bigYield5 = cursor.getFloat(cursor.getColumnIndex("bigYield5"));
                float bigYield6 = cursor.getFloat(cursor.getColumnIndex("bigYield6"));
                float bigYield7 = cursor.getFloat(cursor.getColumnIndex("bigYield7"));
                float bigYield8 = cursor.getFloat(cursor.getColumnIndex("bigYield8"));
                float bigYield9 = cursor.getFloat(cursor.getColumnIndex("bigYield9"));
                float bigYield10 = cursor.getFloat(cursor.getColumnIndex("bigYield10"));
                float smalYield1 = cursor.getFloat(cursor.getColumnIndex("smalYield1"));
                float smalYield2 = cursor.getFloat(cursor.getColumnIndex("smalYield2"));
                float smalYield3 = cursor.getFloat(cursor.getColumnIndex("smalYield3"));
                float smalYield4 = cursor.getFloat(cursor.getColumnIndex("smalYield4"));
                float smalYield5 = cursor.getFloat(cursor.getColumnIndex("smalYield5"));
                float smalYield6 = cursor.getFloat(cursor.getColumnIndex("smalYield6"));
                float smalYield7 = cursor.getFloat(cursor.getColumnIndex("smalYield7"));
                float smalYield8 = cursor.getFloat(cursor.getColumnIndex("smalYield8"));
                float smalYield9 = cursor.getFloat(cursor.getColumnIndex("smalYield9"));
                float smalYield10 = cursor.getFloat(cursor.getColumnIndex("smalYield10"));
                img1 = cursor.getString(cursor.getColumnIndex("img1"));
                img2 = cursor.getString(cursor.getColumnIndex("img2"));
                img3 = cursor.getString(cursor.getColumnIndex("img3"));
                img4 = cursor.getString(cursor.getColumnIndex("img4"));
                img5 = cursor.getString(cursor.getColumnIndex("img5"));
                String commitTime=cursor.getString(cursor.getColumnIndex("time"));

                final JSONObject jsonObject = new JSONObject();
                JSONObject jsonObject_common = new JSONObject();
                JSONObject jsonObject_local = new JSONObject();
                try {
                    jsonObject_common.put("plantingDate", plantingDate);
                    jsonObject_common.put("emergenceDate", emergenceDate);
                    jsonObject_common.put("sproutRate", sproutRate);
                    jsonObject_common.put("squaringStage", squaringStage);
                    jsonObject_common.put("blooming", blooming);
//                                jsonObject.put("leafColour", leafColour);
//                                jsonObject.put("corollaColour", corollaColour);
//                                jsonObject.put("flowering", flowering);
//                                jsonObject.put("stemColour", stemColour);
//                                jsonObject.put("openpollinated", openpollinated);
                    jsonObject_common.put("maturingStage", maturingStage);
                    jsonObject_common.put("growingPeriod", growingPeriod);
//                                jsonObject.put("uniformityOfTuberSize", uniformityOfTuberSize);
//                                jsonObject.put("tuberShape", tuberShape);
//                                jsonObject.put("skinSmoothness", skinSmoothness);
//                                jsonObject.put("eyeDepth", eyeDepth);
//                                jsonObject.put("skinColour", skinColour);
//                                jsonObject.put("fleshColour", fleshColour);
//                                if (isChoozen.equals("yes")) {
//                                    jsonObject.put("isChoozen", 1);
//                                } else {
//                                    jsonObject.put("isChoozen", 0);
//                                }
                    jsonObject_common.put("remark", remark);
                    jsonObject_common.put("harvestNum", harvestNum);
                    jsonObject_common.put("lmNum", lmNum);
                    jsonObject_common.put("lmWeight", lmWeight);
                    jsonObject_common.put("sNum", sNum);
                    jsonObject_common.put("sWeight", sWeight);
                    jsonObject_common.put("commercialRate", commercialRate);
                    jsonObject_common.put("plotYield1", plotYield1);
                    jsonObject_common.put("plotYield2", plotYield2);
                    jsonObject_common.put("plotYield3", plotYield3);
                    jsonObject_common.put("acreYield", acreYield);
                    jsonObject_common.put("bigPlantHeight1", bigPlantHeight1);
                    jsonObject_common.put("bigPlantHeight2", bigPlantHeight2);
                    jsonObject_common.put("bigPlantHeight3", bigPlantHeight3);
                    jsonObject_common.put("bigPlantHeight4", bigPlantHeight4);
                    jsonObject_common.put("bigPlantHeight5", bigPlantHeight5);
                    jsonObject_common.put("bigPlantHeight6", bigPlantHeight6);
                    jsonObject_common.put("bigPlantHeight7", bigPlantHeight7);
                    jsonObject_common.put("bigPlantHeight8", bigPlantHeight8);
                    jsonObject_common.put("bigPlantHeight9", bigPlantHeight9);
                    jsonObject_common.put("bigPlantHeight10", bigPlantHeight10);
                    jsonObject_common.put("plantHeightAvg", plantHeightAvg);
                    jsonObject_common.put("bigBranchNumber1", bigBranchNumber1);
                    jsonObject_common.put("bigBranchNumber2", bigBranchNumber2);
                    jsonObject_common.put("bigBranchNumber3", bigBranchNumber3);
                    jsonObject_common.put("bigBranchNumber4", bigBranchNumber4);
                    jsonObject_common.put("bigBranchNumber5", bigBranchNumber5);
                    jsonObject_common.put("bigBranchNumber6", bigBranchNumber6);
                    jsonObject_common.put("bigBranchNumber7", bigBranchNumber7);
                    jsonObject_common.put("bigBranchNumber8", bigBranchNumber8);
                    jsonObject_common.put("bigBranchNumber9", bigBranchNumber9);
                    jsonObject_common.put("bigBranchNumber10", bigBranchNumber10);
                    jsonObject_common.put("branchNumberAvg", branchNumberAvg);
                    jsonObject_common.put("bigYield1", bigYield1);
                    jsonObject_common.put("bigYield2", bigYield2);
                    jsonObject_common.put("bigYield3", bigYield3);
                    jsonObject_common.put("bigYield4", bigYield4);
                    jsonObject_common.put("bigYield5", bigYield5);
                    jsonObject_common.put("bigYield6", bigYield6);
                    jsonObject_common.put("bigYield7", bigYield7);
                    jsonObject_common.put("bigYield8", bigYield8);
                    jsonObject_common.put("bigYield9", bigYield9);
                    jsonObject_common.put("bigYield10", bigYield10);
                    jsonObject_common.put("smalYield1", smalYield1);
                    jsonObject_common.put("smalYield2", smalYield2);
                    jsonObject_common.put("smalYield3", smalYield3);
                    jsonObject_common.put("smalYield4", smalYield4);
                    jsonObject_common.put("smalYield5", smalYield5);
                    jsonObject_common.put("smalYield6", smalYield6);
                    jsonObject_common.put("smalYield7", smalYield7);
                    jsonObject_common.put("smalYield8", smalYield8);
                    jsonObject_common.put("smalYield9", smalYield9);
                    jsonObject_common.put("smalYield10", smalYield10);
                    jsonObject_common.put("speciesId", speciesId);
                    jsonObject_common.put("testId", blockId);
                    jsonObject_common.put("experimentType", experimentType);
                    jsonObject_common.put("updateTime",commitTime);


                    jsonObject_local.put("corollaColour", corollaColour);
                    jsonObject_local.put("eyeDepth", eyeDepth);
                    jsonObject_local.put("fleshColour", fleshColour);
                    jsonObject_local.put("flowering", flowering);
//                                jsonObject_local.put("img1", img1);
//                                jsonObject_local.put("img2", img2);
//                                jsonObject_local.put("img3", img3);
//                                jsonObject_local.put("img4", img4);
//                                jsonObject_local.put("img5", img5);
                    if (isChoozen.equals("yes")) {
                        jsonObject_local.put("isChoozen", 1);
                    } else {
                        jsonObject_local.put("isChoozen", 0);
                    }
                    jsonObject_local.put("leafColour", leafColour);
                    jsonObject_local.put("openpollinated", openpollinated);
                    jsonObject_local.put("skinColour", skinColour);
                    jsonObject_local.put("skinSmoothness", skinSmoothness);
                    jsonObject_local.put("name", name);
                    jsonObject_local.put("speciesId", speciesId);
                    jsonObject_local.put("stemColour", stemColour);
                    jsonObject_local.put("tuberShape", tuberShape);
                    jsonObject_local.put("uniformityOfTuberSize", uniformityOfTuberSize);

                    jsonObject.put("commontest", jsonObject_common);
                    jsonObject.put("localSpecies", jsonObject_local);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread() {
                    @Override
                    public void run() {
                        HttpRequest.HttpRequest_SpeciesData(jsonObject, self, new HttpRequest.HttpCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    Log.d("result0", result.toString());
                                    if (result.getBoolean("success")) {
                                        nums[0]--;
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("isUpdate", 1);
                                        db.update("SpeciesTable", contentValues, "blockId=?", new String[]{jsonObject.getJSONObject("commontest").getString("testId")});
                                    } else {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                                updateDialog.setCancelable(true);
                                                updateDialog.setTitleText("调查数据更新失败,blockId:" + blockId + ",name:" + name);
                                                updateDialog.setContentText(null);
                                                showShortToast(self, "调查数据更新失败");
                                            }
                                        });
                                    }
                                    if (nums[0] == 0) {
                                        Message msg = new Message();
                                        msg.what = UPLOAD_SURVEY_OK;
                                        mHandler.sendMessage(msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        if (img1 != null) {
                            HttpRequest.doUploadTest(img1, speciesId, "1", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                    Log.d("result1", result);
                                    try {
                                        if (new JSONObject(result).getBoolean("success")) {
                                            nums[1]--;
                                        } else {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showShortToast(self, "图片1上传失败");
                                                }
                                            });
                                        }
                                        if (nums[1] == 0) {
                                            Message msg = new Message();
                                            msg.what = UPLOAD_SURVEY_IMG1;
                                            childHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            nums[1]--;
                            if (nums[1] == 0) {
                                Message msg = new Message();
                                msg.what = UPLOAD_SURVEY_IMG1;
                                childHandler.sendMessage(msg);
                            }
                        }
                        if (img2 != null) {
                            HttpRequest.doUploadTest(img2, speciesId, "2", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        if (new JSONObject(result).getBoolean("success")) {
                                            nums[2]--;
                                        } else {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showShortToast(self, "图片2上传失败");
                                                }
                                            });
                                        }
                                        if (nums[2] == 0) {
                                            Message msg = new Message();
                                            msg.what = UPLOAD_SURVEY_IMG2;
                                            childHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            nums[2]--;
                            if (nums[2] == 0) {
                                Message msg = new Message();
                                msg.what = UPLOAD_SURVEY_IMG2;
                                childHandler.sendMessage(msg);
                            }
                        }
                        if (img3 != null) {
                            HttpRequest.doUploadTest(img3, speciesId, "3", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        if (new JSONObject(result).getBoolean("success")) {
                                            nums[3]--;
                                        } else {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showShortToast(self, "图片3上传失败");
                                                }
                                            });
                                        }
                                        if (nums[3] == 0) {
                                            Message msg = new Message();
                                            msg.what = UPLOAD_SURVEY_IMG3;
                                            childHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            nums[3]--;
                            if (nums[3] == 0) {
                                Message msg = new Message();
                                msg.what = UPLOAD_SURVEY_IMG3;
                                childHandler.sendMessage(msg);
                            }
                        }
                        if (img4 != null) {
                            HttpRequest.doUploadTest(img4, speciesId, "4", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        if (new JSONObject(result).getBoolean("success")) {
                                            nums[4]--;
                                        } else {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showShortToast(self, "图片4上传失败");
                                                }
                                            });
                                        }
                                        if (nums[4] == 0) {
                                            Message msg = new Message();
                                            msg.what = UPLOAD_SURVEY_IMG4;
                                            childHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            nums[4]--;
                            if (nums[4] == 0) {
                                Message msg = new Message();
                                msg.what = UPLOAD_SURVEY_IMG4;
                                childHandler.sendMessage(msg);
                            }
                        }
                        if (img5 != null) {
                            HttpRequest.doUploadTest(img5, speciesId, "5", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        if (new JSONObject(result).getBoolean("success")) {
                                            nums[5]--;
                                        } else {
                                            myHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showShortToast(self, "图片5上传失败");
                                                }
                                            });
                                        }
                                        if (nums[5] == 0) {
                                            Message msg = new Message();
                                            msg.what = UPLOAD_SURVEY_IMG5;
                                            childHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            nums[5]--;
                            if (nums[5] == 0) {
                                Message msg = new Message();
                                msg.what = UPLOAD_SURVEY_IMG5;
                                childHandler.sendMessage(msg);
                            }
                        }
                        //sqLiteDatabase.delete("SpeciesTable", null, null);
                    }
                }.start();

            } while (cursor.moveToNext());

        } else {
            showShortToast(self, getString(R.string.toast_null_pick_data));
        }
        if (nums[0] == 0) {
            Message msg = new Message();
            msg.what = UPLOAD_SURVEY_OK;
            mHandler.sendMessage(msg);
        }
        if (nums[1] == 0) {
            Message msg = new Message();
            msg.what = UPLOAD_SURVEY_IMG1;
            childHandler.sendMessage(msg);
        }
        if (nums[2] == 0) {
            Message msg = new Message();
            msg.what = UPLOAD_SURVEY_IMG2;
            childHandler.sendMessage(msg);
        }
        if (nums[3] == 0) {
            Message msg = new Message();
            msg.what = UPLOAD_SURVEY_IMG3;
            childHandler.sendMessage(msg);
        }
        if (nums[4] == 0) {
            Message msg = new Message();
            msg.what = UPLOAD_SURVEY_IMG4;
            childHandler.sendMessage(msg);
        }
        if (nums[5] == 0) {
            Message msg = new Message();
            msg.what = UPLOAD_SURVEY_IMG5;
            childHandler.sendMessage(msg);
        }
        cursor.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (sp.getBoolean("upload_data", false)) {
            if (badge == null) {
                badge = new QBadgeView(self).bindTarget(uploadIcon).setBadgeText("");
            } else {
                badge.setBadgeText("");
            }
        }
        dbHelper = new SpeciesDBHelper(self, "SpeciesTable.db", null, 15);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sp.getBoolean("upload_data", false)) {
            if (badge == null) {
                badge = new QBadgeView(self).bindTarget(uploadIcon).setBadgeText("");
            } else {
                badge.setBadgeText("");
            }
            if (bigfarmId != null) {
                initView();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    userRole = data.getStringExtra("userRole");
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (db != null) {
            db.close();
            dbHelper.close();
        }
        self.unregisterReceiver(networkChangeReceiver);
        myHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
        childHandler.removeCallbacksAndMessages(null);
    }

    //与firmsurveyfragment进行通信，通知更新了数据
    public interface selectFarm {
        void selectFarm(String bigFarmId);
    }

    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) self.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                isOnline = true;
//                showShortToast(context, getString(R.string.network_normal));
            } else {
                isOnline = false;
//                showShortToast(context, getString(R.string.network_wrong));
            }
        }


    }
}
