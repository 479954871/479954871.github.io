package com.example.hospital;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;

import com.example.hospital.Utils.DownloadBMPFromUrlUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.server.HospitalServer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStop() {
        super.onStop();
        //获取一个文件名为login、权限为private的xml文件的SharedPreferences对象，保存登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        //得到SharedPreferences.Editor对象，并保存数据到该对象中
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", AccountManager.getInstance().getNowAccount());
        editor.putString("password", AccountManager.getInstance().getPassword());
        //保存key-value对到文件中
        editor.apply();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadBMPFromUrlUtils.handleSSLHandshake();
        SharedPreferences sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        HospitalServer.sendLoginRequest(sharedPreferences.getString("account",""), sharedPreferences.getString("password",""), new HospitalServer.LoginCallback() {
            @Override
            public void loginSuccess() {
            }
            @Override
            public void loginFailed() {
            }
            @Override
            public void userNotExists() {
            }
            @Override
            public void networkUnavailable() {

            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_message, R.id.navigation_mine)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

}