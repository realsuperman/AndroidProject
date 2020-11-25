package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordConfirmActivity extends AppCompatActivity implements View.OnClickListener{
    private Button comparePassword;
    private Intent intent;
    private User user;
    private EditText userInput;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);

        comparePassword = findViewById(R.id.comparePassword);
        userInput = findViewById(R.id.userInput);
        layout = findViewById(R.id.layout);

        intent = getIntent();
        user = (User)intent.getSerializableExtra("user");

        layout.setOnClickListener(this);
        comparePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
        }else if(view == comparePassword){
            if(!user.getStorePw().equals(userInput.getText().toString())){
                Toast.makeText(getApplicationContext(),"패스워드 정보가 일치하지 않습니다 다시한번 확인해주세요",Toast.LENGTH_SHORT).show();
                return;
            }
            intent = new Intent(getApplicationContext(),CreateUserActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("flag","1");
            startActivity(intent);
        }
    }
}