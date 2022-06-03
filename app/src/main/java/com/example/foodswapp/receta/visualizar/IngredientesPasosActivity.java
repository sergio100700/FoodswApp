package com.example.foodswapp.receta.visualizar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodswapp.R;
import com.example.foodswapp.receta.Receta;

public class IngredientesPasosActivity extends AppCompatActivity {

    private Receta receta;
    private ListView lvIngredientes,lvPasos;
    private ArrayAdapter<String> adapterIngredientes,adapterPasos;
    private CheckBox vegano,vegetariano,sinGluten;
    private ProgressBar dificultad;
    private TextView tiempo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes_pasos);

        this.receta = (Receta) getIntent().getSerializableExtra("receta");

        lvIngredientes = findViewById(R.id.listViewIngredientes);
        lvPasos = findViewById(R.id.listViewPasos);
        vegano = findViewById(R.id.checkBoxVeganoIP);
        vegetariano = findViewById(R.id.checkBoxVegetarianoIP);
        sinGluten = findViewById(R.id.checkBoxSinGlutenIP);
        dificultad = findViewById(R.id.progressBarDificultad);
        tiempo = findViewById(R.id.textViewTiempoIP);

        adapterIngredientes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, receta.getIngredientes());
        adapterPasos = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, receta.getPasos());
        lvIngredientes.setAdapter(adapterIngredientes);
        lvPasos.setAdapter(adapterPasos);

        setCampos();
    }

    private void setCampos(){
        vegano.setSelected(receta.isVegano());
        vegetariano.setSelected(receta.isVegetariano());
        sinGluten.setSelected(receta.isSinGluten());
        dificultad.setProgress(receta.getDificultad());
        tiempo.setText(receta.getTiempo());
    }
}
