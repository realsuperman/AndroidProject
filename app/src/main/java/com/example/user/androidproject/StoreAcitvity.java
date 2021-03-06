package com.example.user.androidproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StoreAcitvity extends LinearLayout {
    TextView textView;
    TextView textView2;
    TextView storeCd;
    ImageView imageView;
    Button button;

    public StoreAcitvity(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context){
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.activity_store_acitvity,this,true);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        storeCd = (TextView)findViewById(R.id.storeCd);
        imageView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.button);
    }

    public void setStoreCd(String storeCode){storeCd.setText(storeCode);}

    public void setName(String name){
        textView.setText(name);
    }

    public void setGrade(String grade){
        textView2.setText(grade+"점");
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }

    public void setImageFromDataBase(final Context context, final ProgressDialog progressDialog , String url){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://dlc-team.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("images/"+url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(imageView);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        });
    }

    public void setVisible(boolean flag){
        if(flag) textView2.setVisibility(View.GONE);
    }

    public void setButton(String text,OnClickListener clickListener){
        button.setText(text);
        button.setOnClickListener(clickListener);
    }
    public void setClick(OnClickListener clickListener){
        imageView.setOnClickListener(clickListener);
        textView.setOnClickListener(clickListener);
        textView2.setOnClickListener(clickListener);
    }
}