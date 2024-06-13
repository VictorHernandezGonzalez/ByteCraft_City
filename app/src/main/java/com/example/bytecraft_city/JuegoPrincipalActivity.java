package com.example.bytecraft_city;

// JuegoPrincipalActivity.java
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JuegoPrincipalActivity extends AppCompatActivity {

    // Declaración de variables
    private TextView textViewMaderaIncr;
    private TextView textViewPiedraIncr;
    private TextView textViewHierroIncr;
    private TextView textViewOroIncr;
    private LinearLayout linearLayoutMenuOpciones;
    private TerrenoSurfaceView surfaceViewJuego;
    private TextView textViewPoblacion;
    private TextView textViewTemporizador;
    private MediaPlayer mp;
    private final Handler handler = new Handler();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_principal);

        // Asignación de las varibles con sus respectivos ID del layout
        textViewMaderaIncr = findViewById(R.id.textViewMaderaIncr);
        textViewPiedraIncr = findViewById(R.id.textViewPiedraIncr);
        textViewHierroIncr = findViewById(R.id.textViewHierroIncr);
        textViewOroIncr = findViewById(R.id.textViewOroIncr);
        ImageButton imageButtonEngranaje = findViewById(R.id.imageButtonEngranaje);

        linearLayoutMenuOpciones = findViewById(R.id.linearLayoutMenuOpciones);
        Button buttonGuardar = findViewById(R.id.buttonGuardar);
        Button buttonSalir = findViewById(R.id.buttonSalir);
        Button buttonCancelarPartida = findViewById(R.id.buttonCancelarPartida);

        FrameLayout frameLayoutJuego = findViewById(R.id.frameLayoutJuego);
        surfaceViewJuego = new TerrenoSurfaceView(this);

        ImageButton imageButtonDemoler = findViewById(R.id.imageButtonDemoler);
        ImageButton imageButtonEdificios = findViewById(R.id.imageButtonEdificios);
        ImageButton imageButtonCarreteras = findViewById(R.id.imageButtonCarreteras);
        textViewPoblacion = findViewById(R.id.textViewPoblacion);
        textViewTemporizador = findViewById(R.id.textViewTemporizador);

        // Ocultar la barra de estado
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Inicialización del MediaPlayer con una canción de fondo y configuración para repetirla en bucle
        mp = MediaPlayer.create(this, R.raw.musicafondo3);
        mp.setLooping(true);

        // Obtener los valores de los recursos de la partida, guardada anteriormente (si los hay)
        Intent intent = getIntent();
        int madera = intent.getIntExtra("madera", 0);
        int piedra = intent.getIntExtra("piedra", 0);
        int hierro = intent.getIntExtra("hierro", 0);
        int oro = intent.getIntExtra("oro", 0);

        // Asignar los valores de los recursos a los contadores
        surfaceViewJuego.maderaContador = madera;
        surfaceViewJuego.piedraContador = piedra;
        surfaceViewJuego.hierroContador = hierro;
        surfaceViewJuego.oroContador = oro;

        // Iniciar la actualización de los contadores
        iniciarActualizacionContadores();

        // Imagen clicable para mostrar un menú de opciones, con botones para guardar la partida, salir de ella y cancelar para salir del menú de opciones
        imageButtonEngranaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutMenuOpciones.setVisibility(View.VISIBLE);
            }
        });

        // Botón para guardar la partida
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtención de los valores actuales de los contadores
                Map<String, Integer> recursos = new HashMap<>();
                recursos.put("madera", surfaceViewJuego.maderaContador);
                recursos.put("piedra", surfaceViewJuego.piedraContador);
                recursos.put("hierro", surfaceViewJuego.hierroContador);
                recursos.put("oro", surfaceViewJuego.oroContador);

                int poblacion = surfaceViewJuego.poblacionContador;

                surfaceViewJuego.guardarPartida();
                showCustomToast(R.drawable.logo, "Partida guardada correctamente");
            }
        });

        // Botón para salir del juego
        buttonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JuegoPrincipalActivity.this);
                builder.setMessage("¿Desea salir de la partida? El progreso que no haya guardado se perderá")
                        // Configuración del botón positivo para salir del juego
                        .setPositiveButton("Sí", (dialog, which) -> {
                            Intent intent = new Intent(JuegoPrincipalActivity.this, SplashScreenInicio.class);
                            startActivity(intent);
                            finish();
                        })
                        // Configuración del botón negativo para cancelar la salida del juego
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });

        // Botón para ocultar el menú de opciones
        buttonCancelarPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutMenuOpciones.setVisibility(View.GONE); // Ocultar el menú desplegable
            }
        });

        // Añadir el terreno del juego al FrameLayout
        frameLayoutJuego.addView(surfaceViewJuego);

        // Imagen clicable para seleccionar edificios aleatorios
        imageButtonEdificios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceViewJuego.seleccionarEdificioAleatorio();
            }
        });

        // Imagen clicable para seleccionar carreteras aleatorias
        imageButtonCarreteras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceViewJuego.seleccionarCarreteraAleatoria();
            }
        });

        // Imagen clicable para activar el modo de demolición para quitar edificios y carreteras
        imageButtonDemoler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceViewJuego.activarModoDemolicion();
            }
        });

        // Iniciar la actualización del temporizador
        inicioActualizacionHora();
    }

    // Función para iniciar la actualización de los contadores de recursos y población
    private void iniciarActualizacionContadores() {
        // Utilización de un Handler para ejecutar un Runnable de forma periódica
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Aumento de los contadores de recursos
                surfaceViewJuego.maderaContador++;
                surfaceViewJuego.piedraContador++;
                surfaceViewJuego.hierroContador++;
                surfaceViewJuego.oroContador++;

                // Actualización de los TextView con los nuevos valores de recursos
                textViewMaderaIncr.setText(String.valueOf(surfaceViewJuego.maderaContador));
                textViewPiedraIncr.setText(String.valueOf(surfaceViewJuego.piedraContador));
                textViewHierroIncr.setText(String.valueOf(surfaceViewJuego.hierroContador));
                textViewOroIncr.setText(String.valueOf(surfaceViewJuego.oroContador));

                // Actualización de el TextView de población, que aumenta en función de los edificios que haya en la ciudad
                textViewPoblacion.setText(String.valueOf(surfaceViewJuego.poblacionContador));

                // Repetición del Runnable cada segundo
                handler.postDelayed(this, 1000);
            }
        });
    }

    // Método para activar la música y asignar al temporizador la hora actual al iniciar la Activity
    @Override
    protected void onResume() {
        super.onResume();
        if (!mp.isPlaying()) {
            mp.start();
        }
        inicioActualizacionHora();
    }

    // Método para pausar la música y asignar al temporizador la hora actual al iniciar la Activity
    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.pause();
        }
        detencionActualizacionHora();
    }

    // Función para iniciar la actualización del temporizador
    private void inicioActualizacionHora() {
        // Utilización de un Handler para ejecutar un Runnable de forma periódica
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Obtención de la hora actual del calendario
                Calendar calendar = Calendar.getInstance();
                // Calculo de la hora sumando 2 horas al valor actual (ajuste de zona horaria)
                @SuppressLint("DefaultLocale") String horaActual = (calendar.get(Calendar.HOUR_OF_DAY) + 2) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE));
                // Actualización del TextView del temporizador con la hora actual formateada
                textViewTemporizador.setText(horaActual);
                // Repetición del Runnable cada minuto
                handler.postDelayed(this, 60000);
            }
        });
    }

    // Función para detener la actualización del temporizador
    private void detencionActualizacionHora() {
        // Eliminación de todos los mensajes y callbacks del Handler asociado
        handler.removeCallbacksAndMessages(null);
    }

    // Método para hacer que la música empiece desde el inicio y los contadores de recursos paren de añadir recursos al iniciar la Activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        mp.release();
    }

    // Función para mostrar mensajes personalizados
    @SuppressLint("InflateParams")
    public void showCustomToast(int iconResId, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View layout = layoutInflater.inflate(R.layout.activity_custom_toast_layout, null);

        ImageView imageView = layout.findViewById(R.id.imageView);
        imageView.setImageResource(iconResId);

        TextView textView = layout.findViewById(R.id.textView);
        textView.setText(message);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}