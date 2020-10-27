package com.example.GuangMing128.trtc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.tencent.liteav.debug.Constant;
//import com.tencent.rtc.R;
import com.example.GuangMing128.trtc.Constant;
import com.example.GuangMing128.R;

/**
 * RTC视频通话的入口页面（可以设置房间id和用户id）
 * <p>
 * RTC视频通话是基于房间来实现的，通话的双方要进入一个相同的房间id才能进行视频通话
 */
public class RTCEntranceActivity extends AppCompatActivity {

    private EditText mInputUserId;
    private EditText mInputRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtc_entrance);
        getSupportActionBar().hide();
        mInputUserId = findViewById(R.id.et_input_username);
        mInputRoomId = findViewById(R.id.et_input_room_id);
        findViewById(R.id.bt_enter_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEnterRoom(); // 开始进房
            }
        });
        findViewById(R.id.rtc_entrance_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput(); // 点击非EditText输入区域，隐藏键盘
            }
        });
        findViewById(R.id.entrance_ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // 返回结束
            }
        });
        mInputRoomId.setText("128128");
        String time = String.valueOf(System.currentTimeMillis());
        String userId = time.substring(time.length() - 8);
//        String userId="ImpairdPeople";
        mInputUserId.setText(userId);
    }

    private void startEnterRoom() {
        //加入房间函数
        if (TextUtils.isEmpty(mInputUserId.getText().toString().trim())
                || TextUtils.isEmpty(mInputRoomId.getText().toString().trim())) {
            //房间号或用户名为空，弹出提示：房间或用户名不能为空！
            Toast.makeText(RTCEntranceActivity.this, getString(R.string.rtc_room_input_error_tip), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(RTCEntranceActivity.this, RTCActivity.class);
        intent.putExtra(Constant.ROOM_ID, mInputRoomId.getText().toString().trim());
        /*
         * intent.putExtra("A",B);//键为A 值为B，另一个activity使用getXXXXExtra方法获取
         * */
        intent.putExtra(Constant.USER_ID, mInputUserId.getText().toString().trim());
        startActivity(intent);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
