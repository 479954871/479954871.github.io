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

public class ReportGuanLiActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    List<AccountManager.JiuZhenRen> jiuZhenRens;
    int position = 0;
    ListView listView;
    SimpleAdapter mListViewAdapter;
    List<Map<String, String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_guan_li);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("我的电子报告");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        changeList(0);
        listView = findViewById(R.id.listview_guahao2);
        mListViewAdapter = new SimpleAdapter(this, list, R.layout.report_item,
                new String[] {"title", "doc_name", "time", "data"},
                new int[] {R.id.title, R.id.doc_name, R.id.report_time, R.id.report_data});
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
        if (jiuZhenRens.get(position).reports != null && jiuZhenRens.get(position).reports.size() > 0) {
            for (AccountManager.Report report : jiuZhenRens.get(position).reports) {
                Map<String, String> map = new HashMap<>();
                map.put("title", report.reportTitle);
                map.put("doc_name", report.docName);
                map.put("time", report.reportTime);
                map.put("data", report.reportData);
                list.add(map);
            }
        }
    }
}