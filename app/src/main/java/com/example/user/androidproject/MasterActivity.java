package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
    private Button logoutButton,findOrderButton,changeStoreNameButton,SearchMenuButton;
    private Intent intent;
    private User user;
    private TextView storeName,storeGrade;
    private EditText menuName;
    private RelativeLayout layout;
    private TableLayout table;
    private DatabaseReference mDatabase;

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
        table = findViewById(R.id.table);
        SearchMenuButton = findViewById(R.id.SearchMenuButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        logoutButton.setOnClickListener(this);
        findOrderButton.setOnClickListener(this);
        layout.setOnClickListener(this);
        changeStoreNameButton.setOnClickListener(this);
        SearchMenuButton.setOnClickListener(this);

        drawTable();
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
        }else if(view == SearchMenuButton){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(menuName.getWindowToken(), 0);
            drawTable();
        }
    }

    private void drawTable(){
        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }
        if(menuName.getText().length() == 0){
            mDatabase.child("menu").orderByChild("storeId").equalTo(user.getStoreId()).addListenerForSingleValueEvent(this);
            return;
        }
        mDatabase.child("menu").orderByChild("storeIdMenuName").equalTo(user.getStoreId()+"_"+menuName.getText().toString()).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue(Menu.class) != null){
            TableRow.LayoutParams lp;
            TextView menuCode,menuName,price,country;
            Menu menu;

            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                menu = userSnapshot.getValue(Menu.class);

                TableRow row = new TableRow(getApplication());
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                row.setWeightSum(10f);

                menuCode = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, 0, 0f);
                lp.setMargins(0,70,0,0);
                menuCode.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    menuCode.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                menuCode.setText(menu.getMenuCode());
                menuCode.setLayoutParams(lp);

                menuName = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.3f);
                lp.setMargins(0,70,0,0);
                menuName.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    menuName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                menuName.setText(menu.getMenuName());
                menuName.setLayoutParams(lp);

                price = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.3f);
                lp.setMargins(0,70,0,0);
                price.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                price.setText(Integer.toString(menu.getPrice()));
                price.setLayoutParams(lp);

                country = new TextView(getApplication());
                lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.4f);
                lp.setMargins(0,70,0,0);
                country.setTextSize(14f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    country.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                country.setText(menu.getCountry());
                country.setLayoutParams(lp);

                row.addView(menuCode);
                row.addView(menuName);
                row.addView(price);
                row.addView(country);
                table.addView(row);

                int rowNumCount = table.getChildCount();
                for(int count = 1; count < rowNumCount; count++) {
                    View v = table.getChildAt(count);
                    if(v instanceof TableRow) {
                        TableRow clickRow = (TableRow)v;

                        clickRow.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Menu m = new Menu();

                                TableRow row = (TableRow)v;
                                TextView menuCode = (TextView)row.getChildAt(0);
                                TextView menuName = (TextView)row.getChildAt(1);
                                TextView price = (TextView)row.getChildAt(2);
                                TextView country = (TextView)row.getChildAt(3);
                                m.setMenuCode(menuCode.getText().toString());
                                m.setMenuName(menuName.getText().toString());
                                m.setPrice(Integer.parseInt(price.getText().toString()));
                                m.setCountry(country.getText().toString());

                                intent = new Intent(getApplicationContext(),MainActivity.class);
                                intent.putExtra("menuInfo",m);
                                intent.putExtra("user",user);
                                startActivity(intent);
                            }
                        });
                    }
                }

            }
        } else {
            Toast.makeText(getApplicationContext(),"메뉴가 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) { }
}