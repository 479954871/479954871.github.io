package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import java.util.List;

public class ModifyJiuZhenRenActivity extends AppCompatActivity {
    TextView nameText, idText;
    EditText ageEdit, addressEdit, wechatEdit, emailEdit;
    RadioGroup radioGroupSex;
    boolean sex;
    int position, age;
    String name, id, address, wechat, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_jiu_zhen_ren);
        position = getIntent().getIntExtra("position", 0);
        List<AccountManager.JiuZhenRen> patients = AccountManager.getInstance().getJiuZhenRen();
        TextView textView = findViewById(R.id.main_head_title);
        textView.setText("就诊人信息");
        ImageView backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> {
            ModifyJiuZhenRenActivity.this.finish();
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        id = patients.get(position).patientId;
        name = patients.get(position).name;
        age = patients.get(position).age;
        sex = patients.get(position).sex;
        address = patients.get(position).address;
        wechat = patients.get(position).wechatAccount;
        email = patients.get(position).email;
        nameText = findViewById(R.id.patient_name);
        nameText.setText(name);
        idText = findViewById(R.id.patient_id);
        idText.setText(id);
        ageEdit = findViewById(R.id.patient_age);
        ageEdit.setText(String.valueOf(age));
        addressEdit = findViewById(R.id.patient_address);
        addressEdit.setText(address);
        wechatEdit = findViewById(R.id.patient_wechat);
        wechatEdit.setText(wechat);
        emailEdit = findViewById(R.id.patient_email);
        emailEdit.setText(email);
        radioGroupSex = findViewById(R.id.radioGroupSex);
        if (sex) {
            radioGroupSex.check(R.id.maleButtonId);
        } else {
            radioGroupSex.check(R.id.femalButtonId);
        }
        radioGroupSex.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.maleButtonId) {
                sex = true;
            } else if (checkedId == R.id.femalButtonId) {
                sex = false;
            }
        });
        Button button = findViewById(R.id.btn_save);
        button.setOnClickListener(v -> {
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
            address = addressEdit.getText().toString();
            wechat = wechatEdit.getText().toString();
            email = emailEdit.getText().toString();
            HospitalServer.sendAddOrModifyPatientRequest(id, name, AccountManager.getInstance().getNowAccount(),
                    sex?"1":"0", age, address, wechat, email, true, new HospitalServer.AddOrModifyPatientCallback() {
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
        Button button1 = findViewById(R.id.btn_delete);
        button1.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setIcon(R.mipmap.hospital_icon);
            dialog.setTitle("温馨提示");
            dialog.setMessage("删除就诊人后，将无法查看该就诊人的病历信息，删除后将无法恢复，确定要删除吗？");
            dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HospitalServer.sendDeletePatientRequest(AccountManager.getInstance().getNowAccount(),
                            id, new HospitalServer.DeletePatientCallback() {
                                @Override
                                public void deleteSuccess() {
                                    Message msg = new Message();
                                    msg.what = 3;
                                    handler.sendMessage(msg);
                                }
                                @Override
                                public void deleteFailed() {
                                    Message msg = new Message();
                                    msg.what = 4;
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
            //3个按钮
//                dialog.setNeutralButton("等待", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(DialogActivity.this, "等待", Toast.LENGTH_SHORT).show();
//                    }
//                });
            dialog.show();
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(ModifyJiuZhenRenActivity.this, "修改就诊人信息成功", Toast.LENGTH_SHORT).show();
                    AccountManager.getInstance().modifyJiuZhenRen(position, sex, age, address,
                            wechat, email);
                    finish();
                    break;
                case 1:
                    Toast.makeText(ModifyJiuZhenRenActivity.this, "修改失败，请检查数据格式", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ModifyJiuZhenRenActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(ModifyJiuZhenRenActivity.this, "删除就诊人成功", Toast.LENGTH_SHORT).show();
                    AccountManager.getInstance().deleteJiuZhenRen(id);
                    finish();
                    break;
                case 4:
                    Toast.makeText(ModifyJiuZhenRenActivity.this, "删除失败，就诊人不存在", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}