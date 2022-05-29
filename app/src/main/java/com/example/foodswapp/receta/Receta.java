package com.example.foodswapp.receta;

import com.example.foodswapp.receta.comentarios.Comentario;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class Receta implements Serializable {
    private String id;
    private String username;
    private String titulo;
    private int dificultad;
    private String tiempo;//horas minutos
    private boolean vegano;
    private boolean vegetariano;
    private boolean sinGluten;
    private double valoraciones;
    private String imagen;
    private transient Timestamp fecha;
    private List<Comentario> comentarios;
    private List<String> ingredientes;
    private List<String> pasos;

    public Receta(String id, String username, String titulo, int dificultad, String tiempo, boolean vegano, boolean vegetariano,
                  boolean sinGluten, double valoraciones, String imagen, Timestamp fecha, List<Comentario> comentarios,
                  List<String> ingredientes, List<String> pasos) {
        this.id = id;
        this.username = username;
        this.titulo = titulo;
        this.dificultad = dificultad;
        this.tiempo = tiempo;
        this.vegano = vegano;
        this.vegetariano = vegetariano;
        this.sinGluten = sinGluten;
        this.valoraciones = valoraciones;
        this.imagen = imagen;
        this.fecha = fecha;
        this.comentarios = comentarios;
        this.ingredientes = ingredientes;
        this.pasos = pasos;
    }

    public Receta(String titulo, int dificultad, String tiempo, boolean vegano, boolean vegetariano, boolean sinGluten, List<Comentario> comentarios, String imagen) {
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

    public Receta(String titulo, int dificultad, String tiempo, boolean vegano, boolean vegetariano, boolean sinGluten, String imagen, double valoraciones) {
        this.titulo = titulo;
        this.dificultad = dificultad;
        this.tiempo = tiempo;
        this.vegano = vegano;
        this.vegetariano = vegetariano;
        this.sinGluten = sinGluten;
        this.imagen = imagen;
        this.valoraciones = valoraciones;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<String> getPasos() {
        return pasos;
    }

    public void setPasos(List<String> pasos) {
        this.pasos = pasos;
    }

    public double getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(double valoraciones) {
        this.valoraciones = valoraciones;
    }
}
