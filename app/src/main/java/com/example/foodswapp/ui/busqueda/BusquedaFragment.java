package com.example.foodswapp.ui.busqueda;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.databinding.FragmentBusquedaBinding;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.receta.RecetaSeleccionada;
import com.example.foodswapp.receta.comentarios.Comentario;
import com.example.foodswapp.ui.perfil.PerfilExterno;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusquedaFragment extends Fragment {

    private BusquedaViewModel busquedaViewModel;
    private FragmentBusquedaBinding binding;
    private FirebaseFirestore firestore;
    private ListView lvBusqueda,lvIngredientes;
    private ArrayAdapter<String> adapter,adapterIngredientes;
    private List<String> listaBusqueda,listaIngredientes,idRecetasBuscadas,idUsuarioReceta;
    private ConstraintLayout busquedaRecetasLayout;
    private ImageButton btnVolver;
    private TextView tvNotFound;
    private CheckBox vegetariano, vegano, sinGluten;
    private EditText etIngrediente;
    private boolean esUsuario;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        busquedaViewModel =
                new ViewModelProvider(this).get(BusquedaViewModel.class);

        binding = FragmentBusquedaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        firestore = FirebaseFirestore.getInstance();

        busquedaRecetasLayout = binding.busquedaRecetasLayout;
        tvNotFound = binding.tvNotFound;
        listaBusqueda = new ArrayList<>();
        listaIngredientes = new ArrayList<>();
        idRecetasBuscadas = new ArrayList<>();
        idUsuarioReceta = new ArrayList<>();

        vegetariano = binding.cbVegetariano;
        vegano = binding.cbVegano;
        sinGluten = binding.cbGluten;
        btnVolver = binding.imageButtonVolver;
        onButtonVolver();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, listaBusqueda);
        lvBusqueda = binding.listViewBusqueda;
        lvBusqueda.setAdapter(adapter);
        onItemClick();

        etIngrediente = binding.editTextIngrediente;
        addIngredienteListener();
        lvIngredientes = binding.listViewIngredientesBuscar;
        adapterIngredientes = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, listaIngredientes);
        lvIngredientes.setAdapter(adapterIngredientes);
        onClickIngrediente();




        final SearchView searchViewUsuarios = binding.searchViewUsuarios;
        searchViewUsuarios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                esUsuario = true;
                buscarUsuario(query);
                camposBusqueda();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // AÃ±adimos setText en BusquedaViewModel
                busquedaViewModel.setText(newText);
                return false;
            }
        });

        final SearchView searchViewRecetas = binding.searchViewRecetas;
        searchViewRecetas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                esUsuario = false;
                buscarReceta(query);
                camposBusqueda();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // AÃ±adimos setText en BusquedaViewModel

                return false;
            }
        });

        return root;
    }

    private void buscarUsuario(String nombre) {
        CollectionReference users = firestore.collection("users");

        users.orderBy("username").startAt(nombre).endAt(nombre + '\uf8ff').get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    if (!((String) doc.get("username")).equals(HomeActivity.USERNAME)) {
                        listaBusqueda.add((String) doc.get("username"));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void buscarReceta(String titulo) {
        idRecetasBuscadas.clear();
        idUsuarioReceta.clear();
        firestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    doc.getReference().collection("recetas")
                            .orderBy("titulo").startAt(titulo).endAt(titulo + '\uf8ff').get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                if (!((String) doc.get("username")).equals(HomeActivity.USERNAME)
                                        && doc.get("vegano").equals(vegano.isChecked())
                                        && doc.get("vegetariano").equals(vegetariano.isChecked())
                                        && doc.get("sinGluten").equals(sinGluten.isChecked())) {
                                    if(listaIngredientes.size()>0){
                                        doc.getReference().collection("ingredientes").whereArrayContainsAny("nombre",listaIngredientes).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                listaBusqueda.add((String) doc.get("titulo"));
                                                idUsuarioReceta.add((String) doc.get("username"));
                                                idRecetasBuscadas.add(doc.getId());
                                            }
                                        });
                                    } else {
                                        listaBusqueda.add((String) doc.get("titulo"));
                                        idUsuarioReceta.add((String) doc.get("username"));
                                        idRecetasBuscadas.add(doc.getId());
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });


    }

    private void addIngredienteListener(){
        etIngrediente.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    listaIngredientes.add(textView.getText().toString());
                    textView.setText("");
                    adapterIngredientes.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private void onClickIngrediente(){
        lvIngredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listaIngredientes.remove(i);
                adapterIngredientes.notifyDataSetChanged();
            }
        });
    }


    private void onButtonVolver() {
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvBusqueda.setVisibility(View.INVISIBLE);
                busquedaRecetasLayout.setVisibility(View.VISIBLE);
                btnVolver.setVisibility(View.INVISIBLE);
                tvNotFound.setVisibility(View.INVISIBLE);
                listaBusqueda.clear();
                idRecetasBuscadas.clear();
                idUsuarioReceta.clear();
            }
        });
    }

    private void onItemClick() {
        lvBusqueda.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (esUsuario) {
                    Intent intent = new Intent(getContext(), PerfilExterno.class);
                    intent.putExtra("username", (String) adapterView.getItemAtPosition(i));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), RecetaSeleccionada.class);

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("users").whereIn("username", Collections.singletonList(idUsuarioReceta.get(i))).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                                String emailCurrentProfile = query.getId();

                                query.getReference().collection("recetas").document(idRecetasBuscadas.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String id = documentSnapshot.getId();
                                        //Timestamp fecha = (Timestamp) query.get("fecha"); igual lo tengo que implementar para mostrar por fecha
                                        String username = (String) documentSnapshot.get("username");
                                        String titulo = (String) documentSnapshot.get("titulo");
                                        String tiempo = (String) documentSnapshot.get("tiempo");
                                        Number dificultad = (Number) documentSnapshot.get("dificultad");
                                        Boolean vegano = documentSnapshot.getBoolean("vegano");
                                        Boolean vegetariano = documentSnapshot.getBoolean("vegetariano");
                                        Boolean sinGluten = documentSnapshot.getBoolean("sinGluten");
                                        String imagen = (String) documentSnapshot.get("imagen");
                                        Number valoraciones = (Number) documentSnapshot.get("valoraciones");
                                        Number valoracionMedia = (Number) documentSnapshot.get("valoracionMedia");
                                        Timestamp fecha = (Timestamp) documentSnapshot.get("fecha");

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
                                                            pasosDesordenados.put((int) (long) query.get("numero"), (String) query.get("texto"));
                                                        }
                                                    }
                                                });
                                        List<String> pasos = new ArrayList<>();
                                        for (int i = 1; i < pasosDesordenados.size(); i++) {
                                            pasos.add(pasosDesordenados.get(i));
                                        }


                                        intent.putExtra("receta", (Receta) new Receta(id, username, titulo, dif, tiempo, vegano, vegetariano, sinGluten, val, media, imagen, fecha, comentarios, ingredientes, pasos));
                                        intent.putExtra("user", (String) adapterView.getItemAtPosition(i));
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


    private void camposBusqueda() {
        lvBusqueda.setVisibility(View.VISIBLE);
        busquedaRecetasLayout.setVisibility(View.INVISIBLE);
        btnVolver.setVisibility(View.VISIBLE);
        if (listaBusqueda.size() > 0) {
            tvNotFound.setVisibility(View.INVISIBLE);
        } else {
            tvNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}