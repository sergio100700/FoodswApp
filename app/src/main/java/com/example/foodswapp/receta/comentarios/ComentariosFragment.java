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

import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentComentariosBinding;
import com.example.foodswapp.databinding.FragmentPerfilBinding;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.ui.perfil.PerfilViewModel;

import java.util.List;

public class ComentariosFragment extends Fragment {

    private ComentariosViewModel comentariosViewModel;
    private FragmentComentariosBinding binding;
    private Button enviar;
    private Receta receta;
    private AdapterComentarios adapter;
    private ListView listViewComentarios;

    public ComentariosFragment(Receta receta){
        this.receta = receta;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        comentariosViewModel =
                new ViewModelProvider(this).get(ComentariosViewModel.class);



        binding = FragmentComentariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new AdapterComentarios(getContext(),receta.getComentarios());
        listViewComentarios = root.findViewById(R.id.listViewComentarios);
        listViewComentarios.setAdapter(adapter);

        enviar = root.findViewById(R.id.buttonEnviar);
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

    private void onClickEnviar(){
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"HOLA",Toast.LENGTH_SHORT).show();
            }
        });
    }
}