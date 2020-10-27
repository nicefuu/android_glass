package com.example.GuangMing128.ui.center;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ServiceCenterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ServiceCenterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}