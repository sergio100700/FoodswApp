package com.example.foodswapp.receta.comentarios;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentComentariosBinding;
import com.example.foodswapp.databinding.FragmentPerfilBinding;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.ui.perfil.PerfilViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComentariosFragment extends Fragment {

    private ComentariosViewModel comentariosViewModel;
    private FragmentComentariosBinding binding;
    private Button enviar;
    private EditText mensaje;
    private Receta receta;
    private AdapterComentarios adapter;
    private ListView listViewComentarios;
    FirebaseFirestore firestore;

    public ComentariosFragment(Receta receta) {
        this.receta = receta;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        comentariosViewModel =
                new ViewModelProvider(this).get(ComentariosViewModel.class);


        firestore = FirebaseFirestore.getInstance();
        binding = FragmentComentariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        adapter = new AdapterComentarios(getContext(), receta.getComentarios());
        listViewComentarios = root.findViewById(R.id.listViewComentarios);
        listViewComentarios.setAdapter(adapter);

        enviar = root.findViewById(R.id.buttonEnviar);
        mensaje = root.findViewById(R.id.editTextComentario);
        onClickEnviar();

        //Bindings comentarios
        final ListView comentariosListView = binding.listViewComentarios;
        comentariosViewModel.getText().observe(getViewLifecycleOwner(), new Observer<List<Comentario>>() {
            @Override
            public void onChanged(List<Comentario> comentarios) {
                adapter.updateComentarios(comentarios);
                adapter.notifyDataSetChanged();
                comentariosListView.setAdapter(adapter);
            }
        });


        return root;
    }

    private void onClickEnviar() {
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = (String) documentSnapshot.get("username");
                        Comentario comentario = new Comentario(username, mensaje.getText().toString(), Timestamp.now().toString());
                        updateRecetaComentarios(comentario);
                    }
                });
            }
        });
    }

    /**
     * Con el usuario actual de esta sesión añado un comentario en la receta del usuario
     * que corresponda, recorriendo los usuarios hasta encontrar el username igual al de la receta.
     *
     * @param comentario El comentario a añadir en la receta.
     */
    private void updateRecetaComentarios(Comentario comentario) {
        firestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    if (((String) doc.get("username")).equals(receta.getUsername())) {
                        Map<String, Object> nuevoComent = new HashMap<>();
                        nuevoComent.put("comentario", (String) comentario.getTexto());
                        nuevoComent.put("username", (String) comentario.getUserName());

                        nuevoComent.put("fecha", (Timestamp) Timestamp.now());
                        doc.getReference().collection("recetas").document(receta.getId())
                                .collection("comentarios").add(nuevoComent);
                        doc.getReference().collection("recetas").document(receta.getId())
                                .collection("comentarios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                receta.getComentarios().clear();
                                for(DocumentSnapshot comentario : queryDocumentSnapshots){
                                    Timestamp time = (Timestamp) comentario.get("fecha");
                                    receta.getComentarios().add(new Comentario((String)comentario.get("username"),(String) comentario.get("comentario"),time.toDate().toGMTString()));
                                }
                                adapter.updateComentarios(receta.getComentarios());
                                adapter.notifyDataSetChanged();
                            }
                        });

                        break;
                    }
                }
            }
        });
    }
}