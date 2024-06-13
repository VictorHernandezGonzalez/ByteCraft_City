package com.example.bytecraft_city;

// Edificio.java
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class Edificio {

    // Declaración de variables
    private Bitmap imagen;
    private Map<String, Integer> recursos;
    private int poblacion;

    // Constructor de la clase Edificio
    public Edificio(Bitmap imagen, Map<String, Integer> recursos, int poblacion) {
        this.imagen = imagen;
        this.recursos = recursos;
        this.poblacion = poblacion;
    }

    // Método para crear un mapa de recursos para un edificio
    public static Map<String, Integer> crearMapaRecursos(int madera, int piedra, int hierro, int oro) {
        Map<String, Integer> recursos = new HashMap<>();
        recursos.put("madera", madera);
        recursos.put("piedra", piedra);
        recursos.put("hierro", hierro);
        recursos.put("oro", oro);
        return recursos;
    }

    // Método get de la imagen del edificio
    public Bitmap getImagen() {
        return imagen;
    }

    // Método set de la imagen del edificio
    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    // Método get de los recursos necesarios para construir un edificio
    public Map<String, Integer> getRecursos() {
        return recursos;
    }

    // Método set de los recursos necesarios para construir un edificio
    public void setRecursos(Map<String, Integer> recursos) {
        this.recursos = recursos;
    }

    // Método get de la población que tiene un edificio
    public int getPoblacion() {
        return poblacion;
    }

    // Método set de la población que tiene un edificio
    public void setPoblacion(int poblacion) {
        this.poblacion = poblacion;
    }
}