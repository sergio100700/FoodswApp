package com.example.foodswapp.ui.perfil;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentPerfilBinding;
import com.example.foodswapp.receta.AdapterReceta;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.receta.RecetaSeleccionada;
import com.example.foodswapp.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private final int GALLERY_INTENT = 10;
    private final int SELECCION_PROPIETARIO = 20;
    private FirebaseFirestore firestore;
    private PerfilViewModel perfilViewModel;
    private FragmentPerfilBinding binding;
    private SwipeRefreshLayout swipe;
    private AdapterReceta adapterReceta;
    public static Context context;
    private boolean esPerfilPropio;
    private ToggleButton btnSeguir;
    private String currentUsername;
    private String usernameExterno;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            usernameExterno = getArguments().getString("username");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();

        perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        if(usernameExterno!=null){
            perfilViewModel.setUser(usernameExterno);
        } else {
            getCurrentUsername();
        }


        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = root.getContext();

        swipe = root.findViewById(R.id.fragment_perfil);
        btnSeguir = binding.buttonSeguir;

        //Adaptador y montaje RecyclerView
        adapterReceta = new AdapterReceta(getContext(),R.layout.cardview_receta,false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(root.getContext(), 3);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewPerfil);
        recyclerView.setLayoutManager(layoutManager);


        //Bindings campos de usuario
        final TextView tvUserName = binding.textViewUserName;
        perfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tvUserName.setText(s);
            }
        });

        final TextView tvSeguidores = binding.textViewNumSeguidores;
        perfilViewModel.getNumSeguidores().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tvSeguidores.setText(s);
            }
        });

        final TextView tvSeguidos = binding.textViewNumSeguidos;
        perfilViewModel.getNumSeguidos().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tvSeguidos.setText(s);
            }
        });

        final CircleImageView imagenPerfil = binding.imageViewPerfil;
        perfilViewModel.getImagen().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Glide.with(getContext()).load(uri).into(imagenPerfil);
            }
        });

        final RecyclerView contenedor = binding.recyclerViewPerfil;
        perfilViewModel.getRecetasMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Receta>>() {
            @Override
            public void onChanged(ArrayList<Receta> recetas) {
                adapterReceta.updateRecetasList(recetas);
                contenedor.setAdapter(adapterReceta);
            }
        });



        listenerAdapter(contenedor);
        refreshListener(root);

        firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = (String) documentSnapshot.get("username");
                if(username.equals(usernameExterno)){ //Si se cumple el usuario es él mismo
                    esPerfilPropio = true;
                    btnSeguir.setVisibility(View.INVISIBLE);
                    onClickListenerImagen(imagenPerfil);
                } else {
                    esPerfilPropio = false;
                    btnSeguir.setVisibility(View.VISIBLE);

                    onClickSeguir();
                    siguiendo();
                }
            }
        });



        return root;
    }

    private void refreshListener(View view) {
        swipe = view.findViewById(R.id.fragment_perfil);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                perfilViewModel.refresh();
                swipe.setRefreshing(false);
            }
        });
    }

    private void listenerAdapter(RecyclerView recyclerView) {
        adapterReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idSeleccionado = recyclerView.getChildAdapterPosition(view);
                Receta receta = adapterReceta.getRecetas().get(idSeleccionado);

                Intent intent = new Intent(getContext(), RecetaSeleccionada.class);
                intent.putExtra("receta", receta);
                if(!esPerfilPropio) {
                    intent.putExtra("user", usernameExterno);
                } else {
                    intent.putExtra("user", currentUsername);
                }
                startActivityForResult(intent,SELECCION_PROPIETARIO);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onClickListenerImagen(ImageView imagenPerfil) {
        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    private void onClickSeguir(){
        btnSeguir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String username = usernameExterno;
                    if(!isChecked){ //Dejar de seguir
                        firestore.collection("users").document(HomeActivity.EMAIL).collection("siguiendo")
                                .whereIn("username", Collections.singletonList(username)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.getDocuments().size()>0){
                                    queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                                }
                            }
                        });
                        firestore.collection("users").whereIn("username", Collections.singletonList(usernameExterno)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                queryDocumentSnapshots.getDocuments().get(0).getReference().collection("seguidores")
                                        .whereIn("username", Collections.singletonList(HomeActivity.USERNAME)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        queryDocumentSnapshots.getDocuments().get(0).getReference().delete();
                                    }
                                });
                            }
                        });
                    } else { //Seguir
                        Map<String,String> user = new HashMap<>();
                        user.put("username",username);
                        firestore.collection("users").document(HomeActivity.EMAIL).collection("siguiendo").add(user);
                        Map<String,String> esteUser = new HashMap<>();
                        esteUser.put("username",HomeActivity.USERNAME);
                        firestore.collection("users").whereIn("username", Collections.singletonList(usernameExterno)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                queryDocumentSnapshots.getDocuments().get(0).getReference().collection("seguidores").add(esteUser);
                            }
                        });
                    }

            }
        });
    }


    private void getCurrentUsername(){

        firestore.collection("users").document(HomeActivity.EMAIL).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUsername = (String) documentSnapshot.get("username");
                perfilViewModel.setUser(currentUsername);
                usernameExterno = currentUsername;
            }
        });
    }

    private void siguiendo(){
        firestore.collection("users").document(HomeActivity.EMAIL).collection("siguiendo")
                .whereIn("username", Collections.singletonList(usernameExterno)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                btnSeguir.setChecked(queryDocumentSnapshots.size() > 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference filepath = storage.child("imgUsers").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            HashMap<String, Uri> imagen = new HashMap<>();
                            imagen.put("perfil", uri);
                            firestore.collection("users").document(HomeActivity.EMAIL).set(imagen, SetOptions.merge());
                            perfilViewModel.refresh();

                            Toast.makeText(getContext(), "Imagen de perfil actualizada", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }
}
