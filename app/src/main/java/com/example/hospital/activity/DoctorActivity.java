package com.example.hospital.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.hospital.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorActivity extends AppCompatActivity {
    String docId, docName, docSpecialist, docIntro, docGoodAt, docTreatTime;
    Bitmap docPic;
    String keshiSecond, keshiFirst;
    int keshiFirstId, keshiSecondId;
    ImageView imageView;
    TextView nameText, goodAtText, specialistText;
    RadioGroup radioGroup;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GuaHaoFragment fragment1;
    JianJieFragment fragment2;
    int thatDay;
    public static String[] listAll = new String[] { "08:00~09:00", "09:00~10:00",
            "10:00~11:00", "11:00~11:50", "14:00~15:00", "15:00~16:00", "16:00~16:50"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        docId = getIntent().getStringExtra("doc_id");
        docName = getIntent().getStringExtra("doc_name");
        docSpecialist = getIntent().getStringExtra("doc_specialist");
        docIntro = getIntent().getStringExtra("doc_intro");
        docGoodAt = getIntent().getStringExtra("doc_good_at");
        docTreatTime = getIntent().getStringExtra("doc_treat_time");
        docPic = GuaHaoXiangQingActivity.getBitmap();
        thatDay = getIntent().getIntExtra("that_day", 0);
        keshiFirst = getIntent().getStringExtra("keshi_first");
        keshiSecond = getIntent().getStringExtra("keshi_second");
        keshiFirstId = getIntent().getIntExtra("keshi_first_id", 0);
        keshiSecondId = getIntent().getIntExtra("keshi_second_id", 0);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("医生主页");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imageView = findViewById(R.id.doc_pic);
        nameText = findViewById(R.id.doc_name);
        specialistText = findViewById(R.id.doc_specialist);
        goodAtText = findViewById(R.id.doc_good_at);
        if (docPic != null) {
            imageView.setImageBitmap(docPic);
        }
        nameText.setText(docName);
        specialistText.setText(docSpecialist);
        goodAtText.setText(docGoodAt);
        initFragment();
        radioGroup = findViewById(R.id.main_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.main_radiobutton_guahao:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.show(fragment1);
                        fragmentTransaction.hide(fragment2);
                        fragmentTransaction.commit();
                        break;
                    case R.id.main_radiobutton_jianjie:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.hide(fragment1);
                        fragmentTransaction.show(fragment2);
                        fragmentTransaction.commit();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initFragment() {
        // 步骤1：获取FragmentManager
        fragmentManager = getFragmentManager();

        // 步骤2：获取FragmentTransaction
        fragmentTransaction = fragmentManager.beginTransaction();

        // 步骤3：创建需要添加的Fragment ：ExampleFragment
        fragment1 = new GuaHaoFragment();
        fragment1.docTreatTime = docTreatTime;
        fragment1.docSpecialist = docSpecialist;
        fragment1.thatDay = thatDay;
        fragment1.keshiFirst = keshiFirst;
        fragment1.keshiSecond = keshiSecond;
        fragment1.keshiFirstId = keshiFirstId;
        fragment1.keshiSecondId = keshiSecondId;
        fragment1.docId = docId;
        fragment1.docName = docName;
        fragment2 = new JianJieFragment();
        fragment2.docGoodAt = docGoodAt;
        fragment2.docIntro = docIntro;
        // 步骤4：动态添加fragment
        // 即将创建的fragment添加到Activity布局文件中定义的占位符中（FrameLayout）
        fragmentTransaction.add(R.id.about_fragment_container, fragment1);
        fragmentTransaction.add(R.id.about_fragment_container, fragment2);
        fragmentTransaction.commit();
        fragmentTransaction.show(fragment1);
        fragmentTransaction.hide(fragment2);
    }
    // 继承与Fragment
    public static class GuaHaoFragment extends Fragment {
        TextView textView1;
        ListView listView;
        SimpleAdapter mListViewAdapter;

        List<Map<String, String>> list = new ArrayList<>();
        String docId, docName, docSpecialist, docTreatTime;
        String keshiSecond, keshiFirst;
        int keshiFirstId, keshiSecondId;
        int thatDay;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_yuyueguahao, container, false);
            // 将example_fragment.xml作为该Fragment的布局文件
            textView1 = root.findViewById(R.id.day1);
            int p = (thatDay - GuaHaoXiangQingActivity.getDayofWeek("") + 7) % 7;
            textView1.setText(GuaHaoXiangQingActivity.years[p] + "年" + GuaHaoXiangQingActivity.months[p] + "月" + GuaHaoXiangQingActivity.dates[p] + "日");

            if (docTreatTime.charAt(thatDay *2) == '1') {
                for (int i = 0; i < 4; ++i) {
                    Map<String, String> map = new HashMap<>();
                    map.put("time", listAll[i]);
                    map.put("doc_spe", docSpecialist);
                    list.add(map);
                }
            }
            if (docTreatTime.charAt(thatDay *2+1) == '1') {
                for (int i = 0; i < 3; ++i) {
                    Map<String, String> map = new HashMap<>();
                    map.put("time", listAll[i+4]);
                    map.put("doc_spe", docSpecialist);
                    list.add(map);
                }
            }
            listView = root.findViewById(R.id.listview_guahao1);
            mListViewAdapter = new SimpleAdapter(getActivity(), list, R.layout.guahao_item2,
                    new String[] {"time", "doc_spe"}, new int[] {R.id.time, R.id.doc_spe});
            listView.setAdapter(mListViewAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(getActivity(), ConfirmGuaHaoActivity.class);
                intent.putExtra("doc_id", docId);
                intent.putExtra("doc_name", docName);
                intent.putExtra("doc_specialist", docSpecialist);
                intent.putExtra("keshi_first", keshiFirst);
                intent.putExtra("keshi_second", keshiSecond);
                intent.putExtra("keshi_first_id", keshiFirstId);
                intent.putExtra("keshi_second_id", keshiSecondId);
                intent.putExtra("that_day", thatDay);
                intent.putExtra("yuyueshijianduan", list.get(position).get("time"));
                intent.putExtra("yuyueshijianduanindex", getNum(position));
                startActivity(intent);
            });
            mListViewAdapter.notifyDataSetChanged();
            return root;
        }

        int getNum(int position) {
            String time = list.get(position).get("time");
            for (int i = 0; i < listAll.length; i++) {
                if (time.equals(listAll[i]))
                    return i;
            }
            return 0;
        }
    }

    // 继承与Fragment
    public static class JianJieFragment extends Fragment {
        public String docGoodAt, docIntro;
        TextView textViewGoodAt, textViewIntro;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View root = inflater.inflate(R.layout.fragment_gerenjianjie, container, false);
            // 将example_fragment.xml作为该Fragment的布局文件
            textViewGoodAt = root.findViewById(R.id.doc_good_at2);
            textViewIntro = root.findViewById(R.id.doc_intru2);
            textViewGoodAt.setText(docGoodAt);
            textViewIntro.setText(docIntro);
            return root;
        }
    }
}