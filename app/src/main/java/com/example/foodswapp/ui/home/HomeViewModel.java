package com.example.foodswapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.receta.comentarios.Comentario;
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

/**
 * Clase ViewModel del fragment Home que obtiene los datos requeridos en el fragment.
 */
public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Receta>> recetas;
    private ArrayList<Receta> listaRecetas;
    private List<String> siguiendo;
    private FirebaseFirestore firestore;

    /**
     * Constructor sin parámetros para instanciar listas.
     */
    public HomeViewModel() {
        firestore = FirebaseFirestore.getInstance();
        listaRecetas = new ArrayList<>();
        siguiendo = new ArrayList<>();
        recetas = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Receta>> getRecetasMutableLiveData() {
        return recetas;
    }

    /**
     * Obtiene las recetas de los usuarios a los que sigue el actual usuario.
     */
    public void populateList() {
        listaRecetas.clear();


        firestore.collection("users").whereIn("username", siguiendo).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryUsuariosSeguidos) {

                for (DocumentSnapshot seguidos : queryUsuariosSeguidos) {
                    String emailCurrentProfile = seguidos.getId();
                    seguidos.getReference().collection("recetas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                                //Odenar recetas del perfil por su fecha de publicación
                                                listaRecetas.sort(Comparator.comparing(Receta::getFecha));
                                                recetas.setValue(listaRecetas);
                                                listaRecetas.addAll(recetaLista);
                                            }
                                        });

                            }


                        }
                    });
                }


            }

        });

    }

    /**
     * Obtiene los usuarios a los que sigue el actual usuario.
     */
    private void getSiguiendo() {
        firestore.collection("users").document(HomeActivity.EMAIL).collection("siguiendo").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    siguiendo.add((String) doc.get("username"));
                }
                if(siguiendo.size()>0){
                    populateList();
                }
            }
        });
    }

    /**
     * Actualiza los usuarios a los que sigue.
     */
    public void refresh() {
        getSiguiendo();
    }

}