package com.example.foodswapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.foodswapp.databinding.ActivityHomeBinding;
import com.example.foodswapp.ui.perfil.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.prefs.Preferences;

public class HomeActivity extends AppCompatActivity {

    enum ProviderType{
        BASIC,
        GOOGLE
    }
    public static String EMAIL;
    private ActivityHomeBinding binding;
    private SharedPreferences.Editor prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtengo datos de intent Login
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String provider = bundle.getString("provider");

        Toast.makeText(getApplicationContext(),email,Toast.LENGTH_LONG).show();
        HomeActivity.EMAIL = email;

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_busqueda,R.id.navigation_subir, R.id.navigation_perfil,R.id.navigation_lista)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Guardado de datos en prefs
        prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.putString("email",email);
        prefs.putString("provider",provider);
        prefs.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_nav_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.editar_perfil:
                Toast.makeText(getApplicationContext(),"Intent para editar perfil aún por hacer",Toast.LENGTH_LONG).show();
                break;
            case R.id.cerrar_sesion:
                logOut();
                break;
            default:break;

        }
        return true;
    }

    private void listenerAdapter() {
        /*recycleAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idSeleccionado = recyclerView.getChildAdapterPosition(view);
                Articulo articulo = recycleAdapter.articulos.get(idSeleccionado);

                if (articulo.getTipo().equals("externo")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(articulo.getDescripcion()));
                    if (i.resolveActivity(getPackageManager()) != null)
                        startActivity(i);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ArticuloSeleccionado.class);
                    intent.putExtra(EXTRA_MESSAGE, articulo.getCodigo());
                    startActivityForResult(intent, TEXT_REQUEST);
                }
            }
        });*/

    }

    private void logOut(){
        prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.apply();

        FirebaseAuth.getInstance().signOut();
        onBackPressed();
        startActivity(new Intent(this,LoginActivity.class));
    }

}