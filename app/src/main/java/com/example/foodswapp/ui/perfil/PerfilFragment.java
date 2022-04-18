package com.example.foodswapp.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentPerfilBinding;
import com.example.foodswapp.ui.home.HomeViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PerfilFragment extends Fragment {


    private PerfilViewModel perfilViewModel;
    private FragmentPerfilBinding binding;
    private SwipeRefreshLayout swipe;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        swipe = root.findViewById(R.id.fragment_perfil);
        /*final TextView textView = binding.textViewEmail;
        perfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        refreshListener(root);

        return root;
    }

    private void refreshListener(View view){
        swipe = view.findViewById(R.id.fragment_perfil);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                perfilViewModel.refresh();
                swipe.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
