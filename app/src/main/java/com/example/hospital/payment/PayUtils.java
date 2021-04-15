package com.example.hospital.payment;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.hospital.constant.HosptialConstant;
import com.mob.wrappers.PaySDKWrapper;

import java.util.Map;

public class PayUtils {

    public interface PaymentCallBack {
        void paySuccess(PayResult payResult);
        void payFail(PayResult payResult);
    }
    /*
     * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
     * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
     * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
     *
     * orderInfo 的获取必须来自服务端；
     */
    public static void pay(Activity activity, double fee, PaymentCallBack callBack) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX); // 沙箱环境
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(HosptialConstant.ALIPAY_APPID, true, fee);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, HosptialConstant.RSA2_PRIVATE, true);
        final String orderInfo = orderParam + "&" + sign;
        final Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                @SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) result);
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    callBack.paySuccess(payResult);
                } else {
                    callBack.payFail(payResult);
                }
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
