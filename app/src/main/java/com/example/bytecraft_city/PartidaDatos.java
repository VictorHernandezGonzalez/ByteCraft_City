package com.example.bytecraft_city;

// PartidaDatos.java
import java.util.Map;

public class PartidaDatos {

    // Variable estática que almacena los datos del juego usando un mapa
    private static Map<String, Object> datosJuego = null;

    // Método get de los datos del juego
    public static Map<String, Object> getDatosJuego() {
        return datosJuego;
    }

    // Método set de los datos del juego
    public static void setDatosJuego(Map<String, Object> datosJuego) {
        PartidaDatos.datosJuego = datosJuego;
    }
}