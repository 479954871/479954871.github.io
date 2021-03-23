package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospital.account.AccountManager;
import com.example.hospital.server.HospitalServer;
import com.example.hospital.R;
public class AddJiuZhenRenActivity extends AppCompatActivity {
    EditText nameEdit, idEdit, ageEdit, addressEdit, wechatEdit, emailEdit;
    RadioGroup radioGroupSex;
    boolean sex = true;
    int age = 0;
    String name, id, address, wechat, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jiu_zhen_ren);
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("添加就诊人");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            AddJiuZhenRenActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        nameEdit = findViewById(R.id.patient_name);
        idEdit = findViewById(R.id.patient_id);
        ageEdit = findViewById(R.id.patient_age);
        addressEdit = findViewById(R.id.patient_address);
        wechatEdit = findViewById(R.id.patient_wechat);
        emailEdit = findViewById(R.id.patient_email);
        radioGroupSex = findViewById(R.id.radioGroupSex);
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleButtonId) {
                    sex = true;
                } else if (checkedId == R.id.femalButtonId) {
                    sex = false;
                }
            }
        });
        Button button = findViewById(R.id.btn_add);
        button.setOnClickListener(v -> {
            name = nameEdit.getText().toString();
            if ("".equals(name)) {
                Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
                return;
            }
            id = idEdit.getText().toString();
            if ("".equals(id)) {
                Toast.makeText(this, "请输入身份证", Toast.LENGTH_SHORT).show();
                return;
            }
            String ageStr = ageEdit.getText().toString();
            if ("".equals(ageStr)) {
                Toast.makeText(this, "请输入年龄", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "年龄请输入正整数", Toast.LENGTH_SHORT).show();
                return;
            }
            String address = addressEdit.getText().toString();
            String wechat = wechatEdit.getText().toString();
            String email = emailEdit.getText().toString();
            HospitalServer.sendAddOrModifyPatientRequest(id, name, AccountManager.getInstance().getNowAccount(),
                    sex?"1":"0", age, address, wechat, email, false, new HospitalServer.AddOrModifyPatientCallback() {
                        @Override
                        public void addOrModifySuccess() {
                            Message msg = new Message();
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                        @Override
                        public void addOrModifyFail() {
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
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(AddJiuZhenRenActivity.this, "添加就诊人成功", Toast.LENGTH_SHORT).show();
                    AccountManager.JiuZhenRen temp = new AccountManager.JiuZhenRen();
                    temp.patientId = id;
                    temp.name = name;
                    temp.age = age;
                    temp.phone = AccountManager.getInstance().getNowAccount();
                    temp.sex = sex;
                    temp.address = address;
                    temp.wechatAccount = wechat;
                    temp.email = email;
                    AccountManager.getInstance().addJiuZhenRen(temp);
                    finish();
                    break;
                case 1:
                    Toast.makeText(AddJiuZhenRenActivity.this, "添加失败，就诊人已创建", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(AddJiuZhenRenActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}