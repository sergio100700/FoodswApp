package com.example.foodswapp.ui.perfil;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.receta.comentarios.Comentario;
import com.example.foodswapp.receta.Receta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        populateList();
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

    private void populateList() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Receta> recetaLista = new ArrayList<>();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    String id = query.getId();
                    //Timestamp fecha = (Timestamp) query.get("fecha"); igual lo tengo que implementar para mostrar por fecha
                    String username = (String) query.get("username");
                    String titulo = (String) query.get("titulo");
                    String tiempo = (String) query.get("tiempo");
                    Number dificultad = (Number) query.get("dificultad");
                    Boolean vegano = query.getBoolean("vegano");
                    Boolean vegetariano = query.getBoolean("vegetariano");
                    Boolean sinGluten = query.getBoolean("sinGluten");
                    String imagen = (String) query.get("imagen");
                    Integer dif = Integer.valueOf(String.valueOf(dificultad));
                    Double valoraciones = (Double) query.get("valoraciones");
                    Timestamp fecha = (Timestamp) query.get("fecha");

                    List<Comentario> comentarios = new ArrayList<>();
                    firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas")
                            .document(id).collection("comentarios").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                comentarios.add(new Comentario((String)query.get("username"),(String) query.get("comentario"),(Timestamp) query.get("fecha")));
                            }
                        }
                    });

                    List<String> ingredientes = new ArrayList<>();
                    firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas")
                            .document(id).collection("ingredientes").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                        ingredientes.add((String)query.get("nombre"));
                                    }
                                }
                            });

                    Map<Integer,String> pasosDesordenados = new HashMap<>();
                    firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas")
                            .document(id).collection("pasos").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                        pasosDesordenados.put((int) (long) query.get("numero"),(String) query.get("texto"));
                                    }
                                }
                            });
                    List<String> pasos = new ArrayList<>();
                    for(int i  = 1;i< pasosDesordenados.size();i++){
                        pasos.add(pasosDesordenados.get(i));
                    }
                    recetaLista.add(new Receta(id,username,titulo, dif, tiempo, vegano, vegetariano, sinGluten, valoraciones,imagen,fecha,comentarios,ingredientes, pasos));
                }
                listaRecetas = recetaLista;

                //Odenar recetas del perfil por su fecha de publicación
                listaRecetas.sort(Comparator.comparing(Receta::getFecha));

                recetas.setValue(listaRecetas);
            }
        });

    }

    public void refresh() {
        setUsername();
        setImgPerfil();
        populateList();
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
