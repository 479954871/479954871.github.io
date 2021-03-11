package com.example.hospital.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospital.Account.AccountManager;
import com.example.hospital.Constant.HosptialConstant;
import com.example.hospital.R;
import com.example.hospital.Utils.SecurityCodeUtils;

/**
 * 登录
 */
public class SignInActivity extends AppCompatActivity {
    private EditText editTextNumber, editTextPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);
        getSupportActionBar().hide();
        editTextNumber = findViewById(R.id.phone_number);
        editTextPassword = findViewById(R.id.password);
        TextView textViewRegister = findViewById(R.id.register_account);
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        TextView textViewResetPassword = findViewById(R.id.reset_password);
        textViewResetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
        Button buttonLogin = findViewById(R.id.login);
        buttonLogin.setOnClickListener(v -> {
            if ("".equals(editTextNumber.getText().toString())) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("".equals(editTextPassword.getText().toString())) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (editTextPassword.getText().toString().length() < HosptialConstant.MIN_PASSWORD_LENGTH) {
                Toast.makeText(this, "密码长度至少8位", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean result = AccountManager.getInstance().checkAccount(editTextNumber.getText().toString(), editTextPassword.getText().toString());
            if (result) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "登录失败，手机号或密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
