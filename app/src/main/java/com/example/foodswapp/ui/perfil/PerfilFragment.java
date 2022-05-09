package com.example.foodswapp.ui.perfil;

import static android.app.Activity.RESULT_OK;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.foodswapp.HomeActivity;
import com.example.foodswapp.R;
import com.example.foodswapp.databinding.FragmentPerfilBinding;
import com.example.foodswapp.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private final int GALLERY_INTENT = 10;
    private FirebaseFirestore firestore;
    private PerfilViewModel perfilViewModel;
    private FragmentPerfilBinding binding;
    private SwipeRefreshLayout swipe;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();

        perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        swipe = root.findViewById(R.id.fragment_perfil);

        final TextView userName = binding.textViewUserName;
        perfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userName.setText(s);
            }
        });

        final CircleImageView imagenPerfil = binding.imageViewPerfil;
        onClickListenerImagen(imagenPerfil);
        perfilViewModel.getImagen().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {

            @Override
            public void onChanged(Bitmap bitmap) {
                imagenPerfil.setImageBitmap(bitmap);
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
                     HashMap<String,Uri> imagen = new HashMap<>();
                     imagen.put("perfil",uri);
                     firestore.collection("users").document(HomeActivity.EMAIL).set(imagen, SetOptions.merge());
                     perfilViewModel.refresh();
                     Toast.makeText(getContext(),uri.toString(),Toast.LENGTH_LONG).show();
                 }
             });
         }
    }
}
