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
import com.example.foodswapp.receta.RecetaSeleccionada;
import com.example.foodswapp.ui.perfil.PerfilFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.prefs.Preferences;

public class HomeActivity extends AppCompatActivity {

    enum ProviderType{
        BASIC,
        GOOGLE
    }
    public static String EMAIL;
    public static String USERNAME;
    private ActivityHomeBinding binding;
    private SharedPreferences.Editor prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtengo datos de intent Login
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String provider = bundle.getString("provider");

        HomeActivity.EMAIL = email;

        tryToGetDynamicLink();

        FirebaseFirestore.getInstance().collection("users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                USERNAME = (String) documentSnapshot.get("username");
            }
        });

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

    private void logOut(){
        prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.apply();

        FirebaseAuth.getInstance().signOut();
        onBackPressed();
        startActivity(new Intent(this,LoginActivity.class));
    }

    //Pruebas LINKS
    public void tryToGetDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink.getQueryParameter("idReceta") != null && deepLink.getQueryParameter("username")!=null) {
                                Intent intent = new Intent(getApplicationContext(), RecetaSeleccionada.class);
                                intent.putExtra("user",deepLink.getQueryParameter("username"));
                                intent.putExtra("recetaId",deepLink.getQueryParameter("idReceta"));
                                startActivity(intent);
                            }
                        } else {
                            Log.d("WALKIRIA", "ERROR WITH DYNAMIC LINK OR NO LINK AT ALL");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("WALKIRIA", "ERROR WITH DYNAMIC LINK " + e.toString());

                    }
                });
    }



}