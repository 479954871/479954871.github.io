package com.example.hospital.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospital.Account.AccountManager;
import com.example.hospital.Constant.HosptialConstant;
import com.example.hospital.R;
import com.example.hospital.Utils.SecurityCodeUtils;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 重置密码
 */
public class ResetPasswordActivity extends AppCompatActivity {
    private EditText editTextNumber, editTextSecurityCode, editTextPassword;
    private long lastTime = 0;
    private EventHandler eh = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            handler.sendMessage(msg);
        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;//3
            int result = msg.arg2;//0
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //验证码验证成功
                    boolean r = AccountManager.getInstance().resetPassword(editTextNumber.getText().toString(), editTextPassword.getText().toString());
                    if (r) {
                        Toast.makeText(ResetPasswordActivity.this, "密码重设成功", Toast.LENGTH_SHORT).show();
                        ResetPasswordActivity.this.finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "重设失败，账号不存在", Toast.LENGTH_SHORT).show();
                    }
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //发送验证码成功
                    Toast.makeText(ResetPasswordActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                }else if (event ==SMSSDK.RESULT_ERROR){
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            Toast.makeText(ResetPasswordActivity.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }
            }else{
                //验证码错误
                Toast.makeText(ResetPasswordActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                ((Throwable)data).printStackTrace();
            }
        }
    };
    @Override
    protected void onStop() {
        super.onStop();

        SecurityCodeUtils.unregisterEventHandler(eh);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SecurityCodeUtils.registerEventHandler(eh);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_layout);
        getSupportActionBar().hide();
        editTextNumber = findViewById(R.id.phone_number);
        editTextSecurityCode = findViewById(R.id.security_code);
        editTextPassword = findViewById(R.id.password);
        ImageView imageView = findViewById(R.id.back);
        imageView.setOnClickListener(v -> finish());
        Button buttonGetSecurityCode = findViewById(R.id.get_security_code);
        buttonGetSecurityCode.setOnClickListener(v -> {
            String phoneNum = editTextNumber.getText().toString();
            if ("".equals(phoneNum)) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!AccountManager.getInstance().hasAccount(phoneNum)) {
                Toast.makeText(this, "该手机号尚未注册", Toast.LENGTH_SHORT).show();
                return;
            }
            long nowTime = System.currentTimeMillis();
            if (lastTime + HosptialConstant.SECURITY_CODE_INTERVAL > nowTime && SecurityCodeUtils.securityCode.equals("")) {
                Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show();
                return;
            }
            lastTime = nowTime;
            SecurityCodeUtils.sendSecurityCode(HosptialConstant.COUNTRY, editTextNumber.getText().toString());
        });

        Button buttonRegister = findViewById(R.id.reset);
        buttonRegister.setOnClickListener(v -> {
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
            SecurityCodeUtils.checkSecurityCode(HosptialConstant.COUNTRY, editTextNumber.getText().toString(), editTextSecurityCode.getText().toString());
        });
    }
}
