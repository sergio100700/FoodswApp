package com.example.foodswapp.receta.comentarios;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Comentario implements Serializable {

    private String userName;
    private String texto;
    private Timestamp fecha;

    public Comentario(String userName, String texto, Timestamp fecha) {
        this.userName = userName;
        this.texto = texto;
        this.fecha = fecha;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
