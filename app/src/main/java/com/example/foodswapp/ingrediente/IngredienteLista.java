package com.example.foodswapp.ingrediente;
import com.google.firebase.Timestamp;

/**
 * Clase para objeto Ingrediente en el fragment de la lista.
 */
public class IngredienteLista {
    private String id;
    private String nombre;
    private boolean done;
    private Timestamp date;

    public IngredienteLista(String id, String nombre,Timestamp fecha, boolean done) {
        this.id = id;
        this.nombre = nombre;
        this.done = done;
        this.date = fecha;
    }

    public IngredienteLista(String nombre) {
        this.nombre = nombre;
        this.date = Timestamp.now();
    }

    public IngredienteLista(String nombre,boolean done) {
        this.nombre = nombre;
        this.date = Timestamp.now();
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String toString(){
        return getNombre();
    }
}
