package com.example.foodswapp.ui.perfil;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> username;
    private MutableLiveData<Uri> imgPerfil;
    private FirebaseFirestore firestore;

    public PerfilViewModel() {
        firestore = FirebaseFirestore.getInstance();
        username = new MutableLiveData<>();
        imgPerfil = new MutableLiveData<>();
        setUsername();
        setImgPerfil();
    }

    private void setUsername(){
        firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setValue(documentSnapshot.get("username").toString());
            }
        });
    }

    private void setImgPerfil(){
        firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("perfil")!=null) {
                    Uri uri = Uri.parse(documentSnapshot.get("perfil").toString());
                    imgPerfil.setValue(uri);
                }

            }
        });
    }

    public void refresh(){
        setUsername();
        setImgPerfil();
    }

    public LiveData<String> getText() {
        return username;
    }
    public LiveData<Uri> getImagen() {
        return imgPerfil;
    }

}
