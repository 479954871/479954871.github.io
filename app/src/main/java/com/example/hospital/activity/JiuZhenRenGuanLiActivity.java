package com.example.hospital.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.hospital.account.AccountManager;
import com.example.hospital.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiuZhenRenGuanLiActivity extends AppCompatActivity {
    SimpleAdapter adapter;
    List<Map<String, String>> list = new ArrayList<>();
    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        List<AccountManager.JiuZhenRen> patients = AccountManager.getInstance().getJiuZhenRen();
        for (int i = 0; i < patients.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("patient_name", "姓名："+patients.get(i).name);
            map.put("patient_id", "身份证："+patients.get(i).patientId);
            list.add(map);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiu_zhen_ren_guan_li);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("就诊人列表");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            JiuZhenRenGuanLiActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Button button = findViewById(R.id.add_patient);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(JiuZhenRenGuanLiActivity.this, AddJiuZhenRenActivity.class);
            startActivity(intent);
        });

        ListView listViewPatients = (ListView) findViewById(R.id.listview);
        List<AccountManager.JiuZhenRen> patients = AccountManager.getInstance().getJiuZhenRen();
        for (int i = 0; i < patients.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("patient_name", "姓名："+patients.get(i).name);
            map.put("patient_id", "身份证："+patients.get(i).patientId);
            list.add(map);
        }
        // 定义SimpleAdapter适配器。
        // 使用SimpleAdapter来作为ListView的适配器，比ArrayAdapter能展现更复杂的布局效果。为了显示较为复杂的ListView的item效果，需要写一个xml布局文件，来设置ListView中每一个item的格式。
        adapter = new SimpleAdapter(this, list, R.layout.patient_item,
                new String[] { "patient_name", "patient_id" }, new int[] {R.id.patient_name,
                R.id.patient_id});
        listViewPatients.setAdapter(adapter);
        listViewPatients.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(JiuZhenRenGuanLiActivity.this, ModifyJiuZhenRenActivity.class);
            intent.putExtra("position",position);
            startActivity(intent);
        });
    }
}