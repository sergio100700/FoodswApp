package com.example.foodswapp.receta;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodswapp.R;

/**
 * Clase que actúa de ViewHolder para el adaptador AdapterReceta.
 */
public class ViewHolderReceta extends RecyclerView.ViewHolder {

    public ImageView itemImaxe;
    public TextView titulo;
    public TextView tiempo;
    public TextView username;

    public ViewHolderReceta(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.textViewUsernameHome);
        itemImaxe = itemView.findViewById(R.id.imagen);
        titulo = itemView.findViewById(R.id.textViewTitulo);
        tiempo = itemView.findViewById(R.id.textViewTiempo);
    }

}
