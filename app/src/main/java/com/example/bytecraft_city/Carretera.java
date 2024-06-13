package com.example.bytecraft_city;

// Carretera.java
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class Carretera {

    // Declaración de variables
    private Bitmap imagen;
    private Map<String, Integer> recursos;

    // Constructor de la clase Carretera
    public Carretera(Bitmap imagen, Map<String, Integer> recursos) {
        this.imagen = imagen;
        this.recursos = recursos;
    }

    // Método para crear un mapa de recursos para una carretera
    public static Map<String, Integer> crearMapaRecursos(int madera, int piedra, int hierro, int oro) {
        Map<String, Integer> recursos = new HashMap<>();
        recursos.put("madera", madera);
        recursos.put("piedra", piedra);
        recursos.put("hierro", hierro);
        recursos.put("oro", oro);
        return recursos;
    }

    // Método get de la imagen de la carretera
    public Bitmap getImagen() {
        return imagen;
    }

    // Método set de la imagen de la carretera
    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    // Método get de los recursos necesarios para construir una carretera
    public Map<String, Integer> getRecursos() {
        return recursos;
    }

    // Método set de los recursos necesarios para construir una carretera
    public void setRecursos(Map<String, Integer> recursos) {
        this.recursos = recursos;
    }
}