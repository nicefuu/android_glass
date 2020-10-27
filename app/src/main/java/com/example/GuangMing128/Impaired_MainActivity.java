package com.example.GuangMing128;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

import com.example.GuangMing128.camera.CameraActivity;

public class Impaired_MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impaired_main);
        TextView impaired_center = findViewById(R.id.In_impaired_center);
        impaired_center.setOnClickListener(new View.OnClickListener() {
            //进入视障用户用户中心
            @Override
            public void onClick(View view) {
                Intent impaired_center = new Intent(Impaired_MainActivity.this, Impaired_UserCenterActivity.class);
                startActivity(impaired_center);
            }
        });
        Button btn_toAImode = findViewById(R.id.AI);
        btn_toAImode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入ai模式按钮
                Intent ai_mode = new Intent(Impaired_MainActivity.this, CameraActivity.class);
                startActivity(ai_mode);
            }
        });
        findViewById(R.id.manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入人工模式
                startActivity(new Intent(Impaired_MainActivity.this, ManualModeActivity.class));
            }
        });
    }
}
