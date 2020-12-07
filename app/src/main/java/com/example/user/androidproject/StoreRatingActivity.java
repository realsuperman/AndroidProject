package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoreRatingActivity extends Activity implements View.OnClickListener{
    private Intent intent;
    private RatingBar ratingBar;
    private EditText phone;
    private DatabaseReference mDatabase;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_rating);

        ratingBar = findViewById(R.id.ratingBar);
        phone = findViewById(R.id.phone);
        button = findViewById(R.id.button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        intent = getIntent();
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(1.0f>ratingBar.getRating()){
            Toast.makeText(getApplicationContext(),"0점 미만의 점수는 줄 수 없습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.getText().length()<9 || phone.getText().length()>11){
            Toast.makeText(getApplicationContext(),"휴대폰 번호를 다시한번 확인 하세요",Toast.LENGTH_SHORT).show();
            return;
        }

        final StoreGrade storeGrade = new StoreGrade();
        storeGrade.setStoreId(intent.getStringExtra("storeId"));
        storeGrade.setPhone(phone.getText().toString());
        storeGrade.setScore((int)ratingBar.getRating());
        storeGrade.setStoreIdPhone(intent.getStringExtra("storeId")+"_"+phone.getText().toString());

        mDatabase.child("storeGrade").orderByChild("storeIdPhone").equalTo(intent.getStringExtra("storeId")+"_"+phone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(StoreGrade.class) != null){
                    Toast.makeText(getApplicationContext(),"이미 평점을 주셨습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    mDatabase.child("storeGrade/"+System.currentTimeMillis()).setValue(storeGrade).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            final Intent i = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                            mDatabase.child("storeGrade").orderByChild("storeId").equalTo(intent.getStringExtra("storeId")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<StoreGrade> grades = new ArrayList<>();
                                    double sum=0;

                                    if(dataSnapshot.getValue(StoreGrade.class) != null){
                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                            grades.add(userSnapshot.getValue(StoreGrade.class));
                                        }

                                        for(StoreGrade i : grades){
                                            sum+=i.getScore();
                                        }
                                        double value = sum/grades.size();

                                        i.putExtra("storeGrade",Double.toString(Double.parseDouble(String.format("%.2f",value))));
                                        i.putExtra("gradeCnt",Integer.toString(grades.size()));
                                        setResult(RESULT_OK,i);
                                        finish();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}