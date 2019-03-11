package com.example.kerne.potato;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL = "http://10.0.2.2:9529";
    //    private static final String URL = "file:///android_asset/www/index.html";
//    private static final String URL = "file:///android_asset/vue/index.html";
    WebView webView = null;
    Button btn_general;
//    Button btn_farmland;
//    Button btn_shot;
//    Button btn_field;

    Button button;

    String picPath;

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    //用户角色字段
    String userRole = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "hello main");
        super.onCreate(savedInstanceState);
        //获取用户角色
        userRole = getIntent().getStringExtra("userRole");
        Stetho.initializeWithDefaults(this);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        btn_general = (Button) findViewById(R.id.btn_general);
        btn_general.setOnClickListener(this);
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
                    dbHelper = new SpeciesDBHelper(this,
                            "SpeciesTable.db", null, 1);
                    sqLiteDatabase = dbHelper.getWritableDatabase();
                    Cursor cursor = sqLiteDatabase.query("SpeciesTable", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex("id"));
                            String speciesId = cursor.getString(cursor.getColumnIndex("speciesId")); //++++++++++++++
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
                            int commercialRate = cursor.getInt(cursor.getColumnIndex("commercialRate"));
                            int plotYield1 = cursor.getInt(cursor.getColumnIndex("plotYield1"));
                            int plotYield2 = cursor.getInt(cursor.getColumnIndex("plotYield2"));
                            int plotYield3 = cursor.getInt(cursor.getColumnIndex("plotYield3"));
                            int acreYield = cursor.getInt(cursor.getColumnIndex("acreYield"));
                            int bigPlantHeight = cursor.getInt(cursor.getColumnIndex("bigPlantHeight"));
                            int plantHeightAvg = cursor.getInt(cursor.getColumnIndex("plantHeightAvg"));
                            int bigBranchNumber = cursor.getInt(cursor.getColumnIndex("bigBranchNumber"));
                            int branchNumberAvg = cursor.getInt(cursor.getColumnIndex("branchNumberAvg"));
                            int bigYield = cursor.getInt(cursor.getColumnIndex("bigYield"));
                            picPath = cursor.getString(cursor.getColumnIndex("pic_path"));

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
                                jsonObject.put("bigPlantHeight", bigPlantHeight);
                                jsonObject.put("plantHeightAvg", plantHeightAvg);
                                jsonObject.put("bigBranchNumber", bigBranchNumber);
                                jsonObject.put("branchNumberAvg", branchNumberAvg);
                                jsonObject.put("bigYield", bigYield);
                                jsonObject.put("speciesId", speciesId);

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
//                                HttpRequest.doUploadTest(picPath, MainActivity.this, new HttpRequest.HttpCallback_Str() {
//                                    @Override
//                                    public void onSuccess(String result) {
//                                        Log.d("response_pic", result);
//                                    }
//                                });
                                }
                            }.start();
                        } while (cursor.moveToNext());
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
            case R.id.btn_general:
                Intent intent_general = new Intent(MainActivity.this, GeneralClickActivity.class);
                intent_general.putExtra("userRole", userRole);
                startActivity(intent_general);
                break;
//            case R.id.btn_farmland:
//                Intent intent_farmland = new Intent(MainActivity.this, FarmlandClickActivity.class);
//                startActivity(intent_farmland);
//                break;
//            case R.id.btn_shot:
//                Intent intent_shot = new Intent(MainActivity.this, ShotClickActivity.class);
//                startActivity(intent_shot);
//                break;
//            case R.id.btn_field:
//                Intent intent_field = new Intent(MainActivity.this, FieldClickActivity.class);
//                startActivity(intent_field);
//                break;

/*            case R.id.commit_data:
                Intent intent = new Intent(MainActivity.this, SaveDataActivity.class);
                startActivity(intent);
                break;*/

            default:
                 break;
        }
    }
}
