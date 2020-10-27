package com.example.GuangMing128;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    boolean UserType = true;
    //申请权限
    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);
        }else{
            Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText et_account= findViewById(R.id._account);
        et_account.setText("128");
        EditText et_password= findViewById(R.id._password);
        et_password.setText("mzr");
        TextView lost_password = findViewById(R.id.lost_password);
        lost_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_forget_password = new Intent(MainActivity.this, Forget_PasswordActivity.class);
                startActivity(intent_forget_password);
            }
        });

        TextView no_account = findViewById(R.id.no_account);
        no_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent no_account = new Intent(MainActivity.this, User_RegistActivity.class);
                startActivity(no_account);
            }
        });

        final EditText account = findViewById(R.id._account);
        final EditText password = findViewById(R.id._password);
        Button login = findViewById(R.id.Login);
        final RadioGroup RG = findViewById(R.id.ChooseUserType);
//        true表示视障用户
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < RG.getChildCount(); i++) {
                    RadioButton r = (RadioButton) RG.getChildAt(i);
                    if (r.isChecked() && r.getText().equals("服务用户")) {
                        UserType = false;
                    }
                if (account.getText().toString().equals("128")
                        && password.getText().toString().equals("mzr")&& UserType ) {
                    Intent login = new Intent(MainActivity.this, Impaired_MainActivity.class);
                    startActivity(login);

                } else if (account.getText().toString().equals("129")
                        && password.getText().toString().equals("mzr")&& !UserType ){
                    Intent login2 = new Intent(MainActivity.this,ServiceMainActivity.class);
                    startActivity(login2);
                    }
                else{
                    Toast.makeText(MainActivity.this,"账号密码错误,请重新输入",Toast.LENGTH_LONG).show();
                }
                }
            }
        });

        Button donation = findViewById(R.id.donation);
        donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent donation = new Intent(MainActivity.this, DonationActivity.class);
                startActivity(donation);
            }
        });

    }
}

