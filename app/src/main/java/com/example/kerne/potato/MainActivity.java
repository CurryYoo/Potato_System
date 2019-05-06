package com.example.kerne.potato;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SaveDataActivity;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL = "http://10.0.2.2:9529";
    //    private static final String URL = "file:///android_asset/www/index.html";
//    private static final String URL = "file:///android_asset/vue/index.html";
    WebView webView = null;
    Button btn_general;
    Button btn_download;
    Button btn_data;
//    Button btn_farmland;
//    Button btn_shot;
//    Button btn_field;

    Button button;

    String img1;
    String img2;
    String img3;
    String img4;
    String img5;

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    //用户角色字段
    String userRole = null;

    private static final int FARMLIST_OK = 1;
    private static final int EXPERIMENTFIELD_OK = 2;
    private static final int SPECIESLIST_OK = 3;

    private int downloadSuccess_Num = 0;
    private int request_Num = 0;
    private int uploadSuccess_Num = 0;

    private String Fid[] = new String[5000];

    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case FARMLIST_OK:
                    downloadSuccess_Num++;
                    Log.d("num1", "" + downloadSuccess_Num);

                    if(downloadSuccess_Num == 3){
                        Log.d("download1", "ok");
                        Toast.makeText(MainActivity.this, "下载成功!", Toast.LENGTH_SHORT).show();
                        downloadSuccess_Num = 0;
                    }
                    break;
                case EXPERIMENTFIELD_OK:
                    downloadSuccess_Num++;
                    Log.d("num2", "" + downloadSuccess_Num);
                    if(downloadSuccess_Num == 3){
                        Log.d("download2", "ok");
                        Toast.makeText(MainActivity.this, "下载成功!", Toast.LENGTH_SHORT).show();
                        downloadSuccess_Num = 0;
                    }
                    break;
                case SPECIESLIST_OK:
                    downloadSuccess_Num++;
                    Log.d("num3", "" + downloadSuccess_Num);
                    if(downloadSuccess_Num == 3){
                        Log.d("download3", "ok");
                        Toast.makeText(MainActivity.this, "下载成功!", Toast.LENGTH_LONG).show();
                        downloadSuccess_Num = 0;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "hello main");
        super.onCreate(savedInstanceState);
        //获取用户角色
        userRole = getIntent().getStringExtra("userRole");
        Stetho.initializeWithDefaults(this);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        btn_download = (Button) findViewById(R.id.btn_download);
        btn_download.setOnClickListener(this);

        btn_general = (Button) findViewById(R.id.btn_general);
        btn_general.setOnClickListener(this);

        btn_data = (Button) findViewById(R.id.btn_data);
        btn_data.setOnClickListener(this);

        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 8);

//        btn_farmland = (Button)findViewById(R.id.btn_farmland);
//        btn_farmland.setOnClickListener(this);
//        btn_shot = (Button)findViewById(R.id.btn_shot);
//        btn_shot.setOnClickListener(this);
//        btn_field = (Button)findViewById(R.id.btn_field);
//        btn_field.setOnClickListener(this);

