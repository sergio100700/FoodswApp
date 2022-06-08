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

/**
 * Adaptador para visualizar ingredientes de cada receta y añadirlos a la lista personal.
 */
public class AdapterIngredientes extends BaseAdapter {


    private Context context;
    private ArrayList<String> ingredientes;
    private FirebaseFirestore firestore;

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

    /**
     * OnClick para añadir a la lista personal de ingredientes el ingrediente deseado.
     * @param imageButton al que se hace click
     * @param pos posición de la lista en la que se hace click
     */
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

    /**
     * SnackBar que informa de que el ingrediente ha sido añadido con éxito y que permite deshacer la operación.
     * @param view vista del snackBar
     * @param ref referencia del documento que se acaba de crear para deshacerlo en caso de ser necesario.
     */
    private void setSnackBar(View view,String ref){
        Snackbar mensaje = Snackbar.make(view, R.string.ingrediente_added, Snackbar.LENGTH_LONG);
        class DeshacerListener implements View.OnClickListener {
            @Override
            public void onClick(View view){
                // Acción cuando se presione Deshacer
                firestore.collection("users").document(HomeActivity.EMAIL).collection("ingredientes").document(ref).delete();
            }
        }
        mensaje.setAction(R.string.deshacer, new DeshacerListener());
        mensaje.show();
    }

}
