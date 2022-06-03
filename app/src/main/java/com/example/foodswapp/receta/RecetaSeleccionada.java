package com.example.foodswapp.receta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.receta.comentarios.ComentariosFragment;
import com.example.foodswapp.receta.visualizar.IngredientesPasosActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RecetaSeleccionada extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private Receta receta;
    private ImageView imageView;
    private TextView textViewTitulo, valoracionMedia;
    private RatingBar ratingBar;
    private ImageButton pasos;
    private String username;
    private float valoracionNum;
    private boolean isExterna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta_seleccionada);

        this.receta = (Receta) getIntent().getSerializableExtra("receta");
        this.username = (String) getIntent().getExtras().getString("user");
        isExterna = !HomeActivity.USERNAME.equals(username);
        firestore = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.imagenRS);
        textViewTitulo = findViewById(R.id.textViewTituloRS);
        ratingBar = findViewById(R.id.ratingBar);

        pasos = findViewById(R.id.imageButtonPasos);
        onClickPasos();
        valoracionMedia = findViewById(R.id.textViewValoracionMedia);
        rellenarCampos();
        setListenerRatingBar();

        Fragment fragmentComentarios = new ComentariosFragment(receta);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragmentComentarios).commit();

    }

    private void rellenarCampos() {
        if (receta.getImagen() == null) {
            imageView.setImageResource(R.mipmap.librococina);
        } else {
            Glide.with(getApplicationContext()).load(Uri.parse(receta.getImagen())).into(imageView);
        }
        textViewTitulo.setText(receta.getTitulo());
        valoracionMedia.setText(String.valueOf(receta.getValoracionMedia()));

        //Valoracion usuario
        CollectionReference usersRef = firestore.collection("users");
        Query query = usersRef.whereEqualTo("username", receta.getUsername());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot docUsuario = queryDocumentSnapshots.getDocuments().get(0);
                docUsuario.getReference().collection("recetas").document(receta.getId()).collection("valoraciones").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.getId().equals(HomeActivity.USERNAME)) {
                                Number num = (Number) doc.get("valor");
                                ratingBar.setRating(Float.parseFloat(String.valueOf(num)));
                                valoracionNum = ratingBar.getRating();
                            }
                        }
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receta_seleccionada_menu, menu);
        menu.findItem(R.id.action_inform).setVisible(isExterna);
        menu.findItem(R.id.action_edit).setVisible(!isExterna);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                //editar
                return true;
            case R.id.action_share:
                //Compartir
                return true;
            case R.id.action_inform:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.pregunta_denuncia))

                        .setPositiveButton(R.string.denunciar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Introduzco en bd
                                Map<String, Object> reporte = new HashMap<>();
                                reporte.put("username", receta.getUsername());
                                reporte.put("idReceta", receta.getId());
                                reporte.put("fecha", Timestamp.now());
                                firestore.collection("reports").add(reporte);

                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.gracias_denunciar), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();

                return true;

            default:return true;
        }
    }

    private void onClickPasos() {
        pasos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IngredientesPasosActivity.class);
                intent.putExtra("receta",receta);
                startActivity(intent);
            }
        });
    }

    private void setListenerRatingBar() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBarNew, float v, boolean b) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                CollectionReference usersRef = firestore.collection("users");
                Query query = usersRef.whereEqualTo("username", receta.getUsername());
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot docUsuario = queryDocumentSnapshots.getDocuments().get(0);

                        Map<String, Double> valoracion = new HashMap<>();
                        valoracion.put("valor", (double) v);


                        docUsuario.getReference().collection("recetas").document(receta.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Number valoraciones = (Number) documentSnapshot.get("valoraciones");
                                Number valMedia = (Number) documentSnapshot.get("valoracionMedia");
                                double media = Double.parseDouble(String.valueOf(valMedia));
                                Integer val = Integer.valueOf(String.valueOf(valoraciones));

                                if (valoracionNum > 0) {
                                    media = (media * val) - valoracionNum;
                                    val--;
                                }

                                val++;
                                docUsuario.getReference().collection("recetas").document(receta.getId()).update("valoraciones", val);

                                media += v;
                                media /= val;

                                ratingBarNew.setRating((float) v);
                                docUsuario.getReference().collection("recetas").document(receta.getId()).collection("valoraciones").document(HomeActivity.USERNAME).update("valor", v);
                                docUsuario.getReference().collection("recetas").document(receta.getId()).update("valoracionMedia", media);
                                valoracionMedia.setText(String.valueOf(media));
                                valoracionNum = v;

                                docUsuario.getReference().collection("recetas").document(receta.getId()).collection("valoraciones").document(HomeActivity.USERNAME).set(valoracion, SetOptions.merge());


                            }
                        });

                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}