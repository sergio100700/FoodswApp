package com.example.foodswapp.ui.subir;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.ingrediente.IngredienteLista;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NuevaRecetaCrear extends AppCompatActivity {

    private EditText ingrediente;
    private List<String> ingredientes;
    private ArrayAdapter<String> adaptador;
    private ListView listaIngredientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_receta_crear);
        
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Titulo receta");


        ingrediente = findViewById(R.id.etIngredienteNR);
        listaIngredientes = findViewById(R.id.listaIngredientes);

        ingredientes = new ArrayList<>();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,ingredientes);
        listaIngredientes.setAdapter(adaptador);
        addIngredienteListener();
        onItemClickListener();
    }

    private void addIngredienteListener(){
        ingrediente.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    ingredientes.add(textView.getText().toString());
                    textView.setText("");
                    adaptador.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private void onItemClickListener(){
        listaIngredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(view.getContext());
                dialogo1.setTitle("Eliminar");
                dialogo1.setMessage("¿Eliminar este ingrediente?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //borrar
                        ingredientes.remove(i);
                        adaptador.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"Ingrediente eliminado",Toast.LENGTH_SHORT).show();

                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu item siguiente
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nueva_receta_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(),"Siguiente",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}