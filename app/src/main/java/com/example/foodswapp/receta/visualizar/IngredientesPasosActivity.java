package com.example.foodswapp.receta.visualizar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodswapp.R;
import com.example.foodswapp.receta.Receta;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

/**
 * Actividad que gestiona la visualización de la receta y los fragments que compone.
 */
public class IngredientesPasosActivity extends AppCompatActivity {

    private Receta receta;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes_pasos);

        this.receta = (Receta) getIntent().getSerializableExtra("receta");

        viewPager2 = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        //Añadir fragments

        DetallesFragment detalles = new DetallesFragment();
        IngredientesFragment ingredientes = new IngredientesFragment();
        PasosFragment pasos = new PasosFragment();

        Bundle argsD = new Bundle();
        argsD.putSerializable("receta",this.receta);
        detalles.setArguments(argsD);

        Bundle argsI = new Bundle();
        argsI.putStringArrayList("ingredientes", (ArrayList<String>) this.receta.getIngredientes());
        ingredientes.setArguments(argsI);

        Bundle argsP = new Bundle();
        argsP.putStringArrayList("pasos", (ArrayList<String>) this.receta.getPasos());
        pasos.setArguments(argsP);

        viewPagerAdapter.addFragment(detalles);
        viewPagerAdapter.addFragment(ingredientes);
        viewPagerAdapter.addFragment(pasos);

        viewPager2.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText(getString(R.string.detalles));
                        break;
                    case 1:
                        tab.setText(getString(R.string.Ingredientes));
                        break;
                    case 2:
                        tab.setText(getString(R.string.pasos_));
                        break;
                }
            }
        }).attach();

    }

}
