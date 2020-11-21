package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class TableInformationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    private Intent intent;
    DatabaseReference mDatabase;
    private String[] floorItem;
    private ArrayAdapter<String> adapter;
    private String storeId,flag;
    private User user;
    private Spinner floor,orderYn;
    private RelativeLayout relativeLayout;
    private TextView seatName,explain,phone;
    String[] orderItem={"Y","N"};
    private TableOrder table;
    private LinearLayout phoneLayout,orderYnLayout;
    private Button updateButton,genericButton;
    private Boolean chk = false;

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

        flag = intent.getStringExtra("flag");
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
            if(index==0){
                phoneLayout.setVisibility(View.VISIBLE);
            }else if(index==1){
                orderYn.setEnabled(false);
            }
        }
        orderYn.setOnItemSelectedListener(this);
        relativeLayout.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        genericButton.setOnClickListener(this);
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
        }else if(view == updateButton){ // 수정시
            if(floor.getSelectedItem().toString() == null  ){
                Toast.makeText(getApplicationContext(),"층수는 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(seatName.getText().length() == 0){
                Toast.makeText(getApplicationContext(),"자리명은 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(explain.getText().length() == 0){
                Toast.makeText(getApplicationContext(),"설명은 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(orderYn.getSelectedItem().toString() == null){
                Toast.makeText(getApplicationContext(),"예약여부는 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                return;
            }

            TableOrder update = new TableOrder();
            update.setFloor(Integer.parseInt(floor.getSelectedItem().toString()));
            update.setSeatName(seatName.getText().toString());
            update.setSeatExplain(explain.getText().toString());
            update.setOrderYn(orderYn.getSelectedItem().toString());
            if("N".equalsIgnoreCase(orderYn.getSelectedItem().toString())) update.setCustomerPhone("");
            else update.setCustomerPhone(phone.getText().toString());
            update.setStoreIdFloor(storeId+"_"+Integer.parseInt(floor.getSelectedItem().toString()));
            update.setOrderCode(table.getOrderCode());
            mDatabase.child("tableOrder/"+table.getOrderCode()).setValue(update);

            Toast.makeText(getApplicationContext(),"수정 완료 하였습니다.",Toast.LENGTH_SHORT).show();
            Intent changeScreen = new Intent(getApplicationContext(),TableOrderActivity.class);
            changeScreen.putExtra("storeId",storeId);
            changeScreen.putExtra("flag","master");
            startActivity(changeScreen);
        }else if(view ==genericButton){ // 추가 or 삭제시
            if(!"0".equalsIgnoreCase(flag)){ // 삭제버튼 누를때
                mDatabase.child("tableOrder/"+table.getOrderCode()).removeValue();
                Toast.makeText(getApplicationContext(),"삭제 완료 하였습니다.",Toast.LENGTH_SHORT).show();
                Intent changeScreen = new Intent(getApplicationContext(),TableOrderActivity.class);
                changeScreen.putExtra("storeId",storeId);
                changeScreen.putExtra("flag","master");
                startActivity(changeScreen);
            }else{ // 추가에서 왔을때
                if(floor.getSelectedItem().toString() == null  ){
                    Toast.makeText(getApplicationContext(),"층수는 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(seatName.getText().length() == 0){
                    Toast.makeText(getApplicationContext(),"자리명은 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(explain.getText().length() == 0){
                    Toast.makeText(getApplicationContext(),"설명은 필수 입력 값 입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                long millis = System.currentTimeMillis();
                TableOrder insert = new TableOrder();
                insert.setFloor(Integer.parseInt(floor.getSelectedItem().toString()));
                insert.setSeatName(seatName.getText().toString());
                insert.setSeatExplain(explain.getText().toString());
                insert.setOrderYn("N");
                insert.setCustomerPhone("");
                insert.setStoreIdFloor(storeId+"_"+Integer.parseInt(floor.getSelectedItem().toString()));
                insert.setOrderCode(Long.toString(millis));
                mDatabase.child("tableOrder/"+millis).setValue(insert);
                Toast.makeText(getApplicationContext(),"등록 완료 하였습니다.",Toast.LENGTH_SHORT).show();
                Intent changeScreen = new Intent(getApplicationContext(),TableOrderActivity.class);
                changeScreen.putExtra("storeId",storeId);
                changeScreen.putExtra("flag","master");
                startActivity(changeScreen);
            }
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(chk){
            if(i==0){ // 예약한 번호 layout을 보여준다
                phoneLayout.setVisibility(View.VISIBLE);
            }else if(i==1){ // 예약한 번호 layout을 숨긴다.
                phoneLayout.setVisibility(View.INVISIBLE);
            }
        }
        chk = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
}