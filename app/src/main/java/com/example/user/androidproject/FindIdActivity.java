package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindIdActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
    private TextView taxNo,email,id;
    private Button findButton;
    private DatabaseReference mDatabase;
    private User user;
    private Intent intent = null;
    private RelativeLayout layout;
    private String type;
    private LinearLayout pwLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        id = findViewById(R.id.id);
        taxNo = findViewById(R.id.taxNo);
        email = findViewById(R.id.email);
        findButton = findViewById(R.id.idButton);
        layout = findViewById(R.id.layout);
        pwLayout = findViewById(R.id.pwLayout);

        intent = getIntent();
        type = intent.getStringExtra("type");
        if(type==null){ //type이 비어있으면 아이디 찾기 화면 이다.
            pwLayout.setVisibility(View.INVISIBLE);
        }else{
            findButton.setText("PW 찾기");
        }

        findButton.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(taxNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
        }else if(view == findButton){
            if(!check()) return;
            findUserId();
        }

    }

    private void findUserId(){
        String userId = id.getText().toString();
        String taxNm = taxNo.getText().toString();
        String emailAdd = email.getText().toString();

        String taxNoEmail = taxNm+"_"+emailAdd;
        String idTaxNoEmail = userId+"_"+taxNoEmail;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(type!=null){
            mDatabase.child("user").orderByChild("idTaxNoEmail").equalTo(idTaxNoEmail).addValueEventListener(this);
        }else{
            mDatabase.child("user").orderByChild("taxNoEmail").equalTo(taxNoEmail).addValueEventListener(this);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if(dataSnapshot.getValue(User.class) != null){
            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                user = userSnapshot.getValue(User.class);
            }

            intent = new Intent(this,informationActivity.class); // 아이디 정보 확인 페이지로 이동합니다.

            if(type!=null){
                intent.putExtra("email",user.getEmail());
                startActivity(intent);//액티비티 띄우기
            }else{
                intent.putExtra("userId",user.getStoreId());
                startActivity(intent);//액티비티 띄우기
            }

        } else {
            Toast.makeText(this,"해당하는 아이디 정보가 없습니다. 다시 한번 확인해 주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {}

    private boolean check(){
        if(type!=null){
            if(id.length() == 0 ){
                Toast.makeText(getApplicationContext(),"아이디 정보를 입력하세요.",Toast.LENGTH_SHORT).show();
                id.requestFocus();
                return false;
            }
        }

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