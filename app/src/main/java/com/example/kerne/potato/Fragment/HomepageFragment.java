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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kerne.potato.FirmPlanActivity;
import com.example.kerne.potato.LoginActivity;
import com.example.kerne.potato.MainActivity;
import com.example.kerne.potato.R;
import com.example.kerne.potato.Util.FarmPlanView;
import com.example.kerne.potato.Util.HttpRequest;
import com.example.kerne.potato.Util.PowerFullLayout;
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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.app.Activity.RESULT_OK;
import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class HomepageFragment extends Fragment {
    private static final int BIGFARMLIST_OK = 0;
    private static final int FARMLIST_OK = 1;
    private static final int EXPERIMENTFIELD_OK = 2;
    private static final int SPECIESLIST_OK = 3;
    private static final int DATA_OK = 5;
    private static final int FIELD_DATA_OK = 6;
    private static int downloadSuccess_Num = 0;
    private static int request_Num = 4;
    private static int uploadSuccess_Num = 0;
    private static boolean isOnline = false;
    public SweetAlertDialog downloadDataDialog;
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
    private LinearLayout farmButton;
    private View view;
    private Context self;
    private LinearLayout btnDownload;
    private ImageView uploadIcon;
    private LinearLayout btnUpload;
    private Spinner homepageYears;
    private LinearLayout planFarm;
    private LinearLayout changeFarmView;
    private RelativeLayout homepageFarm;
    private PowerFullLayout scaleLayout;
    private TextView farmType;
    private int farm_flag = 0;//标识当前的firm视图 0,棚外  1,棚内
    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase db;
    private String Fid[] = new String[5000];
    private List<JSONObject> mBigFarmList = new ArrayList<>();
    private List<JSONObject> mOutShackList = new ArrayList<>();
    private List<JSONObject> mInShackList = new ArrayList<>();
    private List<TextView> mOutList = new ArrayList<>();
    private List<TextView> mInList = new ArrayList<>();
    private List<String> mYears = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private String bigfarmId;

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BIGFARMLIST_OK:
                    downloadSuccess_Num++;
                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_farm_data));
                    break;
                case FARMLIST_OK:
                    downloadSuccess_Num++;
                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_farm_plan_data));
                    break;
                case EXPERIMENTFIELD_OK:
                    downloadSuccess_Num++;
                    downloadDataDialog.setTitleText(getContext().getString(R.string.download_species_data));
                    break;
                case SPECIESLIST_OK:
                    downloadSuccess_Num++;
                    break;
                case DATA_OK:
                    if (mBigFarmList.size() > 0) {

                        //spinner加载
                        try {
                            bigfarmId = mBigFarmList.get(0).getString("bigfarmId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mYears.clear();
                        try {
                            for (int i = 0; i < mBigFarmList.size(); i++) {
                                mYears.add(mBigFarmList.get(i).getString("year"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (spinnerAdapter == null) {
                            spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_textview, mYears);
                            homepageYears.setAdapter(spinnerAdapter);
                        } else {
                            Log.d("cheatGZ spinner ", "big " + mBigFarmList.size() + " year" + mYears.size());
                            spinnerAdapter.notifyDataSetChanged();
                        }
                        initView(farm_flag);
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
                mainActivity.updateData(true);
                mainActivity.selectFarm(bigfarmId);
            }
        }
    };
    //监听事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
                        uploadPlanData();
                        uploadSurveyData();
                        showShortToast(self, getString(R.string.toast_upload_data_complete));
                    } else {
                        showShortToast(self, getString(R.string.toast_log_in));
                        Intent intent = new Intent(self, LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    editor.putBoolean("upload_data", false);
                    editor.apply();
                    break;
                case R.id.plan_farm:
                    Intent intent = new Intent(self, FirmPlanActivity.class);
                    intent.putExtra("bigfarmId", bigfarmId);
                    self.startActivity(intent);
                    break;
                case R.id.change_farm_view:
                    if (farm_flag == 0) {
                        farmType.setText(self.getString(R.string.farm));
                        farm_flag = 1;

                        initView(farm_flag);
                    } else if (farm_flag == 1) {
                        farmType.setText(self.getString(R.string.shack_farm));
                        farm_flag = 0;

                        initView(farm_flag);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                bigfarmId = mBigFarmList.get(position).getString("bigfarmId");
                initView(farm_flag);
                MainActivity mainActivity = new MainActivity();
                mainActivity.selectFarm(bigfarmId);
                mainActivity.updateData(true);
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

    private void init() {
        sp = self.getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();

        btnDownload = view.findViewById(R.id.btn_download);
        btnUpload = view.findViewById(R.id.btn_upload);
        uploadIcon = view.findViewById(R.id.upload_icon);
        homepageYears = view.findViewById(R.id.homepage_years);
        planFarm = view.findViewById(R.id.plan_farm);
        changeFarmView = view.findViewById(R.id.change_farm_view);
        farmType = view.findViewById(R.id.firm_type);
        homepageFarm = view.findViewById(R.id.homepage_farm);
        scaleLayout = view.findViewById(R.id.scale_layout);

        homepageYears.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down2);

        btnDownload.setOnClickListener(onClickListener);
        btnUpload.setOnClickListener(onClickListener);
        planFarm.setOnClickListener(onClickListener);
        changeFarmView.setOnClickListener(onClickListener);
        homepageYears.setOnItemSelectedListener(onItemSelectedListener);
        initData();


        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        self.registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void initView(int flag) {
        initFieldData();
        switch (flag) {
            case 0:
                //加载棚外
                homepageFarm.removeAllViews();
                mOutShackList = new ArrayList<>();
                try {
                    for (int i = 0; i < 3; i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("num", 50 + i * 10);
                        jsonObject.put("rows", 1 + i);
                        jsonObject.put("x", 300000 + i * 100000);
                        jsonObject.put("y", 300000 + i * 100000);
                        jsonObject.put("name", "加工鉴定");
                        mOutShackList.add(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (homepageFarm != null) {
                    FarmPlanView farmPlanView = new FarmPlanView(getContext(), homepageFarm, homepageFarm.getWidth(), homepageFarm.getHeight(), mOutShackList);
                    farmPlanView.createRoad("common");
                    mOutList = farmPlanView.createField("common");
                    for (int i = 0; i < mOutList.size(); i++) {
//                            mOutList.get(i).setTag(mOutShackList.get(i));
                        mOutList.get(i).setBackgroundResource(R.drawable.bg_field);
                    }
                }

                break;
            case 1:
                //加载棚内
                homepageFarm.removeAllViews();
                if (homepageFarm != null) {
                    FarmPlanView farmPlanView = new FarmPlanView(getContext(), homepageFarm, homepageFarm.getWidth(), homepageFarm.getHeight(), mInShackList);
                    farmPlanView.createRoad("greenhouse");
                    mInList = farmPlanView.createField("greenhouse");
                    for (int i = 0; i < mInList.size(); i++) {
                        mInList.get(i).setBackgroundResource(R.drawable.bg_field);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
                SpeciesDBHelper dbHelper = new SpeciesDBHelper(getContext(), "SpeciesTable.db", null, 10);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

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
                cursor.close();

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
        //TODO
        //获取大棚区域
        mInShackList.clear();
        Cursor cursor2 = db.query("ExperimentField", null, "farmlandId=?", new String[]{bigfarmId}, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                JSONObject jsonObject0 = new JSONObject();
                try {
                    jsonObject0.put("fieldId", cursor2.getString(cursor2.getColumnIndex("id")));
                    jsonObject0.put("name", cursor2.getString(cursor2.getColumnIndex("name")));
                    jsonObject0.put("expType", cursor2.getString(cursor2.getColumnIndex("expType")));
                    jsonObject0.put("num", cursor2.getInt(cursor2.getColumnIndex("num")));
                    jsonObject0.put("farmlandId", cursor2.getString(cursor2.getColumnIndex("farmlandId")));
                    jsonObject0.put("rows", cursor2.getInt(cursor2.getColumnIndex("rows")));
                    jsonObject0.put("x", cursor2.getInt(cursor2.getColumnIndex("moveX")));
                    jsonObject0.put("y", cursor2.getInt(cursor2.getColumnIndex("moveY")));
                    jsonObject0.put("type", "greenhouse");
                    mInShackList.add(jsonObject0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor2.moveToNext());
        }
        cursor2.close();
    }

    private void downloadData() {
        downloadDataDialog = new SweetAlertDialog(self, SweetAlertDialog.PROGRESS_TYPE);
        downloadDataDialog.setTitleText(getString(R.string.download_data));
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

                                Uri uri = getImageURI(HttpRequest.serverUrl + jsonObject0.getString("img"), cache);
                                contentValues.put("uri", uri.toString());

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

        HttpRequest.HttpRequest_farm(null, self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) { //获取FarmList信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("FarmList", null, null);
                        try {
                            JSONArray rows = new JSONArray();
                            rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            JSONObject jsonObject0;
                            for (int i = 0; i < total; i++) {
                                jsonObject0 = rows.getJSONObject(i);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
                                if (jsonObject0.getBoolean("deleted")) {
                                    contentValues.put("deleted", "true");
                                } else {
                                    contentValues.put("deleted", "false");
                                }
                                contentValues.put("name", jsonObject0.getString("name"));
                                if (jsonObject0.get("length") != null) {
                                    contentValues.put("length", jsonObject0.getInt("length"));
                                }
                                if (jsonObject0.get("width") != null) {
                                    contentValues.put("width", jsonObject0.getInt("width"));
                                }
                                contentValues.put("type", jsonObject0.getString("type"));
                                contentValues.put("bigfarmId", jsonObject0.getString("bigfarmId"));

                                db.insert("FarmList", null, contentValues);
                                contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = FARMLIST_OK;
                        myHandler.sendMessage(msg);
                    }
                }).start();

            }
        });

        HttpRequest.HttpRequest_map(null, self, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) { //获取ExperimentField信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("ExperimentField", null, null);
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
                                contentValues.put("num", jsonObject0.getString("num"));
                                contentValues.put("color", jsonObject0.getString("color"));
                                contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
                                if (jsonObject0.get("rows") != null) {
                                    contentValues.put("rows", jsonObject0.getInt("rows"));
                                }
                                contentValues.put("description", jsonObject0.getString("description"));
                                contentValues.put("speciesList", jsonObject0.getString("speciesList"));

                                db.insert("ExperimentField", null, contentValues);
//                                    contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = EXPERIMENTFIELD_OK;
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
                        db.delete("SpeciesList", null, null);
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

                                db.insert("SpeciesList", null, contentValues);
//                                        updateSqlite("SpeciesList", "blockId", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = SPECIESLIST_OK;
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
                    }
                }).start();

            }
        });
    }

    //获取bigfarm图片uri
    public Uri getImageURI(String path, File cache) throws Exception {
//        String name = path;
        String name = path.substring(path.lastIndexOf("/") + 1);
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.code() == 200) {

//                InputStream is = conn.getInputStream();
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

    private void uploadPlanData() {
        JSONArray jsonArray = new JSONArray();
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor0 = sqLiteDatabase.query("SpeciesList", null, null, null, null, null, null);
        if (cursor0.moveToFirst()) {
            do {
                String blockId = cursor0.getString(cursor0.getColumnIndex("blockId"));
                String fieldId = cursor0.getString(cursor0.getColumnIndex("fieldId"));
                String speciesId = cursor0.getString(cursor0.getColumnIndex("speciesId"));
                int x = cursor0.getInt(cursor0.getColumnIndex("x"));
                int y = cursor0.getInt(cursor0.getColumnIndex("y"));
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
            HttpRequest.HttpRequest_SpeciesList(jsonArray, self, new HttpRequest.HttpCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                }
            });

        } else {
            showShortToast(self, getString(R.string.toast_null_plan_data));
        }
        cursor0 = sqLiteDatabase.query("ExperimentField", null, null, null, null, null, null);
        if (cursor0.moveToFirst()) {
            do {
                String experimentFieldId = cursor0.getString(cursor0.getColumnIndex("id"));
                String description = cursor0.getString(cursor0.getColumnIndex("description"));
                HttpRequest.HttpRequest_description(experimentFieldId, description, self, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                    }
                });
            } while (cursor0.moveToNext());
        }
        cursor0.close();
    }


    private void uploadSurveyData() {
        String sql = "select SpeciesTable.*, LocalSpecies.* from SpeciesTable, LocalSpecies " +
                "where SpeciesTable.speciesId=LocalSpecies.name";
        sqLiteDatabase = dbHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
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
                            }
                        });
                        if (img1 != null)
                            HttpRequest.doUploadTest(img1, speciesId, "1", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                }
                            });
                        if (img2 != null)
                            HttpRequest.doUploadTest(img2, speciesId, "2", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                }
                            });
                        if (img3 != null)
                            HttpRequest.doUploadTest(img3, speciesId, "3", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                }
                            });
                        if (img4 != null)
                            HttpRequest.doUploadTest(img4, speciesId, "4", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                }
                            });
                        if (img5 != null)
                            HttpRequest.doUploadTest(img5, speciesId, "5", self, new HttpRequest.HttpCallback_Str() {
                                @Override
                                public void onSuccess(String result) {
                                }
                            });
                        //sqLiteDatabase.delete("SpeciesTable", null, null);
                    }
                }.start();

            } while (cursor.moveToNext());
        } else {
            showShortToast(self, getString(R.string.toast_null_pick_data));
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
        dbHelper = new SpeciesDBHelper(self, "SpeciesTable.db", null, 10);
        db = dbHelper.getWritableDatabase();
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
                initView(farm_flag);
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
        }
        self.unregisterReceiver(networkChangeReceiver);
    }

    //与firmsurveyfragment进行通信，通知下载了数据
    public interface updateData {
        void updateData(Boolean update_flag);
    }

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
                showShortToast(context, getString(R.string.network_normal));
            } else {
                isOnline = false;
                showShortToast(context, getString(R.string.network_wrong));
            }
        }
    }
}
