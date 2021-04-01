package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospital.R;
import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.server.HospitalServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZaiXianJiaoFeiActivity extends AppCompatActivity {
    ListView listView;
    SimpleAdapter mListViewAdapter;
    List<Map<String, String>> list = new ArrayList<>();
    int position = 0;
    String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zai_xian_jiao_fei);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("在线缴费");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        List<AccountManager.JiuZhenRen> jiuZhenRens =  AccountManager.getInstance().getJiuZhenRen();
        for (AccountManager.JiuZhenRen jiuZhenRen : jiuZhenRens) {
            if (jiuZhenRen.payments != null && jiuZhenRen.payments.size() > 0) {
                for (AccountManager.Payment payment : jiuZhenRen.payments) {
                    if (payment.isPay) continue;
                    AccountManager.Report report = null;
                    for (AccountManager.Report report1 : jiuZhenRen.reports) {
                        if (report1.reportId == payment.reportId) report = report1;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("jiuzhenren", "就诊人：" + jiuZhenRen.name);
                    map.put("patient_id", jiuZhenRen.patientId);
                    map.put("fee", "金额：" + payment.amount);
                    map.put("fee_double", ""+payment.amount);
                    map.put("doc_name", "医生：" + payment.docName);
                    map.put("task_id", ""+payment.taskId);
                    map.put("data", payment.data);
                    if (report != null) {
                        map.put("date_n_time", report.reportTime);
                        map.put("keshi", "科室：" + report.secondDep);
                    }
                    list.add(map);
                }
            }
        }

        listView = findViewById(R.id.listview_jiaofei);
        mListViewAdapter = new SimpleAdapter(this, list, R.layout.zaixianjiaofei_item,
                new String[] {"jiuzhenren", "doc_name", "fee", "data", "date_n_time", "keshi"},
                new int[] {R.id.jiuzhenren, R.id.doc_name, R.id.fee, R.id.data, R.id.date_n_time,
                        R.id.keshi});
        listView.setAdapter(mListViewAdapter);
        listView.setOnItemClickListener((parent, view, p, id) -> {
            position = p;
            Intent intent = new Intent(ZaiXianJiaoFeiActivity.this, PaymentActivity.class);
            intent.putExtra("fee", Double.valueOf(list.get(p).get("fee_double")));
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            int what = data.getIntExtra("what", 3);
            String result = data.getStringExtra("obj");
            Message msg = new Message();
            if (what == 0) {
                time = HosptialUtils.getTime();
                HospitalServer.sendPayRequest(list.get(position).get("task_id"),
                        time, new HospitalServer.PayCallback() {
                    @Override
                    public void paySuccess() {
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void payFailed() {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void networkUnavailable() {
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                });
            } else {
                msg.what = what;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(ZaiXianJiaoFeiActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    List<AccountManager.JiuZhenRen> jiuZhenRens =  AccountManager.getInstance().getJiuZhenRen();
                    for (AccountManager.JiuZhenRen jiuZhenRen : jiuZhenRens) {
                        if (!jiuZhenRen.patientId.equals(list.get(position).get("patient_id"))) continue;
                        if (jiuZhenRen.payments != null && jiuZhenRen.payments.size() > 0) {
                            for (AccountManager.Payment payment : jiuZhenRen.payments) {
                                if (payment.taskId == Integer.valueOf(list.get(position).get("task_id"))) {
                                    payment.isPay = true;
                                    payment.paymentTime = time;
                                }
                            }
                        }
                    }
                    list.remove(position);
                    mListViewAdapter.notifyDataSetChanged();
                    Log.w("PAY", "handleMessage: " + msg.obj);
                    break;
                case 1:
                    Toast.makeText(ZaiXianJiaoFeiActivity.this, "其他错误", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ZaiXianJiaoFeiActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(ZaiXianJiaoFeiActivity.this, "支付失败 " + msg.obj, Toast.LENGTH_SHORT).show();
                    Log.w("PAY", "handleMessage: " + msg.obj);
                    break;
            }
        }
    };
}