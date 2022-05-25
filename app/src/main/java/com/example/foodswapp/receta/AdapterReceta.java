package com.example.foodswapp.receta;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.ingrediente.IngredienteLista;
import com.example.foodswapp.ui.perfil.PerfilFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdapterReceta extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private ArrayList<Receta> recetas = new ArrayList<Receta>();;
    private View.OnClickListener listener;
    private final Context context = PerfilFragment.context;

    public AdapterReceta() {

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

        ViewHolderReceta viewHolderReceta = (ViewHolderReceta) viewHolder;

        Receta receta = recetas.get(i);
        if(receta.getImagen()==null){
            viewHolderReceta.itemImaxe.setImageResource(R.mipmap.librococina);
        } else {
            Glide.with(context).load(Uri.parse(receta.getImagen())).into(viewHolderReceta.itemImaxe);
        }

        viewHolderReceta.titulo.setText(receta.getTitulo());
        viewHolderReceta.tiempo.setText(receta.getTiempo());
    }

    @Override
    public int getItemCount() {
        return recetas.size();
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

    public void updateRecetasList(ArrayList<Receta> recetas){
        //this.recetas.clear();
        this.recetas = recetas;
        notifyDataSetChanged();
    }

}

