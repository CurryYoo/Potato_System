package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kerne.potato.Util.CustomToast.showShortToast;

public class BigfarmClickActivity extends AppCompatActivity implements BigfarmClickAdapter.OnItemClickListener {

    private static final String TAG = BigfarmClickActivity.class.getSimpleName();
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;

    private List<JSONObject> mList = new ArrayList<>();

    private String name = null;
    private String userRole;

    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigfarm_click_activity);
        ButterKnife.bind(this);

//        userRole = getIntent().getStringExtra("userRole");

        initToolBar();
        initData();

        //initView();
    }

    private void initToolBar() {
        titleText.setText(getText(R.string.big_farm));
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        leftOneLayout.setOnClickListener(toolBarOnClickListener);
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //获取数据库中数据
                SpeciesDBHelper dbHelper = new SpeciesDBHelper(BigfarmClickActivity.this, "SpeciesTable.db", null, 10);
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
                            mList.add(jsonObject0);
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
                msg.what = 1;
                uiHandler.sendMessage(msg);
                Looper.loop();
            }
        }).start();

        //Log.d("mList.toString", mList.toString());
//        initView();

//        new Thread(){
//            @Override
//            public void run(){
//                HttpRequest.HttpRequest_farm(name, GeneralClickActivity.this, new HttpRequest.HttpCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        try {
//                            JSONArray rows = new JSONArray();
//                            rows = result.getJSONArray("rows");
//                            int total = result.getInt("total");
//                            for(int i = 0; i < total; i++){
//                                JSONObject jsonObject0 = rows.getJSONObject(i);
//                                jsonObject0.put("userRole", userRole);
//                                mList.add(jsonObject0);
//                            }
//                            Log.d("GeneralJsonList", mList.toString());
//
//                            initView();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        }.start();

    }

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i(BigfarmClickActivity.TAG, "init data ok");
                    initView();
                    break;
            }
        }
    };

    private void initView() {
        BigfarmClickAdapter adapter = new BigfarmClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.rcv_click);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        if (mList.size() == 0) {
            showShortToast(this, getString(R.string.toast_bigFarm_null_error));
        }
        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(final String content) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
    }
}
