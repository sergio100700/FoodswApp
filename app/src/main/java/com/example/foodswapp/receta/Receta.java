package com.example.foodswapp.receta;

import android.net.Uri;

import java.util.List;
import java.util.Map;

public class Receta {
    private String titulo;
    private int dificultad;
    private String tiempo;//horas minutos
    private boolean vegano;
    private boolean vegetariano;
    private boolean sinGluten;
    private List<Comentario> comentarios;
    private Uri imagen;
    private List<String> ingredientes;
    private Map<Integer,String> pasos;

    public Receta(String titulo, int dificultad, String tiempo, boolean vegano, boolean vegetariano, boolean sinGluten, List<Comentario> comentarios, Uri imagen) {
        this.titulo = titulo;
        this.dificultad = dificultad;
        this.tiempo = tiempo;
        this.vegano = vegano;
        this.vegetariano = vegetariano;
        this.sinGluten = sinGluten;
        this.comentarios = comentarios;
        this.imagen = imagen;
    }

    public Receta(String titulo, int dificultad, String tiempo, boolean vegano, boolean vegetariano, boolean sinGluten, List<Comentario> comentarios) {
        this.titulo = titulo;
        this.dificultad = dificultad;
        this.tiempo = tiempo;
        this.vegano = vegano;
        this.vegetariano = vegetariano;
        this.sinGluten = sinGluten;
        this.comentarios = comentarios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDificultad() {
        return dificultad;
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public boolean isVegano() {
        return vegano;
    }

    public void setVegano(boolean vegano) {
        this.vegano = vegano;
    }

    public boolean isVegetariano() {
        return vegetariano;
    }

    public void setVegetariano(boolean vegetariano) {
        this.vegetariano = vegetariano;
    }

    public boolean isSinGluten() {
        return sinGluten;
    }

    public void setSinGluten(boolean sinGluten) {
        this.sinGluten = sinGluten;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public Uri getImagen() {
        return imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public Map<Integer, String> getPasos() {
        return pasos;
    }

    public void setPasos(Map<Integer, String> pasos) {
        this.pasos = pasos;
    }
}
