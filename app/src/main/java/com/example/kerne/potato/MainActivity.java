package com.example.kerne.potato;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.complextable.widget.BannerCommom.BannerLayout;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.facebook.drawee.backends.pipeline.Fresco;
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
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;
import static com.example.kerne.potato.Util.ShowKeyBoard.delayShowSoftKeyBoard;
import static com.example.kerne.potato.temporarystorage.Util.watchBannerLargePhoto;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL = "http://10.0.2.2:9529";
    //    private static final String URL = "file:///android_asset/www/index.html";
//    private static final String URL = "file:///android_asset/vue/index.html";
    LinearLayout btn_general;
    //    LinearLayout btn_download;
//    LinearLayout btn_data;
    LinearLayout btn_mutlilevel;
    LinearLayout btn_location;
    LinearLayout btn_pick;
    ImageView icon_update_location;
    ImageView icon_update_pick;


    QBadgeView qBadgeView_location;
    QBadgeView qBadgeView_pick;
    Badge badge_location;
    Badge badge_pick;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    SweetAlertDialog downloadDataDialog;

    String img1;
    String img2;
    String img3;
    String img4;
    String img5;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.main_banner)
    BannerLayout mainBanner;
    @BindView(R.id.right_two_button)
    ImageView rightTwoButton;
    @BindView(R.id.right_two_layout)
    LinearLayout rightTwoLayout;
    @BindView(R.id.right_one_button)
    ImageView rightOneButton;
    @BindView(R.id.right_one_layout)
    LinearLayout rightOneLayout;
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;

    //banner 的数据
    private List<JSONObject> bannerPoints = new ArrayList<>();
    private List<String> bannerUri = new ArrayList<>();
    private List<String> bannerTitle = new ArrayList<>();
    private String bigfarmId;
    private String name;
    private String img;
    private int year;
    private String uri;

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase db;

    //用户角色字段
    String userRole = "farmer";

    private static final int BIGFARMLIST_OK = 0;
    private static final int FARMLIST_OK = 1;
    private static final int EXPERIMENTFIELD_OK = 2;
    private static final int SPECIESLIST_OK = 3;

    private static final int Banner_info_OK = 5;

    private static int downloadSuccess_Num = 0;
    private static int request_Num = 4;
    private static int uploadSuccess_Num = 0;

    private String Fid[] = new String[5000];

    private List<JSONObject> mBigFarmList = new ArrayList<>();

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private static boolean isOnline = false;

    //避免内存泄漏
    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;

        private MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = (MainActivity) reference.get();
            if (activity != null) {
                //TODO
                switch (msg.what) {
                    case BIGFARMLIST_OK:
                        downloadSuccess_Num++;
                        activity.downloadDataDialog.setTitleText(activity.getString(R.string.download_farm_data));
                        activity.getBigfarmData();
                        break;
                    case FARMLIST_OK:
                        downloadSuccess_Num++;
                        activity.downloadDataDialog.setTitleText(activity.getString(R.string.download_farm_plan_data));
                        break;
                    case EXPERIMENTFIELD_OK:
                        downloadSuccess_Num++;
                        activity.downloadDataDialog.setTitleText(activity.getString(R.string.download_species_data));
                        break;
                    case SPECIESLIST_OK:
                        downloadSuccess_Num++;
                        break;
                    case Banner_info_OK:
                        activity.initBanner();
                        break;
                }
                if (downloadSuccess_Num == request_Num) {
                    activity.downloadDataDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    activity.downloadDataDialog.setCancelable(true);
                    activity.downloadDataDialog.setTitleText(activity.getString(R.string.download_complete));
                    activity.downloadDataDialog.setContentText(null);
                    downloadSuccess_Num = 0;
                }
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update_location_data:
                    //检查网络状况
                    if (!isOnline) {
                        showShortToast(MainActivity.this, getString(R.string.toast_check_network_state));
                        break;
                    }

                    userRole = UserRole.getUserRole();
                    if (!userRole.equals("farmer")) {
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

                                Log.d("json_SpeciesList", jsonObject0.toString());

                            } while (cursor0.moveToNext());
                            HttpRequest.HttpRequest_SpeciesList(jsonArray, MainActivity.this, new HttpRequest.HttpCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                }
                            });

                        } else {
                            showShortToast(MainActivity.this, getString(R.string.toast_null_plan_data));
                        }
                        cursor0 = sqLiteDatabase.query("ExperimentField", null, null, null, null, null, null);
                        if (cursor0.moveToFirst()) {
                            do {
                                String experimentFieldId = cursor0.getString(cursor0.getColumnIndex("id"));
                                String description = cursor0.getString(cursor0.getColumnIndex("description"));
                                HttpRequest.HttpRequest_description(experimentFieldId, description, MainActivity.this, new HttpRequest.HttpCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                    }
                                });
                            } while (cursor0.moveToNext());
                        }
                        cursor0.close();

                        editor.putBoolean("update_location_data", false);
                        editor.apply();
                        if (badge_location != null) {
                            badge_location.hide(false);
                        }

                        showShortToast(MainActivity.this, getString(R.string.toast_update_plan_complete));
                    } else {
                        showShortToast(MainActivity.this, getString(R.string.toast_log_in));
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    break;
                case R.id.update_pick_data:
                    //检查网络状况
                    if (!isOnline) {
                        showShortToast(MainActivity.this, getString(R.string.toast_check_network_state));
                        break;
                    }

                    //将暂存的数据从数据库取出并提交到远程服务器
                    userRole = UserRole.getUserRole();
                    if (!userRole.equals("farmer")) {
                        String sql = "select SpeciesTable.*, LocalSpecies.* from SpeciesTable, LocalSpecies " +
                                "where SpeciesTable.speciesId=LocalSpecies.name";
                        sqLiteDatabase = dbHelper.getReadableDatabase();
                        final Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
//                    final Cursor cursor = sqLiteDatabase.query("SpeciesTable", null, null, null, null, null, null);

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

                                    Log.d("testJson", jsonObject.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                new Thread() {
                                    @Override
                                    public void run() {
                                        HttpRequest.HttpRequest_SpeciesData(jsonObject, MainActivity.this, new HttpRequest.HttpCallback() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                Log.d("response_update", result.toString());
                                            }
                                        });
                                        if (img1 != null)
                                            HttpRequest.doUploadTest(img1, speciesId, "1", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    Log.d("response_pic1", result);
                                                }
                                            });
                                        if (img2 != null)
                                            HttpRequest.doUploadTest(img2, speciesId, "2", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    Log.d("response_pic2", result);
                                                }
                                            });
                                        if (img3 != null)
                                            HttpRequest.doUploadTest(img3, speciesId, "3", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    Log.d("response_pic3", result);
                                                }
                                            });
                                        if (img4 != null)
                                            HttpRequest.doUploadTest(img4, speciesId, "4", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    Log.d("response_pic4", result);
                                                }
                                            });
                                        if (img5 != null)
                                            HttpRequest.doUploadTest(img5, speciesId, "5", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    Log.d("response_pic5", result);
                                                }
                                            });
                                        //sqLiteDatabase.delete("SpeciesTable", null, null);
                                    }
                                }.start();

                            } while (cursor.moveToNext());
                            editor.putBoolean("update_pick_data", false);
                            editor.apply();
                            if (badge_pick != null) {
                                badge_pick.hide(false);
                            }

                            showShortToast(MainActivity.this, getString(R.string.toast_update_pick_complete));
                        } else {
                            showShortToast(MainActivity.this, getString(R.string.toast_null_pick_data));
                        }
                        cursor.close();
                        btn_pick.setEnabled(true);
                    } else {
                        showShortToast(MainActivity.this, getString(R.string.toast_log_in));
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 1);
                        btn_pick.setEnabled(true);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "hello main");
        Fresco.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();

        //获取用户角色
