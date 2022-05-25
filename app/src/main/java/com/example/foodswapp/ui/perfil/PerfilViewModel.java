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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.receta.AdapterReceta;
import com.example.foodswapp.receta.Receta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> username;
    private MutableLiveData<Uri> imgPerfil;
    private MutableLiveData<ArrayList<Receta>> recetas;
    private ArrayList<Receta> listaRecetas;
    private FirebaseFirestore firestore;

    public PerfilViewModel() {
        firestore = FirebaseFirestore.getInstance();
        username = new MutableLiveData<>();
        imgPerfil = new MutableLiveData<>();
        recetas = new MutableLiveData<>();
        setUsername();
        setImgPerfil();
        init();
    }

    private void setUsername() {
        firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setValue(documentSnapshot.get("username").toString());
            }
        });
    }

    private void setImgPerfil() {
        firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("perfil") != null) {
                    Uri uri = Uri.parse(documentSnapshot.get("perfil").toString());
                    imgPerfil.setValue(uri);
                }

            }
        });
    }

    private void init(){
        populateList();
    }

    private void populateList() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Receta> recetaLista = new ArrayList<>();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    //String id = query.getId(); IGUAL NO lo necesito
                    //Timestamp fecha = (Timestamp) query.get("fecha"); igual lo tengo que implementar para mostrar por fecha

                    String titulo = (String) query.get("titulo");
                    String tiempo = (String) query.get("tiempo");
                    Number dificultad = (Number) query.get("dificultad");
                    Boolean vegano = query.getBoolean("vegano");
                    Boolean vegetariano = query.getBoolean("vegetariano");
                    Boolean sinGluten = query.getBoolean("sinGluten");
                    String imagen = (String) query.get("imagen");
                    Integer i = Integer.valueOf(String.valueOf(dificultad));
                    recetaLista.add(new Receta(titulo, i, tiempo, vegano, vegetariano, sinGluten, imagen, 2.2));
                }
                listaRecetas = recetaLista;
                recetas.setValue(listaRecetas);
            }
        });

    }

    public void refresh() {
        setUsername();
        setImgPerfil();
        init();
    }

    public LiveData<String> getText() {
        return username;
    }

    public LiveData<Uri> getImagen() {
        return imgPerfil;
    }

    public MutableLiveData<ArrayList<Receta>> getRecetasMutableLiveData() {
        return recetas;
    }

}
