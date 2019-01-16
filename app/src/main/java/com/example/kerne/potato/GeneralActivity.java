package com.example.kerne.potato;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GeneralActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        Intent intent = getIntent();
        TextView textView = (TextView)findViewById(R.id.test_general);
        textView.setText(intent.getStringExtra("option"));
    }
}
