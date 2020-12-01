package com.example.user.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StoreAcitvity extends LinearLayout {
    TextView textView;
    TextView textView2;
    ImageView imageView;

    public StoreAcitvity(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context){
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.activity_store_acitvity,this,true);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        imageView = (ImageView)findViewById(R.id.imageView);
    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setMobile(String mobile){
        textView2.setText(mobile);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }
}