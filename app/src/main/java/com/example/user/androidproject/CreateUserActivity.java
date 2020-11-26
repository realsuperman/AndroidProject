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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener,View.OnFocusChangeListener {
    private Button createUserButton,insertImageButton,duplicateIdButton,duplicateTaxNoButton;
    private Intent intent = null;
    private RelativeLayout layout;
    private Spinner category,floor;
    private ImageView img_view;
    private boolean isDuplicateId = false ,isDuplicateTaxNo=false,check=false;
    private Uri filePath;
    private TextView id,pw,mail,tel,strNm,taxNo;
    private String filename;
    private DatabaseReference mDatabase;
    private User user;

    String[] categoryItem={"한식","분식","중식","치킨","피자","술집"};
    Integer[] floorItem={1,2,3,4,5,6,7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        createUserButton = findViewById(R.id.createUserButton);
        insertImageButton = findViewById(R.id.insertImageButton);
        duplicateIdButton = findViewById(R.id.duplicateIdButton);
        duplicateTaxNoButton=findViewById(R.id.duplicateTaxNoButton);
        layout = findViewById(R.id.layout);
        category = findViewById(R.id.category);
        floor = findViewById(R.id.floor);
        img_view = findViewById(R.id.img_view);
        id = findViewById(R.id.storeId);
        pw = findViewById(R.id.storePw);
        mail = findViewById(R.id.email);
        tel = findViewById(R.id.phone);
        strNm = findViewById(R.id.storeName);
        taxNo = findViewById(R.id.taxNo);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categoryItem);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,floorItem);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        floor.setAdapter(adapter2);
        category.setOnItemSelectedListener(this);
        floor.setOnItemSelectedListener(this);

        layout.setOnClickListener(this);
        createUserButton.setOnClickListener(this);
        insertImageButton.setOnClickListener(this);
        duplicateIdButton.setOnClickListener(this);
        duplicateTaxNoButton.setOnClickListener(this);

        id.setOnFocusChangeListener(this);
        taxNo.setOnFocusChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        intent = getIntent();
        if("1".equals(intent.getStringExtra("flag"))){ // 수정화면 이라면
            user = (User)intent.getSerializableExtra("user");
            loadImage(user.getLogo());
            filename = user.getLogo();
            isDuplicateId = true;
            isDuplicateTaxNo = true;
        }
    }

    @Override
    public void onClick(View view) {
        if(view==createUserButton){
            if(!check()) return;
            if(filePath==null && "1".equals(intent.getStringExtra("flag"))){ // 이미지 수정안한 경우.
                insertUser();
            }else { // 이미지를 수정한 경우 or 등록하는 경우
                uploadFile();
            }
        }else if(view==insertImageButton){
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);

        }else if(view==duplicateIdButton){
            checkDuplicate(1);
        }else if(view==duplicateTaxNoButton){
            checkDuplicate(2);
        }else if(view==layout){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
        }
    }

    private boolean check(){
        if(id.length() == 0 ){
            Toast.makeText(getApplicationContext(),"아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
            id.requestFocus();
            return false;
        }
        if(pw.length() == 0){
            Toast.makeText(getApplicationContext(),"패스워드를 입력해주세요",Toast.LENGTH_SHORT).show();
            pw.requestFocus();
            return false;
        }
        if(mail.length() == 0){
            Toast.makeText(getApplicationContext(),"이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
            mail.requestFocus();
            return false;
        }
        if(tel.length() == 0){
            Toast.makeText(getApplicationContext(),"전화번호를 입력해주세요",Toast.LENGTH_SHORT).show();
            tel.requestFocus();
            return false;
        }
        if(strNm.length() == 0){
            Toast.makeText(getApplicationContext(),"매장명을 입력해주세요",Toast.LENGTH_SHORT).show();
            strNm.requestFocus();
            return false;
        }
        if(taxNo.length() == 0){
            Toast.makeText(getApplicationContext(),"사업자번호를 입력해주세요",Toast.LENGTH_SHORT).show();
            taxNo.requestFocus();
            return false;
        }
        if(filePath==null&&(!"1".equals(intent.getStringExtra("flag")))){
            Toast.makeText(getApplicationContext(),"로고를 등록하세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isDuplicateId){
            Toast.makeText(getApplicationContext(),"아이디 중복체크를 다시 해주세요",Toast.LENGTH_SHORT).show();
            duplicateIdButton.requestFocus();
            return false;
        }
        if(!isDuplicateTaxNo){
            Toast.makeText(getApplicationContext(),"사업자 번호 중복체크를 다시 해주세요.",Toast.LENGTH_SHORT).show();
            duplicateTaxNoButton.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

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

    //upload the file
    private void uploadFile() {
        //if (filePath != null) {
        //업로드 진행 Dialog 보이기
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("요청 기능 수행 중...");
        progressDialog.show();

        //storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Unique한 파일명을 만들자.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
        Date now = new Date();
        filename = formatter.format(now) + ".png";
        //storage 주소와 폴더 파일명을 지정해 준다.
        StorageReference storageRef = storage.getReferenceFromUrl("gs://dlc-team.appspot.com").child("images/" + filename);
        //올라가거라...
        storageRef.putFile(filePath)
                //성공시
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                        insertUser();
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

    private void insertUser(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        User insertUser = new User();
        insertUser.setCategory(category.getSelectedItem().toString());
        insertUser.setEmail(mail.getText().toString());
        insertUser.setFloor((int)floor.getSelectedItem());
        insertUser.setIdPw(id.getText().toString()+"_"+pw.getText().toString());
        insertUser.setIdTaxNoEmail(id.getText().toString()+"_"+taxNo.getText().toString()+"_"+mail.getText().toString());
        insertUser.setLogo(filename);
        insertUser.setPhone(tel.getText().toString());
        insertUser.setStoreId(id.getText().toString());
        insertUser.setStoreName(strNm.getText().toString());
        insertUser.setStorePw(pw.getText().toString());
        insertUser.setTaxNo(taxNo.getText().toString());
        insertUser.setTaxNoEmail(taxNo.getText().toString()+"_"+mail.getText().toString());
        mDatabase.child("user/"+id.getText().toString()).setValue(insertUser);

        if(!"1".equals(intent.getStringExtra("flag"))) {
            Toast.makeText(getApplicationContext(), "회원가입 완료!", Toast.LENGTH_SHORT).show();
            intent = new Intent(getApplicationContext(), informationActivity.class);
        }else{
            intent = new Intent(this,MasterActivity.class); // 박준영씨 작업 완료시 그 페이지로 이동하게끔
            intent.putExtra("user",insertUser);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }else{
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);//액티비티 띄우기
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus && view == id) {
            isDuplicateId = false;
        }
        if (hasFocus && view == taxNo) {
            isDuplicateTaxNo = false;
        }
    }

    private void checkDuplicate(int flag) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(flag==1){ // 아이디 중복 체크
            if(id.length() == 0 ){
                isDuplicateId = false;
                Toast.makeText(getApplicationContext(),"아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
                id.requestFocus();
                return;
            }
            String storeId = id.getText().toString();
            mDatabase.child("user").orderByChild("storeId").equalTo(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(User.class) != null){
                        isDuplicateId = false;
                        Toast.makeText(getApplicationContext(),"아이디가 중복됩니다.",Toast.LENGTH_SHORT).show();
                    } else {
                        isDuplicateId = true;
                        Toast.makeText(getApplicationContext(),"아이디가 사용가능합니다.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }else{ // 사업자 번호 중복 체크
            if(taxNo.length() == 0){
                isDuplicateTaxNo = false;
                Toast.makeText(getApplicationContext(),"사업자번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                taxNo.requestFocus();
                return;
            }
            String taxNumber = taxNo.getText().toString();
            mDatabase.child("user").orderByChild("taxNo").equalTo(taxNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(User.class) != null){
                        isDuplicateTaxNo = false;
                        Toast.makeText(getApplicationContext(),"사업자번호가 중복됩니다.",Toast.LENGTH_SHORT).show();
                    } else {
                        isDuplicateTaxNo = true;
                        Toast.makeText(getApplicationContext(),"사업자번호 사용 가능합니다.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
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

    private void loadImage(String imgName) { // 이미지 불러오는 법 알려줌.
        if(check){
            return;
        }
        check = true;
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://dlc-team.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("images/"+imgName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(img_view);
                duplicateIdButton.setVisibility(View.INVISIBLE);
                duplicateTaxNoButton.setVisibility(View.INVISIBLE);
                insertImageButton.setText("로고수정");
                id.setText(user.getStoreId());
                id.setEnabled(false);
                mail.setText(user.getEmail());
                mail.setEnabled(false);
                tel.setText(user.getPhone());
                category.setSelection(getIndex(category,user.getCategory()));
                strNm.setText(user.getStoreName());
                taxNo.setText(user.getTaxNo());
                taxNo.setEnabled(false);
                floor.setSelection(getIndex(floor,Integer.toString(user.getFloor())));
                filename = user.getLogo();
                createUserButton.setText("수정하기");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "수정하기 기능에 에러가 있습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}