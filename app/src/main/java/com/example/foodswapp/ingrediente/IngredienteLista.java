package com.example.foodswapp.ingrediente;

import java.time.LocalDateTime;

public class IngredienteLista {
    private int id;
    private String nombre;
    private boolean done;
    private LocalDateTime date;

    public IngredienteLista(int id, String nombre, boolean done, LocalDateTime date) {
        this.id = id;
        this.nombre = nombre;
        this.done = done;
        this.date = date;
    }

    public IngredienteLista(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String toString(){
        return getNombre();
    }
}
