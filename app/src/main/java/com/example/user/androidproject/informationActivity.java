package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class informationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView idInfo,login,findPw;
    private LinearLayout layout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        idInfo = findViewById(R.id.idInfo);
        login = findViewById(R.id.login);
        findPw = findViewById(R.id.findPw);
        layout = findViewById(R.id.linearLayout);
        findPw.setOnClickListener(this);
        login.setOnClickListener(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("userId");
        String email = intent.getStringExtra("email");

        if(name!=null){ // 화면 재사용을 위한 코드이다.
            idInfo.setText("ID 정보는 "+name+" 입니다.");

        }else if(email!=null){
            idInfo.setText(email+"로 임시 비밀번호를 보냈습니다. 확인하세요");
            layout.setVisibility(View.INVISIBLE);
        }else{

        }


    }

    @Override
    public void onClick(View view) {
        if(view==findPw){

        }else if(view==login){
            intent = new Intent(this,LoginActivity.class); // 아이디 정보 확인 페이지로 이동합니다.
            startActivity(intent);//액티비티 띄우기
        }
    }
}