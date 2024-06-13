package com.example.bytecraft_city;

// SplashScreenSeleccion.java
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

@SuppressLint("CustomSplashScreen")
public class SplashScreenSeleccion extends Activity {

    // Declaración de variables
    private ProgressBar progressBar;
    private int estadoBarraProgreso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Asignación de la ProgressBar a la variable
        progressBar = findViewById(R.id.progressBar);

        // Utiliza un Handler para programar la ejecución del progreso de la ProgressBar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
            }
        }, 100);
    }

    // Función para actualizar el progreso de la ProgressBar
    private void updateProgressBar() {
        // Incrementación del progreso
        estadoBarraProgreso += 10;

        // Actualización del progreso de la ProgressBar
        progressBar.setProgress(estadoBarraProgreso);

        // Si el progreso es menor que 100, sigue actualizando la ProgressBar
        if (estadoBarraProgreso < 100) {
            // Tiempo en milisegundos
            long tiempo = 3000L;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateProgressBar();
                }
            }, tiempo / 10);
        } else {
            Intent intent = new Intent(SplashScreenSeleccion.this, SeleccionPartidaActivity.class);
            startActivity(intent);
            finish();
        }
    }
}