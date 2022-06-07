package com.example.foodswapp.receta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.foodswapp.receta.comentarios.Comentario;
import com.example.foodswapp.receta.comentarios.ComentariosFragment;
import com.example.foodswapp.receta.visualizar.IngredientesPasosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Clase que maneja y muestra la activity de una receta que se selecciona en alguno de los
 * RecyclerViews.
 */
public class RecetaSeleccionada extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private Receta receta;
    private ImageView imageView;
    private CircleImageView circlePerfil;
    private TextView textViewTitulo, valoracionMedia,textViewUser;
    private RatingBar ratingBar;
    private ImageButton pasos;
    private String username;
    private float valoracionNum;
    private boolean isExterna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta_seleccionada);

        firestore = FirebaseFirestore.getInstance();
        this.username = (String) getIntent().getExtras().getString("user");
        if(getIntent().getExtras().getString("recetaId")!=null){
            cargarRecetaLink(getIntent().getExtras().getString("recetaId"));
        } else {
            this.receta = (Receta) getIntent().getSerializableExtra("receta");
            init();
        }


    }

    /**
     * Inicia los pasos necesarios para la correcta visualización de la receta.
     */
    private void init(){
        isExterna = !HomeActivity.USERNAME.equals(username);

        imageView = findViewById(R.id.imagenRS);
        circlePerfil = findViewById(R.id.imagenPerfilRS);
        textViewTitulo = findViewById(R.id.textViewTituloRS);
        textViewUser = findViewById(R.id.textViewUserNameRS);
        ratingBar = findViewById(R.id.ratingBar);

        pasos = findViewById(R.id.imageButtonPasos);
        onClickPasos();
        valoracionMedia = findViewById(R.id.textViewValoracionMedia);
        rellenarCampos();
        setListenerRatingBar();

        Fragment fragmentComentarios = new ComentariosFragment(receta);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragmentComentarios).commit();
    }

    /**
     * Rellena los campos y las imágenes de la receta
     */
    private void rellenarCampos() {
        if (receta.getImagen() == null) {
            imageView.setImageResource(R.mipmap.librococina);
        } else {
            Glide.with(getApplicationContext()).load(Uri.parse(receta.getImagen())).into(imageView);
        }
        firestore.collection("users").whereIn("username", Collections.singletonList(receta.getUsername())).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String imgPerfil = (String) queryDocumentSnapshots.getDocuments().get(0).get("perfil");
                if(imgPerfil!=null){
                    Glide.with(getApplicationContext()).load(Uri.parse(imgPerfil)).into(circlePerfil);
                }
            }
        });
        textViewUser.setText(receta.getUsername());
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

    /**
     * Inflater del menú que se visualiza para poder eliminar o informar sobre la receta.
     * @param menu el menú que se crea.
     * @return true si se crea correctamente el menú o false en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receta_seleccionada_menu, menu);
        menu.findItem(R.id.action_inform).setVisible(isExterna);
        menu.findItem(R.id.action_remove).setVisible(!isExterna);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Se establece el comportamiento de cada item del menú.
     * @param item el item del menú que se selecciona.
     * @return true
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                AlertDialog.Builder builderRM = new AlertDialog.Builder(this);
                builderRM.setMessage(getString(R.string.eliminar_publicacion))

                        .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                firestore.collection("users").document(HomeActivity.EMAIL).collection("recetas").document(receta.getId()).delete();
                                Toast.makeText(getApplicationContext(), getString(R.string.publicacion_eliminada), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builderRM.create().show();

                return true;
            case R.id.action_share:
                //Compartir
                generateDynamicLink();

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

            default:
                return true;
        }
    }

    /**
     * Se abre la actividad que muestra información adicional sobre la receta y sus pasos e ingredientes.
     */
    private void onClickPasos() {
        pasos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IngredientesPasosActivity.class);
                intent.putExtra("receta", receta);
                startActivity(intent);
            }
        });
    }

    /**
     * Obtiene la valoración dada en caso de que exista y actualiza esta en caso de que cambie.
     */
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

    /**
     * Finaliza esta actividad.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * En caso de que la receta que se reciba sea proveniente de un link, se carga directamente de la base de datos.
     * @param idReceta id de la receta que se descargará.
     */
    private void cargarRecetaLink(String idReceta) {
        firestore.collection("users").whereIn("username", Collections.singletonList(this.username)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryUsuariosSeguidos) {

                for (DocumentSnapshot user : queryUsuariosSeguidos) {
                    String emailCurrentProfile = user.getId();
                    user.getReference().collection("recetas").document(idReceta).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()) {

                                DocumentSnapshot query = documentSnapshot;

                                String id = query.getId();
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
                                                    Integer numero = Integer.valueOf(String.valueOf(num));
                                                    pasosDesordenados.put(numero, (String) query.get("texto"));
                                                }
                                                List<String> pasos = new ArrayList<>();
                                                for (int i = 1; i < pasosDesordenados.size() + 1; i++) {
                                                    pasos.add(pasosDesordenados.get(i));
                                                }

                                                receta = new Receta(id, username, titulo, dif, tiempo, vegano, vegetariano, sinGluten, val, media, imagen, fecha, comentarios, ingredientes, pasos);
                                                init();
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
     * Genera un link dinámico a esta receta con el username actual y el id de la receta.
     */
    public void generateDynamicLink() {

        Task<ShortDynamicLink> dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://foodswapp.page.link?idReceta=" + receta.getId() + "&username=" + receta.getUsername()))
                .setDomainUriPrefix("https://foodswapp.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.example.foodswapp")
                                .setMinimumVersion(1)
                                .build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.example.foodswapp")
                                .setAppStoreId("whatever")
                                .setMinimumVersion("1.0.1")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Receta compartida")
                                .setDescription("Mira la receta de " + receta.getUsername())
                                .setImageUrl(Uri.parse(receta.getImagen()))
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mira la receta que he encontrado" + ": " + shortLink.toString());
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                        } else {
                            // Error
                            // ...
                            Toast.makeText(RecetaSeleccionada.this, "ERROR", Toast.LENGTH_LONG).show();
                            Log.d("FOODSWAPP", "ERROR " + task.getException());
                        }
                    }
                });
    }
}