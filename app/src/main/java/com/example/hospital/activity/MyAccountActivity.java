package com.example.hospital.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hospital.R;
import com.example.hospital.account.AccountManager;
import com.example.hospital.server.HospitalServer;

public class MyAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("我的账号");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> MyAccountActivity.this.finish());
        LinearLayout linearLayout = findViewById(R.id.linear);
        linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MyAccountActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
        Button button = findViewById(R.id.btn_logout);
        button.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setIcon(R.mipmap.hospital_icon);
            dialog.setTitle("温馨提示");
            dialog.setMessage("您确定要退出登录吗？");
            dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AccountManager.getInstance().logout();
                    finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        });
    }
}