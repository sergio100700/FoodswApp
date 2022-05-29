package com.example.foodswapp.ui.subir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.receta.Receta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NuevaRecetaPasos extends AppCompatActivity {

    private ListView lvPasos;
    private EditText paso;
    private TextView numPaso;
    private Receta receta;
    private List<String> pasos;
    private ArrayAdapter<String> adaptador;
    private boolean editando = false;
    private int idEditando;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_receta_pasos);

        this.receta = (Receta) getIntent().getSerializableExtra("RECETA");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(receta.getTitulo());

        lvPasos = findViewById(R.id.listViewPasos);
        paso = findViewById(R.id.etPaso);
        numPaso = findViewById(R.id.tvNumPaso);
        btnAdd = findViewById(R.id.btnAdd);
        pasos = new ArrayList<>();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, pasos);
        lvPasos.setAdapter(adaptador);

        addPasoListener();
        onItemClickListener();
    }

    private void addPasoListener() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editando) {
                    pasos.set(idEditando, paso.getText().toString());
                    Toast.makeText(getApplicationContext(), "Paso modificado", Toast.LENGTH_SHORT).show();
                } else {
                    pasos.add(paso.getText().toString());
                    adaptador.notifyDataSetChanged();

                }
                editando = false;
                btnAdd.setText(getString(R.string.btnAdd));
                adaptador.notifyDataSetChanged();
                String num = String.valueOf(pasos.size() + 1);
                numPaso.setText(num);
                paso.setText("");
            }
        });
    }


    private void onItemClickListener() {
        lvPasos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(view.getContext());
                dialogo1.setTitle("Paso " + i + 1);
                dialogo1.setMessage("¿Qué deseas hacer con este ingrediente?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        pasos.remove(i);
                        adaptador.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Paso eliminado", Toast.LENGTH_SHORT).show();

                    }
                });
                dialogo1.setNegativeButton("Modificar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        paso.setText(pasos.get(i));
                        editando = true;
                        idEditando = i;
                        btnAdd.setText(R.string.btnGuardar);
                        numPaso.setText(String.valueOf(idEditando + 1));

                    }
                });
                dialogo1.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialogo1.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu item finalizar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nueva_receta_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (pasos.size() <= 0) {
                    paso.setError(getString(R.string.err_noPasos));
                } else {
                    guardarReceta();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void guardarReceta() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (receta.getImagen() == null) {
            subir(firestore, null);
        } else {

            Uri uri = Uri.parse(receta.getImagen());
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference filepath = storage.child("recetas").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            subir(firestore, uri);
                        }
                    });
                }
            });
        }

    }

    private void subir(FirebaseFirestore firestore, Uri uri) {
        Map<String, Object> subir = new HashMap<>();
        subir.put("titulo", receta.getTitulo());
        subir.put("dificultad", receta.getDificultad());
        subir.put("tiempo", receta.getTiempo());
        subir.put("vegano", receta.isVegano());
        subir.put("vegetariano", receta.isVegetariano());
        subir.put("sinGluten", receta.isSinGluten());
        subir.put("imagen", uri);
        subir.put("valoraciones",0.0);
        subir.put("fecha", Timestamp.now());

        Task<DocumentReference> addReceta = firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas").add(subir);

        addReceta.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                DocumentReference referenceReceta = task.getResult();

                //Collections

                //ingredientes
                Map<String, String> ingredientes = new HashMap<>();
                for (String ingrediente : receta.getIngredientes()) {
                    ingredientes.put("nombre", ingrediente);
                    firestore.collection("users").document(HomeActivity.EMAIL).
                            collection("recetas").document(referenceReceta.getId()).
                            collection("ingredientes").add(ingredientes);
                }


                //pasos
                Map<String, Object> pasosReceta = new HashMap<>();
                int i = 1;
                for (String paso : pasos) {
                    pasosReceta.put("numero", i);
                    pasosReceta.put("texto", paso);
                    i++;
                    firestore.collection("users").document(HomeActivity.EMAIL).
                            collection("recetas").document(referenceReceta.getId()).
                            collection("pasos").add(pasosReceta);
                }

                //ID receta
                Map<String,String> idReceta = new HashMap<>();
                idReceta.put("id", referenceReceta.getId());
                firestore.collection("users").document(HomeActivity.EMAIL).
                        collection("recetas").document(referenceReceta.getId()).
                        set(idReceta, SetOptions.merge());

                //Username
                Map<String,String> username = new HashMap<>();
                firestore.collection("users").document(HomeActivity.EMAIL).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        username.put("username",task.getResult().get("username").toString());
                        firestore.collection("users").document(HomeActivity.EMAIL).
                                collection("recetas").document(referenceReceta.getId()).
                                set(username, SetOptions.merge());
                    }
                });

                Toast.makeText(getApplicationContext(), "Receta subida con éxito!", Toast.LENGTH_LONG).show();
                finish();
                setResult(NuevaRecetaCrear.RECETA_FINALIZADA, getIntent());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}