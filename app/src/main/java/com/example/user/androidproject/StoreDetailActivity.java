package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class StoreDetailActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
    private Intent intent;
    private ImageView imageView;
    private TextView storeName,storeGrade;
    private String phone,storeCd,logo;
    private Button gradeButton,phoneButton;
    private ListView listview;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        imageView = findViewById(R.id.imageView);
        storeName = findViewById(R.id.storeName);
        storeGrade = findViewById(R.id.storeGrade);
        gradeButton = findViewById(R.id.gradeButton);
        phoneButton = findViewById(R.id.phoneButton);
        listview = findViewById(R.id.listView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        intent = getIntent();
        storeCd = intent.getStringExtra("storeId");
        phone = intent.getStringExtra("storePhone");
        logo = intent.getStringExtra("logo");
        storeName.setText(intent.getStringExtra("storeName"));
        storeGrade.setText(intent.getStringExtra("storeGrade")+"점("+intent.getStringExtra("gradeCnt")+"명참여)");
        loadImage(logo);
        gradeButton.setOnClickListener(this);
        phoneButton.setOnClickListener(this);
        storeName.setOnClickListener(this);
    }

    /*@Override
    protected void onResume() {
        super.onResume();


        mDatabase.child("storeGrade").orderByChild("storeId").equalTo(storeCd).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    storeGrade.setText(Double.toString(Double.parseDouble(String.format("%.2f",value)))+"점("+grades.size()+"명참여)");
                }else{
                    storeGrade.setText("0.0점(0명 참여)");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        storeGrade.setText(intent.getStringExtra("storeGrade")+"점("+intent.getStringExtra("gradeCnt")+"명참여)");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234 && resultCode == RESULT_OK){
            storeGrade.setText(data.getStringExtra("storeGrade")+"점("+data.getStringExtra("gradeCnt")+"명참여)");
        }
    }

    @Override
    public void onClick(View view) {
        if(view == gradeButton){
            Intent ratingIntent = new Intent(getApplicationContext(),StoreRatingActivity.class);
            ratingIntent.putExtra("storeId",storeCd);
            startActivityForResult(ratingIntent,1234);
        }else if(view == phoneButton){
            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:/"+phone));
            startActivity(mIntent);
        }else if(view == storeName){
            storeName.setSelected(true);
        }
    }

    private void loadImage(String imgName) { // 이미지 불러오는 법 알려줌.
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("요청 기능 수행 중...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://dlc-team.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("images/"+imgName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imageView);
                loadMenu();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "사진 불러오기 실패.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMenu(){
        System.out.println(storeCd);
        mDatabase.child("menu").orderByChild("storeId").equalTo(storeCd).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        menuAdapter = new MenuAdapter();
        List<Menu> menu = new ArrayList<>();

        if (dataSnapshot.getValue(Menu.class) != null) {
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                menu.add(userSnapshot.getValue(Menu.class));
            }

            for (Menu s : menu) {
                System.out.println(s.getMenuCode());
                Menu m = new Menu();
                m.setMenuName(s.getMenuName());
                m.setPrice(s.getPrice());
                m.setLogo(s.getLogo());
                menuAdapter.addItem(m);
            }
        } else {
            Toast.makeText(getApplicationContext(), "해당 매장에 메뉴가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        listview.setAdapter(menuAdapter);
        progressDialog.dismiss();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) { Toast.makeText(getApplicationContext(), "해당 매장에 메뉴가 없습니다.", Toast.LENGTH_SHORT).show(); }

    class MenuAdapter extends BaseAdapter {
        ArrayList<Menu> items = new ArrayList<>();

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

        public void addItem(Menu item){
            items.add(item);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            StoreDetailInfo view = new StoreDetailInfo(getApplicationContext());

            Menu item = items.get(i);
            final String menuName = item.getMenuName();
            final String price = Integer.toString(item.getPrice());
            final String logo = item.getLogo();

            view.setName(menuName);
            view.setPrice(price);
            view.setImage(getApplicationContext(),progressDialog,logo);
            return view;
        }
    }
}