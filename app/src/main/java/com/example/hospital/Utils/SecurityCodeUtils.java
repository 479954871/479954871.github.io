package com.example.hospital.Utils;

import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

public class SecurityCodeUtils {
    public static String securityCode = "";


    public static void registerEventHandler(EventHandler eh) {
        MobSDK.submitPolicyGrantResult(true, null);
        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);
    }

    public static void unregisterEventHandler(EventHandler eh) {
        SMSSDK.unregisterEventHandler(eh);
    }

    public static void sendSecurityCode(String country, String phoneNumber) {
        SMSSDK.getVerificationCode(country, phoneNumber, new OnSendMessageHandler() {
            @Override
            public boolean onSendMessage(String s, String s1) {
                return false;
            }
        });
    }

    public static void checkSecurityCode(String country, String phoneNumber, String code) {
        SMSSDK.submitVerificationCode(country, phoneNumber, code);
    }
}
