package com.example.foodswapp.ui.subir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodswapp.databinding.FragmentListaBinding;
import com.example.foodswapp.databinding.FragmentNuevarecetaBinding;
import com.example.foodswapp.ui.lista.ListaViewModel;

public class NuevaRecetaFragment  extends Fragment {

    private NuevaRecetaViewModel recetaViewModel;
    private FragmentNuevarecetaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recetaViewModel =
                new ViewModelProvider(this).get(NuevaRecetaViewModel.class);

        binding = FragmentNuevarecetaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
