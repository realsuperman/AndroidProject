package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TableOrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,ValueEventListener, View.OnClickListener {
    private Spinner floor;
    //private String storeId="tjdgns461";
    private String storeId = null;
    private Integer[] floorItem;
    DatabaseReference mDatabase;
    private User user;
    private ArrayAdapter<Integer> adapter;
    private TableLayout table;
    private Intent intent;
    private Button insertFloor;
    //private String flag = "customer";
    private String flag = null;

    // 고객이 예약하기 클릭시 storeId는 클릭된 매장의 storeId를 넘기고 flag를 customer로 넘긴다
    // 점주가 예약하기 클릭시 storeId는 자신의 매장의 storeId를 넘기고 flag를 master로 넘긴다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_order);
        intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        flag = intent.getStringExtra("flag"); // flag가 customer인지 아닌지

        floor = findViewById(R.id.floor);
        table = findViewById(R.id.table);
        insertFloor = findViewById(R.id.insertFloor);

        if("customer".equalsIgnoreCase(flag)){
            TextView plain1 = findViewById(R.id.plain1);
            TextView phone = findViewById(R.id.phone);
            TextView orderOk1 = findViewById(R.id.orderOk1);
            plain1.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            orderOk1.setVisibility(View.GONE);
            insertFloor.setVisibility(View.GONE);
        }else{
            TextView plain2 = findViewById(R.id.plain2);
            TextView orderOk2 = findViewById(R.id.orderOk2);
            plain2.setVisibility(View.GONE);
            orderOk2.setVisibility(View.GONE);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        initSpinner();


        floor.setOnItemSelectedListener(this);
        insertFloor.setOnClickListener(this);
    }

    private void initSpinner() {
        mDatabase.child("user").orderByChild("storeId").equalTo(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(User.class) != null){
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        user = userSnapshot.getValue(User.class);
                    }
                    floorItem = new Integer[user.getFloor()];
                    for(int i=0;i<user.getFloor();i++) floorItem[i] = i+1;
                    adapter = new ArrayAdapter<>(getApplication(),android.R.layout.simple_spinner_item,floorItem);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    floor.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(),"알 수 없는 에러",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        table.removeViews(0, table.getChildCount());

        String storeIdFloor = storeId+"_"+adapterView.getItemAtPosition(i).toString();
        mDatabase.child("tableOrder").orderByChild("storeIdFloor").equalTo(storeIdFloor).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(getApplicationContext(),TableInformationActivity.class);
        intent.putExtra("flag","0");
        intent.putExtra("floor",floor.getSelectedItem().toString());
        intent.putExtra("storeId",storeId);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue(TableOrder.class) != null){
            TableRow.LayoutParams lp;
            TextView floor,seatName,seatExplain,orderYn,customerPhone,orderCode;
            TableOrder tableOrder;

            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                tableOrder = userSnapshot.getValue(TableOrder.class);

                TableRow row = new TableRow(getApplication());
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                row.setWeightSum(10f);

                floor = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp.setMargins(0,70,0,0);
                floor.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    floor.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                floor.setText(Integer.toString(tableOrder.getFloor()));
                floor.setLayoutParams(lp);

                seatName = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
                lp.setMargins(0,70,0,0);
                seatName.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    seatName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                seatName.setText(tableOrder.getSeatName());
                seatName.setLayoutParams(lp);

                seatExplain = new TextView(getApplication());
                if(flag.equalsIgnoreCase("customer")){
                    lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 5.7f);
                }else{
                    lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.5f);
                }
                lp.setMargins(0,70,0,0);
                seatExplain.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    seatExplain.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                seatExplain.setText(tableOrder.getSeatExplain());
                seatExplain.setLayoutParams(lp);

                orderYn = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f);
                lp.setMargins(0,70,0,0);
                orderYn.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    orderYn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                if("y".equalsIgnoreCase(tableOrder.getOrderYn())) orderYn.setText("N");
                else orderYn.setText("Y");

                if(flag.equalsIgnoreCase("master")) orderYn.setText(tableOrder.getOrderYn());
                orderYn.setLayoutParams(lp);

                customerPhone = new TextView(getApplication());
                if(flag.equalsIgnoreCase("customer")){
                    lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0f);
                }else{
                    lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2.2f);
                }
                lp.setMargins(0,70,0,0);
                customerPhone.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    customerPhone.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                if("y".equalsIgnoreCase(orderYn.getText().toString())) {
                    customerPhone.setText(tableOrder.getCustomerPhone());
                }
                customerPhone.setLayoutParams(lp);

                orderCode = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0f);
                lp.setMargins(0,0,0,0);
                orderCode.setTextSize(0f);
                orderCode.setText(tableOrder.getOrderCode());
                orderCode.setLayoutParams(lp);

                row.addView(floor);
                row.addView(seatName);
                row.addView(seatExplain);
                row.addView(orderYn);
                row.addView(customerPhone);
                row.addView(orderCode);
                table.addView(row);

                int rowNumCount = table.getChildCount();
                for(int count = 0; count < rowNumCount; count++) {
                    View v = table.getChildAt(count);
                    if(v instanceof TableRow) {
                        TableRow clickRow = (TableRow)v;

                        clickRow.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                TableOrder t = new TableOrder();

                                TableRow row = (TableRow)v;
                                TextView floor = (TextView)row.getChildAt(0);
                                TextView setName = (TextView)row.getChildAt(1);
                                TextView seatExplain = (TextView)row.getChildAt(2);
                                TextView orderYn = (TextView)row.getChildAt(3);
                                TextView customerPhone = (TextView)row.getChildAt(4);
                                TextView orderCode = (TextView)row.getChildAt(5);
                                t.setFloor(Integer.parseInt(floor.getText().toString()));
                                t.setSeatName(setName.getText().toString());
                                t.setSeatExplain(seatExplain.getText().toString());
                                t.setOrderYn(orderYn.getText().toString());
                                t.setCustomerPhone(customerPhone.getText().toString());
                                t.setOrderCode(orderCode.getText().toString());

                                if("master".equalsIgnoreCase(flag)){
                                    intent = new Intent(getApplicationContext(),TableInformationActivity.class);
                                }else if("customer".equalsIgnoreCase(flag)){
                                    intent = new Intent(getApplicationContext(),OrderActivity.class);
                                }
                                intent.putExtra("TableInfo",t);
                                intent.putExtra("storeId",storeId);
                                intent.putExtra("flag","1"); // 예약하기 화면에서 사용할 플래그 및 점주 등록,수정 구분

                                if("N".equalsIgnoreCase(t.getOrderYn())&&"customer".equalsIgnoreCase(flag)){
                                    Toast.makeText(getApplicationContext(),"해당 테이블은 예약가능하지 않습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                startActivity(intent);
                            }
                        });
                    }
                }

            }
        } else {
            Toast.makeText(getApplicationContext(),"해당 층에 등록된 테이블이 없어요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {}
}