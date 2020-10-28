package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class informationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView idInfo,login,findPw;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        idInfo = findViewById(R.id.idInfo);
        login = findViewById(R.id.login);
        findPw = findViewById(R.id.findPw);
        findPw.setOnClickListener(this);
        login.setOnClickListener(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("userId");
        String email = intent.getStringExtra("email");

        if(name!=null){ // 화면 재사용을 위한 코드이다.
            idInfo.setText("ID 정보는 "+name+" 입니다.");
            // 로그인 화면에 android:layout_marginLeft="30dp" marginRight="10dp"

        }else if(email!=null){
            idInfo.setText(email+"로 임시 비밀번호를 보냈습니다. 확인하세요");
            findPw.setVisibility(View.INVISIBLE);

        }else{
            idInfo.setText("등록이 완료 되었습니다.");
            findPw.setVisibility(View.INVISIBLE);
            idInfo.setTextSize(56);
        }


    }

    @Override
    public void onClick(View view) {
        if(view==findPw){
            intent = new Intent(this,FindIdActivity.class); // 패스워드 찾기 화면으로 이동한다.
            intent.putExtra("type","notNull");
            startActivity(intent);//액티비티 띄우기
        }else if(view==login){
            intent = new Intent(this,LoginActivity.class); // 아이디 정보 확인 페이지로 이동합니다.
            startActivity(intent);//액티비티 띄우기
        }
    }
}