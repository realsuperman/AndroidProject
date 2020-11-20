package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TableInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    DatabaseReference mDatabase;
    private String[] floorItem;
    private ArrayAdapter<String> adapter;
    private String storeId;
    private User user;
    private Spinner floor,orderYn;
    private RelativeLayout relativeLayout;
    private TextView seatName,explain,phone;
    String[] orderItem={"Y","N"};
    private TableOrder table;
    private LinearLayout phoneLayout,orderYnLayout;
    private Button updateButton,genericButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_information);
        floor = findViewById(R.id.floor);
        orderYn = findViewById(R.id.orderYn);
        relativeLayout = findViewById(R.id.layout);
        seatName = findViewById(R.id.seatName);
        explain = findViewById(R.id.explain);
        phone = findViewById(R.id.phone);
        phoneLayout = findViewById(R.id.phoneLayout);
        orderYnLayout = findViewById(R.id.orderYnLayout);
        updateButton = findViewById(R.id.updateButton);
        genericButton = findViewById(R.id.genericButton);

        intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initSpinner();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,orderItem);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderYn.setAdapter(adapter);

        String flag = intent.getStringExtra("flag");
        if("0".equalsIgnoreCase(flag)){ // 추가버튼 클릭시
            orderYnLayout.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.INVISIBLE);
            genericButton.setText("테이블 등록하기");
        }else{ // 수정될시
            table = (TableOrder)intent.getSerializableExtra("TableInfo");
            int index=getIndex(orderYn, table.getOrderYn());
            seatName.setText(table.getSeatName());
            explain.setText(table.getSeatExplain());
            orderYn.setSelection(index);
            phone.setText(table.getCustomerPhone());
            if(index==0) phoneLayout.setVisibility(View.VISIBLE);
        }


        relativeLayout.setOnClickListener(this);
    }

    private void initSpinner() {
        mDatabase.child("user").orderByChild("storeId").equalTo(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(User.class) != null){
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        user = userSnapshot.getValue(User.class);
                    }
                    floorItem = new String[user.getFloor()];
                    for(int i=0;i<user.getFloor();i++) floorItem[i] = Integer.toString(i+1);
                    adapter = new ArrayAdapter<>(getApplication(),android.R.layout.simple_spinner_item,floorItem);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    floor.setAdapter(adapter);

                    if("0".equalsIgnoreCase(intent.getStringExtra("flag"))) { // 추가버튼 클릭시
                        floor.setSelection(getIndex(floor, intent.getStringExtra("floor")));
                    }else{
                        floor.setSelection(getIndex(floor, Integer.toString(table.getFloor())));
                        floor.setEnabled(false);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"알 수 없는 에러",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onClick(View view) {
        if(view == relativeLayout){
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(seatName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(explain.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(floor.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(orderYn.getWindowToken(), 0);
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }
}