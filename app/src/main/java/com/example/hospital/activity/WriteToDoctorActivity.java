package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospital.R;
import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.adapter.ItemAdapter;
import com.example.hospital.server.HospitalServer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WriteToDoctorActivity extends AppCompatActivity {
    String docId, docName, docSpecialist, docIntro, docGoodAt;
    Bitmap docPic;
    String keshiSecond, keshiFirst;
    int keshiFirstId, keshiSecondId;
    ImageView imageView;
    TextView nameText, goodAtText, specialistText;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    int index=0;
    Button button;


    EditText editText;
    ImageView imageViewAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_to_doctor);
        docId = getIntent().getStringExtra("doc_id");
        docName = getIntent().getStringExtra("doc_name");
        docSpecialist = getIntent().getStringExtra("doc_specialist");
        docIntro = getIntent().getStringExtra("doc_intro");
        docGoodAt = getIntent().getStringExtra("doc_good_at");
        int flag = getIntent().getIntExtra("flag", 0);
        docPic = flag == 0 ? ChooseDoctorActivity.getBitmap() : ZiXunGuanLiActivity.getBitmap();
        if (flag == 2) docPic = ZaiXianZiXunActivity.getBitmap();
        keshiFirst = getIntent().getStringExtra("keshi_first");
        keshiSecond = getIntent().getStringExtra("keshi_second");
        keshiFirstId = getIntent().getIntExtra("keshi_first_id", 0);
        keshiSecondId = getIntent().getIntExtra("keshi_second_id", 0);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("线上问诊");
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
        button = findViewById(R.id.send);
        editText = findViewById(R.id.edit_text);
        imageViewAdd = findViewById(R.id.add_pic);

        recyclerView= (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter=new ItemAdapter(this);
        recyclerView.setAdapter(itemAdapter);

        button.setOnClickListener(v -> {
            String data = editText.getText().toString();
            send(data);
        });

        imageViewAdd.setOnClickListener(v -> leftClick("picture"));

        showCommunication();
    }
    /**
     * 显示对话
     */
    private void showCommunication() {
        String key = (keshiFirstId*100 + keshiSecondId) + docName;
        if (AccountManager.getInstance().messages.containsKey(key)) {
            List<AccountManager.Msg> list = AccountManager.getInstance().messages.get(key);
            for (int i = 0; i < list.size(); i++) {
                AccountManager.Msg msg = null;
                for (AccountManager.Msg msg1 : list) {
                    if (msg1.index == i) {
                        msg = msg1;
                        break;
                    }
                }
                if (msg != null) {
                    if (msg.whoSend.equals("p")) {
                        rightClick(msg.data);
                    } else if (msg.whoSend.equals("d")){
                        leftClick(msg.data);
                    }
                }
            }
        }
    }

    /**
     * 医生
     */
    public void leftClick(String data){
        //第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem(data,ItemAdapter.LEFT);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        index++;
    }

    /**
     * 用户
     */
    public void rightClick(String data){
        //第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem(data,ItemAdapter.RIGHT);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        index++;
    }

    public void send(String data) {
        itemAdapter.addItem(data,ItemAdapter.RIGHT);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        AccountManager.Msg msg = new AccountManager.Msg();
        msg.docId = docId;
        msg.index = index;
        msg.firstDepId = keshiFirstId;
        msg.secondDepId = keshiSecondId;
        msg.docName = docName;
        msg.data = data;
        msg.time = HosptialUtils.getTime();
        msg.whoSend = "p";
        index++;
        HospitalServer.sendCommunicateRequest(AccountManager.getInstance().getNowAccount(), msg,
                new HospitalServer.CommunicateCallback() {
                    @Override
                    public void communicateSuccess(JSONObject jsonObject) {
                        if (!AccountManager.getInstance().messages.containsKey(msg.getKey())) {
                            AccountManager.getInstance().messages.put(msg.getKey(), new ArrayList<>());
                        }
                        AccountManager.getInstance().messages.get(msg.getKey()).add(msg);
                    }

                    @Override
                    public void networkUnavailable() {
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(WriteToDoctorActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(WriteToDoctorActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}