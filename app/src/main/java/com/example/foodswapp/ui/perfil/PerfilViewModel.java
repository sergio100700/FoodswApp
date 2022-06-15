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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que actúa de ViewModel para el fragment del Perfil y obtiene los datos que se le pasarán de
 * la base de datos.
 */
public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> username, numSeguidores, numSeguidos;
    private MutableLiveData<Uri> imgPerfil;
    private MutableLiveData<ArrayList<Receta>> recetas;
    private ArrayList<Receta> listaRecetas;
    private FirebaseFirestore firestore;
    private String user;
    private String emailCurrentProfile;

    public PerfilViewModel() {
        firestore = FirebaseFirestore.getInstance();
        username = new MutableLiveData<>();
        imgPerfil = new MutableLiveData<>();
        listaRecetas = new ArrayList<>();
        recetas = new MutableLiveData<>();
        numSeguidores = new MutableLiveData<>();
        numSeguidos = new MutableLiveData<>();
    }

    /**
     * Obtiene el usuario y obtiene sus datos.
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
        getEmail();
    }

    /**
     * Cuenta el número de seguidores del usuario actual.
     */
    private void setNumSeguidores() {
        firestore.collection("users").document(emailCurrentProfile).collection("seguidores").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    numSeguidores.setValue(String.valueOf(queryDocumentSnapshots.size()));
                } else {
                    numSeguidores.setValue("0");
                }

            }
        });
    }

    /**
     * Cuenta el número de seguidos del usuario actual.
     */
    private void setNumSeguidos() {
        firestore.collection("users").document(emailCurrentProfile).collection("siguiendo").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    numSeguidos.setValue(String.valueOf(queryDocumentSnapshots.size()));
                } else {
                    numSeguidos.setValue("0");
                }

            }
        });
    }

    /**
     * Establece el nombre de usuario del actual.
     */
    private void setUsername() {
        firestore.collection("users").document(emailCurrentProfile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setValue((String) documentSnapshot.get("username"));
            }
        });
    }

    /**
     * Establece la imagen de perfil del usuario actual.
     */
    private void setImgPerfil() {
        firestore.collection("users").document(emailCurrentProfile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("perfil") != null) {
                    Uri uri = Uri.parse(documentSnapshot.get("perfil").toString());
                    imgPerfil.setValue(uri);
                }

            }
        });
    }

    /**
     * Obtiene el email del usuario actual para comprobar que quien está visualizando el perfil es él mismo o un usuario externo.
     */
    private void getEmail() {
        CollectionReference users = firestore.collection("users");
        users.whereIn("username", Collections.singletonList(user)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() == 1) {
                    emailCurrentProfile = queryDocumentSnapshots.getDocuments().get(0).getId();
                    populateList();
                    setUsername();
                    setImgPerfil();
                    setNumSeguidores();
                    setNumSeguidos();
                }
            }
        });
    }

    /**
     * Rellena la lista de recetas para esta rellenar el recyclerview.
     */
    public void populateList() {
        listaRecetas.clear();
        recetas.setValue(listaRecetas);


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(emailCurrentProfile).collection("recetas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Receta> recetaLista = new ArrayList<>();
                for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                    String id = query.getId();
                    String username = (String) query.get("username");
                    String titulo = (String) query.get("titulo");
                    String tiempo = (String) query.get("tiempo");
                    Number dificultad = (Number) query.get("dificultad");
                    Boolean vegano = query.getBoolean("vegano");
                    Boolean vegetariano = query.getBoolean("vegetariano");
                    Boolean sinGluten = query.getBoolean("sinGluten");
                    String imagen = (String) query.get("imagen");
                    Number valoraciones = (Number) query.get("valoraciones");
                    Number valoracionMedia = (Number) query.get("valoracionMedia");
                    Timestamp fecha = (Timestamp) query.get("fecha");

                    Integer dif = Integer.valueOf(String.valueOf(dificultad));
                    Integer val = Integer.valueOf(String.valueOf(valoraciones));
                    Double media = Double.valueOf(String.valueOf(valoracionMedia));

                    List<Comentario> comentarios = new ArrayList<>();
                    firestore.collection("users").document(emailCurrentProfile).collection("recetas")
                            .document(id).collection("comentarios").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                        Timestamp time = (Timestamp) query.get("fecha");

                                        comentarios.add(new Comentario((String) query.get("username"), (String) query.get("comentario"), time.toDate().toGMTString()));
                                    }
                                }
                            });

                    List<String> ingredientes = new ArrayList<>();
                    firestore.collection("users").document(emailCurrentProfile).collection("recetas")
                            .document(id).collection("ingredientes").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                        ingredientes.add((String) query.get("nombre"));
                                    }
                                }
                            });

                    Map<Integer, String> pasosDesordenados = new HashMap<>();
                    firestore.collection("users").document(emailCurrentProfile).collection("recetas")
                            .document(id).collection("pasos").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                        Number num = (Number) query.get("numero");
                                        Integer numero =  Integer.valueOf(String.valueOf(num));
                                        pasosDesordenados.put(numero, (String) query.get("texto"));
                                    }
                                    List<String> pasos = new ArrayList<>();
                                    for (int i = 1; i < pasosDesordenados.size()+1; i++) {
                                        pasos.add(pasosDesordenados.get(i));
                                    }
                                    recetaLista.add(new Receta(id, username, titulo, dif, tiempo, vegano, vegetariano, sinGluten, val, media, imagen, fecha, comentarios, ingredientes, pasos));
                                    listaRecetas = recetaLista;

                                    //Odenar recetas del perfil por su fecha de publicación
                                    listaRecetas.sort(Comparator.comparing(Receta::getFecha));

                                    recetas.setValue(listaRecetas);
                                }
                            });


                }

            }
        });


    }

    /**
     * Actualiza todos los campos.
     */
    public void refresh() {
        setUsername();
        setImgPerfil();
        setNumSeguidores();
        setNumSeguidos();
        populateList();
    }

    public LiveData<String> getText() {
        return username;
    }

    public LiveData<String> getNumSeguidores() {
        return numSeguidores;
    }

    public LiveData<String> getNumSeguidos() {
        return numSeguidos;
    }

    public LiveData<Uri> getImagen() {
        return imgPerfil;
    }

    public MutableLiveData<ArrayList<Receta>> getRecetasMutableLiveData() {
        return recetas;
    }

}
