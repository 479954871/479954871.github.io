package com.example.hospital.Account;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManager {

    private static AccountManager sInstance = null;
    private boolean isInited = false;
    private String nowAccount = "";

    public static class JiuZhenRen {
        public String patientId;
        public String name;
        public boolean sex; // 1 man, 0 woman
        public int age;
        public String phone;
        public String address;
        public String wechatAccount;
        public String email;
    };
    private final List<JiuZhenRen> patients = new ArrayList<>();

    private AccountManager() {
        init();
    }

    public static AccountManager getInstance() {
        if (sInstance == null) {
            synchronized (AccountManager.class) {
                if (sInstance == null) {
                    sInstance = new AccountManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     */
    void init() {
        if (isInited) {
            return;
        }
        isInited = true;
    }

    public String getNowAccount() {
        return nowAccount;
    }

    public void setNowAccount(String phoneNum, JiuZhenRen[] jiuZhenRens) {
        nowAccount = phoneNum;
        if (jiuZhenRens != null) {
            patients.addAll(Arrays.asList(jiuZhenRens));
        }
    }

    public List<JiuZhenRen> getJiuZhenRen() {
        return patients;
    }

}