//        userRole = getIntent().getStringExtra("userRole");
        Stetho.initializeWithDefaults(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBar();

//        btn_download = findViewById(R.id.btn_download);
//        btn_download.setOnClickListener(this);

        btn_general = findViewById(R.id.btn_general);
        btn_general.setOnClickListener(this);

        btn_mutlilevel = findViewById(R.id.btn_mutlilevel);
        btn_mutlilevel.setOnClickListener(this);
//
//        btn_data = findViewById(R.id.btn_data);
//        btn_data.setOnClickListener(this);

        btn_location = findViewById(R.id.update_location_data);
        btn_location.setOnClickListener(onClickListener);

        btn_pick = findViewById(R.id.update_pick_data);
        btn_pick.setOnClickListener(onClickListener);

        qBadgeView_location = new QBadgeView(this);
        qBadgeView_pick = new QBadgeView(this);

        icon_update_location = findViewById(R.id.update_location_data_icon);
        icon_update_pick = findViewById(R.id.update_pick_data_icon);

        if (sp.getBoolean("update_pick_data", false)) {
            if (badge_pick == null) {
                badge_pick = new QBadgeView(this).bindTarget(icon_update_pick).setBadgeText("");
            } else {
                badge_pick.setBadgeText("");
            }
        }
        if (sp.getBoolean("update_location_data", false)) {
            if (badge_location == null) {
                badge_location = new QBadgeView(this).bindTarget(icon_update_location).setBadgeText("");
            } else {
                badge_location.setBadgeText("");
            }
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void initToolBar() {
        titleText.setText(getText(R.string.system_name));

        leftOneButton.setBackgroundResource(R.drawable.download);
        rightOneButton.setBackgroundResource(R.drawable.search_input);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            leftOneLayout.setTooltipText(getString(R.string.download_data));
            rightOneLayout.setTooltipText(getString(R.string.search_species));
        }
        leftOneLayout.setOnClickListener(this);
        rightOneLayout.setOnClickListener(this);
        getBigfarmData();

    }

    private void getBigfarmData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
                bannerPoints.clear();
                SpeciesDBHelper dbHelper = new SpeciesDBHelper(MainActivity.this, "SpeciesTable.db", null, 10);
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

//                    jsonObject0.put("userRole", userRole);
                            bannerPoints.add(jsonObject0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("bigfarmId_error", cursor.getString(cursor.getColumnIndex("bigfarmId")));
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                db.close();
                dbHelper.close();

                Message msg = new Message();
                msg.what = Banner_info_OK;
                myHandler.sendMessage(msg);
                Looper.loop();
            }
        }).start();
    }

    //显示banner
    private void initBanner() {
        bannerTitle.clear();
        bannerUri.clear();
        for (int i = 0; i < bannerPoints.size(); i++) {
            try {
                year = bannerPoints.get(i).getInt("year");
                bannerTitle.add(bannerPoints.get(i).getString("name") + " (" + year + ")");
                bannerUri.add(bannerPoints.get(i).getString("uri"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mainBanner.removeAllViews();
        mainBanner.setViewUrls(bannerTitle, bannerUri);
        mainBanner.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mainBanner.stopAutoPlay();
                try {
                    name = bannerPoints.get(position).getString("name");
                    year = bannerPoints.get(position).getInt("year");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                watchBannerLargePhoto(MainActivity.this, mainBanner, Uri.parse(bannerUri.get(position)), name + " (" + year + ")");
            }
        });
        mainBanner.setOnBannerTitleClickListener(new BannerLayout.OnBannerTitleClickListener() {
            @Override
            public void onTitleClick(int position) {
                try {
                    bigfarmId = bannerPoints.get(position).getString("bigfarmId");
                    img = bannerPoints.get(position).getString("img");
                    year = bannerPoints.get(position).getInt("year");
                    uri = bannerPoints.get(position).getString("uri");
                    name = bannerPoints.get(position).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getBaseContext(), GeneralClickActivity.class);
                intent.putExtra("bigfarmId", bigfarmId);
                intent.putExtra("img", img);
                intent.putExtra("year", year);
                intent.putExtra("uri", uri);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp.getBoolean("update_pick_data", false)) {
            if (badge_pick == null) {
                badge_pick = new QBadgeView(this).bindTarget(icon_update_pick).setBadgeText("");
            } else {
                badge_pick.setBadgeText("");
            }
        }
        if (sp.getBoolean("update_location_data", false)) {
            if (badge_location == null) {
                badge_location = new QBadgeView(this).bindTarget(icon_update_location).setBadgeText("");
            } else {
                badge_location.setBadgeText("");
            }
        }
    }


    @Override
    public void onClick(View v) {
        //获取大田信息
        switch (v.getId()) {
            case R.id.left_one_layout:
                //检查网络状况
                if (!isOnline) {
                    showShortToast(MainActivity.this, getString(R.string.toast_check_network_state));
                    break;
                }
                if (!sp.getBoolean("update_pick_data", false) && !sp.getBoolean("update_location_data", false)) {
                    downloadData();
                } else {
                    final SweetAlertDialog downloadWarnDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                            .setContentText(getString(R.string.download_data_warn))
                            .setConfirmText("确定")
                            .setCancelText("取消")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    editor.putBoolean("update_location_data", false);
                                    editor.putBoolean("update_pick_data", false);
                                    editor.apply();
                                    if (badge_location != null) {
                                        badge_location.hide(false);
                                    }
                                    if (badge_pick != null) {
                                        badge_pick.hide(false);
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
                }
                break;
            case R.id.btn_general:
                Intent intent_general = new Intent(MainActivity.this, BigfarmClickActivity.class);
//              intent_general.putExtra("userRole", userRole);
                startActivity(intent_general);
                break;
            case R.id.btn_mutlilevel:
                Intent intent_mutlilevel = new Intent(MainActivity.this, MultiLevelActivity.class);
                startActivity(intent_mutlilevel);
                break;
            case R.id.right_one_layout:
                final SweetAlertDialog inputDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
                LayoutInflater mlayoutInflater = LayoutInflater.from(this);
                @SuppressLint("InflateParams") final View view = mlayoutInflater.inflate(R.layout.dialog_input, null);
                final EditText dialog_input = view.findViewById(R.id.dialog_input);
                inputDialog.addContentView(view, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                inputDialog.setCustomView(view);
                inputDialog.setConfirmText("确定");
                inputDialog.setCancelText("取消");
                inputDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String speciesId;
                        speciesId = dialog_input.getText().toString();
                        if ("".equals(speciesId)) {
                            showShortToast(MainActivity.this, getString(R.string.input_species_data));
                        } else {
                            sweetAlertDialog.dismiss();
//                            db.close();
//                            dbHelper.close();
                            Intent intent = new Intent(MainActivity.this, SpeciesListActivity.class);
                            intent.putExtra("speciesId", speciesId);
                            intent.putExtra("userRole", userRole);
                            startActivity(intent);
                        }
                    }
                });
                inputDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                inputDialog.show();
                delayShowSoftKeyBoard(dialog_input);
                break;
            default:
                break;
        }
    }

    private void downloadData() {
        downloadDataDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        downloadDataDialog.setTitleText(getString(R.string.download_big_farm_data));
        downloadDataDialog.setContentText("下载云端数据耗时较长，请勿中途退出，以免造成数据缺失");
        downloadDataDialog.setCancelable(false);
        downloadDataDialog.show();

        HttpRequest.HttpRequest_bigfarm(null, MainActivity.this, new HttpRequest.HttpCallback() {
            @Override
            public void onSuccess(final JSONObject result) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.delete("BigfarmList", null, null);
                        File cache = new File(getExternalCacheDir(), "cache"); //缓存图片的文件夹目录
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
                                Log.d("getImageURI", uri.toString());
                                contentValues.put("uri", uri.toString());

//                                        Log.d("db begin", "BigfarmList");
                                db.insert("BigfarmList", null, contentValues);
//                                        Log.d("db end--", "BigfarmList");
//                                        updateSqlite("BigfarmList", "bigfarmId", contentValues);
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

        HttpRequest.HttpRequest_farm(null, MainActivity.this, new HttpRequest.HttpCallback() {
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

//                                        Log.d("db begin", "FarmList");
                                db.insert("FarmList", null, contentValues);
//                                        Log.d("db end--", "FarmList");
//                                        updateSqlite("FarmList", "farmlandId", contentValues); //缓存数据到本地sqlite
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

        HttpRequest.HttpRequest_map(null, MainActivity.this, new HttpRequest.HttpCallback() {
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

//                                        Log.d("db begin", "ExperimentField");
                                db.insert("ExperimentField", null, contentValues);
//                                        Log.d("db end--", "ExperimentField");
//                                        updateSqlite("ExperimentField", "id", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }
                            //Log.d("GeneralJsonList", mList.toString());

                        } catch (Exception e) {
//                            Log.e("HttpRequest_map", "");
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = EXPERIMENTFIELD_OK;
                        myHandler.sendMessage(msg);
                    }
                }).start();

            }
        });

        HttpRequest.HttpRequest_Species(MainActivity.this, new HttpRequest.HttpCallback() {
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

//                                        Log.d("db begin", "SpeciesList");
                                db.insert("SpeciesList", null, contentValues);
//                                        Log.d("db end--", "SpeciesList");
//                                        updateSqlite("SpeciesList", "blockId", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }
                            //Log.d("GeneralJsonList", mList.toString());

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

        HttpRequest.HttpRequest_LocalSpecies(new JSONObject(), MainActivity.this, new HttpRequest.HttpCallback() {
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

//                                        Log.d("db begin", "LocalSpecies");
                                db.insert("LocalSpecies", null, contentValues);
//                                        Log.d("db end--", "LocalSpecies");
//                                        updateSqlite("LocalSpecies", "speciesid", contentValues); //缓存数据到本地sqlite
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

    public void updateSqlite(String table_name, String column_name, ContentValues contentValues) {
//        db = dbHelper.getWritableDatabase();

        Log.d("db begin", table_name);
        db.delete(table_name, null, null);
        db.insert(table_name, null, contentValues);
        Log.d("db end", table_name);
//        Cursor cursor = db.query(table_name, new String[]{column_name}, null, null, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            boolean isExist = false;
//            do {
//                if (contentValues.getAsString(column_name).equals(cursor.getString(0))) {
//                    db.update(table_name, contentValues, column_name + "=?", new String[]{cursor.getString(0)});
//                    isExist = true;
//                }
//            } while (cursor.moveToNext());
//            if (!isExist) {
//                db.insert(table_name, null, contentValues);
//            }
//
////            db.insert(table_name, null, contentValues);
//        } else {
//            Log.d("updateSqlite", "CursorNull");
//            db.insert(table_name, null, contentValues);
//        }
//        cursor.close();
//        db.close();
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
            java.net.URL url = new URL(path);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            Log.d("response.code", response.code() + "");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    userRole = data.getStringExtra("userRole");
//                    Log.d("userRole", userRole);
                }
                break;
            default:
                break;
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (db != null) {
            db.close();
        }
        unregisterReceiver(networkChangeReceiver);
//        dbHelper.close();
    }
}
