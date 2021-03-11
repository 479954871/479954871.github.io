package com.example.hospital.Activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospital.Adapter.GuaHaoFirstRecyclerAdapter;
import com.example.hospital.Adapter.GuaHaoSecondRecyclerAdapter;
import com.example.hospital.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 门诊挂号
 */
public class MenZhenGuaHaoActivity extends AppCompatActivity {
    private List<String> keshiFirst;
    private List<List<String>> keshiSecond;
    private RecyclerView recyclerViewFirst, recyclerViewSecond;
    private GuaHaoFirstRecyclerAdapter adapterFirst;
    private GuaHaoSecondRecyclerAdapter adapterSecond;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.men_zhen_gua_hao_layout);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("科室列表");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            MenZhenGuaHaoActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getKeshiList();
        recyclerViewFirst = findViewById(R.id.keshi_first);
        recyclerViewFirst.setLayoutManager(new LinearLayoutManager(this));
        adapterFirst = new GuaHaoFirstRecyclerAdapter(keshiFirst);
        adapterFirst.setOnItemClickListener(new GuaHaoFirstRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapterSecond.changeData(keshiSecond.get(position));
                recyclerViewSecond.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerViewFirst.setAdapter(adapterFirst);
        recyclerViewSecond = findViewById(R.id.keshi_second);
        recyclerViewSecond.setLayoutManager(new LinearLayoutManager(this));
        adapterSecond = new GuaHaoSecondRecyclerAdapter(keshiSecond.get(0));
        adapterSecond.setOnItemClickListener(new GuaHaoSecondRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MenZhenGuaHaoActivity.this, GuaHaoXiangQingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerViewSecond.setAdapter(adapterSecond);
        recyclerViewSecond.addItemDecoration(new DividerItemDecoration(
                MenZhenGuaHaoActivity.this, DividerItemDecoration.VERTICAL));
    }

    /**
     * 获取科室名，本应是通过网络从服务器获取，现简化为从本地读取。
     */
    private void getKeshiList() {
        keshiFirst = new ArrayList<>(Arrays.asList("内科", "外科", "骨科", "妇产科", "生殖医学中心",
                "儿科", "眼科", "耳鼻咽喉科", "口腔科", "感染科", "肿瘤科", "康复医学科", "医学影像科",
                "中医科", "其他科室"));
        keshiSecond = new ArrayList<>();
        keshiSecond.add(new ArrayList<>(Arrays.asList("大内科", "心血管内科", "神经内科",
                "神经内科及癫痫专科", "呼吸与危重症医学科", "消化内科", "肾内科", "内分泌科",
                "风湿免疫科", "血液淋巴瘤科","内科门诊", "全科医学科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("胆道外科", "胃肠外科", "肛肠外科",
                "疝和腹壁外科", "血管甲状腺外科", "乳腺外一科", "乳腺外二科", "胸外科", "心血管外科",
                "小儿外科","肿瘤及显微神经外科", "微创及脊髓神经外科", "功能神经外科",
                "血管及介入神经外科", "泌尿外科", "烧伤整形创面修复外科", "创面治疗中心", "外科门诊",
                "整形美容二科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("骨科", "创伤骨外科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("普通妇科", "肿瘤妇科", "计内妇科", "产科",
                "妇科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("生殖医学中心")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("儿科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("眼科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("耳鼻咽喉头颈外科", "咽喉头颈外科", "鼻科",
                "耳科门诊")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("口腔医学中心", "口腔内科", "口腔颌面外科",
                "修复种植科(镶牙种牙)", "牙齿矫正")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("感染科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("头颈乳腺放疗科", "鼻咽放疗一科", "鼻咽放疗二科",
                "胸腹放疗科", "门诊放疗部", "头颈胸肿瘤内科", "乳腺肿瘤内科", "胃肠肿瘤内科",
                "肿瘤靶向介入科", "201A静脉导管单元")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("康复医学科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("放射科", "核医学科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("中医科")));
        keshiSecond.add(new ArrayList<>(Arrays.asList("整形美容一科", "日间手术中心", "疼痛科", "心理咨询",
                "急诊科", "临床药学办", "高压氧", "临床营养中心")));
    }
}
