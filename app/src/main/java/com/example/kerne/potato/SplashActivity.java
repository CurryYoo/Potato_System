package com.example.kerne.potato;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sp=getSharedPreferences("update_flag", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=sp.edit();
//        editor.putBoolean("update_pick_data",false);
//        editor.putBoolean("update_location_data",false);
//        editor.apply();
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
