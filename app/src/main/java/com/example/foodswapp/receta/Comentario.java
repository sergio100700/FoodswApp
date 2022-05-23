package com.example.foodswapp.receta;

public class Comentario {
    private String userName;
    private String texto;

    public Comentario(String userName, String texto) {
        this.userName = userName;
        this.texto = texto;
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
}
