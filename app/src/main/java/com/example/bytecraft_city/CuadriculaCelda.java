package com.example.bytecraft_city;

// CuadriculaCelda.java
public class CuadriculaCelda {

    // Declaración de variables
    private int x;
    private int y;
    private Edificio edificio;
    private Carretera carretera;

    // Constructor de la clase CuadriculaCelda
    public CuadriculaCelda(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Método get de la coordenada X de la celda
    public int getX() {
        return x;
    }

    // Método set de la coordenada X de la celda
    public void setX(int x) {
        this.x = x;
    }

    // Método get de la coordenada Y de la celda
    public int getY() {
        return y;
    }

    // Método set de la coordenada Y de la celda
    public void setY(int y) {
        this.y = y;
    }

    // Método get de la ubicación del edificio en la celda
    public Edificio getEdificio() {
        return edificio;
    }

    // Método set de la ubicación del edificio en la celda
    public void setEdificio(Edificio edificio) {
        this.edificio = edificio;
    }

    // Método get de la ubicación de la carretera en la celda
    public Carretera getCarretera() {
        return carretera;
    }

    // Método set de la ubicación de la carretera en la celda
    public void setCarretera(Carretera carretera) {
        this.carretera = carretera;
    }
}