package com.example.hospital.Account;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {

    private static AccountManager sInstance = null;
    private final Map<String, String> accountMap; // key 手机号 value 密码
    private boolean isInited = false;
    private String nowAccount = "";

    private AccountManager() {
        accountMap = new HashMap<>();
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
     * 初始化，获取所有账号信息，存在本地map
     * TODO 数据库
     */
    void init() {
        if (isInited) {
            return;
        }
        accountMap.put("111", "00000000");
        isInited = true;
    }

    public String getNowAccount() {
        return nowAccount;
    }

    public void setNowAccount(String phoneNum) {
        nowAccount = phoneNum;
    }

    public boolean registerAccount(String phoneNum, String password) {
        if (accountMap.get(phoneNum) == null) {
            accountMap.put(phoneNum, password);
            return true;
        }
        return false;
    }

    public boolean checkAccount(String phoneNum, String password) {
        String passwordReal = accountMap.get(phoneNum);
        return passwordReal != null && TextUtils.equals(password, passwordReal);
    }

    public boolean resetPassword(String phoneNum, String password) {
        if (accountMap.get(phoneNum) == null) {
            return false;
        }
        accountMap.put(phoneNum, password);
        return true;
    }

    public boolean hasAccount(String phoneNum) {
        return accountMap.get(phoneNum) != null;
    }
}
