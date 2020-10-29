package com.example.user.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
    private boolean isDuplicateId = false ,isDuplicateTaxNo=false,isUpload=false;
    private Uri filePath;
    private TextView id,pw,mail,tel,strNm,taxNo;
    private String filename;
    private DatabaseReference mDatabase;

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
    public void onClick(View view) {
        if(view==createUserButton){
            if(!check()) return;
            //uploadFile();
            //if(!isUpload) return; // 업로드가 제대로 안되었다면 그만둔다.

            //intent = new Intent(this,informationActivity.class);
            //startActivity(intent);//액티비티 띄우기
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
            InputMethodManager imm;
            imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(tel.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(category.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(strNm.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(taxNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(taxNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(floor.getWindowToken(), 0);
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
        if(filePath==null){
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
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
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
        isUpload=false;
        //if (filePath != null) {
        //업로드 진행 Dialog 보이기
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업로드중...");
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
                        Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        isUpload=true;
                    }
                })
                //실패시
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        isUpload=false;
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
            String storeId = id.getText().toString();
            mDatabase.child("user").orderByChild("storeId").equalTo(storeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(User.class) != null){
                        isDuplicateId = false;
                    } else {
                        isDuplicateId = true;
                        System.out.println("데이터 없음?");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }else{ // 사업자 번호 중복 체크
            int taxNumber = Integer.parseInt(taxNo.getText().toString());
            mDatabase.child("user").orderByChild("taxNo").equalTo(taxNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(User.class) != null){
                        isDuplicateTaxNo = false;
                    } else {
                        isDuplicateTaxNo = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    /*private void test() { // 이미지 불러오는 법 알려줌.
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://dlc-team.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("images/20201015_4453.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지 로드 성공시

                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(img_view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}