///*       if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }*/
//
///*        Button btnCommitData = (Button) findViewById(R.id.commit_data);
//        btnCommitData.setOnClickListener(this);*/
//        webView = (WebView) findViewById(R.id.web_view);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        //自适应屏幕
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webSettings.setLoadWithOverviewMode(true);
//        //支持缓存
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        //支持缩放
//        webSettings.setSupportZoom(true);
//        //支持扩大比例的缩放
//        webSettings.setUseWideViewPort(true);
////        webView.setWebViewClient(new WebViewClient());
//
//        WebViewClient webViewClient = new WebViewClient() {
////            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
////            @Override
////            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
////                final Uri uri = request.getUrl();
////                return handlerUri(uri);
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if(url == null) return false;
//
//                try {
//                    if(url.startsWith("weixin://") //微信
//                            || url.startsWith("alipays://") //支付宝
//                            || url.startsWith("mailto://") //邮件
//                            || url.startsWith("tel://")//电话
//                            || url.startsWith("dianping://")//大众点评
//                            || url.startsWith("baiduboxapp://")//大众点评
//                            || url.startsWith("baiduboxlite://")//大众点评
//                        //其他自定义的scheme
//                            ) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(intent);
//                        return true;
//                    }
//                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
//                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
//                }
//
//                //处理http和https开头的url
//                webView.loadUrl(url);
//                return true;
//            }
//        };
//
//        webView.setWebViewClient(webViewClient);
//        webView.loadUrl(URL);

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity", userRole);
        if (userRole.equals("farmer")) {
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_offline:
                Intent intent = new Intent(MainActivity.this, SaveDataActivity.class);
                startActivity(intent);
                break;
            case R.id.commit:
                //将暂存的数据从数据库取出并提交到远程服务器
                if (userRole.equals("admin")) {
                    sqLiteDatabase = dbHelper.getReadableDatabase();
                    final Cursor cursor = sqLiteDatabase.query("SpeciesTable", null, null, null, null, null, null);

                    if (cursor.moveToFirst()) {
                        do {
                            final String speciesId = cursor.getString(cursor.getColumnIndex("speciesId")); //++++++++++++++
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

                            Gson gson = new Gson();

                            final JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("plantingDate", plantingDate);
                                jsonObject.put("emergenceDate", emergenceDate);
                                jsonObject.put("sproutRate", sproutRate);
                                jsonObject.put("squaringStage", squaringStage);
                                jsonObject.put("blooming", blooming);
                                jsonObject.put("leafColour", leafColour);
                                jsonObject.put("corollaColour", corollaColour);
                                jsonObject.put("flowering", flowering);
                                jsonObject.put("stemColour", stemColour);
                                jsonObject.put("openpollinated", openpollinated);
                                jsonObject.put("maturingStage", maturingStage);
                                jsonObject.put("growingPeriod", growingPeriod);
                                jsonObject.put("uniformityOfTuberSize", uniformityOfTuberSize);
                                jsonObject.put("tuberShape", tuberShape);
                                jsonObject.put("skinSmoothness", skinSmoothness);
                                jsonObject.put("eyeDepth", eyeDepth);
                                jsonObject.put("skinColour", skinColour);
                                jsonObject.put("fleshColour", fleshColour);
                                if (isChoozen.equals("yes")) {
                                    jsonObject.put("isChoozen", 1);
                                } else {
                                    jsonObject.put("isChoozen", 0);
                                }
                                jsonObject.put("remark", remark);
                                jsonObject.put("harvestNum", harvestNum);
                                jsonObject.put("lmNum", lmNum);
                                jsonObject.put("lmWeight", lmWeight);
                                jsonObject.put("sNum", sNum);
                                jsonObject.put("sWeight", sWeight);
                                jsonObject.put("commercialRate", commercialRate);
                                jsonObject.put("plotYield1", plotYield1);
                                jsonObject.put("plotYield2", plotYield2);
                                jsonObject.put("plotYield3", plotYield3);
                                jsonObject.put("acreYield", acreYield);
                                jsonObject.put("bigPlantHeight1", bigPlantHeight1);
                                jsonObject.put("bigPlantHeight2", bigPlantHeight2);
                                jsonObject.put("bigPlantHeight3", bigPlantHeight3);
                                jsonObject.put("bigPlantHeight4", bigPlantHeight4);
                                jsonObject.put("bigPlantHeight5", bigPlantHeight5);
                                jsonObject.put("bigPlantHeight6", bigPlantHeight6);
                                jsonObject.put("bigPlantHeight7", bigPlantHeight7);
                                jsonObject.put("bigPlantHeight8", bigPlantHeight8);
                                jsonObject.put("bigPlantHeight9", bigPlantHeight9);
                                jsonObject.put("bigPlantHeight10", bigPlantHeight10);
                                jsonObject.put("plantHeightAvg", plantHeightAvg);
                                jsonObject.put("bigBranchNumber1", bigBranchNumber1);
                                jsonObject.put("bigBranchNumber2", bigBranchNumber2);
                                jsonObject.put("bigBranchNumber3", bigBranchNumber3);
                                jsonObject.put("bigBranchNumber4", bigBranchNumber4);
                                jsonObject.put("bigBranchNumber5", bigBranchNumber5);
                                jsonObject.put("bigBranchNumber6", bigBranchNumber6);
                                jsonObject.put("bigBranchNumber7", bigBranchNumber7);
                                jsonObject.put("bigBranchNumber8", bigBranchNumber8);
                                jsonObject.put("bigBranchNumber9", bigBranchNumber9);
                                jsonObject.put("bigBranchNumber10", bigBranchNumber10);
                                jsonObject.put("branchNumberAvg", branchNumberAvg);
                                jsonObject.put("bigYield1", bigYield1);
                                jsonObject.put("bigYield2", bigYield2);
                                jsonObject.put("bigYield3", bigYield3);
                                jsonObject.put("bigYield4", bigYield4);
                                jsonObject.put("bigYield5", bigYield5);
                                jsonObject.put("bigYield6", bigYield6);
                                jsonObject.put("bigYield7", bigYield7);
                                jsonObject.put("bigYield8", bigYield8);
                                jsonObject.put("bigYield9", bigYield9);
                                jsonObject.put("bigYield10", bigYield10);
                                jsonObject.put("smalYield1", smalYield1);
                                jsonObject.put("smalYield2", smalYield2);
                                jsonObject.put("smalYield3", smalYield3);
                                jsonObject.put("smalYield4", smalYield4);
                                jsonObject.put("smalYield5", smalYield5);
                                jsonObject.put("smalYield6", smalYield6);
                                jsonObject.put("smalYield7", smalYield7);
                                jsonObject.put("smalYield8", smalYield8);
                                jsonObject.put("smalYield9", smalYield9);
                                jsonObject.put("smalYield10", smalYield10);
                                jsonObject.put("speciesId", speciesId);
                                jsonObject.put("testId", blockId);
                                jsonObject.put("experimentType", experimentType);

                                Log.d("testJson", jsonObject.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new Thread() {
                                @Override
                                public void run() {
                                    final int[] signal = {1};
                                    HttpRequest.HttpRequest_SpeciesData(jsonObject, MainActivity.this, new HttpRequest.HttpCallback() {
                                        @Override
                                        public void onSuccess(JSONObject result) {
                                            Log.d("response_update", result.toString());
                                            try {
                                                if(result.getBoolean("success"))
                                                    signal[0]--;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    if(img1 != null)
                                        HttpRequest.doUploadTest(img1, speciesId, "1", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Log.d("response_pic1", result);
                                            }
                                        });
                                    if(img2 != null)
                                        HttpRequest.doUploadTest(img2, speciesId, "2", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Log.d("response_pic2", result);
                                            }
                                        });
                                    if(img3 != null)
                                        HttpRequest.doUploadTest(img3, speciesId, "3", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Log.d("response_pic3", result);
                                            }
                                        });
                                    if(img4 != null)
                                        HttpRequest.doUploadTest(img4, speciesId, "4", MainActivity.this, new HttpRequest.HttpCallback_Str() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Log.d("response_pic4", result);
                                            }
                                        });
                                    if(img5 != null)
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
                        Toast.makeText(MainActivity.this, "数据上传成功！", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "尚未采集数据!", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(MainActivity.this, "对不起，您没有该权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                Toast.makeText(MainActivity.this, "开始下载数据……", Toast.LENGTH_SHORT).show();

                HttpRequest.HttpRequest_general(null, MainActivity.this, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(JSONObject result) { //获取FarmList信息
                        try {
                            JSONArray rows = new JSONArray();
                            rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            JSONObject jsonObject0;
                            for(int i = 0; i < total; i++){
                                jsonObject0 = rows.getJSONObject(i);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
                                if(jsonObject0.getBoolean("deleted")){
                                    contentValues.put("deleted", "true");
                                }
                                else{
                                    contentValues.put("deleted", "false");
                                }
                                contentValues.put("name", jsonObject0.getString("name"));
                                if(jsonObject0.get("length") != null){
                                    contentValues.put("length", jsonObject0.getString("length"));
                                }
                                if(jsonObject0.get("width") != null){
                                    contentValues.put("width", jsonObject0.getString("width"));
                                }
                                contentValues.put("spare1", jsonObject0.getString("spare1"));
                                contentValues.put("spare2", jsonObject0.getString("spare2"));
                                updateSqlite("FarmList", "farmlandId", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = FARMLIST_OK;
                        uiHandler.sendMessage(msg);
                    }
                });

                HttpRequest.HttpRequest_map(null, MainActivity.this, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(JSONObject result) { //获取ExperimentField信息
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            ContentValues contentValues = new ContentValues();
                            JSONObject jsonObject0;
                            for(int i = 0; i < total; i++){
                                jsonObject0 = rows.getJSONObject(i);

                                contentValues.put("id", jsonObject0.getString("id"));
                                if(jsonObject0.getBoolean("deleted")){
                                    contentValues.put("deleted", "true");
                                }
                                else{
                                    contentValues.put("deleted", "false");
                                }
                                contentValues.put("expType", jsonObject0.getString("expType"));
                                if(jsonObject0.get("moveX") != null){
                                    contentValues.put("moveX", jsonObject0.getInt("moveX"));
                                }
                                if(jsonObject0.get("moveY") != null){
                                    contentValues.put("moveY", jsonObject0.getInt("moveY"));
                                }
                                if(jsonObject0.get("moveX1") != null){
                                    contentValues.put("moveX1", jsonObject0.getInt("moveX1"));
                                }
                                if(jsonObject0.get("moveY1") != null){
                                    contentValues.put("moveY1", jsonObject0.getInt("moveY1"));
                                }
                                contentValues.put("spare1", jsonObject0.getString("spare1"));
                                contentValues.put("spare2", jsonObject0.getString("spare2"));
                                contentValues.put("num", jsonObject0.getString("num"));
                                contentValues.put("color", jsonObject0.getString("color"));
                                contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
                                if(jsonObject0.get("year") != null){
                                    contentValues.put("year", jsonObject0.getInt("year"));
                                }
                                updateSqlite("ExperimentField", "id", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }
                            //Log.d("GeneralJsonList", mList.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = EXPERIMENTFIELD_OK;
                        uiHandler.sendMessage(msg);

                    }
                });

                HttpRequest.HttpRequest_Species(MainActivity.this, new HttpRequest.HttpCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            JSONArray rows = result.getJSONArray("rows");
                            int total = result.getInt("total");
                            ContentValues contentValues = new ContentValues();
                            JSONObject jsonObject0;
                            for(int i = 0; i < total; i++){
                                jsonObject0 = rows.getJSONObject(i);

                                contentValues.put("blockId", jsonObject0.getString("blockId"));
                                contentValues.put("fieldId", jsonObject0.getString("fieldId"));
                                contentValues.put("speciesId", jsonObject0.getString("speciesId"));
                                contentValues.put("x", jsonObject0.getString("x"));
                                contentValues.put("y", jsonObject0.getString("y"));
                                updateSqlite("SpeciesList", "blockId", contentValues); //缓存数据到本地sqlite
                                contentValues.clear();
                            }
                            //Log.d("GeneralJsonList", mList.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = SPECIESLIST_OK;
                        uiHandler.sendMessage(msg);
                    }
                });

//                new Thread(){
//                    @Override
//                    public void run(){
//                        HttpRequest.HttpRequest_general(null, MainActivity.this, new HttpRequest.HttpCallback() {
//                            @Override
//                            public void onSuccess(JSONObject result) { //获取FarmList信息
//                                try {
//                                    JSONArray rows = new JSONArray();
//                                    rows = result.getJSONArray("rows");
//                                    int total = result.getInt("total");
//                                    for(int i = 0; i < total; i++){
//                                        JSONObject jsonObject0 = rows.getJSONObject(i);
//                                        ContentValues contentValues = new ContentValues();
//                                        contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
//                                        if(jsonObject0.getBoolean("deleted")){
//                                            contentValues.put("deleted", "true");
//                                        }
//                                        else{
//                                            contentValues.put("deleted", "false");
//                                        }
//                                        contentValues.put("name", jsonObject0.getString("name"));
//                                        if(jsonObject0.get("length") != null){
//                                            contentValues.put("length", jsonObject0.getString("length"));
//                                        }
//                                        if(jsonObject0.get("width") != null){
//                                            contentValues.put("width", jsonObject0.getString("width"));
//                                        }
//                                        contentValues.put("spare1", jsonObject0.getString("spare1"));
//                                        contentValues.put("spare2", jsonObject0.getString("spare2"));
//                                        updateSqlite("FarmList", "farmlandId", contentValues); //缓存数据到本地sqlite
//                                        contentValues.clear();
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                                Message msg = new Message();
//                                msg.what = FARMLIST_OK;
//                                uiHandler.sendMessage(msg);
//                            }
//                        });
//
//                    }
//                }.start();
//
//                new Thread(){
//                    @Override
//                    public void run() {
//                        HttpRequest.HttpRequest_map(null, MainActivity.this, new HttpRequest.HttpCallback() {
//                            @Override
//                            public void onSuccess(JSONObject result) { //获取ExperimentField信息
//                                try {
//                                    JSONArray rows = result.getJSONArray("rows");
//                                    int total = result.getInt("total");
//                                    ContentValues contentValues = new ContentValues();
//                                    for(int i = 0; i < total; i++){
//                                        JSONObject jsonObject0 = rows.getJSONObject(i);
//
//                                        contentValues.put("id", jsonObject0.getString("id"));
//                                        if(jsonObject0.getBoolean("deleted")){
//                                            contentValues.put("deleted", "true");
//                                        }
//                                        else{
//                                            contentValues.put("deleted", "false");
//                                        }
//                                        contentValues.put("expType", jsonObject0.getString("expType"));
//                                        if(jsonObject0.get("moveX") != null){
//                                            contentValues.put("moveX", jsonObject0.getInt("moveX"));
//                                        }
//                                        if(jsonObject0.get("moveY") != null){
//                                            contentValues.put("moveY", jsonObject0.getInt("moveY"));
//                                        }
//                                        if(jsonObject0.get("moveX1") != null){
//                                            contentValues.put("moveX1", jsonObject0.getInt("moveX1"));
//                                        }
//                                        if(jsonObject0.get("moveY1") != null){
//                                            contentValues.put("moveY1", jsonObject0.getInt("moveY1"));
//                                        }
//                                        contentValues.put("spare1", jsonObject0.getString("spare1"));
//                                        contentValues.put("spare2", jsonObject0.getString("spare2"));
//                                        contentValues.put("num", jsonObject0.getString("num"));
//                                        contentValues.put("color", jsonObject0.getString("color"));
//                                        contentValues.put("farmlandId", jsonObject0.getString("farmlandId"));
//                                        if(jsonObject0.get("year") != null){
//                                            contentValues.put("year", jsonObject0.getInt("year"));
//                                        }
//                                        updateSqlite("ExperimentField", "id", contentValues); //缓存数据到本地sqlite
//                                        contentValues.clear();
//                                    }
//                                    //Log.d("GeneralJsonList", mList.toString());
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                                Message msg = new Message();
//                                msg.what = EXPERIMENTFIELD_OK;
//                                uiHandler.sendMessage(msg);
//
//                            }
//                        });
//                    }
//                }.start();
//
//                new Thread(){
//                    @Override
//                    public void run() {
//                        HttpRequest.HttpRequest_Species(MainActivity.this, new HttpRequest.HttpCallback() {
//                            @Override
//                            public void onSuccess(JSONObject result) {
//                                try {
//                                    JSONArray rows = result.getJSONArray("rows");
//                                    int total = result.getInt("total");
//                                    ContentValues contentValues = new ContentValues();
//                                    for(int i = 0; i < total; i++){
//                                        JSONObject jsonObject0 = rows.getJSONObject(i);
//
//                                        contentValues.put("blockId", jsonObject0.getString("blockId"));
//                                        contentValues.put("fieldId", jsonObject0.getString("fieldId"));
//                                        contentValues.put("speciesId", jsonObject0.getString("speciesId"));
//                                        contentValues.put("x", jsonObject0.getString("x"));
//                                        contentValues.put("y", jsonObject0.getString("y"));
//                                        updateSqlite("SpeciesList", "blockId", contentValues); //缓存数据到本地sqlite
//                                        contentValues.clear();
//                                    }
//                                    //Log.d("GeneralJsonList", mList.toString());
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                                Message msg = new Message();
//                                msg.what = SPECIESLIST_OK;
//                                uiHandler.sendMessage(msg);
//                            }
//                        });
//                    }
//                }.start();

                break;
            case R.id.btn_general:
                Intent intent_general = new Intent(MainActivity.this, GeneralClickActivity.class);
                intent_general.putExtra("userRole", userRole);
                startActivity(intent_general);
                break;

            case R.id.btn_data:
                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(MainActivity.this).builder()
                        .setTitle("请输入品种编号：")
                        .setEditText("");
                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showMsg(myAlertInputDialog.getResult());
                        String speciesId;
                        speciesId = myAlertInputDialog.getResult();

                        Intent intent = new Intent(MainActivity.this, SpeciesListActivity.class);
                        intent.putExtra("speciesId", speciesId);
                        intent.putExtra("userRole", userRole);
                        startActivity(intent);
                        myAlertInputDialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //showMsg("取消");
                        myAlertInputDialog.dismiss();
                    }
                });
                myAlertInputDialog.show();
                break;
/*            case R.id.commit_data:
                Intent intent = new Intent(MainActivity.this, SaveDataActivity.class);
                startActivity(intent);
                break;*/

            default:
                 break;
        }
    }

    public void updateSqlite(String table_name, String column_name, ContentValues contentValues){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(table_name, new String[]{column_name}, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            Boolean isExist = false;
            do {
                if (contentValues.getAsString(column_name).equals(cursor.getString(0))) {
                    db.update(table_name, contentValues, column_name + "=?", new String[]{cursor.getString(0)});
                    isExist = true;
                }
            } while (cursor.moveToNext());
            if (!isExist) {
                db.insert(table_name, null, contentValues);
            }
        }
        else {
            Log.d("updateSqlite", "CursorNull");
            db.insert(table_name, null, contentValues);
        }
        cursor.close();
    }
}
