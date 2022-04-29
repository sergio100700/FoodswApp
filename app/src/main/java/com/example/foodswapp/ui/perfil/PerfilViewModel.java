package com.example.foodswapp.ui.perfil;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;

public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ImageView> imgPerfil;


    public PerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("MIEMAIL@gmail.com");
        imgPerfil = new MutableLiveData<>();
    }

    public void refresh(){
        mText.setValue(HomeActivity.EMAIL);
        //YimgPerfil.setValue();
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ImageView> getImagen() {
        return imgPerfil;
    }
}
