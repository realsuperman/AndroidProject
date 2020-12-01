package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout layout;
    private EditText menuName,price,country;
    private Intent intent;
    private int flag=0;
    private User user;
    private Menu menu;
    private Button updateMenuButton,deleteMenuButton,insertImageButton;
    private boolean check=false;
    private ImageView img_view;
    private Uri filePath = null;
    private DatabaseReference mDatabase;
    private String filename,menuCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        layout = findViewById(R.id.layout);
        menuName = findViewById(R.id.menuName);
        price = findViewById(R.id.price);
        country = findViewById(R.id.country);
        updateMenuButton = findViewById(R.id.updateMenuButton);
        deleteMenuButton = findViewById(R.id.deleteMenuButton);
        insertImageButton = findViewById(R.id.insertImageButton);
        img_view = findViewById(R.id.img_view);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();
        user = (User)intent.getSerializableExtra("user");
        if((Menu)intent.getSerializableExtra("menuInfo")!=null){
            flag = 1;
            menu = (Menu)intent.getSerializableExtra("menuInfo");
            loadImage(menu.getLogo());
            insertImageButton.setText("로고수정");
        }else{
            deleteMenuButton.setText("등록하기");
            updateMenuButton.setVisibility(View.INVISIBLE);
        }

        layout.setOnClickListener(this);
        insertImageButton.setOnClickListener(this);
        updateMenuButton.setOnClickListener(this);
        deleteMenuButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == layout){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(menuName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(price.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(country.getWindowToken(), 0);
        }else if(view==insertImageButton) {
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
        }else if(view == updateMenuButton || (view == deleteMenuButton && flag==0)){
            if(!check()) return;
            if(filePath==null&&flag==1){
                insertMenu();
            }else { // 이미지를 수정한 경우 or 등록하는 경우
                uploadFile();
            }
        }else if(view == deleteMenuButton && flag==1){ // 메뉴 삭제시
            mDatabase.child("menu/"+menu.getMenuCode()).removeValue();
            changeActiviry();
        }
    }

    private void loadImage(String imgName) { // 이미지 불러오는 법 알려줌.
        if(check){
            return;
        }
        check = true;
        System.out.println("images/"+imgName);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://dlc-team.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("images/"+imgName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(img_view);
                menuName.setText(menu.getMenuName());
                price.setText(Integer.toString(menu.getPrice()));
                country.setText(menu.getCountry());
                filename = menu.getLogo();
                menuCode = menu.getMenuCode();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "수정하기 기능에 에러가 있습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img_view.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean check(){
        if(menuName.length() == 0 ){
            Toast.makeText(getApplicationContext(),"메뉴명을 입력해주세요",Toast.LENGTH_SHORT).show();
            menuName.requestFocus();
            return false;
        }
        if(price.length() == 0){
            Toast.makeText(getApplicationContext(),"가격을 입력하세요",Toast.LENGTH_SHORT).show();
            price.requestFocus();
            return false;
        }
        if(country.length() == 0){
            Toast.makeText(getApplicationContext(),"원산지를 입력하세요",Toast.LENGTH_SHORT).show();
            country.requestFocus();
            return false;
        }
        if(filePath==null&&flag!=1){
            Toast.makeText(getApplicationContext(),"로고를 등록하세요.",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void changeActiviry(){
        intent = new Intent(this,MasterActivity.class); // 박준영씨 작업 완료시 그 페이지로 이동하게끔
        intent.putExtra("user",user);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }else{
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);//액티비티 띄우기
    }

    private void insertMenu(){
        Menu insertMenu = new Menu();
        insertMenu.setCountry(country.getText().toString());
        insertMenu.setLogo(filename);
        insertMenu.setMenuCode(menuCode);
        insertMenu.setMenuName(menuName.getText().toString());
        insertMenu.setPrice(Integer.parseInt(price.getText().toString()));
        insertMenu.setStoreId(user.getStoreId());
        insertMenu.setStoreIdMenuCode(user.getStoreId()+"_"+menuCode);
        insertMenu.setStoreIdMenuName(user.getStoreId()+"_"+menuName.getText().toString());
        mDatabase.child("menu/"+menuCode).setValue(insertMenu);

        changeActiviry();
    }

    private void uploadFile() {
        //if (filePath != null) {
        //업로드 진행 Dialog 보이기
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("요청 기능 수행 중...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        //storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Unique한 파일명을 만들자.
        long millis = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
        Date now = new Date();
        filename = formatter.format(now) + ".png";
        if(flag!=1) menuCode = Long.toString(millis);
        //storage 주소와 폴더 파일명을 지정해 준다.
        StorageReference storageRef = storage.getReferenceFromUrl("gs://dlc-team.appspot.com").child("images/" + filename);
        //올라가거라...
        storageRef.putFile(filePath)
                //성공시
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                        insertMenu();
                    }
                })
                //실패시
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "요청 실패", Toast.LENGTH_SHORT).show();
                    }
                })
                //진행중
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                    }
                });
        //}
        //else {
        //    Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        //}
    }

}