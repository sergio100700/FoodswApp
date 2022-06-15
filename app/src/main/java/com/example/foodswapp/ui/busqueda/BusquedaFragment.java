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
import android.widget.Button;
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
import com.example.foodswapp.R;
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

/**
 * Clase que determina el funcionamiento del fragment de búsqueda de usuarios y recetas.
 */
public class BusquedaFragment extends Fragment {

    private BusquedaViewModel busquedaViewModel;
    private FragmentBusquedaBinding binding;
    private FirebaseFirestore firestore;
    private ListView lvBusqueda,lvIngredientes;
    private ArrayAdapter<String> adapter,adapterIngredientes;
    private List<String> listaBusqueda,listaIngredientes,idRecetasBuscadas,idUsuarioReceta;
    private List<Receta> listaRecetas;
    private ConstraintLayout busquedaRecetasLayout;
    private ImageButton btnVolver;
    private Button btnAdd;
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
        listaBusqueda = new ArrayList<>();
        listaIngredientes = new ArrayList<>();
        idRecetasBuscadas = new ArrayList<>();
        idUsuarioReceta = new ArrayList<>();
        listaRecetas = new ArrayList<>();

        vegetariano = binding.cbVegetariano;
        vegano = binding.cbVegano;
        sinGluten = binding.cbGluten;
        btnVolver = binding.imageButtonVolver;
        btnAdd = binding.buttonAddIB;
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

    /**
     * Busca el usuario indicado en la base de datos.
     * @param nombre username del usuario que se busca.
     */
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

    /**
     * Busca la receta indicada en la base de datos con los filtros indicados.
     * @param titulo el nombre de la receta por el cual se buscará.
     */
    private void buscarReceta(String titulo) {
        idRecetasBuscadas.clear();
        idUsuarioReceta.clear();
        listaRecetas.clear();
        firestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    String emailCurrentReceta = doc.getId();
                    doc.getReference().collection("recetas")
                            .orderBy("titulo").startAt(titulo).endAt(titulo + '\uf8ff').get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot docReceta : queryDocumentSnapshots) {
                                if (!((String) docReceta.get("username")).equals(HomeActivity.USERNAME)
                                        && docReceta.get("vegano").equals(vegano.isChecked())
                                        && docReceta.get("vegetariano").equals(vegetariano.isChecked())
                                        && docReceta.get("sinGluten").equals(sinGluten.isChecked())) {
                                    String tituloR = (String) docReceta.get("titulo");
                                    String usernameR = (String) docReceta.get("username");
                                    if (listaIngredientes.size() > 0) {
                                        docReceta.getReference().collection("ingredientes").whereIn("nombre",listaIngredientes).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                listaBusqueda.add(tituloR);
                                                idUsuarioReceta.add(usernameR);
                                                idRecetasBuscadas.add(docReceta.getId());
                                                queryReceta(docReceta,emailCurrentReceta);
                                            }
                                        });
                                    } else {
                                        listaBusqueda.add((String) docReceta.get("titulo"));
                                        idUsuarioReceta.add((String) docReceta.get("username"));
                                        idRecetasBuscadas.add(docReceta.getId());
                                        queryReceta(docReceta,emailCurrentReceta);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });


    }

    /**
     * Obtiene todos los datos de la receta indicada para su visualización.
     * @param docReceta documento de referencia de la receta buscada.
     * @param emailCurrentReceta el email del usuario propietario de la receta.
     */
    private void queryReceta(DocumentSnapshot docReceta,String emailCurrentReceta){
        String id = docReceta.getId();
        String username = (String) docReceta.get("username");
        String titulo = (String) docReceta.get("titulo");
        String tiempo = (String) docReceta.get("tiempo");
        Number dificultad = (Number) docReceta.get("dificultad");
        Boolean vegano = docReceta.getBoolean("vegano");
        Boolean vegetariano = docReceta.getBoolean("vegetariano");
        Boolean sinGluten = docReceta.getBoolean("sinGluten");
        String imagen = (String) docReceta.get("imagen");
        Number valoraciones = (Number) docReceta.get("valoraciones");
        Number valoracionMedia = (Number) docReceta.get("valoracionMedia");
        Timestamp fecha = (Timestamp) docReceta.get("fecha");

        Integer dif = Integer.valueOf(String.valueOf(dificultad));
        Integer val = Integer.valueOf(String.valueOf(valoraciones));
        Double media = Double.valueOf(String.valueOf(valoracionMedia));

        List<Comentario> comentarios = new ArrayList<>();
        firestore.collection("users").document(emailCurrentReceta).collection("recetas")
                .document(id).collection("comentarios").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot query : queryDocumentSnapshots) {
                            Timestamp time = (Timestamp) query.get("fecha");

                            comentarios.add(new Comentario((String) query.getId(),(String) query.get("username"), (String) query.get("comentario"), time.toDate().toGMTString()));
                        }
                    }
                });

        List<String> ingredientes = new ArrayList<>();
        firestore.collection("users").document(emailCurrentReceta).collection("recetas")
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
        firestore.collection("users").document(emailCurrentReceta).collection("recetas")
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
        listaRecetas.add(new Receta(id, username, titulo, dif, tiempo, vegano, vegetariano, sinGluten, val, media, imagen, fecha, comentarios, ingredientes, pasos));
        adapter.notifyDataSetChanged();
    }

    /**
     * Listener para añadir un ingrediente a la lista de filtros por ingredientes.
     */
    private void addIngredienteListener(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaIngredientes.size()<10) {
                    listaIngredientes.add(etIngrediente.getText().toString());
                    etIngrediente.setText("");
                    adapterIngredientes.notifyDataSetChanged();
                } else {
                    etIngrediente.setError(getString(R.string.err_maxingre));
                }
            }
        });
    }

    /**
     * Listener para eliminar un ingrediente de la lista en caso de hacer click en él.
     */
    private void onClickIngrediente(){
        lvIngredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listaIngredientes.remove(i);
                adapterIngredientes.notifyDataSetChanged();
            }
        });
    }

    /**
     * Oculta y los campos del resultado de la búsqueda anterior y limpia las listas de búsqueda.
     */
    private void onButtonVolver() {
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvBusqueda.setVisibility(View.INVISIBLE);
                busquedaRecetasLayout.setVisibility(View.VISIBLE);
                btnVolver.setVisibility(View.INVISIBLE);
                listaBusqueda.clear();
                idRecetasBuscadas.clear();
                idUsuarioReceta.clear();
                listaRecetas.clear();
            }
        });
    }

    /**
     * Listener que abre la visualización del usuario o la receta al que se ha hecho click.
     */
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
                    intent.putExtra("receta",listaRecetas.get(i));
                    intent.putExtra("user", (String) adapterView.getItemAtPosition(i));
                    startActivity(intent);
                }
            }


        });
    }

    /**
     * Visualización de los objetos del resultado de búsqueda.
     */
    private void camposBusqueda() {
        lvBusqueda.setVisibility(View.VISIBLE);
        busquedaRecetasLayout.setVisibility(View.INVISIBLE);
        btnVolver.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}