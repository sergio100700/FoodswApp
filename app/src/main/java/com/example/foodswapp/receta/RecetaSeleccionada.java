package com.example.foodswapp.receta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import com.example.foodswapp.R;
import com.example.foodswapp.receta.comentarios.ComentariosFragment;

public class RecetaSeleccionada extends AppCompatActivity {

    private Receta receta;
    private ImageView imageView;
    private TextView textViewTitulo;
    private TextView valoraciones;
    private RatingBar ratingBar;
    private ImageButton comentarios;
    private boolean isComentarios = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta_seleccionada);

        this.receta  = (Receta) getIntent().getSerializableExtra("receta");

        imageView = findViewById(R.id.imagenRS);
        textViewTitulo = findViewById(R.id.textViewTituloRS);
        ratingBar = findViewById(R.id.ratingBar);
        comentarios = findViewById(R.id.imageButtonComentarios);
        onClickComentarios();
        valoraciones = findViewById(R.id.textViewValoracionMedia);
        rellenarCampos();
    }

    private void rellenarCampos(){
        if(receta.getImagen()==null){
            imageView.setImageResource(R.mipmap.librococina);
        } else {
            Glide.with(getApplicationContext()).load(Uri.parse(receta.getImagen())).into(imageView);
        }
        textViewTitulo.setText(receta.getTitulo());
        valoraciones.setText(String.valueOf(receta.getValoraciones()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receta_seleccionada_menu, menu);
        menu.findItem(R.id.action_inform).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                action(R.string.editar);
                return true;
            case R.id.action_share:
                action(R.string.compartir);
                return true;
            case R.id.action_inform:
                action(R.string.denunciar);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickComentarios(){
        comentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isComentarios) {
                    Fragment fragmentComentarios = new ComentariosFragment(receta);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragmentComentarios).commit();
                    isComentarios = true;
                } else {
                    isComentarios = false;
                }
            }
        });
    }

    private void action(int resid) {
        Toast.makeText(this, getText(resid), Toast.LENGTH_SHORT).show();
    }

}