package com.example.foodswapp.ui.lista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodswapp.databinding.FragmentHomeBinding;
import com.example.foodswapp.databinding.FragmentListaBinding;
import com.example.foodswapp.ui.home.HomeViewModel;

public class ListaFragment extends Fragment {

    private ListaViewModel listaViewModel;
    private FragmentListaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listaViewModel =
                new ViewModelProvider(this).get(ListaViewModel.class);

        binding = FragmentListaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
