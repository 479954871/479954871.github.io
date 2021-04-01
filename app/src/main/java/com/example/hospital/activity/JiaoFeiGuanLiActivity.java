package com.example.hospital.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.hospital.R;
import com.example.hospital.account.AccountManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiaoFeiGuanLiActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    List<AccountManager.JiuZhenRen> jiuZhenRens;
    int position = 0;
    ListView listView;
    SimpleAdapter mListViewAdapter;
    List<Map<String, String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiao_fei_guan_li);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("我的缴费记录");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        changeList(0);
        listView = findViewById(R.id.listview_guahao2);
        mListViewAdapter = new SimpleAdapter(this, list, R.layout.jiaofei_item,
                new String[] {"text", "fee", "create_time"},
                new int[] {R.id.text11, R.id.text13, R.id.text12});
        listView.setAdapter(mListViewAdapter);


        radioGroup = findViewById(R.id.radioGroupJiuZhenRen);
        jiuZhenRens =  AccountManager.getInstance().getJiuZhenRen();
        for (int i = 0; i < jiuZhenRens.size(); ++i) {
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

            radioGroup.addView(radioButton,layoutParams);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                position = checkedId;
                changeList(position);
                mListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    void changeList(int position) {
        jiuZhenRens = AccountManager.getInstance().getJiuZhenRen();
        list.clear();
        if (jiuZhenRens.get(position).guaHaos != null && jiuZhenRens.get(position).guaHaos.size() > 0) {
            for (AccountManager.GuaHao guaHao : jiuZhenRens.get(position).guaHaos) {
                Map<String, String> map = new HashMap<>();
                map.put("text", "挂号");
                map.put("fee", "￥" + guaHao.fee);
                String temp = guaHao.createTime;
                if (temp.indexOf('T') != -1) {
                    temp = temp.substring(0, temp.indexOf('T')) + " " + temp.substring(temp.indexOf('T')+1);
                }
                map.put("create_time", temp);
                list.add(map);
            }
        }
        if (jiuZhenRens.get(position).payments != null && jiuZhenRens.get(position).payments.size() > 0) {
            for (AccountManager.Payment payment : jiuZhenRens.get(position).payments) {
                if (!payment.isPay) continue;
                Map<String, String> map = new HashMap<>();
                map.put("text", TextUtils.isEmpty(payment.data)?"诊断":payment.data);
                map.put("fee", "￥" + payment.amount);
                map.put("create_time", payment.paymentTime);
                list.add(map);
            }
        }
    }
}