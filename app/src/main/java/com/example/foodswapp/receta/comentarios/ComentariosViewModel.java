package com.example.foodswapp.receta.comentarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodswapp.receta.Receta;

import java.util.List;

/**
 * Clase modelo para los datos de la lista en el fragment de Comentarios.
 */
public class ComentariosViewModel extends ViewModel {

    private MutableLiveData<List<Comentario>> mComentarios;

    public ComentariosViewModel() {
        mComentarios = new MutableLiveData<>();
    }

    public LiveData<List<Comentario>> getText() {
        return mComentarios;
    }

}
