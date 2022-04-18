package com.example.foodswapp.ui.perfil;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;

public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("MIEMAIL@gmail.com");

    }

    public void refresh(){
        mText.setValue(HomeActivity.EMAIL);

    }

    public LiveData<String> getText() {
        return mText;
    }
}
