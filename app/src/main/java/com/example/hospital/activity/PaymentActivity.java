package com.example.hospital.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hospital.R;
import com.example.hospital.payment.PayResult;
import com.example.hospital.payment.PayUtils;

public class PaymentActivity extends AppCompatActivity {
    double fee;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("支付");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fee = getIntent().getDoubleExtra("fee", 0);
        TextView textView1 = findViewById(R.id.money);
        textView1.setText("￥ " + fee);
        ConstraintLayout constraintLayout = findViewById(R.id.cons);
        constraintLayout.setOnClickListener(v -> {});
        Button button = findViewById(R.id.pay);
        button.setOnClickListener(v -> {
            PayUtils.pay(PaymentActivity.this, 20, new PayUtils.PaymentCallBack() {
                @Override
                public void paySuccess(PayResult payResult) {
                    final Intent intent = getIntent();//在B类中得到原来的activity
                    intent.putExtra("what", 0);//在B类中设置返回值传到A类中
                    intent.putExtra("obj", payResult.toString());
                    PaymentActivity.this.setResult(1, intent);// 跳转回原来的activity
                    PaymentActivity.this.finish();// 一定要结束当前activity
                }

                @Override
                public void payFail(PayResult payResult) {
                    final Intent intent = getIntent();//在B类中得到原来的activity
                    intent.putExtra("what", 3);//在B类中设置返回值传到A类中
                    intent.putExtra("obj", payResult.toString());
                    PaymentActivity.this.setResult(1, intent);// 跳转回原来的activity
                    PaymentActivity.this.finish();// 一定要结束当前activity
                }
            });
        });
    }
}