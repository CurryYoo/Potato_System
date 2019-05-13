package com.example.kerne.potato;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.mianliner);
        View view_basic = LayoutInflater.from(TestActivity.this).inflate(R.layout.item_basicinfo, null);
        InfoItemBar mbar_basic = new InfoItemBar(TestActivity.this, "基本信息");
        mbar_basic.addView(view_basic);
        mbar_basic.setShow(true);
        mLinearLayout.addView(mbar_basic);
        View view_height = LayoutInflater.from(TestActivity.this).inflate(R.layout.item_height, null);
        InfoItemBar mbar_height = new InfoItemBar(TestActivity.this, "株高");
        mbar_height.addView(view_height);
        mbar_height.setShow(true);
        mLinearLayout.addView(mbar_height);
        View view_branch = LayoutInflater.from(TestActivity.this).inflate(R.layout.item_branch, null);
        InfoItemBar mbar_branch = new InfoItemBar(TestActivity.this, "分支数");
        mbar_branch.addView(view_branch);
        mbar_branch.setShow(true);
        mLinearLayout.addView(mbar_branch);
        View view_big = LayoutInflater.from(TestActivity.this).inflate(R.layout.item_branch, null);
        InfoItemBar mbar_big = new InfoItemBar(TestActivity.this, "大薯十株测产");
        mbar_big.addView(view_big);
        mbar_big.setShow(true);
        mLinearLayout.addView(mbar_big);
        View view_small = LayoutInflater.from(TestActivity.this).inflate(R.layout.item_branch, null);
        InfoItemBar mbar_small = new InfoItemBar(TestActivity.this, "小薯十株测产");
        mbar_small.addView(view_small);
        mbar_small.setShow(true);
        mLinearLayout.addView(mbar_small);
        

//        final TextView xdown = (TextView)findViewById(R.id.xdown);
//        xdown.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view=LayoutInflater.from(TestActivity.this).inflate(R.layout.item_height, null);
//                HiddenAnimUtils.newInstance(TestActivity.this, view, xdown, 100);
//            }
//        });
    }
}
