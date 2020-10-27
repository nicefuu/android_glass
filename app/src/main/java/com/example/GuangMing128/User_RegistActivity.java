package com.example.GuangMing128;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class User_RegistActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_main);
        Button b1 = findViewById(R.id.impairment);
        Button b2 = findViewById(R.id.service);
        b1.setOnClickListener(l);
        b2.setOnClickListener(l);
        ImageView back = findViewById(R.id.back_main);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    View.OnClickListener l =new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            FrameLayout impairment,service;
            impairment=findViewById(R.id.FrameLayout_Impairment);
            service=findViewById(R.id.FrameLayout_Service);
            switch (view.getId()){
                case R.id.impairment:
                    service.setVisibility(View.GONE);
                    impairment.setVisibility(View.VISIBLE);
                    break;
                case R.id.service:
                    service.setVisibility(View.VISIBLE);
                    impairment.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
}
