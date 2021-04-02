package com.example.hospital.account;

import androidx.annotation.Nullable;

import com.example.hospital.server.HospitalServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public List<GuaHao> guaHaos;
        public List<Payment> payments;
        public List<Report> reports;
    };

    public static class GuaHao {
        public String docId;
        public String docName;
        public int firstDepId;
        public String firstDep;
        public int secondDepId;
        public String secondDep;
        public String createTime;
        public String reserveDate;
        public int reserveTime;
        public double fee;
    }
    public static class Payment {
        public String docName;
        public boolean isPay;
        public int reportId;
        public double amount;
        public String data;
        public int taskId;
        public String paymentTime;
        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return taskId == ((Payment) o).taskId;
        }
        @Override
        public int hashCode() {
            return taskId;
        }
    }
    public static class Report {
        public String docName;
        public String reportTime;
        public String reportTitle;
        public String reportData;
        public int reportId;
        public String firstDep;
        public String secondDep;
    }

    public static class Msg {
        public int firstDepId;
        public int secondDepId;
        public String docId;
        public String docName;
        public String data;
        public String time;
        public int index;
        public String whoSend;

        public String getKey() {
            return (firstDepId *100+ secondDepId) + docName;
        }
    }

    private final List<JiuZhenRen> patients = new ArrayList<>();

    public final Map<String, List<Msg>> messages = new HashMap<>();

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

    public void addGuaHao(int position, String docId, String docName, int firstDepId,
                          String firstDep, int secondDepId, String secondDep, String createTime,
                          String reserveDate, int reserveTime, double fee) {
        if (position >= patients.size()) return;
        if (patients.get(position).guaHaos == null) {
            patients.get(position).guaHaos = new ArrayList<>();
        }
        GuaHao guaHao = new GuaHao();
        guaHao.docId = docId;
        guaHao.docName = docName;
        guaHao.firstDepId = firstDepId;
        guaHao.firstDep = firstDep;
        guaHao.secondDep = secondDep;
        guaHao.secondDepId = secondDepId;
        guaHao.createTime = createTime;
        guaHao.reserveTime = reserveTime;
        guaHao.fee = fee;
        guaHao.reserveDate = reserveDate;
        patients.get(position).guaHaos.add(guaHao);
    }

    public void deleteGuaHao(int p1, int p2) {
        if (p1 >= patients.size() || patients.get(p1).guaHaos == null || p2 >= patients.get(p1).guaHaos.size())
            return;
        patients.get(p1).guaHaos.remove(p2);
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
