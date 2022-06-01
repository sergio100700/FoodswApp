package com.example.foodswapp.ui.perfil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.foodswapp.R;

public class PerfilExterno extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_externo);

        String username = (String) getIntent().getExtras().get("username");
        Fragment perfilFragment = new PerfilFragment();

        Bundle args = new Bundle();
        args.putString("username",username);
        perfilFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, perfilFragment).commit();
    }
}