package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.hospital.R;
import com.example.hospital.Utils.DownloadBMPFromUrlUtils;
import com.example.hospital.server.HospitalServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseDoctorActivity extends AppCompatActivity {
    List<Map<String, ?>> listAllDoctor = new ArrayList<>();
    String keshiSecond, keshiFirst;
    int keshiFirstId, keshiSecondId;
    ListView listView;
    SimpleAdapter mListViewAdapter;
    static Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_doctor);
        DownloadBMPFromUrlUtils.handleSSLHandshake();
        keshiFirst = getIntent().getStringExtra("keshi_first");
        keshiSecond = getIntent().getStringExtra("keshi_second");
        keshiFirstId = getIntent().getIntExtra("keshi_first_id", 1);
        keshiSecondId = getIntent().getIntExtra("keshi_second_id", 1);
        sendGetDoctorRequest();
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("选择医生");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        listView = findViewById(R.id.listview_guahao);
        mListViewAdapter = new SimpleAdapter(this, listAllDoctor, R.layout.guahao_item,
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
            Intent intent = new Intent(ChooseDoctorActivity.this, WriteToDoctorActivity.class);
            intent.putExtra("doc_id", (String)listAllDoctor.get(position).get("doc_id"));
            if (listAllDoctor.get(position).get("doc_pic") instanceof Bitmap) {
                bitmap = (Bitmap) listAllDoctor.get(position).get("doc_pic");
            } else {
                bitmap = null;
            }
            intent.putExtra("doc_name", (String)listAllDoctor.get(position).get("doc_name"));
            intent.putExtra("doc_specialist", (String)listAllDoctor.get(position).get("doc_specialist"));
            intent.putExtra("doc_intro", (String)listAllDoctor.get(position).get("doc_intro"));
            intent.putExtra("doc_good_at", (String)listAllDoctor.get(position).get("doc_good_at"));
            intent.putExtra("doc_treat_time", (String)listAllDoctor.get(position).get("doc_treat_time"));
            intent.putExtra("keshi_first", keshiFirst);
            intent.putExtra("keshi_second", keshiSecond);
            intent.putExtra("keshi_first_id", keshiFirstId);
            intent.putExtra("keshi_second_id", keshiSecondId);
            intent.putExtra("flag", 0);
            startActivity(intent);
        });
    }

    private void sendGetDoctorRequest() {
        listAllDoctor.clear();
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
                        String docPic = jsonObject1.getString("doc_pic");

                        map.put("doc_id", docId);
                        map.put("doc_name", docName);
                        map.put("doc_specialist", docSpecialist);
                        map.put("doc_intro", docIntro);
                        map.put("doc_good_at", docGoodAt);
                        map.put("doc_treat_time", docTreatTime);
                        if (docPic.equals("null") || docPic.equals("")) {
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
                    mListViewAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    Toast.makeText(ChooseDoctorActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public static Bitmap getBitmap() {
        return bitmap;
    }
}