package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kerne.potato.complextable.TableActivity;
import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GeneralActivity extends AppCompatActivity {

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
    private LinearLayout layout;
    private ImageView iv_canvas;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private String farmlandId = null;
    private int length;  //试验田的长
    private int width;  //试验田的宽
    private String type;
    private String farmName;
    private String bigFarmName;
    private int year;
    private String userRole;
    private int flag = 0;

    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase db;

    private List<JSONObject> mList = new ArrayList<>();
    private JSONObject jsonObject[] = new JSONObject[100];

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mList.size() != 0) {
                        Log.d("Data-----", mList.get(0).toString());
                        flag = 1;

                        final DrawView view = new DrawView(GeneralActivity.this, mList, length, width);

                        view.setMinimumHeight(500);
                        view.setMinimumWidth(300);
                        //通知view组件重绘
                        view.invalidate();
                        layout.addView(view);
                        view.setOnTouchListener(touch);
                    } else {
                        Toast.makeText(GeneralActivity.this, R.string.field_plan_null_error, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };


    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    Intent intent = new Intent(GeneralActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        ButterKnife.bind(this);

        initToolBar();


        //获取farmlandId、year
        farmlandId = getIntent().getStringExtra("farmlandId");
        length = getIntent().getIntExtra("length", 0);
        width = getIntent().getIntExtra("width", 0);
        type = getIntent().getStringExtra("type");
        farmName=getIntent().getStringExtra("farmName");
        bigFarmName = getIntent().getStringExtra("bigFarmName");
        year = getIntent().getIntExtra("year", 1970);

        //获取权限角色
//        userRole = getIntent().getStringExtra("userRole");

        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 10);
        db = dbHelper.getReadableDatabase();

        getData();

        layout = (LinearLayout) findViewById(R.id.root);
//        final DrawView view=new DrawView(this);
//
//        view.setMinimumHeight(500);
//        view.setMinimumWidth(300);
//        //通知view组件重绘
//        view.invalidate();

//        canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);

//        try {
//            Log.d("mList.color", mList.get(0).getString("color"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        view.Draw(canvas, mList);

//        layout.addView(view);
//        view.setOnTouchListener(touch);

        layout.setGravity(View.TEXT_ALIGNMENT_CENTER);
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        //canvas.drawRect(400, 400, 600, 600, paint);

//        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
//        iv_canvas.setOnTouchListener(touch);

//        baseBitmap = BitmapFactory.decodeResource(getResources(), R.id.iv_canvas);
////        baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
////                iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(baseBitmap);
//        canvas.drawRect(0, 0, 30, 30, paint);

//        Intent intent = getIntent();
//        TextView textView = (TextView)findViewById(R.id.test_general);
//        textView.setText(intent.getStringExtra("option"));

    }
    private void initToolBar() {
        titleText.setText("种植图");
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.ic_menu_home);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
        rightOneLayout.setOnClickListener(toolBarOnClickListener);
    }


    private void getData() {
        //获取分块的坐标信息
        Cursor cursor = db.query("ExperimentField", null, "farmlandId=?", new String[]{farmlandId}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject.put("deleted", cursor.getString(cursor.getColumnIndex("deleted")));
                    jsonObject.put("expType", cursor.getString(cursor.getColumnIndex("expType")));
                    jsonObject.put("moveX", cursor.getString(cursor.getColumnIndex("moveX")));
                    jsonObject.put("moveY", cursor.getString(cursor.getColumnIndex("moveY")));
                    jsonObject.put("moveX1", cursor.getString(cursor.getColumnIndex("moveX1")));
                    jsonObject.put("moveY1", cursor.getString(cursor.getColumnIndex("moveY1")));
                    jsonObject.put("num", cursor.getString(cursor.getColumnIndex("num")));
                    jsonObject.put("color", cursor.getString(cursor.getColumnIndex("color")));
                    jsonObject.put("farmlandId", cursor.getString(cursor.getColumnIndex("farmlandId")));
                    jsonObject.put("rows", cursor.getInt(cursor.getColumnIndex("rows")));
                    jsonObject.put("speciesList", cursor.getString(cursor.getColumnIndex("speciesList")));
                    mList.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("fieldId_error", cursor.getString(cursor.getColumnIndex("id")));
                }
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(GeneralActivity.this, R.string.field_null_error, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        Log.d("mList.toString", mList.toString());

        Message msg = new Message();
        msg.what = 1;
        uiHandler.sendMessage(msg);

//        new Thread(){
//            @Override
//            public void run(){
//                HttpRequest.HttpRequest_map(farmlandId, GeneralActivity.this, new HttpRequest.HttpCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        try {
//                            JSONArray rows = new JSONArray();
//                            rows = result.getJSONArray("rows");
//                            int total = result.getInt("total");
//                            for(int i = 0; i < total; i++){
//                                mList.add(rows.getJSONObject(i));
//                                //jsonObject[i] = rows.getJSONObject(i);
//                                //jsonObject0.put("userRole", userRole);
//                            }
//                            //Log.d("GeneralJsonList", mList.toString());
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        Message msg = new Message();
//                        msg.what = 1;
//                        uiHandler.sendMessage(msg);
//
//                    }
//                });
//            }
//        }.start();
    }

    private View.OnTouchListener touch = new View.OnTouchListener() {
        // 定义手指开始触摸的坐标
        float X;
        float Y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                // 用户按下动作
                case MotionEvent.ACTION_DOWN:
//                    // 第一次绘图初始化内存图片，指定背景为白色
//                    if (baseBitmap == null) {
//
////                        baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
////                                iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
//                        canvas = new Canvas();
//                        canvas.drawColor(Color.WHITE);
//                    }
//                    // 记录开始触摸的点的坐标
//                    X = event.getX();
//                    Y = event.getY();
                    break;
                // 用户手指在屏幕上移动的动作
                case MotionEvent.ACTION_MOVE:
//                    // 记录移动位置的点的坐标
//                    float stopX = event.getX();
//                    float stopY = event.getY();
//
//                    //根据两点坐标，绘制连线
//                    //canvas.drawLine(X, Y, stopX, stopY, paint);
//                    canvas.drawRect(0, 0, 30, 30, paint);
//
//                    // 更新开始点的位置
//                    X = event.getX();
//                    Y = event.getY();
//
//                    // 把图片展示到ImageView中
//
//                    iv_canvas.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:
                    X = event.getX();
                    Y = event.getY();
                    Log.d("X,Y", X + "," + Y);

                    for (int i = 0; i < mList.size(); i++) {
                        if (X > DrawView.coord[i][0] && X < DrawView.coord[i][2] && Y > DrawView.coord[i][1] && Y < DrawView.coord[i][3]) {
                            try {
                                Toast.makeText(GeneralActivity.this, "试验类型：" + mList.get(i).getString("expType"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final int finalI = i;
                            String fieldId = null, expType = null;
                            try {
                                fieldId = mList.get(finalI).getString("id");
                                expType = mList.get(finalI).getString("expType");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(GeneralActivity.this, TableActivity.class);
//                                        intent.putExtra("fields_json", jsonObject.toString());
                            intent.putExtra("fieldId", fieldId);
                            intent.putExtra("expType", expType);
                            intent.putExtra("farmlandId", farmlandId);
                            intent.putExtra("type", type);
                            intent.putExtra("farmName",farmName);
                            intent.putExtra("bigFarmName", bigFarmName);
                            intent.putExtra("year", year);
//                                        intent.putExtra("userRole", userRole);
                            startActivity(intent);
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeCallbacksAndMessages(null);
        db.close();
        dbHelper.close();
    }

}
