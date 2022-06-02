package com.example.foodswapp.receta.comentarios;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodswapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        TextView username = view.findViewById(R.id.textViewUserNameC);
        TextView fecha = view.findViewById(R.id.textViewFechaC);
        CircleImageView imagen = view.findViewById(R.id.imagenUsuarioC);

        fecha.setText(comentario.getFecha().replace(" GMT",""));
        texto.setText(comentario.getTexto());
        username.setText(comentario.getUserName());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot doc:queryDocumentSnapshots){
                    if(((String) doc.get("username")).equals(comentario.getUserName())) {
                        if(doc.get("perfil") != null){
                            Uri uri = Uri.parse(doc.get("perfil").toString());
                            Glide.with(context).load(uri).into(imagen);
                        }

                        break;
                    }
                }
            }
        });

        return view;

    }

    public void updateComentarios(List<Comentario> comentarios){
        this.comentarios = comentarios;
    }

}
