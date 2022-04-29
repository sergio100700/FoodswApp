package com.example.foodswapp.ui.subir;

import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodswapp.HomeActivity;

public class NuevaRecetaViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    public NuevaRecetaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("MIEMAIL@gmail.com");

    }

    public void refresh(){
        mText.setValue(HomeActivity.EMAIL);
        //YimgPerfil.setValue();
    }

    public LiveData<String> getText() {
        return mText;
    }
}
