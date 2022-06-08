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

import com.example.foodswapp.databinding.FragmentDetallesBinding;
import com.example.foodswapp.databinding.FragmentIngredientesBinding;
import com.example.foodswapp.receta.Receta;

import java.util.ArrayList;

/**
 * Fragment para visualizar los ingredientes de la receta seleccionada.
 */
public class IngredientesFragment extends Fragment {

    private ArrayList<String> ingredientes;
    private ListView lvIngredientes;
    private AdapterIngredientes adapterIngredientes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ingredientes = getArguments().getStringArrayList("ingredientes");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentIngredientesBinding binding = FragmentIngredientesBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        lvIngredientes = binding.listViewIngredientes;
        adapterIngredientes = new AdapterIngredientes(root.getContext(),ingredientes);
        lvIngredientes.setAdapter(adapterIngredientes);

        return root;
    }
}
