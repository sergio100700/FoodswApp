package com.example.foodswapp.ui.lista;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is lista fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
