package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class TableOrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,ValueEventListener {
    private Spinner floor;
    private String storeId="tjdgns461";
    private Integer[] floorItem;
    DatabaseReference mDatabase;
    private User user;
    private ArrayAdapter<Integer> adapter;
    private TableLayout table;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_order);
        intent = getIntent();
        String flag = intent.getStringExtra("storeId");
        if(flag!=null){
            storeId = flag;
        }

        floor = findViewById(R.id.floor);
        table = findViewById(R.id.table);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        initSpinner();
        floor.setOnItemSelectedListener(this);
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
        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }

        String storeIdFloor = storeId+"_"+adapterView.getItemAtPosition(i).toString();
        mDatabase.child("tableOrder").orderByChild("storeIdFloor").equalTo(storeIdFloor).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue(TableOrder.class) != null){
            TableRow.LayoutParams lp;
            TextView floor,seatName,seatExplain,orderYn,customerPhone;
            TableOrder tableOrder;

            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                tableOrder = userSnapshot.getValue(TableOrder.class);

                TableRow row = new TableRow(getApplication());
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                row.setWeightSum(10f);

                floor = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp.setMargins(0,50,0,0);
                floor.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    floor.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                floor.setText(Integer.toString(tableOrder.getFloor()));
                floor.setLayoutParams(lp);

                seatName = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
                lp.setMargins(0,50,0,0);
                seatName.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    seatName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                seatName.setText(tableOrder.getSeatName());
                seatName.setLayoutParams(lp);

                seatExplain = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.5f);
                lp.setMargins(0,50,0,0);
                seatExplain.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    seatExplain.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                seatExplain.setText(tableOrder.getSeatExplain());
                seatExplain.setLayoutParams(lp);

                orderYn = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f);
                lp.setMargins(0,50,0,0);
                orderYn.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    orderYn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                orderYn.setText(tableOrder.getOrderYn());
                orderYn.setLayoutParams(lp);

                customerPhone = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2.2f);
                lp.setMargins(0,50,0,0);
                customerPhone.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    customerPhone.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                if("y".equalsIgnoreCase(orderYn.getText().toString())) {
                    customerPhone.setText(tableOrder.getCustomerPhone());
                }
                customerPhone.setLayoutParams(lp);

                row.addView(floor);
                row.addView(seatName);
                row.addView(seatExplain);
                row.addView(orderYn);
                row.addView(customerPhone);
                table.addView(row);
            }
        } else {
            Toast.makeText(getApplicationContext(),"해당 층에 등록된 테이블이 없어요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {}
}