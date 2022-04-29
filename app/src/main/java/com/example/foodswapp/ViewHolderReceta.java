package com.example.foodswapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderReceta extends RecyclerView.ViewHolder {

    public ImageView itemImaxe;
    public TextView titulo;
    public TextView tiempo;
    public TextView likes;

    public ViewHolderReceta(@NonNull View itemView) {
        super(itemView);

        itemImaxe = itemView.findViewById(R.id.imagen);
        titulo = itemView.findViewById(R.id.textViewTitulo);
        tiempo = itemView.findViewById(R.id.textViewTiempo);
        likes = itemView.findViewById(R.id.textViewLikes);

    }

}
