package com.example.foodswapp.ui.subir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.ingrediente.IngredienteLista;
import com.example.foodswapp.receta.Receta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NuevaRecetaCrear extends AppCompatActivity {

    private EditText ingrediente;
    private ImageView imagen;
    private List<String> ingredientes;
    private ArrayAdapter<String> adaptador;
    private ListView listaIngredientes;
    private Receta receta;
    private final int GALLERY_INTENT = 10;
    public static final int RECETA_FINALIZADA = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_receta_crear);

        receta = (Receta) getIntent().getSerializableExtra("RECETA");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(receta.getTitulo());

        imagen = findViewById(R.id.ivImagenNR);
        ingrediente = findViewById(R.id.etIngredienteNR);
        listaIngredientes = findViewById(R.id.listaIngredientes);

        ingredientes = new ArrayList<>();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,ingredientes);
        listaIngredientes.setAdapter(adaptador);
        addIngredienteListener();
        onItemClickListener();
        onClickListenerImagen();
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

    private void onClickListenerImagen(){
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            receta.setImagen(uri.toString());
            imagen.setImageURI(uri);
        }
        if(requestCode==RECETA_FINALIZADA){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu item siguiente
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nueva_receta_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(ingredientes.size()<=0){
                    ingrediente.setError(getString(R.string.err_noIngredientes));
                } else {
                    Intent intent = new Intent(getApplicationContext(),NuevaRecetaPasos.class);
                    receta.setIngredientes(ingredientes);
                    intent.putExtra("RECETA",receta);
                    startActivityForResult(intent,RECETA_FINALIZADA);
                }
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