package com.example.GuangMing128;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Impaired_UserCenterActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impaired_center);
        Button charge=findViewById(R.id._query);
        Button add_money=findViewById(R.id._charge);
        Button setting=findViewById(R.id._setting);
        ImageButton back=findViewById(R.id.back_impaired_main);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent charge = new Intent(Impaired_UserCenterActivity.this, Impaired_Charge_ListActivity.class);
                startActivity(charge);
            }
        });
        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_money = new Intent(Impaired_UserCenterActivity.this, Impaired_Add_MoneyActivity.class);
                startActivity(add_money);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting = new Intent(Impaired_UserCenterActivity.this, SettingActivity.class);
                startActivity(setting);
            }
        });
    }
}
