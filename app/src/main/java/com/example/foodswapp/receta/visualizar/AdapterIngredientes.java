package com.example.foodswapp.receta.visualizar;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.ingrediente.IngredienteLista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterIngredientes extends BaseAdapter {


    private Context context;
    private ArrayList<String> ingredientes;
    FirebaseFirestore firestore;

    public AdapterIngredientes(Context context, ArrayList<String> ingredientes) {
        this.context = context;
        this.ingredientes = ingredientes;
         this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return ingredientes.size();
    }

    @Override
    public Object getItem(int i) {
        return ingredientes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        String ingrediente = (String) getItem(i);

        view = LayoutInflater.from(context).inflate(R.layout.ingrediente_add,null);

        TextView tvIngrediente = view.findViewById(R.id.textViewIngrediente);
        ImageButton imageButton = view.findViewById(R.id.imageButtonAddIngrediente);

        tvIngrediente.setText(ingrediente);

        setOnClickImageButton(imageButton,i);

        return view;

    }

    private void setOnClickImageButton(ImageButton imageButton,int pos){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Map<String,Object> ingrediente = new HashMap<>();
                ingrediente.put("nombre",ingredientes.get(pos));
                ingrediente.put("fecha", Timestamp.now());
                ingrediente.put("done",false);
                firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").add(ingrediente).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        String ref = task.getResult().getId();
                        setSnackBar(view,ref);
                    }
                });



            }
        });
    }

    private void setSnackBar(View view,String ref){
        Snackbar mensaje = Snackbar.make(view,"Ingrediente añadido a tu lista personal.", Snackbar.LENGTH_LONG);
        class DeshacerListener implements View.OnClickListener {
            @Override
            public void onClick(View view){
                // Acción cuando se presione Deshacer
                firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").document(ref).delete();
            }
        }
        mensaje.setAction("Deshacer", new DeshacerListener());
        mensaje.show();
    }

}
