package com.example.GuangMing128;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Impaired_Charge_ListActivity extends MainActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impaired_charge_list);
        ImageView back2 = findViewById(R.id.back_impaired_center2);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
