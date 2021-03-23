package com.example.hospital.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hospital.R;

public class DoctorActivity extends AppCompatActivity {
    String docId, docName, docSpecialist, docIntro, docGoodAt, docTreatTime;
    Bitmap docPic;
    ImageView imageView;
    TextView nameText, goodAtText, specialistText;
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
    }
}