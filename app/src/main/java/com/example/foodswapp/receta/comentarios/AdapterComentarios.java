package com.example.foodswapp.receta.comentarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodswapp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentarios extends BaseAdapter {


    private Context context;
    private List<Comentario> comentarios;

    public AdapterComentarios(Context context, List<Comentario> comentarios) {
        this.context = context;
        this.comentarios = comentarios;
    }

    @Override
    public int getCount() {
        return comentarios.size();
    }

    @Override
    public Object getItem(int i) {
        return comentarios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Comentario comentario = (Comentario) getItem(i);

        view = LayoutInflater.from(context).inflate(R.layout.comentario, null);

        TextView texto = view.findViewById(R.id.textViewComentario);
        TextView fecha = view.findViewById(R.id.textViewFechaC);
        CircleImageView imagen = view.findViewById(R.id.imagenUsuarioC);

        //fecha.setText(comentario.getFecha().toDate().getDay() + "/" + comentario.getFecha().toDate().getMonth());
        texto.setText(comentario.getTexto());

        /*FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference users = firestore.collection("users");
        Query query = users.whereEqualTo("username",comentario.getUserName());
        Task<QuerySnapshot> querySnapshot = query.get();

        for (DocumentSnapshot document : querySnapshot.getResult().getDocuments()) {
            Toast.makeText(context, String.valueOf(document.get("perfil")), Toast.LENGTH_SHORT).show();
        }*/

        return view;

    }

    public void updateComentarios(List<Comentario> comentarios){
        this.comentarios = comentarios;
    }

}
