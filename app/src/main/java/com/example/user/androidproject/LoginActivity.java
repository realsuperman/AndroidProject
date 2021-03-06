package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,ValueEventListener{
    private EditText id,pw;
    private TextView findId,findPw,createUser;
    private Button loginButton;
    private DatabaseReference mDatabase;
    private User user;
    private Intent intent = null;
    private RelativeLayout layout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id=findViewById(R.id.id);
        pw=findViewById(R.id.pw);
        findId=findViewById(R.id.findId);
        findPw=findViewById(R.id.findPw);
        createUser=findViewById(R.id.createUser);
        loginButton=findViewById(R.id.loginButton);
        layout = findViewById(R.id.layout);

        id.setOnClickListener(this);
        pw.setOnClickListener(this);
        findId.setOnClickListener(this);
        findPw.setOnClickListener(this);
        createUser.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
        }
        if(view == loginButton){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
            login();
        }else if(view == findId){
            findId();
        }else if(view==findPw){
            findPw();
        }else if(view==createUser){
            createUser();
        }
    }

    private void login() {
        if(!check()) return;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("로그인 중 입니다.");
        progressDialog.show();
        progressDialog.setCancelable(false);
        readUser();
    }

    private void readUser(){
        String storeId = id.getText().toString();
        String storePw = pw.getText().toString();
        String idPw = storeId+"_"+storePw;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").orderByChild("idPw").equalTo(idPw).addListenerForSingleValueEvent(this);
    }

    private void findId() {
        intent = new Intent(this,FindIdActivity.class); // 아이디 찾기 화면으로 이동한다.
        startActivity(intent);//액티비티 띄우기
    }

    private void findPw() {
        intent = new Intent(this,FindIdActivity.class); // 패스워드 찾기 화면으로 이동한다.
        intent.putExtra("type","notNull");
        startActivity(intent);//액티비티 띄우기
    }
    private void createUser() {
        intent = new Intent(this,CreateUserActivity.class); // 아이디 찾기 화면으로 이동한다.
        startActivity(intent);//액티비티 띄우기
    }

    private boolean check(){
        if(id.length() == 0 ){
            Toast.makeText(getApplicationContext(),"아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
            id.requestFocus();
            return false;
        }
        if(pw.length() == 0){
            Toast.makeText(getApplicationContext(),"패스워드를 입력해주세요",Toast.LENGTH_SHORT).show();
            pw.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue(User.class) != null){
            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                user = userSnapshot.getValue(User.class);
            }

            intent = new Intent(this,MasterActivity.class); // 박준영씨 작업 완료시 그 페이지로 이동하게끔
            intent.putExtra("user",user);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }else{
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            progressDialog.dismiss();
            startActivity(intent);//액티비티 띄우기
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"아이디나 패스워드 정보가 틀렸습니다 다시 확인해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {}

}