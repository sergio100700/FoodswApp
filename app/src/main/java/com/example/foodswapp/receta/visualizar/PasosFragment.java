package com.example.foodswapp.receta.visualizar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodswapp.databinding.FragmentIngredientesBinding;
import com.example.foodswapp.databinding.FragmentPasosBinding;

import java.util.ArrayList;

public class PasosFragment extends Fragment {

    private ArrayList<String> pasos;
    private ListView lvPasos;
    private ArrayAdapter<String> adapterPasos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pasos = getArguments().getStringArrayList("pasos");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentPasosBinding binding = FragmentPasosBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        lvPasos = binding.listViewPasos;
        adapterPasos = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_activated_1, pasos);
        lvPasos.setAdapter(adapterPasos);

        return root;
    }
}


