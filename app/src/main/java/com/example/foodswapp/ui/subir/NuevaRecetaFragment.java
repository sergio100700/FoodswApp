package com.example.foodswapp.ui.subir;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentNuevarecetaBinding;
import com.example.foodswapp.receta.comentarios.Comentario;
import com.example.foodswapp.receta.Receta;

import java.util.ArrayList;

public class NuevaRecetaFragment  extends Fragment {

    private NuevaRecetaViewModel recetaViewModel;
    private FragmentNuevarecetaBinding binding;
    private Button btnSiguiente;
    private EditText titulo,etMinutos,etHoras;
    private CheckBox vegano,vegetariano,sinGluten;
    private SeekBar dificultad;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recetaViewModel =
                new ViewModelProvider(this).get(NuevaRecetaViewModel.class);

        binding = FragmentNuevarecetaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        titulo = root.findViewById(R.id.etTituloReceta);
        vegano = root.findViewById(R.id.cbVeganoNR);
        vegetariano = root.findViewById(R.id.cbVegetarianoNR);
        sinGluten = root.findViewById(R.id.cbSinGlutenNR);
        etHoras = root.findViewById(R.id.etHoras);
        onKeyUpMinutos(root);
        dificultad = root.findViewById(R.id.sbDificultadNR);
        btnSiguiente = root.findViewById(R.id.btnSiguiente);
        onClickBtnSiguiente();
        return root;
    }

    private void onKeyUpMinutos(View view){
        etMinutos = view.findViewById(R.id.etMinutos);

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    etMinutos.setError(getString(R.string.err_solo_minutos));
                    etMinutos.setText("");
                }
            }

            private boolean filterLongEnough() {
                String content = etMinutos.getText().toString().trim();
                if(content.length()>0) {
                    return Integer.parseInt(content) >= 60 || Integer.parseInt(content) < 0;
                } else {
                    return false;
                }
            }
        };
        etMinutos.addTextChangedListener(fieldValidatorTextWatcher);
    }


    private void onClickBtnSiguiente(){
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titulo.getText().length()<=0){
                    titulo.setError(getString(R.string.err_titulo_obligatorio));
                } else {
                    Intent intent = new Intent(getContext(), NuevaRecetaCrear.class);
                    intent.putExtra("RECETA", new Receta(titulo.getText().toString(), dificultad.getProgress(),
                            etHoras.getText().toString().concat(getString(R.string.horas_y)).concat(etMinutos.getText().toString()).concat(getString(R.string.minutos)),
                            vegano.isSelected(), vegetariano.isSelected(), sinGluten.isSelected(), new ArrayList<Comentario>()));
                    startActivityForResult(intent,NuevaRecetaCrear.RECETA_FINALIZADA);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==NuevaRecetaCrear.RECETA_FINALIZADA){
            titulo.setText("");
            dificultad.setProgress(0);
            etHoras.setText("");
            etMinutos.setText("");
            vegano.setSelected(false);
            vegetariano.setSelected(false);
            sinGluten.setSelected(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
