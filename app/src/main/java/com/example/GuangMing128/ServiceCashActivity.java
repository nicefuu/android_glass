package com.example.GuangMing128;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ServiceCashActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_cash);
        ImageView back = findViewById(R.id.back_service);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
