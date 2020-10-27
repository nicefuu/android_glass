package com.example.GuangMing128.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.GuangMing128.R;


public class CameraActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_camera);
        if (savedInstanceState == null) {
            final CameraFragment fragment = new CameraFragment();
            getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();//在container显示fragment
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
