package com.example.user.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class FindIdActivity extends AppCompatActivity {
    TextView taxNo,email;
    Button findId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        taxNo = findViewById(R.id.taxNo);
        email = findViewById(R.id.email);
        findId = findViewById(R.id.idButton);

    }
}