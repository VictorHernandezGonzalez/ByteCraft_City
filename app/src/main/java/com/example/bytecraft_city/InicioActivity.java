package com.example.bytecraft_city;

// InicioActivity.java
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicioActivity extends AppCompatActivity {

    // Declaración de variables
    private MediaPlayer mp;
    private int milisegundos;
    private boolean mpPlaying;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Asignación de las varibles con sus respectivos ID del layout
        Button buttonRegistro = findViewById(R.id.buttonRegistrarInicio);
        Button buttonInicioSesion = findViewById(R.id.buttonInicioSesionInicio);

        // Ocultar la barra de estado
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Inicialización del MediaPlayer con una canción de fondo y configuración para repetirla en bucle
        mp = MediaPlayer.create(this, R.raw.musicafondo1);
        mp.setLooping(true);

        // Recuperación de datos del MediaPlayer pasados a través de un Intent
        milisegundos = getIntent().getIntExtra("mediaplayerSegundos", 0);
        mpPlaying = getIntent().getBooleanExtra("mpPlaying", false);

        // Ajuste del MediaPlayer según los datos recuperados
        mp.seekTo(milisegundos);
        if (mpPlaying) {
            mp.start();
        }

        // Comprobación y gestión de la reproducción de la música
        if(mp.isPlaying()){
            mp.pause();
            mpPlaying = false;
        } else {
            mp.start();
            mpPlaying = true;
        }

        // Botón para ir al registro de usuario
        buttonRegistro.setOnClickListener(v -> {
            Intent intent1 = new Intent(InicioActivity.this, RegistroActivity.class);
            milisegundos = mp.getCurrentPosition();
            mpPlaying = mp.isPlaying();
            intent1.putExtra("mediaplayerSegundos", milisegundos);
            intent1.putExtra("mpPlaying", mpPlaying);
            mp.pause();
            startActivity(intent1);
        });

        // Botón para ir al inicio de sesión de usuario
        buttonInicioSesion.setOnClickListener(v -> {
            Intent intent1 = new Intent(InicioActivity.this, LoginActivity.class);
            milisegundos = mp.getCurrentPosition();
            mpPlaying = mp.isPlaying();
            intent1.putExtra("mediaplayerSegundos", milisegundos);
            intent1.putExtra("mpPlaying", mpPlaying);
            mp.pause();
            startActivity(intent1);
        });
    }

    // Método para activar la música al iniciar la Activity
    @Override
    protected void onResume() {
        super.onResume();
        // Recuperación de datos de la música pasados desde otra Activity
        milisegundos = getIntent().getIntExtra("mediaplayerSegundos", 0);
        mpPlaying = getIntent().getBooleanExtra("mpPlaying", false);

        // Ajuste del MediaPlayer según los datos recuperados desde otra Activity
        mp.seekTo(milisegundos);
        if (mpPlaying) {
            mp.start();
        }
    }

    // Método para pausar la música al iniciar la Activity
    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    // Método para hacer que la música empiece desde el inicio al iniciar la Activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }
}