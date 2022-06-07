package com.example.foodswapp.ui.lista;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentListaBinding;
import com.example.foodswapp.ingrediente.Adaptador;
import com.example.foodswapp.ingrediente.IngredienteLista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que determina el funcionamiento del fragment de Lista para almacenar lista de la compra.
 */
public class ListaFragment extends Fragment {

    private ListaViewModel listaViewModel;
    private FragmentListaBinding binding;
    private Context context;
    private ArrayList<IngredienteLista> ingredientes;
    private Adaptador adaptador;
    private LocalDate date;
    private ListView listView;
    private FirebaseFirestore firestore;
    private Button btnAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listaViewModel =
                new ViewModelProvider(this).get(ListaViewModel.class);

        binding = FragmentListaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.context = root.getContext();
        firestore = FirebaseFirestore.getInstance();

        btnAdd = root.findViewById(R.id.btnAdd);
        clickAddListener();
        listView = root.findViewById(R.id.lista);
        ingredientes = new ArrayList<>();
        adaptador = new Adaptador(context, ingredientes);
        listView.setAdapter(adaptador);


        consultarBD();

        setItemClick();
        setItemClickLong();
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Listener del botón de añadir un nuevo elemento a la lista.
     */
    public void clickAddListener() {

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.nuevo_ingrediente, null);
                builder.setView(view);

                View finalViewe = view;

                builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText meta = finalViewe.findViewById(R.id.etIngrediente);
                        IngredienteLista nuevo = new IngredienteLista(meta.getText().toString(),false);

                        //Introduzco en bd
                        Map<String,Object> ingrediente = new HashMap<>();
                        ingrediente.put("nombre", nuevo.getNombre());
                        ingrediente.put("fecha",nuevo.getDate());
                        ingrediente.put("done",false);
                        firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").add(ingrediente);

                        //actualizar lista
                        consultarBD();

                        dialog.dismiss();
                        Toast.makeText(context, R.string.nueva_add, Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

    }

    /**
     * Listener para mostrar diálogo al hacer una pulsación larga en un item de la lista. Pregunta si
     * se quiere eliminar el elemento y si se elimina actualiza la lista y la base de datos.
     */
    private void setItemClickLong() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion = i;

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(binding.getRoot().getContext());
                dialogo1.setTitle("Eliminar");
                dialogo1.setMessage("¿Eliminar este ingrediente?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //borrar
                        IngredienteLista ingrediente = ingredientes.get(i);
                        firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").document(ingrediente.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                consultarBD();
                                adaptador.notifyDataSetChanged();
                                Toast.makeText(getContext(),"Ingrediente eliminado con éxito",Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

                return false;
            }
        });
    }

    /**
     * Listener para el click en el item. En caso de hacer click el objeto seleccionado se marca como realizado y
     * se tacha en la lista.
     */
    private void setItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IngredienteLista ingrediente = ingredientes.get(i);
                if (ingrediente.isDone()) {
                    //update
                    ingrediente.setDone(false);
                    firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").document(ingrediente.getId()).update("done",false);
                } else {
                    //update
                    ingrediente.setDone(true);
                    firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").document(ingrediente.getId()).update("done",true);
                }
            adaptador.notifyDataSetChanged();
            }
        });
    }

    /**
     * Consulta la lista en la base de datos y la actualiza.
     */
    private void consultarBD() {
        firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ingredientes.clear();
                for(QueryDocumentSnapshot query : queryDocumentSnapshots){
                    String id = query.getId();
                    String nombre = (String) query.get("nombre");
                    Timestamp fecha = (Timestamp) query.get("fecha");
                    boolean done = (boolean) query.get("done");
                    ingredientes.add(new IngredienteLista(id,nombre,fecha,done));
                    adaptador.notifyDataSetChanged();
                }
            }
        });

    }
}
