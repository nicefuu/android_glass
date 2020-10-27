package com.example.GuangMing128.ui.center;

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

import com.example.GuangMing128.R;
import com.example.GuangMing128.ServiceCashActivity;
import com.example.GuangMing128.ServiceCheckActivity;

public class ServiceCenterFragment extends Fragment {

    private ServiceCenterViewModel serviceCenterViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btn_charge = getActivity().findViewById(R.id.btn_to_cash);
        btn_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(), ServiceCashActivity.class);
                startActivity(intent);
//                Toast.makeText(getActivity(),"点击了按钮",Toast.LENGTH_SHORT).show();
            }
        });
        Button btn_check_point = getActivity().findViewById(R.id.btn_to_check_point);
        btn_check_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(), ServiceCheckActivity.class);
                startActivity(intent);
//                Toast.makeText(getActivity(),"点击了按钮",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serviceCenterViewModel =
                ViewModelProviders.of(this).get(ServiceCenterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_center, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        serviceCenterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}