package com.example.hospital.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hospital.R;
public class AddJiuZhenRenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jiu_zhen_ren);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("添加就诊人");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            AddJiuZhenRenActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}