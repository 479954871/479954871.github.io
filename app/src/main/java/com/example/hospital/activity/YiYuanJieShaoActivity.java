package com.example.hospital.activity;


import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospital.R;

/**
 * 医院介绍
 */
public class YiYuanJieShaoActivity extends AppCompatActivity {
    private static final String TAG = "YiYuanJieShaoActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yi_yuan_jie_shao_layout);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("医院介绍");
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("https://www.fsyyy.com/index.aspx");//https://www.fsyyy.com/index.aspx
        // 系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError: "); //如果是证书问题，会打印出此条log到console
            }
        });
        //webView.getSettings().setDomStorageEnabled(true);
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            YiYuanJieShaoActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
