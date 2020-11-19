package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class TableInformationActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_information);

        intent = getIntent();
        String flag = intent.getStringExtra("flag");
        if("0".equalsIgnoreCase(flag)){ // 추가버튼 클릭시
            String floor = intent.getStringExtra("floor");
        }else{ // 수정될시
            TableOrder table = (TableOrder)intent.getSerializableExtra("TableInfo");
        }
        
    }
}