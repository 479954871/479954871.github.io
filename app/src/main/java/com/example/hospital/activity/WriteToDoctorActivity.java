package com.example.hospital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telecom.Call;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospital.R;
import com.example.hospital.Utils.HosptialUtils;
import com.example.hospital.account.AccountManager;
import com.example.hospital.adapter.ItemAdapter;
import com.example.hospital.server.HospitalServer;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    String mCurrentPhotoPath = null;

    EditText editText;
    ImageView imageViewAdd;

    static final int RESULT_LOAD_IMAGE = 0;
    static final int RESULT_CAMERA_IMAGE = 1;
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

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindow();
            }
        });

        showCommunication();
    }

    private void showPopueWindow(){
        View popView = View.inflate(this,R.layout.popupwindow_camera_need,null);
        Button btn_album = popView.findViewById(R.id.btn_pop_album);
        Button btn_camera = popView.findViewById(R.id.btn_pop_camera);
        Button btn_cancle = popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels / 3;
        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        btn_album.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
            popupWindow.dismiss();

        });
        btn_camera.setOnClickListener(v -> {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(WriteToDoctorActivity.this, Manifest.permission.CAMERA)){
                ActivityCompat.requestPermissions(WriteToDoctorActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1); // 申请权限
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, RESULT_CAMERA_IMAGE);
            popupWindow.dismiss();

        });
        btn_cancle.setOnClickListener(v -> popupWindow.dismiss());
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK ) {
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                Uri selectedImage = data.getData();
                rightClick(selectedImage);
            }else if (requestCode == RESULT_CAMERA_IMAGE){
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                rightClick(bm);
            }
        }
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
        itemAdapter.addItem(data, ItemAdapter.LEFT, null, null);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        index++;
    }

    /**
     * 用户
     */
    public void rightClick(String data){
        //第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem(data,ItemAdapter.RIGHT, null, null);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        index++;
    }

    public void rightClick(Uri uri){
        //第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem(null, ItemAdapter.URI, uri, null);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        index++;
    }
    public void rightClick(Bitmap bitmap){
        //第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem(null, ItemAdapter.BITMAP, null, bitmap);
        recyclerView.smoothScrollToPosition(index);//移动到指定位置
        index++;
    }


    public void send(String data) {
        itemAdapter.addItem(data,ItemAdapter.RIGHT, null, null);
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