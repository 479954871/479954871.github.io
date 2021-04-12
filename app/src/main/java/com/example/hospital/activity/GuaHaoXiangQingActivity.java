package com.example.hospital.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospital.R;
import com.example.hospital.Utils.DownloadBMPFromUrlUtils;
import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.adapter.HorizontalListViewAdapter;
import com.example.hospital.server.HospitalServer;
import com.example.hospital.ui.HorizontalListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 挂号详情
 */
public class GuaHaoXiangQingActivity extends AppCompatActivity {

    private HorizontalListView mHorizontalListView;
    TextView textView1;
    List<Map<String, ?>> list = new ArrayList<>();
    List<Map<String, ?>> listAllDoctor = new ArrayList<>();
    List<List<Integer>> listDoctorInWeek = new ArrayList<>();
    int[] month = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    String[] week = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    HorizontalListViewAdapter mHorizontalAdapter;
    String keshiSecond, keshiFirst;
    int keshiFirstId, keshiSecondId;
    ListView listView;
    SimpleAdapter mListViewAdapter;
    int thatDay = HosptialUtils.getDayofWeek("");
    static Bitmap bitmap = null;
    static String[] weeks = new String[7];
    static String[] dates = new String[7];
    static String[] months = new String[7];
    static String[] years = new String[7];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_hao_xiang_qing_layout);
        keshiFirst = getIntent().getStringExtra("keshi_first");
        keshiSecond = getIntent().getStringExtra("keshi_second");
        keshiFirstId = getIntent().getIntExtra("keshi_first_id", 1);
        keshiSecondId = getIntent().getIntExtra("keshi_second_id", 1);
        sendGetDoctorRequest();
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText(keshiSecond);
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            GuaHaoXiangQingActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        listView = findViewById(R.id.listview_guahao);
        mListViewAdapter = new SimpleAdapter(this, list, R.layout.guahao_item,
                new String[] {"doc_pic", "doc_name", "doc_specialist", "doc_good_at"}, new int[] {R.id.doc_pic, R.id.doc_name,
                R.id.doc_specialist, R.id.doc_good_at});
        mListViewAdapter.setViewBinder((view, data, textRepresentation) -> {
            if (view instanceof ImageView && data instanceof Bitmap) {
                ImageView iv = (ImageView) view;
                iv.setImageBitmap((Bitmap) data);
                return true;
            }
            return false;
        });
        listView.setAdapter(mListViewAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(GuaHaoXiangQingActivity.this, DoctorActivity.class);
            intent.putExtra("doc_id", (String)list.get(position).get("doc_id"));
            if (list.get(position).get("doc_pic") instanceof Bitmap) {
                bitmap = (Bitmap) list.get(position).get("doc_pic");
            } else {
                bitmap = null;
            }
            intent.putExtra("doc_name", (String)list.get(position).get("doc_name"));
            intent.putExtra("doc_specialist", (String)list.get(position).get("doc_specialist"));
            intent.putExtra("doc_intro", (String)list.get(position).get("doc_intro"));
            intent.putExtra("doc_good_at", (String)list.get(position).get("doc_good_at"));
            intent.putExtra("doc_treat_time", (String)list.get(position).get("doc_treat_time"));
            intent.putExtra("that_day", thatDay);
            intent.putExtra("keshi_first", keshiFirst);
            intent.putExtra("keshi_second", keshiSecond);
            intent.putExtra("keshi_first_id", keshiFirstId);
            intent.putExtra("keshi_second_id", keshiSecondId);
            startActivity(intent);
        });

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        Log.w("TAG", "onCreate: " + mYear + mMonth + mDay);
        textView1 = findViewById(R.id.day);
        textView1.setText(mYear + "年" + mMonth + "月" + mDay + "日");

        int now = HosptialUtils.getDayofWeek("");
        for (int i = 0; i < 7; i++) {
            weeks[i] = " 周"+week[(now + i) % 7]+" ";
            dates[i] = mDay;
            months[i] = mMonth;
            years[i] = mYear;
            int day = Integer.parseInt(mDay) + 1;
            if(day > month[Integer.parseInt(mMonth)-1]) {
                //说明该下个月了
                day = 1;
                int m = Integer.parseInt(mMonth) + 1;
                if (m > 12) {
                    //说明该下一年了
                    m = 1;
                    mYear = String.valueOf(Integer.parseInt(mYear) + 1);
                }
                mMonth = String.valueOf(m);
            }
            mDay = String.valueOf(day);
        }

        mHorizontalListView = findViewById(R.id.horizontal_lv);
        mHorizontalAdapter = new HorizontalListViewAdapter(getApplicationContext(),weeks,dates);
        mHorizontalListView.setAdapter(mHorizontalAdapter);
        mHorizontalListView.setOnItemClickListener((parent, view, position, id) -> {
            textView1.setText(years[position] + "年" + months[position] + "月" + dates[position] + "日");
            mHorizontalAdapter.setSelectedPosition(position);
            mHorizontalAdapter.notifyDataSetChanged();
            thatDay = (now + position) % 7;
            list.clear();
            for (int i = 0; i < listDoctorInWeek.get(thatDay).size(); ++i) {
                list.add(listAllDoctor.get(listDoctorInWeek.get(thatDay).get(i)));
            }
            mListViewAdapter.notifyDataSetChanged();
        });
        mHorizontalAdapter.setSelectedPosition(0);
    }

    private void sendGetDoctorRequest() {
        listDoctorInWeek.clear();
        for (int i = 0; i < 7; i++) listDoctorInWeek.add(new ArrayList<>());
        HospitalServer.sendGetDoctorRequest(keshiFirstId, keshiSecondId, new HospitalServer.GetDoctorCallback() {
            @Override
            public void getSuccess(JSONObject jsonObject) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        String docId = jsonObject1.getString("doc_id");
                        String docName = jsonObject1.getString("doc_name");
                        String docSpecialist = jsonObject1.getString("doc_specialist");
                        String docGoodAt = jsonObject1.getString("doc_good_at");
                        if (docGoodAt.equals("null")) {
                            docGoodAt = "";
                        }
                        String docIntro = jsonObject1.getString("doc_intro");
                        if (docIntro.equals("null")) {
                            docIntro = "";
                        }
                        String docTreatTime = jsonObject1.getString("doc_treat_time");
                        for (int j = 0; j < 7; ++j) {
                            if (docTreatTime.charAt(2*j) == '1' || docTreatTime.charAt(2*j+1) == '1') {
                                listDoctorInWeek.get(j).add(i);
                            }
                        }
                        String docPic = jsonObject1.getString("doc_pic");

                        map.put("doc_id", docId);
                        map.put("doc_name", docName);
                        map.put("doc_specialist", docSpecialist);
                        map.put("doc_intro", docIntro);
                        map.put("doc_good_at", docGoodAt);
                        map.put("doc_treat_time", docTreatTime);
                        if (docPic == null || docPic.equals("null") || docPic.equals("")) {
                            map.put("doc_pic", R.mipmap.nothing);
                        } else {
                            map.put("doc_pic", DownloadBMPFromUrlUtils.getBitmapFromUrl(docPic));
                        }
                        listAllDoctor.add(map);
                        Log.w("TAG", "getSuccess: doc_id:" + docId + " docName:" + docName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }

            @Override
            public void networkUnavailable() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    list.clear();
                    int now = HosptialUtils.getDayofWeek("");
                    for (int i = 0; i < listDoctorInWeek.get(now).size(); ++i) {
                        list.add(listAllDoctor.get(listDoctorInWeek.get(now).get(i)));
                    }
                    mListViewAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    Toast.makeText(GuaHaoXiangQingActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public static Bitmap getBitmap() {
        return bitmap;
    }
}
