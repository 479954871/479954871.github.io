package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospital.R;
import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.payment.PayResult;
import com.example.hospital.payment.PayUtils;
import com.example.hospital.server.HospitalServer;

import java.util.Calendar;
import java.util.List;

public class ConfirmGuaHaoActivity extends AppCompatActivity {
    String docId, docName, docSpecialist, time;
    int timeIndex;
    Bitmap docPic;
    String keshiSecond, keshiFirst;
    int jiuZhenRenNum = 0;
    List<AccountManager.JiuZhenRen> jiuZhenRens;
    int position = 0;
    @Override
    protected void onResume() {
        super.onResume();
        jiuZhenRens =  AccountManager.getInstance().getJiuZhenRen();
        for (int i = jiuZhenRenNum; i < jiuZhenRens.size(); ++i) {
            //radioButton
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(jiuZhenRens.get(i).name+ " (身份证: " + jiuZhenRens.get(i).patientId + ")");
            radioButton.setTextColor(getResources().getColor(R.color.black));
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            //必须有ID，否则默认选中的选项会一直是选中状态
            radioButton.setId(i);
            if (i==0){
                //默认选中
                radioButton.setChecked(true);
            }
            //layoutParams 设置margin值
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            if (i!=0){
//                int i1 = (int) (getResources().getDimension(R.dimen.qb_px_40) + 0.5f);
//                layoutParams.setMargins(i1,0,0,0);
//            }else {
//                layoutParams.setMargins(0,0,0,0);
//            }
            //注意这里addView()里传入layoutParams
            radioGroup.addView(radioButton,layoutParams);
        }
        jiuZhenRenNum = jiuZhenRens.size();
    }

    int keshiFirstId, keshiSecondId;
    int thatDay;
    TextView textView1, textView2, textView3, textView4;
    RadioGroup radioGroup;
    ImageView add;
    String createTime, reserveDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_gua_hao);

        docId = getIntent().getStringExtra("doc_id");
        docName = getIntent().getStringExtra("doc_name");
        docSpecialist = getIntent().getStringExtra("doc_specialist");
        docPic = GuaHaoXiangQingActivity.getBitmap();
        thatDay = getIntent().getIntExtra("that_day", 0);
        keshiFirst = getIntent().getStringExtra("keshi_first");
        keshiSecond = getIntent().getStringExtra("keshi_second");
        keshiFirstId = getIntent().getIntExtra("keshi_first_id", 0);
        keshiSecondId = getIntent().getIntExtra("keshi_second_id", 0);
        time = getIntent().getStringExtra("yuyueshijianduan");
        timeIndex = getIntent().getIntExtra("yuyueshijianduanindex", 0);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("确认挂号");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        textView1 = findViewById(R.id.keshi_name);
        textView2 = findViewById(R.id.doc_name);
        textView3 = findViewById(R.id.yuyue_date);
        textView4 = findViewById(R.id.yuyue_time);
        textView1.setText(keshiSecond);
        textView2.setText(docName);
        int p = (thatDay - HosptialUtils.getDayofWeek("") + 7) % 7;
        textView3.setText(GuaHaoXiangQingActivity.years[p] + "年" + GuaHaoXiangQingActivity.months[p] + "月" + GuaHaoXiangQingActivity.dates[p] + "日");
        textView4.setText(time);
        add = findViewById(R.id.add_jiu);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmGuaHaoActivity.this, AddJiuZhenRenActivity.class);
            startActivity(intent);
        });

        radioGroup = findViewById(R.id.radioGroupJiuZhenRen);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                position = checkedId;
            }
        });
        Button button = findViewById(R.id.ok);
        button.setOnClickListener(v -> {
            if (jiuZhenRenNum == 0) {
                Toast.makeText(this, "请先添加就诊人", Toast.LENGTH_SHORT).show();
            }
            //TODO 根据预约信息和就诊人信息和服务器通信
            reserveDate = GuaHaoXiangQingActivity.years[p] + "-" + GuaHaoXiangQingActivity.months[p] + "-" + GuaHaoXiangQingActivity.dates[p];
            createTime = HosptialUtils.getTime();
            Intent intent = new Intent(ConfirmGuaHaoActivity.this, PaymentActivity.class);
            intent.putExtra("fee", 20.0);
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
                HospitalServer.sendGuaHaoRequest(jiuZhenRens.get(position).patientId, keshiFirstId,
                        keshiSecondId, docId, createTime, 20, reserveDate, timeIndex,
                        new HospitalServer.GuaHaoCallback() {
                            @Override
                            public void GuaHaoSuccess() {
                                Message msg = new Message();
                                msg.what = 0;
                                handler.sendMessage(msg);
                            }
                            @Override
                            public void GuaHaoFailed() {
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
                    Toast.makeText(ConfirmGuaHaoActivity.this, "挂号成功", Toast.LENGTH_SHORT).show();
                    Log.w("PAY", "handleMessage: " + msg.obj);
                    AccountManager.getInstance().addGuaHao(position, docId, docName, keshiFirstId,
                    keshiFirst, keshiSecondId, keshiSecond, createTime, reserveDate, timeIndex, 20);
                    finish();
                    break;
                case 1:
                    Toast.makeText(ConfirmGuaHaoActivity.this, "挂号失败，就诊人已挂号", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ConfirmGuaHaoActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(ConfirmGuaHaoActivity.this, "支付失败 " + msg.obj, Toast.LENGTH_SHORT).show();
                    Log.w("PAY", "handleMessage: " + msg.obj);
                    break;
            }
        }
    };
}