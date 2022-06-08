package com.example.foodswapp.ui.subir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Clase para crear una nueva receta comenzando por su imagen y los ingredientes.
 */
public class NuevaRecetaCrear extends AppCompatActivity {

    private EditText ingrediente;
    private ImageView imagen;
    private List<String> ingredientes;
    private ArrayAdapter<String> adaptador;
    private ListView listaIngredientes;
    private Receta receta;
    public static final int RECETA_FINALIZADA = 10;

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

    /**
     * Listener para añadir un ingrediente a la lista cuando se haga click en el tick del teclado.
     */
    private void addIngredienteListener(){
        ingrediente.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String[] separados = textView.getText().toString().split(",");

                    for (String separado : separados) {
                        if (!ingredientes.contains(separado)) {
                            ingredientes.add(separado);
                        } else {
                            ingrediente.setError(getString(R.string.err_ingredientes_repetidos));
                        }
                    }

                    textView.setText("");
                    adaptador.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    /**
     * Listener para eliminar el ingrediente al que se le hace click.
     */
    private void onItemClickListener(){
        listaIngredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(view.getContext());
                dialogo1.setTitle(getString(R.string.eliminar));
                dialogo1.setMessage(R.string.eliminar_ingrediente);
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //borrar
                        ingredientes.remove(i);
                        adaptador.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.ingrediente_eliminado),Toast.LENGTH_SHORT).show();

                    }
                });
                dialogo1.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();

            }
        });
    }

    /**
     * Listener para iniciar la activity de selección de imagen.
     */
    private void onClickListenerImagen(){
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCropActivity();
            }
        });
    }

    /**
     * Inicia la activity de selección de imagen.
     */
    private void startCropActivity(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setFixAspectRatio(true)
                .start(this);
    }

    /**
     * Obtiene el resultado de la activity de selección de imagen y actualiza la uri.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RECETA_FINALIZADA){
            finish();
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imagen.setImageURI(resultUri);
                receta.setImagen(resultUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    /**
     * Creación del menú para pasar a la siguiente activity.
     * @param menu
     * @return
     */
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