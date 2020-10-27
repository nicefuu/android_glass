package com.example.GuangMing128;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Impaired_Add_MoneyActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impaired_add_money);
        ImageView back1 = findViewById(R.id.back_impaired_center1);
        Button confirm = findViewById(R.id.confirm);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog confirm = new AlertDialog.Builder(Impaired_Add_MoneyActivity.this).create();
                confirm.setMessage("确认充值？");
                confirm.setButton(DialogInterface.BUTTON_NEGATIVE, "否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Impaired_Add_MoneyActivity.this,"取消充值",Toast.LENGTH_LONG).show();
                    }
                });
                confirm.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Impaired_Add_MoneyActivity.this,"确认充值",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        }
    }

