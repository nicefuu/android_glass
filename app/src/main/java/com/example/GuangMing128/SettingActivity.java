package com.example.GuangMing128;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SettingActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        ImageView back = findViewById(R.id.back_impaired_center);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
