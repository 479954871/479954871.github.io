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
import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.server.HospitalServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZiXunGuanLiActivity extends AppCompatActivity {
    List<Map<String, ?>> listAllDoctor = new ArrayList<>();
    ListView listView;
    SimpleAdapter mListViewAdapter;
    static Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zi_xun_guan_li);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("在线咨询记录");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setList();
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
            Intent intent = new Intent(ZiXunGuanLiActivity.this, WriteToDoctorActivity.class);
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
            intent.putExtra("keshi_first", (String)listAllDoctor.get(position).get("first_dep"));
            intent.putExtra("keshi_second", (String)listAllDoctor.get(position).get("second_dep"));
            intent.putExtra("keshi_first_id", Integer.parseInt((String)listAllDoctor.get(position).get("first_dep_id")));
            intent.putExtra("keshi_second_id", Integer.parseInt((String)listAllDoctor.get(position).get("second_dep_id")));
            intent.putExtra("flag", 1);
            startActivity(intent);
        });
    }

    private void setList() {
        Map<String, List<AccountManager.Msg>> messages = AccountManager.getInstance().messages;
        for (String key : messages.keySet()) {
            List<AccountManager.Msg> msgList= messages.get(key);
            if (msgList == null || msgList.size() == 0) continue;
            HospitalServer.sendGetDoctorFromIdRequest(msgList.get(0).docId, new HospitalServer.GetDoctorFromIdCallback() {
                @Override
                public void getSuccess(JSONObject jsonObject) {
                    try {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
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
                        map.put("first_dep", jsonObject1.getString("first_dep"));
                        map.put("first_dep_id", jsonObject1.getString("first_dep_id"));
                        map.put("second_dep", jsonObject1.getString("second_dep"));
                        map.put("second_dep_id", jsonObject1.getString("second_dep_id"));
                        listAllDoctor.add(map);
                        Log.w("TAG", "getSuccess: doc_id:" + docId + " docName:" + docName);
                    } catch (
                    JSONException e) {
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
                    Toast.makeText(ZiXunGuanLiActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public static Bitmap getBitmap() {
        return bitmap;
    }
}