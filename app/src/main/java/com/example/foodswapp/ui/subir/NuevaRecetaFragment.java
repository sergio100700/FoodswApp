package com.example.foodswapp.ui.subir;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentListaBinding;
import com.example.foodswapp.databinding.FragmentNuevarecetaBinding;
import com.example.foodswapp.ui.lista.ListaViewModel;

public class NuevaRecetaFragment  extends Fragment {

    private NuevaRecetaViewModel recetaViewModel;
    private FragmentNuevarecetaBinding binding;
    private Button btnSiguiente;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recetaViewModel =
                new ViewModelProvider(this).get(NuevaRecetaViewModel.class);

        binding = FragmentNuevarecetaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnSiguiente = root.findViewById(R.id.btnSiguiente);
        onClickBtnSiguiente();
        return root;
    }

    private void onClickBtnSiguiente(){
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),NuevaRecetaCrear.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
