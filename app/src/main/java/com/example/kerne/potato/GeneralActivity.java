package com.example.kerne.potato;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kerne.potato.temporarystorage.SpeciesDBHelper;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeneralActivity extends AppCompatActivity {

    private LinearLayout layout;
    private ImageView iv_canvas;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private String farmlandId = null;
    private int year;
    private String userRole;
    private int flag = 0;

    private List<JSONObject> mList = new ArrayList<>();
    private JSONObject jsonObject[] = new JSONObject[100];

    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    if(mList.size() != 0){
                        Log.d("Data-----", mList.get(0).toString());
                        flag = 1;

                        final DrawView view=new DrawView(GeneralActivity.this, mList);

                        view.setMinimumHeight(500);
                        view.setMinimumWidth(300);
                        //通知view组件重绘
                        view.invalidate();
                        layout.addView(view);
                        view.setOnTouchListener(touch);
                    }
                    else {
                        Toast.makeText(GeneralActivity.this, "选择的实验田尚未规划种植图", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        //获取farmlandId、year
        farmlandId = getIntent().getStringExtra("farmlandId");
        year = getIntent().getIntExtra("year", 0);

        //获取权限角色
        userRole = getIntent().getStringExtra("userRole");

        getData();

        layout=(LinearLayout) findViewById(R.id.root);
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

    private void getData(){
        //获取分块的坐标信息
        SpeciesDBHelper dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 8);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "select * from ExperimentField where (farmlandId,year) in (('" + farmlandId + "',"+ year +"))";
//        String sql = "select * from ExperimentField where farmlandId='" + farmlandId + "'";
        Cursor cursor = db.rawQuery(sql, null);
//        Cursor cursor = db.query("ExperimentField", null, "farmlandId=?", new String[]{farmlandId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("deleted", cursor.getString(cursor.getColumnIndex("deleted")));
                    jsonObject.put("expType", cursor.getString(cursor.getColumnIndex("expType")));
                    jsonObject.put("moveX", cursor.getString(cursor.getColumnIndex("moveX")));
                    jsonObject.put("moveY", cursor.getString(cursor.getColumnIndex("moveY")));
                    jsonObject.put("moveX1", cursor.getString(cursor.getColumnIndex("moveX1")));
                    jsonObject.put("moveY1", cursor.getString(cursor.getColumnIndex("moveY1")));
                    jsonObject.put("num", cursor.getString(cursor.getColumnIndex("num")));
                    jsonObject.put("color", cursor.getString(cursor.getColumnIndex("color")));
                    jsonObject.put("farmlandId", cursor.getString(cursor.getColumnIndex("farmlandId")));
                    jsonObject.put("year", cursor.getString(cursor.getColumnIndex("year")));
                    mList.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("fieldId_error", cursor.getString(cursor.getColumnIndex("id")));
                }
            } while (cursor.moveToNext());
        }
        else {
            Toast.makeText(GeneralActivity.this, "ExperimentField null", Toast.LENGTH_SHORT).show();
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

                    for(int i = 0; i < mList.size(); i++){
                        if(X > DrawView.coord[i][0] && X < DrawView.coord[i][2] && Y > DrawView.coord[i][1] && Y < DrawView.coord[i][3]){
                            try {
                                Toast.makeText(GeneralActivity.this, "点击了'"+ mList.get(i).getString("expType") +"'模块", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(GeneralActivity.this, SpeciesClickActivity.class);
                            try {
                                intent.putExtra("fieldId", mList.get(i).getString("id"));
                                intent.putExtra("expType", mList.get(i).getString("expType"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent.putExtra("userRole", userRole);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // 为ActionBar扩展菜单项
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.map, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.map_color:
//
//                break;
////            case android.R.id.home:
////                Intent intent = new Intent(this, MainActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                startActivity(intent);
//            default:
//        }
//        return true;
//    }

}
