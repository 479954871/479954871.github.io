package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospital.R;
import com.example.hospital.account.AccountManager;
import com.example.hospital.server.HospitalServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuaHaoGuanLiActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    List<AccountManager.JiuZhenRen> jiuZhenRens;
    int position = 0;
    ListView listView;
    SimpleAdapter mListViewAdapter;
    List<Map<String, String>> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_hao_guan_li);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("我的预约挂号");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        changeList(0);
        listView = findViewById(R.id.listview_guahao2);
        mListViewAdapter = new SimpleAdapter(this, list, R.layout.hao_item,
                new String[] {"keshi", "doc_name", "fee", "reserve_date", "reserve_time"},
                new int[] {R.id.keshi_name, R.id.doc_name, R.id.guahao_fee, R.id.yuyue_date,
                        R.id.yuyue_time});
        listView.setAdapter(mListViewAdapter);
        listView.setOnItemClickListener((parent, view, p, id) -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(GuaHaoGuanLiActivity.this);
            dialog.setIcon(R.mipmap.hospital_icon);
            dialog.setTitle("温馨提示");
            dialog.setMessage("您确定要取消挂号吗？");
            dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HospitalServer.sendDeleteGuaHaoRequest(jiuZhenRens.get(position).patientId,
                            list.get(p).get("doc_id"), list.get(p).get("create_time"),
                            list.get(p).get("reserve_date"), list.get(p).get("reserve_time_index"),
                            new HospitalServer.DeleteGuaHaoCallback() {
                                @Override
                                public void deleteSuccess() {
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.arg1 = position;
                                    msg.arg2 = p;
                                    handler.sendMessage(msg);
                                }

                                @Override
                                public void deleteFailed() {
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
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        });
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
                map.put("doc_id", guaHao.docId);
                map.put("doc_name", guaHao.docName);
                map.put("keshi", guaHao.secondDep);
                map.put("fee", String.valueOf(guaHao.fee));
                map.put("create_time", guaHao.createTime);
                String temp = guaHao.reserveDate.indexOf('T') == -1 ? guaHao.reserveDate : guaHao.reserveDate.substring(0, guaHao.reserveDate.indexOf('T'));
                map.put("reserve_date", temp);
                map.put("reserve_time", DoctorActivity.listAll[guaHao.reserveTime]);
                map.put("reserve_time_index", String.valueOf(guaHao.reserveTime));
                list.add(map);
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(GuaHaoGuanLiActivity.this, "取消挂号成功", Toast.LENGTH_SHORT).show();
                    AccountManager.getInstance().deleteGuaHao(msg.arg1, msg.arg2);
                    break;
                case 1:
                    Toast.makeText(GuaHaoGuanLiActivity.this, "取消挂号失败，挂号信息不存在", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(GuaHaoGuanLiActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}