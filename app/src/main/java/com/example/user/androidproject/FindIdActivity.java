package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindIdActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
    private TextView taxNo,email;
    private Button findId;
    private DatabaseReference mDatabase;
    private User user;
    private Intent intent = null;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        taxNo = findViewById(R.id.taxNo);
        email = findViewById(R.id.email);
        findId = findViewById(R.id.idButton);
        layout = findViewById(R.id.layout);

        findId.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(taxNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
        }else if(view == findId){
            if(!check()) return;
            findUserId();
        }

    }

    private void findUserId(){
        String taxNm = taxNo.getText().toString();
        String emailAdd = email.getText().toString();

        String taxNoEmail = taxNm+"_"+emailAdd;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("user").orderByChild("taxNoEmail").equalTo(taxNoEmail).addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if(dataSnapshot.getValue(User.class) != null){
            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                user = userSnapshot.getValue(User.class);
            }

            intent = new Intent(this,informationActivity.class); // 아이디 정보 확인 페이지로 이동합니다.
            intent.putExtra("userId",user.getStoreId());
            startActivity(intent);//액티비티 띄우기
        } else {
            Toast.makeText(this,"해당하는 아이디 정보가 없습니다. 다시 한번 확인해 주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {}

    private boolean check(){
        if(taxNo.length() == 0 ){
            Toast.makeText(getApplicationContext(),"사업자번호를 입력하세요",Toast.LENGTH_SHORT).show();
            taxNo.requestFocus();
            return false;
        }
        if(email.length() == 0){
            Toast.makeText(getApplicationContext(),"이메일을 입력하세요",Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        }
        return true;
    }

}