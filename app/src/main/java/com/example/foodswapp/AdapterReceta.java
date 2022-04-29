package com.example.foodswapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class AdapterReceta extends RecyclerView.Adapter implements View.OnClickListener {


    //public ArrayList<Articulo> articulos = new ArrayList<Articulo>();

    private View.OnClickListener listener;

    public AdapterReceta(){

    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        LayoutInflater mInflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.cardview_receta, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolderReceta(v);
        v.setOnClickListener(this);

        return viewHolder;

    }


    @Override

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ViewHolderReceta viewHolderMeu = (ViewHolderReceta) viewHolder;

        //Articulo articulo = articulos.get(i);

        /*viewHolderMeu.itemImaxe.setImageBitmap(articulo.getImaxe());
        String titulo = articulo.getTitulo();
        if(titulo.length() > 45){
            viewHolderMeu.titulo.setText(titulo.substring(0,45) + "...");
        } else {
            viewHolderMeu.titulo.setText(articulo.getTitulo());
        }*/


        //viewHolderMeu.precio.setText(MainActivity.formateador.format(articulo.getPrecio()) + "€");

        /*if (articulo.getTipo().equals("externo")) {
            SpannableString mitextoU = new SpannableString("AD " + articulo.getDescripcion().substring(0, 20));
            mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
            viewHolderMeu.anuncio.setText(mitextoU);
            viewHolderMeu.anuncio.setTextColor(Color.BLUE);
        }*/

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }


}

