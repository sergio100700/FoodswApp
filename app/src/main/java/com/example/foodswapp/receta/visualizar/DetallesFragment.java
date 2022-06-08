package com.example.foodswapp.receta.visualizar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodswapp.databinding.FragmentDetallesBinding;
import com.example.foodswapp.receta.Receta;

/**
 * Fragment para mostrar los detalles de la receta seleccionada.
 */
public class DetallesFragment extends Fragment {

    private Receta receta;
    private CheckBox vegano,vegetariano,sinGluten;
    private ProgressBar dificultad;
    private TextView tiempo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receta = (Receta) getArguments().getSerializable("receta");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDetallesBinding binding = FragmentDetallesBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        vegano = binding.checkBoxVeganoIP;
        vegetariano = binding.checkBoxVegetarianoIP;
        sinGluten = binding.checkBoxSinGlutenIP;
        dificultad = binding.progressBarDificultad;
        tiempo = binding.textViewTiempoIP;
        setCampos();

        return root;
    }

    /**
     * Rellena los campos para que correspondan con los detalles de la receta.
     */
    private void setCampos(){
        vegano.setSelected(receta.isVegano());
        vegetariano.setSelected(receta.isVegetariano());
        sinGluten.setSelected(receta.isSinGluten());
        dificultad.setProgress(receta.getDificultad());
        tiempo.setText(receta.getTiempo());
    }


}


