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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentPerfilBinding;
import com.example.foodswapp.receta.AdapterReceta;
import com.example.foodswapp.receta.Receta;
import com.example.foodswapp.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private final int GALLERY_INTENT = 10;
    private FirebaseFirestore firestore;
    private PerfilViewModel perfilViewModel;
    private FragmentPerfilBinding binding;
    private SwipeRefreshLayout swipe;
    private AdapterReceta adapterReceta;
    public static Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();

        perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = root.getContext();

        swipe = root.findViewById(R.id.fragment_perfil);

        //Adaptador y montaje RecyclerView
        adapterReceta = new AdapterReceta();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(root.getContext(),3);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewPerfil);
        recyclerView.setLayoutManager(layoutManager);


        //Bindings campos de usuario
        final TextView userName = binding.textViewUserName;
        perfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userName.setText(s);
            }
        });

        final CircleImageView imagenPerfil = binding.imageViewPerfil;
        onClickListenerImagen(imagenPerfil);
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

    public void onClickListenerImagen(ImageView imagenPerfil){
        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==GALLERY_INTENT && resultCode == RESULT_OK){
             Uri uri = data.getData();
             StorageReference storage = FirebaseStorage.getInstance().getReference();
             StorageReference filepath = storage.child("imgUsers").child(uri.getLastPathSegment());

             filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {

                             HashMap<String,Uri> imagen = new HashMap<>();
                             imagen.put("perfil",uri);
                             firestore.collection("users").document(HomeActivity.EMAIL).set(imagen, SetOptions.merge());
                             perfilViewModel.refresh();

                             Toast.makeText(getContext(),"Imagen de perfil actualizada",Toast.LENGTH_LONG).show();
                         }
                     });
                 }
             });
         }
    }
}
