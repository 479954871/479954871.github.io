package com.example.hospital.account;

import com.example.hospital.server.HospitalServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountManager {

    private static AccountManager sInstance = null;
    private boolean isInited = false;
    private String nowAccount = "";
    private String password = "";

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

    public String getPassword() {
        return password;
    }

    public void setNowAccount(String phoneNum, JiuZhenRen[] jiuZhenRens, String password1) {
        nowAccount = phoneNum;
        password = password1;
        patients.clear();
        if(jiuZhenRens != null) {
            patients.addAll(Arrays.asList(jiuZhenRens));
        }
    }

    public void logout() {
        nowAccount = "";
        patients.clear();
    }

    public List<JiuZhenRen> getJiuZhenRen() {
        return patients;
    }

    public void addJiuZhenRen(JiuZhenRen jiuZhenRen) {
        patients.add(jiuZhenRen);
    }

    public void modifyJiuZhenRen(int position, boolean sex, int age, String address, String wechat,
                                 String email) {
        JiuZhenRen patient = patients.get(position);
        if (patient == null) return;
        patient.sex = sex;
        patient.age = age;
        patient.address = address;
        patient.wechatAccount = wechat;
        patient.email = email;
    }

    public void deleteJiuZhenRen(String id) {
        for (JiuZhenRen jiuZhenRen : patients) {
            if (jiuZhenRen.patientId.equals(id)) {
                patients.remove(jiuZhenRen);
                return;
            }
        }
    }

    public void reload() {
        HospitalServer.sendLoginRequest(nowAccount, password, new HospitalServer.LoginCallback() {
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
    }
}
