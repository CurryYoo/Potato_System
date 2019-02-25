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
                            String plotId = cursor.getString(cursor.getColumnIndex("plotId")); //++++++++++++++
                            String sowing_period = cursor.getString(cursor.getColumnIndex("sowing_period"));
                            String emergence_period = cursor.getString(cursor.getColumnIndex("emergence_period"));
                            int rate_of_emergence = cursor.getInt(cursor.getColumnIndex("rate_of_emergence"));
                            String squaring_period = cursor.getString(cursor.getColumnIndex("squaring_period"));
                            String flowering_period = cursor.getString(cursor.getColumnIndex("flowering_period"));
                            String leaf_color = cursor.getString(cursor.getColumnIndex("leaf_color"));
                            String corolla_colors = cursor.getString(cursor.getColumnIndex("corolla_colors"));
                            String plant_flourish = cursor.getString(cursor.getColumnIndex("plant_flourish"));
                            String stem_color = cursor.getString(cursor.getColumnIndex("stem_color"));
                            String natural_fecundity = cursor.getString(cursor.getColumnIndex("natural_fecundity"));
                            String mature_period = cursor.getString(cursor.getColumnIndex("mature_period"));
                            int growing_days = cursor.getInt(cursor.getColumnIndex("growing_days"));
                            String tuber_uniformity = cursor.getString(cursor.getColumnIndex("tuber_uniformity"));
                            String tuber_shape = cursor.getString(cursor.getColumnIndex("tuber_shape"));
                            String potato_skin_smoothness = cursor.getString(cursor.getColumnIndex("potato_skin_smoothness"));
                            String eye = cursor.getString(cursor.getColumnIndex("eye"));
                            String skin_color = cursor.getString(cursor.getColumnIndex("skin_color"));
                            String flesh_color = cursor.getString(cursor.getColumnIndex("flesh_color"));
                            String whether_to_be_included = cursor.getString(cursor.getColumnIndex("whether_to_be_included"));
                            final String remark = cursor.getString(cursor.getColumnIndex("remark"));
                            int num_of_harvested_plants = cursor.getInt(cursor.getColumnIndex("num_of_harvested_plants"));
                            int num_of_large_and_medium_potatoes = cursor.getInt(cursor.getColumnIndex("num_of_large_and_medium_potatoes"));
                            int weight_of_large_and_medium_potatoes = cursor.getInt(cursor.getColumnIndex("weight_of_large_and_medium_potatoes"));
                            int num_of_small_potatoes = cursor.getInt(cursor.getColumnIndex("num_of_small_potatoes"));
                            int weight_of_small_potatoes = cursor.getInt(cursor.getColumnIndex("weight_of_small_potatoes"));
                            int rate_of_economic_potato = cursor.getInt(cursor.getColumnIndex("rate_of_economic_potato"));
                            int small_section_yield1 = cursor.getInt(cursor.getColumnIndex("small_section_yield1"));
                            int small_section_yield2 = cursor.getInt(cursor.getColumnIndex("small_section_yield2"));
                            int small_section_yield3 = cursor.getInt(cursor.getColumnIndex("small_section_yield3"));
                            int per_mu_yield = cursor.getInt(cursor.getColumnIndex("per_mu_yield"));
                            int ten_plant_height = cursor.getInt(cursor.getColumnIndex("ten_plant_height"));
                            int average_plant_height = cursor.getInt(cursor.getColumnIndex("average_plant_height"));
                            int branch_num_of_ten_plants = cursor.getInt(cursor.getColumnIndex("branch_num_of_ten_plants"));
                            int average_branch_num = cursor.getInt(cursor.getColumnIndex("average_branch_num"));
                            int yield_monitoring_of_ten_plants = cursor.getInt(cursor.getColumnIndex("yield_monitoring_of_ten_plants"));
                            picPath = cursor.getString(cursor.getColumnIndex("pic_path"));

                            Gson gson = new Gson();

                            final JSONObject jsonObject0 = new JSONObject();
                            final JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("planting_date", sowing_period);
                                jsonObject.put("emergence_date", emergence_period);
                                jsonObject.put("sprout_rate", rate_of_emergence);
                                jsonObject.put("squaring_stage", squaring_period);
                                jsonObject.put("blooming", flowering_period);
                                jsonObject.put("leaf_colour", leaf_color);
                                jsonObject.put("corolla_colour", corolla_colors);
                                jsonObject.put("flowering", plant_flourish);
                                jsonObject.put("stem_colour", stem_color);
                                jsonObject.put("openpollinated", natural_fecundity);
                                jsonObject.put("maturing_stage", mature_period);
                                jsonObject.put("growing_period", growing_days);
                                jsonObject.put("uniformity_of_tuber_size", tuber_uniformity);
                                jsonObject.put("tuber_shape", tuber_shape);
                                jsonObject.put("skin_smoothness", potato_skin_smoothness);
                                jsonObject.put("eye_depth", eye);
                                jsonObject.put("skin_colour", skin_color);
                                jsonObject.put("flesh_colour", flesh_color);
                                if (whether_to_be_included.equals("yes")) {
                                    jsonObject.put("is_choozen", 1);
                                } else {
                                    jsonObject.put("is_choozen", 0);
                                }
                                jsonObject.put("remark", remark);
                                jsonObject.put("harvest_num", num_of_harvested_plants);
                                jsonObject.put("lm_num", num_of_large_and_medium_potatoes);
                                jsonObject.put("lm_weight", weight_of_large_and_medium_potatoes);
                                jsonObject.put("s_num", num_of_small_potatoes);
                                jsonObject.put("s_weight", weight_of_small_potatoes);
                                jsonObject.put("commercial_rate", rate_of_economic_potato);
                                jsonObject.put("plot_yield1", small_section_yield1);
                                jsonObject.put("plot_yield2", small_section_yield2);
                                jsonObject.put("plot_yield3", small_section_yield3);
                                jsonObject.put("acre_yield", per_mu_yield);
                                jsonObject.put("plant_height10", ten_plant_height);
                                jsonObject.put("plant_height_avg", average_plant_height);
                                jsonObject.put("branch_number10", branch_num_of_ten_plants);
                                jsonObject.put("branch_number_avg", average_branch_num);
                                jsonObject.put("yield10", yield_monitoring_of_ten_plants);
                                jsonObject0.put("plotId", plotId);
                                jsonObject0.put("data", jsonObject);
                                Log.d("testJson", jsonObject0.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new Thread() {
                                @Override
                                public void run() {
                                    HttpRequest.HttpRequest_PlotData(jsonObject0, MainActivity.this, new HttpRequest.HttpCallback() {
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
<<<<<<< HEAD
            case R.id.button:
                Intent intent = new Intent(MainActivity.this, GeneralActivity.class);
                intent.putExtra("userRole", userRole);
                startActivity(intent);
                break;
=======
>>>>>>> master

/*            case R.id.commit_data:
                Intent intent = new Intent(MainActivity.this, SaveDataActivity.class);
                startActivity(intent);
                break;*/

            default:
                break;
        }
    }
}
