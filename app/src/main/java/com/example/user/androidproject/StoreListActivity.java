package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StoreListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener, ValueEventListener {
    private StoreAdapter adapter;
    private EditText editText;
    private EditText editText2;
    private ListView listview;
    private Intent intent;
    private String type;
    private DatabaseReference mDatabase;
    private RelativeLayout layout;
    private EditText storeName;
    private Button noRegistrationButton,RegistrationButton;
    private boolean flag = true; // flag가 true이면 등록안됨 눌러진 상태, flag가 false이면 등록됨이 눌러진 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        listview = findViewById(R.id.listView);
        layout = findViewById(R.id.layout);
        storeName = findViewById(R.id.storeName);
        noRegistrationButton = findViewById(R.id.noRegistrationButton);
        RegistrationButton = findViewById(R.id.RegistrationButton);

        adapter = new StoreAdapter();
        intent = getIntent();
        type = intent.getStringExtra("type");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        layout.setOnClickListener(this);
        noRegistrationButton.setOnClickListener(this);
        RegistrationButton.setOnClickListener(this);
        noRegistration();
    }

    private void noRegistration(){
        mDatabase.child("store").orderByChild("category").equalTo(type).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Store item = (Store)adapter.getItem(i);
        Toast.makeText(getApplicationContext(),"선택"+item.getStoreName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(storeName.getWindowToken(), 0);
        }else if(view == noRegistrationButton){
            flag = true;
            noRegistration();
        }else if(view == RegistrationButton){
            flag = false;

        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<Store> store = new ArrayList<>();

        if(dataSnapshot.getValue(User.class) != null){
            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                store.add(userSnapshot.getValue(Store.class));
            }

            for(Store s : store){
                int image;

                if(type.equals("한식")) {
                    image = R.drawable.korean;
                }else if(type.equals("분식")){
                    image = R.drawable.snack;
                }else if(type.equals("중식")){
                    image = R.drawable.china;
                }else if(type.equals("치킨")){
                    image = R.drawable.chicken;
                }else if(type.equals("피자")){
                    image = R.drawable.pizza;
                }else{
                    image = R.drawable.beer;
                }

                Store str = new Store(s.getStoreName(),s.getPhone(),s.getCategory(),image,s.getStoreNameCategory());
                adapter.addItem(str);

            }
        } else {}

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) { }

    class StoreAdapter extends BaseAdapter {
        ArrayList<Store> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void addItem(Store item){
            items.add(item);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            StoreAcitvity view = new StoreAcitvity(getApplicationContext());

            Store item = items.get(i);
            view.setName(item.getStoreName());
            view.setMobile(item.getPhone());
            view.setImage(item.getImageView());

            return view;
        }
    }
}