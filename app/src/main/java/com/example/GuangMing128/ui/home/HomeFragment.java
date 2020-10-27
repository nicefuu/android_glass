package com.example.GuangMing128.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.GuangMing128.ManualModeActivity;
import com.example.GuangMing128.R;
import com.example.GuangMing128.trtc.RTCActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    String time = String.valueOf(System.currentTimeMillis());
    private String mUserId = time.substring(4);
    private String mRoomId="1314520";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btn_nopay_service= getActivity().findViewById(R.id.nopay_service);
        btn_nopay_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RTCActivity.class);
                intent.putExtra("USER_ID", mUserId);
                intent.putExtra("ROOM_ID", mRoomId);
                startActivity(intent);
            }
        });
    }
}