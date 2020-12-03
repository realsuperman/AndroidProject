package com.example.user.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StoreAcitvity extends LinearLayout {
    TextView textView;
    TextView textView2;
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
        imageView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.button);
    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setGrade(String grade){
        textView2.setText(grade);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }

    public void setVisible(boolean flag){
        if(flag) textView2.setVisibility(View.GONE);
    }

    public void setButton(String text,OnClickListener clickListener){
        button.setText(text);
        button.setOnClickListener(clickListener);
    }
}