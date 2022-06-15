package com.example.foodswapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentHomeBinding;
import com.example.foodswapp.receta.AdapterReceta;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.receta.RecetaSeleccionada;

import java.util.ArrayList;

/**
 * Clase que determina el funcionamiento del fragment Home y muestra las recetas de los usuarios a
 * los que sigue el actual.
 */
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private AdapterReceta adapterReceta;
    private SwipeRefreshLayout swipe;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Adaptador y montaje RecyclerView
        adapterReceta = new AdapterReceta(getContext(), R.layout.cardview_receta_home, true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(root.getContext(), 1);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewPublicaciones);
        recyclerView.setLayoutManager(layoutManager);

        final RecyclerView contenedor = binding.recyclerViewPublicaciones;
        homeViewModel.getRecetasMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Receta>>() {
            @Override
            public void onChanged(ArrayList<Receta> recetas) {
                adapterReceta.updateRecetasList(recetas);
                contenedor.setAdapter(adapterReceta);
            }
        });

        listenerAdapter(contenedor);

        swipe = binding.fragmentHome;
        refreshListener(root);
        homeViewModel.refresh();
        return root;
    }

    /**
     * Actualiza los datos del ViewModel cuando se hace un swipe en la pantalla.
     * @param view Vista que se actualiza.
     */
    private void refreshListener(View view) {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeViewModel.refresh();
                swipe.setRefreshing(false);
            }
        });
    }

    /**
     * Listener del click en caso de hacer click en alguna receta para su visualización.
     * @param recyclerView el RecyclerView del que se obtiene el item al que se hace click.
     */
    private void listenerAdapter(RecyclerView recyclerView) {
        adapterReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idSeleccionado = recyclerView.getChildAdapterPosition(view);
                if(adapterReceta.getRecetas().size()>0) {
                    Receta receta = adapterReceta.getRecetas().get(idSeleccionado);

                    Intent intent = new Intent(getContext(), RecetaSeleccionada.class);
                    intent.putExtra("receta", receta);
                    intent.putExtra("user", HomeActivity.USERNAME);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}