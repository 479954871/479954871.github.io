package com.example.hospital.Utils;

import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

public class SecurityCodeUtils {
    public static String securityCode = "";
    public static boolean isSending = false;


    public static void registerEventHandler(EventHandler eh) {
        MobSDK.submitPolicyGrantResult(true, null);
        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);
    }

    public static void unregisterEventHandler(EventHandler eh) {
        SMSSDK.unregisterEventHandler(eh);
    }

    public static void sendSecurityCode(String country, String phoneNumber) {
        if (isSending) {
            return;
        }

        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        SMSSDK.getVerificationCode(country, phoneNumber, new OnSendMessageHandler() {
            @Override
            public boolean onSendMessage(String s, String s1) {
                /**
                 * 此方法在发送验证短信前被调用，传入参数为接收者号码
                 * 返回true表示此号码无须实际接收短信
                 */
                return false;
            }
        });
    }

    public static void checkSecurityCode(String country, String phoneNumber, String code) {
        // 提交验证码，其中的code表示验证码，如“1357”
        SMSSDK.submitVerificationCode(country, phoneNumber, code);
    }
}
