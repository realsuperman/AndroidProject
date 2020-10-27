package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView korean,snack,china,chicken,pizza,beer;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        korean = findViewById(R.id.korean);
        snack = findViewById(R.id.snack);
        china = findViewById(R.id.china);
        chicken = findViewById(R.id.chicken);
        pizza = findViewById(R.id.pizza);
        beer = findViewById(R.id.beer);
        login = findViewById(R.id.login);

        korean.setOnClickListener(this);
        snack.setOnClickListener(this);
        china.setOnClickListener(this);
        chicken.setOnClickListener(this);
        pizza.setOnClickListener(this);
        beer.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        if(view == login) { // 만약 로그인 버튼 클릭시
            intent = new Intent(this,LoginActivity.class);
        }else{ // 카테고리 클릭시
            intent = new Intent(this,LoginActivity.class); // 주환이가 짜는 페이지로 변경해야함 우선은 내페이지로 씀
            if(view==beer) intent.putExtra("type","beer");
            else intent.putExtra("type","noBeer");
        }
        startActivity(intent);//액티비티 띄우기
    }
}