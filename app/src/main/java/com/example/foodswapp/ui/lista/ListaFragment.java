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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
                        IngredienteLista nuevo = new IngredienteLista(meta.getText().toString());

                        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                        nuevo.setDate(LocalDateTime.now());
                        //Introduzco en bd

                        //insertar
                        ingredientes.add(nuevo);
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
                        consultarBD();
                        ingredientes.remove(id);
                        adaptador.notifyDataSetChanged();
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

    private void setItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IngredienteLista ingrediente = ingredientes.get(i);
                if (ingrediente.isDone()) {
                    //update
                    ingrediente.setDone(false);
                } else {
                    //update
                    ingrediente.setDone(true);
                }
            adaptador.notifyDataSetChanged();
            }
        });
    }


    private void consultarBD() {


    }
}
