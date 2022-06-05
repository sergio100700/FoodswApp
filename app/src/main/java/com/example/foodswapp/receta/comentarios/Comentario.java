package com.example.foodswapp.receta.comentarios;

import java.io.Serializable;

/**
 * Clase objeto Comentario donde se guardarán los datos de cada comentario de cada receta.
 */
public class Comentario implements Serializable {

    private static final long serialVersionUID = 44L;
    private String idComentario;
    private String userName;
    private String texto;
    private String fecha;

    public Comentario(String userName, String texto, String fecha) {
        this.userName = userName;
        this.texto = texto;
        this.fecha = fecha;
    }

    public Comentario(String idComentario, String userName, String texto, String fecha) {
        this.idComentario = idComentario;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }
}
