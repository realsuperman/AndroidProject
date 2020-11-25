package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logoutButton,findOrderButton,changeStoreNameButton;
    private Intent intent;
    private User user;
    private TextView storeName,storeGrade;
    private EditText menuName;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        intent = getIntent();
        user = (User)intent.getSerializableExtra("user");

        layout = findViewById(R.id.layout);
        logoutButton = findViewById(R.id.logoutButton);
        findOrderButton = findViewById(R.id.findOrderButton);
        storeName = findViewById(R.id.storeName);
        storeGrade = findViewById(R.id.storeGrade);
        menuName = findViewById(R.id.menuName);
        changeStoreNameButton = findViewById(R.id.changeStoreNameButton);

        logoutButton.setOnClickListener(this);
        findOrderButton.setOnClickListener(this);
        layout.setOnClickListener(this);
        changeStoreNameButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==layout) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(menuName.getWindowToken(), 0);
        }else if(view == logoutButton){ // 로그아웃 버튼 클릭시
            intent = new Intent(getApplicationContext(),MainActivity.class); // 박준영씨 작업 완료시 그 페이지로 이동하게끔
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }else{
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            startActivity(intent);//액티비티 띄우기
        }else if(view == findOrderButton){
            intent = new Intent(getApplicationContext(),TableOrderActivity.class); // 박준영씨 작업 완료시 그 페이지로 이동하게끔
            intent.putExtra("storeId",user.getStoreId());
            intent.putExtra("flag","master");
            startActivity(intent);//액티비티 띄우기
        }else if(view == changeStoreNameButton){
            intent = new Intent(getApplicationContext(),PasswordConfirmActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
        }
    }
}