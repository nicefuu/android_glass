package com.example.GuangMing128;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.GuangMing128.trtc.RTCActivity;

public class ManualModeActivity extends AppCompatActivity {
    private String mUserId;
    private String mRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);
        //设置用户ID和房间号
        String time = String.valueOf(System.currentTimeMillis());
        mUserId = time.substring(4);
//        mUserId = "128";
        mRoomId = "1314520";
        goToManualMode();
    }

    private void goToManualMode() {
        //进入人工模式（视频通话）
        Intent intent = new Intent(ManualModeActivity.this, RTCActivity.class);
        intent.putExtra("USER_ID", mUserId);
        intent.putExtra("ROOM_ID", mRoomId);
        startActivity(intent);
    }
}