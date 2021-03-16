package com.example.hospital.Correspondence;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.hospital.Account.AccountManager;
import com.example.hospital.Activity.ResetPasswordActivity;
import com.example.hospital.Constant.HosptialConstant;

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
import java.net.ProtocolException;
import java.net.URL;

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
                                patients[i].sex = json.optBoolean("sex", true);
                                patients[i].age = json.optInt("age", 0);
                                patients[i].phone = json.optString("phone", "");
                                patients[i].address = json.optString("address", "");
                                patients[i].wechatAccount = json.optString("wechat_account", "");
                                patients[i].email = json.optString("email", "");
                            }
                            AccountManager.getInstance().setNowAccount(phone, patients);
                        } else {
                            AccountManager.getInstance().setNowAccount(phone, null);
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
                    if (response.toString().equals("注册成功") ){
                        callback.signUpSuccess();
                    }else if(response.toString().equals("用户名重复") ){
                        callback.signUpFailed();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
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
                    if (response.toString().equals("修改密码成功") ){
                        callback.resetSuccess();
                    }else if(response.toString().equals("用户不存在") ){
                        callback.userNotExists();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "url form wrong");
                    callback.networkUnavailable();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 1:" + e.getMessage());
                    callback.networkUnavailable();
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
