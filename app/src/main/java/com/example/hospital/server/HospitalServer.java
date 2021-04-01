package com.example.hospital.server;

import android.util.Log;

import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.constant.HosptialConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 与服务器通信
 */
public class HospitalServer {
    private static final String TAG = "HospitalServer";
    public interface LoginCallback {
        void loginSuccess();
        void loginFailed();
        void userNotExists();
        void networkUnavailable();
    }
    /**
     * 发送登录请求
     */
    public static void sendLoginRequest(String phone, String password, LoginCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_LOGIN);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("phone="+phone+"&password="+password);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("登录成功") ){
                        callback.loginSuccess();
                        JSONArray jsonArray = jsonObject.optJSONArray("jiuzhenren");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            AccountManager.JiuZhenRen[] patients = new AccountManager.JiuZhenRen[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                // JSON数组里面的具体-JSON对象
                                JSONObject json = jsonArray.getJSONObject(i);
                                patients[i] = new AccountManager.JiuZhenRen();
                                patients[i].patientId = json.optString("patient_id", "");
                                patients[i].name = json.optString("name", "");
                                patients[i].sex = "1".equals(json.optString("sex", "1"));
                                patients[i].age = json.optInt("age", 0);
                                patients[i].phone = json.optString("phone", "");
                                patients[i].address = json.optString("address", "");
                                patients[i].wechatAccount = json.optString("wechat_account", "");
                                patients[i].email = json.optString("email", "");
                                JSONArray jsonArray1 = json.optJSONArray("guahao");
                                JSONArray jsonArray2 = json.optJSONArray("payment");
                                JSONArray jsonArray3 = json.optJSONArray("report");
                                patients[i].guaHaos = new ArrayList<>();
                                patients[i].payments = new ArrayList<>();
                                patients[i].reports = new ArrayList<>();
                                if (jsonArray1 != null && jsonArray1.length() > 0) {
                                    for (int j = 0; j < jsonArray1.length(); ++j) {
                                        JSONObject json1 = jsonArray1.getJSONObject(j);
                                        AccountManager.GuaHao guahao = new AccountManager.GuaHao();
                                        guahao.docId = json1.optString("doc_id");
                                        guahao.docName = json1.optString("doc_name");
                                        guahao.firstDepId = Integer.parseInt(json1.optString("first_dep_id"));
                                        guahao.secondDepId = Integer.parseInt(json1.optString("second_dep_id"));
                                        guahao.firstDep = json1.optString("first_dep");
                                        guahao.secondDep = json1.optString("second_dep");
                                        guahao.createTime = json1.optString("create_time");
                                        guahao.reserveDate = json1.optString("reserve_date");
                                        guahao.reserveTime = Integer.parseInt(json1.optString("reserve_time"));
                                        guahao.fee = json1.optDouble("fee");
                                        patients[i].guaHaos.add(guahao);
                                    }
                                }
                                if (jsonArray2 != null && jsonArray2.length() > 0) {
                                    for (int j = 0; j < jsonArray2.length(); ++j) {
                                        JSONObject json1 = jsonArray2.getJSONObject(j);
                                        AccountManager.Payment payment = new AccountManager.Payment();
                                        payment.docName = json1.optString("doc_name");
                                        payment.data = json1.optString("data");
                                        payment.isPay = "t".equals(json1.optString("second_dep"));
                                        payment.reportId = json1.optInt("report_id");
                                        payment.taskId = json1.optInt("task_id");
                                        payment.amount = json1.optDouble("amount");
                                        String temp = json1.optString("time");
                                        if (temp.indexOf('T') != -1) {
                                            temp = temp.substring(0, temp.indexOf('T')) + " " + temp.substring(temp.indexOf('T')+1);
                                        }
                                        payment.paymentTime = temp;
                                        patients[i].payments.add(payment);
                                    }
                                }
                                if (jsonArray3 != null && jsonArray3.length() > 0) {
                                    for (int j = 0; j < jsonArray3.length(); ++j) {
                                        JSONObject json1 = jsonArray3.getJSONObject(j);
                                        AccountManager.Report report = new AccountManager.Report();
                                        report.docName = json1.optString("doc_name");
                                        report.reportData = json1.optString("data");
                                        report.reportId = json1.optInt("report_id");
                                        report.reportTitle = json1.optString("title");
                                        report.firstDep = json1.optString("first_dep");
                                        report.secondDep = json1.optString("second_dep");
                                        String temp = json1.optString("time");
                                        if (temp.indexOf('T') != -1) {
                                            temp = temp.substring(0, temp.indexOf('T')) + " " + temp.substring(temp.indexOf('T')+1);
                                        }
                                        report.reportTime = temp;
                                        patients[i].reports.add(report);
                                    }
                                }
                            }
                            AccountManager.getInstance().setNowAccount(phone, patients, password);
                        } else {
                            AccountManager.getInstance().setNowAccount(phone, null, password);
                        }


                    }else if(status.equals("登录失败") ){
                        callback.loginFailed();

                    }else if(status.equals("用户不存在") ){
                        callback.userNotExists();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface SignUpCallback {
        void signUpSuccess();
        void signUpFailed();
        void networkUnavailable();
    }

    /**
     * 发送注册请求
     */
    public static void sendSignUpRequest(String phone, String password, SignUpCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_SIGN_UP);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("phone="+phone+"&password="+password);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("注册成功") ){
                        callback.signUpSuccess();
                    }else if(status.equals("用户名重复") ){
                        callback.signUpFailed();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public interface ResetPasswordCallback {
        void resetSuccess();
        void userNotExists();
        void networkUnavailable();
    }

    /**
     * 发送修改密码请求
     */
    public static void sendResetPasswordRequest(String phone, String password, ResetPasswordCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_RESET);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("phone="+phone+"&password="+password);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("修改密码成功") ){
                        callback.resetSuccess();
                    }else if(status.equals("用户不存在") ){
                        callback.userNotExists();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public interface AddOrModifyPatientCallback {
        void addOrModifySuccess();
        void addOrModifyFail();
        void networkUnavailable();
    }

    /**
     * 发送创建或修改就诊人请求
     */
    public static void sendAddOrModifyPatientRequest(String patient_id, String name, String phone,
                                                     String sex, int age, String address,
                                                     String wechat, String email, boolean is_modify,
                                                     AddOrModifyPatientCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_ADD_PATIENT);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("patientid="+patient_id+"&name="+name+"&phone="+phone+
                            "&sex="+sex+"&age="+age+"&address="+address+"&wechat="+
                            wechat+"&email="+email+"&modify="+(is_modify?"1":"0"));
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("创建就诊人成功") || status.equals("就诊人信息修改成功")){
                        callback.addOrModifySuccess();
                    }else if(status.equals("创建就诊人失败，就诊人已创建") || status.equals("修改失败，找不到就诊人")){
                        callback.addOrModifyFail();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface DeletePatientCallback {
        void deleteSuccess();
        void deleteFailed();
        void networkUnavailable();
    }

    /**
     * 发送删除就诊人请求
     */
    public static void sendDeletePatientRequest(String phone, String patientId, DeletePatientCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_DELETE_PATIENT);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("phone="+phone+"&patientid="+patientId);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("删除就诊人成功") ){
                        callback.deleteSuccess();
                    }else if(status.equals("删除失败，就诊人不存在") ){
                        callback.deleteFailed();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public interface GetDoctorCallback {
        void getSuccess(JSONObject jsonObject);
        void networkUnavailable();
    }

    /**
     * 发送医生信息请求
     */
    public static void sendGetDoctorRequest(int firstDep, int secondDep, GetDoctorCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_GET_DOCTOR);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("firstdepid="+firstDep+"&seconddepid="+secondDep);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("医生信息返回成功") ){
                        callback.getSuccess(jsonObject);
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface GuaHaoCallback {
        void GuaHaoSuccess();
        void GuaHaoFailed();
        void networkUnavailable();
    }

    /**
     * 发送挂号请求
     */
    public static void sendGuaHaoRequest(String patientId, int firstDepId, int secondDepId,
                                         String docId, String createTime, double fee,
                                         String reserveDate, int reserveTime,
                                         GuaHaoCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_GUA_HAO);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("patientid="+patientId+"&firstdepid="+firstDepId+
                            "&seconddepid="+secondDepId+"&docid="+docId+ "&createtime="+
                            createTime+"&fee="+fee+"&reservedate="+ reserveDate+"&reservetime="+reserveTime);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("挂号成功") ){
                        callback.GuaHaoSuccess();
                    }else if(status.equals("此就诊人已挂号，挂号失败") ){
                        callback.GuaHaoFailed();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface DeleteGuaHaoCallback {
        void deleteSuccess();
        void deleteFailed();
        void networkUnavailable();
    }

    /**
     * 发送取消挂号请求
     */
    public static void sendDeleteGuaHaoRequest(String patientId, String docId, String createTime,
                                               String reserveDate, String reserveTime,
                                               DeleteGuaHaoCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_DELETE_GUA_HAO);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("docid="+docId+ "&createtime="+ createTime+"&reservedate="+
                            reserveDate+"&reservetime="+reserveTime+"&patientid="+patientId);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("取消挂号成功") ){
                        callback.deleteSuccess();
                    }else if(status.equals("取消挂号失败，挂号信息不存在") ){
                        callback.deleteFailed();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface GetPaymentCallback {
        void getSuccess(JSONObject jsonObject);
        void networkUnavailable();
    }

    /**
     * 发送缴费信息请求
     */
    public static void sendPaymentRequest(String phone, GetPaymentCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_PAYMENT);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("phone="+phone);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("查询缴费信息成功") ){
                        callback.getSuccess(jsonObject);
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface GetReportCallback {
        void getSuccess(JSONObject jsonObject);
        void networkUnavailable();
    }

    /**
     * 发送电子报告请求
     */
    public static void sendReportRequest(String phone, GetReportCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_REPORT);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("phone="+phone);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("查询电子报告成功") ){
                        callback.getSuccess(jsonObject);
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface PayCallback {
        void paySuccess();
        void payFailed();
        void networkUnavailable();
    }

    /**
     * 发送取消挂号请求
     */
    public static void sendPayRequest(String taskId, String time, PayCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(HosptialConstant.WEBSITE + HosptialConstant.WEBSITE_PAY);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("taskid="+taskId+"&time="+time);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d(TAG, response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("支付成功") ){
                        callback.paySuccess();
                    }else if(status.equals("订单信息不存在") || status.equals("订单已付款")){
                        callback.payFailed();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try { reader.close(); }
                        catch (IOException e) { Log.e(TAG, "IOException 2:" +e.getMessage()); }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
