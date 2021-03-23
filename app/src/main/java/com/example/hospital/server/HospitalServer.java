package com.example.hospital.server;

import android.util.Log;

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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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
                                String temp = json.optString("sex", "1");
                                patients[i].sex = temp.equals("1");
                                patients[i].age = json.optInt("age", 0);
                                patients[i].phone = json.optString("phone", "");
                                patients[i].address = json.optString("address", "");
                                patients[i].wechatAccount = json.optString("wechat_account", "");
                                patients[i].email = json.optString("email", "");
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
     * 发送注册请求
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

}
