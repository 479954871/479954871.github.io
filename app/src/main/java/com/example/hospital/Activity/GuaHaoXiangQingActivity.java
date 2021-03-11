package com.example.hospital.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospital.R;

/**
 * 挂号详情
 */
public class GuaHaoXiangQingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.men_zhen_gua_hao_layout);
        TextView textView = findViewById(R.id.main_head_title);
        // TODO 挂号详情页面，里面有后五天可挂号的医生、时间点等。
        textView.setText("xxx");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            GuaHaoXiangQingActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
