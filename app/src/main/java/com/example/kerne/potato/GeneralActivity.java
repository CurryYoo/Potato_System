package com.example.kerne.potato;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GeneralActivity extends AppCompatActivity {

    private ImageView iv_canvas;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        //获取权限角色
        userRole = getIntent().getStringExtra("userRole");

        LinearLayout layout=(LinearLayout) findViewById(R.id.root);
        final DrawView view=new DrawView(this);
        view.setMinimumHeight(500);
        view.setMinimumWidth(300);
        //通知view组件重绘
        view.invalidate();
        layout.addView(view);
        view.setOnTouchListener(touch);

        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
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
                    if(X > DrawView.left + 0 && X < DrawView.left + 200 && Y > DrawView.top + 0 && Y < DrawView.top + 200){
                        Toast.makeText(GeneralActivity.this, "点击了第一个模块", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GeneralActivity.this, SpeciesClickActivity.class);
                        intent.putExtra("fieldId", "1111");
                        intent.putExtra("userRole", userRole);
                        startActivity(intent);
                    }
                    if(X > DrawView.left + 1000 && X < DrawView.left + 1440 && Y > DrawView.top + 0 && Y < DrawView.top + 500){
                        Toast.makeText(GeneralActivity.this, "点击了第二个模块", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GeneralActivity.this, SpeciesClickActivity.class);
                        intent.putExtra("fieldId", "2222");
                        startActivity(intent);
                    }

                    break;
                default:
                    break;
            }
            return true;
        }
    };
}
