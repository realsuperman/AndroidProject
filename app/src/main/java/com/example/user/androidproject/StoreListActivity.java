package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StoreListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener, ValueEventListener {
    private LinearLayout alignLayout;
    private StoreAdapter adapter;
    private UserAdapter userAdapter;
    private ListView listview;
    private Intent intent;
    private String type;
    private DatabaseReference mDatabase;
    private RelativeLayout layout;
    private EditText storeName;
    private Button noRegistrationButton,RegistrationButton,searchButton;
    private boolean flag = true; // flag가 true이면 등록안됨 눌러진 상태, flag가 false이면 등록됨이 눌러진 상태
    private Spinner alignSpinner;
    String[] orderItem={"매장명","별점"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        listview = findViewById(R.id.listView);
        layout = findViewById(R.id.layout);
        storeName = findViewById(R.id.storeName);
        noRegistrationButton = findViewById(R.id.noRegistrationButton);
        RegistrationButton = findViewById(R.id.RegistrationButton);
        searchButton = findViewById(R.id.searchButton);
        alignLayout = findViewById(R.id.alignLayout);
        alignSpinner = findViewById(R.id.alignSpinner);

        intent = getIntent();
        type = intent.getStringExtra("type");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        layout.setOnClickListener(this);
        noRegistrationButton.setOnClickListener(this);
        RegistrationButton.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        searchButton.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,orderItem);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alignSpinner.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noRegistration(null);
    }

    private void noRegistration(String text){
        alignLayout.setVisibility(View.INVISIBLE);
        if(text!=null){
            mDatabase.child("store").orderByChild("storeNameCategory").equalTo(text+"_"+type).addListenerForSingleValueEvent(this);
        }else{
            mDatabase.child("store").orderByChild("category").equalTo(type).addListenerForSingleValueEvent(this);
        }
    }

    private void registration(String text){
        alignLayout.setVisibility(View.VISIBLE);
        if(text!=null){
            mDatabase.child("user").orderByChild("storeNameCategory").equalTo(text+"_"+type).addListenerForSingleValueEvent(this);
        }else{
            mDatabase.child("user").orderByChild("category").equalTo(type).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userAdapter = new UserAdapter();
                    List<User> user = new ArrayList<>();

                    if(dataSnapshot.getValue(User.class) != null){
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            user.add(userSnapshot.getValue(User.class));
                        }

                        for(final User s : user){
                            mDatabase.child("storeGrade").orderByChild("storeId").equalTo(s.getStoreId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<StoreGrade> grades = new ArrayList<>();
                                    double sum = 0,value;

                                    if(dataSnapshot.getValue(StoreGrade.class) != null){
                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                            grades.add(userSnapshot.getValue(StoreGrade.class));
                                        }

                                        for(StoreGrade i : grades){
                                            sum+=i.getScore();
                                        }
                                        value = sum/grades.size();

                                        User str = new User();
                                        str.setStoreName(s.getStoreName());
                                        str.setStoreId(s.getStoreId());
                                        str.setLogo(s.getLogo());
                                        str.setGrade(value);
                                        userAdapter.addItem(str);
                                        listview.setAdapter(userAdapter);
                                    }else{ }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                    } else {Toast.makeText(getApplicationContext(),"해당되는 매장 정보가 없습니다.",Toast.LENGTH_SHORT).show();}

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Store item = (Store)adapter.getItem(i);
        Toast.makeText(getApplicationContext(),"선택"+item.getStoreName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            keyBoardHide();
        }else if(view == noRegistrationButton){
            keyBoardHide();
            flag = true;
            storeName.setText("");
            noRegistration(null);
        }else if(view == RegistrationButton){
            keyBoardHide();
            flag = false;
            storeName.setText("");
            registration(null);
        }else if(view == searchButton){
            keyBoardHide();
            if(flag){
                if(storeName.getText().length()!=0){
                    noRegistration(storeName.getText().toString());
                }else{
                    noRegistration(null);
                }

            }else{

            }
        }
    }

    private void keyBoardHide(){
        InputMethodManager imm;
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(storeName.getWindowToken(), 0);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        adapter = new StoreAdapter();
        List<Store> store = new ArrayList<>();

        if(dataSnapshot.getValue(Store.class) != null){
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
        } else {Toast.makeText(getApplicationContext(),"해당되는 매장 정보가 없습니다.",Toast.LENGTH_SHORT).show();}

        listview.setAdapter(adapter);
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
            view.setImage(item.getImageView());
            view.setVisible(flag);
            final String phone = item.getPhone();
            view.setButton("전화하기 ☎", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:/"+phone));
                    startActivity(mIntent);
                }
            });
            return view;
        }
    }

    class UserAdapter extends BaseAdapter {
        ArrayList<User> items = new ArrayList<>();

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

        public void addItem(User item){
            items.add(item);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            StoreAcitvity view = new StoreAcitvity(getApplicationContext());

            User item = items.get(i);
            final String storeCd = item.getStoreId();
            final String storeNm = item.getStoreName();
            final double grade = item.getGrade();

            view.setName(storeNm);
            view.setGrade(Double.toString(grade));
            view.setVisible(flag);

            view.setButton("예약하기", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(getApplicationContext(),TableOrderActivity.class);
                    intent.putExtra("storeId",storeCd);
                    intent.putExtra("flag","customer");
                    startActivity(intent);//액티비티 띄우기
                }
            });

            return view;
        }
    }
}