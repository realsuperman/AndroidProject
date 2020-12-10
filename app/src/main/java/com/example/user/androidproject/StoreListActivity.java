package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class StoreListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener, ValueEventListener , AdapterView.OnItemSelectedListener {
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
    private boolean flag = true,nullCheck=true; // flag가 true이면 등록안됨 눌러진 상태, flag가 false이면 등록됨이 눌러진 상태
    private Spinner alignSpinner;
    private ProgressDialog progressDialog;
    String[] orderItem={"매장명","별점"};
    private ArrayList<User> items = new ArrayList<>();

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
        alignSpinner.setOnItemSelectedListener(this);

        noRegistration(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!flag && nullCheck){
            //items = new ArrayList<>();
            if(storeName.getText().length()!=0){
                registration(storeName.getText().toString());
            }else{
                registration(null);
            }
        }
    }

    private void noRegistration(String text){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("요청 기능 수행 중...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        alignLayout.setVisibility(View.INVISIBLE);
        if(text!=null){
            //mDatabase.child("store").orderByChild("storeNameCategory").equalTo(text+"_"+type).addListenerForSingleValueEvent(this);
            mDatabase.child("store").orderByChild("storeName").startAt(text).endAt(text+"\uf8ff").addListenerForSingleValueEvent(this);
        }else{
            mDatabase.child("store").orderByChild("category").equalTo(type).addListenerForSingleValueEvent(this);
        }
    }

    private void registration(String text){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("요청 기능 수행 중...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        alignLayout.setVisibility(View.VISIBLE);
        if(text!=null){
            //mDatabase.child("user").orderByChild("storeNameCategory").equalTo(text+"_"+type).addListenerForSingleValueEvent(this);
            mDatabase.child("user").orderByChild("storeName").startAt(text).endAt(text+"\uf8ff").addListenerForSingleValueEvent(this);
        }else{
            mDatabase.child("user").orderByChild("category").equalTo(type).addListenerForSingleValueEvent(this);
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
            nullCheck = true;
            registration(null);
        }else if(view == searchButton){
            keyBoardHide();
            //items = new ArrayList<>();
            if(flag){
                if(storeName.getText().length()!=0){
                    noRegistration(storeName.getText().toString());
                }else{
                    noRegistration(null);
                }

            }else{
                if(storeName.getText().length()!=0){
                    registration(storeName.getText().toString());
                }else{
                    registration(null);
                }
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
        if("store".equalsIgnoreCase(dataSnapshot.getKey())) {
            adapter = new StoreAdapter();
            List<Store> store = new ArrayList<>();
            boolean c = false;

            if (dataSnapshot.getValue(Store.class) != null) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    store.add(userSnapshot.getValue(Store.class));
                }

                for (Store s : store) {
                    if(type.equalsIgnoreCase(s.getCategory())){
                        c = true;
                    }
                }
                if(!c) Toast.makeText(getApplicationContext(), "해당되는 매장 정보가 없습니다.", Toast.LENGTH_SHORT).show();

                for (Store s : store) {
                    int image;

                    if (type.equals("한식")) {
                        image = R.drawable.korean;
                    } else if (type.equals("분식")) {
                        image = R.drawable.snack;
                    } else if (type.equals("중식")) {
                        image = R.drawable.china;
                    } else if (type.equals("치킨")) {
                        image = R.drawable.chicken;
                    } else if (type.equals("피자")) {
                        image = R.drawable.pizza;
                    } else {
                        image = R.drawable.beer;
                    }

                    Store str = new Store(s.getStoreName(), s.getPhone(), s.getCategory(), image, s.getStoreNameCategory());
                    if(type.equalsIgnoreCase(s.getCategory())) adapter.addItem(str);
                }
            } else {
                Toast.makeText(getApplicationContext(), "해당되는 매장 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            }

            listview.setAdapter(adapter);
            progressDialog.dismiss();
        }else if("user".equalsIgnoreCase(dataSnapshot.getKey())){
            userAdapter = new UserAdapter();
            List<User> user = new ArrayList<>();
            items = new ArrayList<>();
            boolean c = false;

            if(dataSnapshot.getValue(User.class) != null){
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    user.add(userSnapshot.getValue(User.class));
                }

                for (User u : user) {
                    if(type.equalsIgnoreCase(u.getCategory())){
                        c = true;
                    }
                    if(c) break;
                }
                if(!c) Toast.makeText(getApplicationContext(), "해당되는 매장 정보가 없습니다.", Toast.LENGTH_SHORT).show();

                for(final User s : user){
                    mDatabase.child("storeGrade").orderByChild("storeId").equalTo(s.getStoreId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            nullCheck = true;
                            List<StoreGrade> grades = new ArrayList<>();
                            double sum = 0,value;
                            //boolean check = false;

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
                                str.setPhone(s.getPhone());
                                str.setGrade(value);
                                str.setGradeCnt(grades.size());
                                if(type.equalsIgnoreCase(s.getCategory())) {
                                    userAdapter.addItem(str);
                                    listview.setAdapter(userAdapter);
                                    //check = true;
                                }else{
                                    listview.setAdapter(userAdapter);
                                    progressDialog.dismiss();
                                }
                            }else{
                                User str = new User();
                                str.setStoreName(s.getStoreName());
                                str.setStoreId(s.getStoreId());
                                str.setLogo(s.getLogo());
                                str.setPhone(s.getPhone());
                                str.setGrade(0);
                                if(type.equalsIgnoreCase(s.getCategory())) {
                                    userAdapter.addItem(str);
                                    listview.setAdapter(userAdapter);
                                    //check = true;
                                }else{
                                    listview.setAdapter(userAdapter);
                                    progressDialog.dismiss();
                                }
                            }

                            changeListView(alignSpinner.getSelectedItem().toString());
                            //System.out.println(check);
                            //if(!check){
                            //    Toast.makeText(getApplicationContext(),"해당되는 매장 정보가 없습니다!.",Toast.LENGTH_SHORT).show();
                            //}
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            } else {
                Toast.makeText(getApplicationContext(),"해당되는 매장 정보가 없습니다.",Toast.LENGTH_SHORT).show();
                nullCheck = false;
                listview.setAdapter(userAdapter);
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) { progressDialog.dismiss(); }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(!flag && items.size()>0) {
            changeListView(adapterView.getItemAtPosition(i).toString());
        }
    }

    private void changeListView(String t){
        String type = t;
        List<User> myArrayData = new ArrayList<>();
        Comparator<User> comperator;

        for (int index = 0; index < items.size(); index++) {
            myArrayData.add(items.get(index));
        }

        if("매장명".equalsIgnoreCase(type)){
            comperator = new Comparator<User>() {
                @Override
                public int compare(User object1, User object2) {
                    return object1.getStoreName().compareToIgnoreCase(object2.getStoreName());
                    //return (object1.getGrade() < object2.getGrade() ? 1: -1);
                }
            };
        }else{
            comperator = new Comparator<User>() {
                @Override
                public int compare(User object1, User object2) {
                    //return object1.getStoreName().compareToIgnoreCase(object2.getStoreName());
                    return (object1.getGrade() < object2.getGrade() ? 1: -1);
                }
            };
        }
        Collections.sort(myArrayData, comperator);
        userAdapter = new UserAdapter();
        items = new ArrayList<>();

        for(User s : myArrayData){
            User str = new User();
            str.setStoreName(s.getStoreName());
            str.setStoreId(s.getStoreId());
            str.setLogo(s.getLogo());
            str.setPhone(s.getPhone());
            str.setGrade(s.getGrade());
            str.setGradeCnt(s.getGradeCnt());

            userAdapter.addItem(str);
        }

        listview.setAdapter(userAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

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
        //ArrayList<User> items = new ArrayList<>();

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
            final String logo = item.getLogo();
            final String phone = item.getPhone();
            final String gradeCnt = Integer.toString(item.getGradeCnt());

            view.setName(storeNm);
            view.setGrade(Double.toString(Double.parseDouble(String.format("%.2f",grade))));
            view.setImageFromDataBase(getApplicationContext(),progressDialog,logo);
            view.setStoreCd(storeCd);
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
            view.setClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(getApplicationContext(),StoreDetailActivity.class);
                    intent.putExtra("storeId",storeCd);
                    intent.putExtra("storeName",storeNm);
                    intent.putExtra("storeGrade",Double.toString(Double.parseDouble(String.format("%.2f",grade))));
                    intent.putExtra("storePhone",phone);
                    intent.putExtra("gradeCnt",gradeCnt);
                    intent.putExtra("logo",logo);

                    startActivity(intent);//액티비티 띄우기
                }
            });
            return view;
        }
    }
}