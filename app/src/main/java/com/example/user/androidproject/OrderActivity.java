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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout layout;
    private TextView choiceFloor,choiceSeatName,choiceSeatPlain,order1,order2;
    private EditText insertPhone;
    private Intent intent;
    private String flag;
    private Button genericButton;
    private TableOrder tableOrder;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        intent = getIntent();
        layout = findViewById(R.id.layout);
        choiceFloor = findViewById(R.id.choiceFloor);
        choiceSeatName = findViewById(R.id.choiceSeatName);
        choiceSeatPlain = findViewById(R.id.choiceSeatPlain);
        insertPhone = findViewById(R.id.insertPhone);
        order1 = findViewById(R.id.order1);
        order2 = findViewById(R.id.order2);
        genericButton = findViewById(R.id.genericButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        flag = intent.getStringExtra("flag");
        if(!"1".equalsIgnoreCase(flag)){
            order1.setVisibility(View.VISIBLE);
            order2.setVisibility(View.VISIBLE);
            genericButton.setText("돌아가기");
            insertPhone.setEnabled(false);
        }

        tableOrder = (TableOrder)intent.getSerializableExtra("TableInfo");
        choiceFloor.setText(tableOrder.getFloor()+"층");
        choiceSeatName.setText(tableOrder.getSeatName());
        choiceSeatPlain.setText(tableOrder.getSeatExplain());
        insertPhone.setText(tableOrder.getCustomerPhone());

        genericButton.setOnClickListener(this);
        layout.setOnClickListener(this);
        // flag == 1이면 customer cell click이다. flag == 0이면 예약 성공 페이지이다.
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(insertPhone.getWindowToken(), 0);
        }else if(view == genericButton && "1".equalsIgnoreCase(flag)){ // 예약하기 버튼 클릭시
            if(insertPhone.getText().length() < 9){
                Toast.makeText(getApplicationContext(),"휴대폰 정보를 정확하게 입력하세요",Toast.LENGTH_SHORT).show();
                return;
            }
            TableOrder update = new TableOrder();
            update.setFloor(tableOrder.getFloor());
            update.setSeatName(tableOrder.getSeatName());
            update.setSeatExplain(tableOrder.getSeatExplain());
            update.setOrderYn("Y");
            update.setCustomerPhone(insertPhone.getText().toString());
            update.setStoreIdFloor(intent.getStringExtra("storeId")+"_"+tableOrder.getFloor());
            update.setOrderCode(tableOrder.getOrderCode());
            mDatabase.child("tableOrder/"+tableOrder.getOrderCode()).setValue(update);

            intent = new Intent(this,OrderActivity.class);
            intent.putExtra("TableInfo",update);
            intent.putExtra("flag","0");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }else{
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            startActivity(intent);//액티비티 띄우기
        }else if(view == genericButton && !"1".equalsIgnoreCase(flag)){ // 돌아가기 버튼 클릭시
            intent = new Intent(this,MainActivity.class);
            startActivity(intent);//액티비티 띄우기
        }
    }
}