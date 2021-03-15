package com.example.hospital.Account;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {

    private static AccountManager sInstance = null;
    private boolean isInited = false;
    private String nowAccount = "";

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

    public void setNowAccount(String phoneNum) {
        nowAccount = phoneNum;
    }

}
