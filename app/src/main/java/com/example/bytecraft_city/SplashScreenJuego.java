package com.example.bytecraft_city;

// SplashScreenJuego.java
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

@SuppressLint("CustomSplashScreen")
public class SplashScreenJuego extends Activity {

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
            // Envió de recursos a la partida seleccionada anteriormente
            Intent intent = new Intent(SplashScreenJuego.this, JuegoPrincipalActivity.class);
            intent.putExtra("madera", getIntent().getIntExtra("madera", 0));
            intent.putExtra("piedra", getIntent().getIntExtra("piedra", 0));
            intent.putExtra("hierro", getIntent().getIntExtra("hierro", 0));
            intent.putExtra("oro", getIntent().getIntExtra("oro", 0));
            startActivity(intent);
            finish();
        }
    }
}