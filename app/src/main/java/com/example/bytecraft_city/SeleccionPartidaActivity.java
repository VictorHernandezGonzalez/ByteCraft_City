package com.example.bytecraft_city;

// SeleccionPartidaActivity.java
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SeleccionPartidaActivity extends AppCompatActivity {

    // Declaración de variables
    private FirebaseFirestore firestore;
    private String currentUserUid;

    private TextView textViewMadera;
    private TextView textViewPiedra;
    private TextView textViewHierro;
    private TextView textViewOro;

    private MediaPlayer mp;

    private boolean partidaCreada = false;
    private String partidaId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_partida);

        // Inicialización de Firebase, Firestore y obtención del usuario actual
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        assert currentUser != null;
        currentUserUid = currentUser.getUid();

        // Asignación de variables con sus respectivos IDs del layout
        textViewMadera = findViewById(R.id.textViewMadera1);
        textViewPiedra = findViewById(R.id.textViewPiedra1);
        textViewHierro = findViewById(R.id.textViewHierro1);
        textViewOro = findViewById(R.id.textViewOro1);

        Button buttonCrear = findViewById(R.id.buttonCrear);
        Button buttonCargar = findViewById(R.id.buttonCargar);
        Button buttonBorrar = findViewById(R.id.buttonBorrar);

        // Ocultar la barra de estado
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Inicialización del MediaPlayer con una canción de fondo y configuración para repetirla en bucle
        mp = MediaPlayer.create(this, R.raw.musicafondo2);
        mp.setLooping(true);

        // Obtención de los datos de la partida guardada
        Map<String, Object> datosJuego = PartidaDatos.getDatosJuego();

        // Uso de los datos para recuperar los datos guardados de la partida en Firestore
        if (datosJuego != null) {
            Map<String, Object> recursos = (Map<String, Object>) datosJuego.get("recursos");
            List<Object> edificios = (List<Object>) datosJuego.get("edificios");
            List<Object> carreteras = (List<Object>) datosJuego.get("carreteras");
        }

        // Verificación de si un usuario tiene una partida creada, y si la tiene, obtiene los recursos con los que guardara la partida
        if (currentUserUid != null) {
            firestore.collection("partidas").document(currentUserUid)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document != null && document.exists()) {
                            // Esto indica de si el usuario tiene una partida creada o no
                            partidaId = document.getId();
                            partidaCreada = true;

                            // Obtención de los datos de la partida y actualizar los TextView
                            firestore.collection("partidas").document(partidaId)
                                    .get()
                                    .addOnSuccessListener(partidaDocument -> {
                                        if (partidaDocument != null) {
                                            Map<String, Object> recursos = (Map<String, Object>) partidaDocument.get("recursos");
                                            if (recursos != null) {
                                                textViewMadera.setText(Objects.requireNonNull(recursos.get("madera")).toString());
                                                textViewPiedra.setText(Objects.requireNonNull(recursos.get("piedra")).toString());
                                                textViewHierro.setText(Objects.requireNonNull(recursos.get("hierro")).toString());
                                                textViewOro.setText(Objects.requireNonNull(recursos.get("oro")).toString());
                                            } else {
                                                showCustomToast(R.drawable.logo, "No se encontraron recursos en la partida");
                                            }
                                        } else {
                                            showCustomToast(R.drawable.logo, "No se encontraron datos de la partida");
                                        }
                                    })
                                    .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al obtener los datos de la partida: " + e));
                        } else {
                            // Esto indica si el usuario no tiene una partida creada o no
                            partidaCreada = false;
                        }
                    })
                    .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al obtener los datos del usuario: " + e));
        }

        // Obtención de los recursos y posiciones pasados desde la Activity del juego principal
        int madera = getIntent().getIntExtra("madera", 0);
        int piedra = getIntent().getIntExtra("piedra", 0);
        int hierro = getIntent().getIntExtra("hierro", 0);
        int oro = getIntent().getIntExtra("oro", 0);

        // Actualización de los TextView con los recursos obtenidos
        textViewMadera.setText(String.valueOf(madera));
        textViewPiedra.setText(String.valueOf(piedra));
        textViewHierro.setText(String.valueOf(hierro));
        textViewOro.setText(String.valueOf(oro));

        // Botón para crear una partida
        buttonCrear.setOnClickListener(v -> {
            if (partidaCreada) {
                showCustomToast(R.drawable.logo, "Ya hay una partida creada");
            } else {
                crearPartida();
            }
        });

        // Botón para cargar una partida
        buttonCargar.setOnClickListener(v -> {
            if (partidaCreada) {
                cargarPartida();
            } else {
                showCustomToast(R.drawable.logo, "No hay ninguna partida creada");
            }
        });

        // Botón para borrar una partida
        buttonBorrar.setOnClickListener(v -> {
            if (partidaCreada) {
                borrarPartida();
            } else {
                showCustomToast(R.drawable.logo, "No hay ninguna partida creada");
            }
        });
    }

    // Método para activar la música al iniciar la Activity
    @Override
    protected void onStart() {
        super.onStart();
        mp.start();
    }

    // Método para pausar la música al iniciar la Activity
    @Override
    protected void onStop() {
        super.onStop();
        mp.pause();
    }

    // Método para hacer que la música empiece desde el inicio al iniciar la Activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    // Función para crear una nueva partida
    private void crearPartida() {
        HashMap<String, Object> partida = new HashMap<>();
        partida.put("madera", 0);
        partida.put("piedra", 0);
        partida.put("hierro", 0);
        partida.put("oro", 0);
        partida.put("poblacion", 0);

        firestore.collection("partidas").document(currentUserUid)
                .set(partida)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(SeleccionPartidaActivity.this, SplashScreenJuego.class);
                    showCustomToast(R.drawable.logo, "Partida creada con éxito");
                    partidaCreada = true;
                    partidaId = currentUserUid;
                    startActivity(intent);
                })
                .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al crear la partida: " + e));
    }

    // Función para cargar una partida existente
    private void cargarPartida() {
        if (partidaId != null) {
            firestore.collection("partidas").document(partidaId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document != null && document.exists()) {
                            // Obtención de los recursos de la partida guardada anteiormente
                            int madera = (document.getLong("madera") != null) ? Objects.requireNonNull(document.getLong("madera")).intValue() : 0;
                            int piedra = (document.getLong("piedra") != null) ? Objects.requireNonNull(document.getLong("piedra")).intValue() : 0;
                            int hierro = (document.getLong("hierro") != null) ? Objects.requireNonNull(document.getLong("hierro")).intValue() : 0;
                            int oro = (document.getLong("oro") != null) ? Objects.requireNonNull(document.getLong("oro")).intValue() : 0;

                            showCustomToast(R.drawable.logo, "Partida cargada con éxito");

                            // Redirige a la Activity del juego principal con los valores de los recursos guardados anteriormente
                            Intent intent = new Intent(SeleccionPartidaActivity.this, SplashScreenJuego.class);
                            intent.putExtra("madera", madera);
                            intent.putExtra("piedra", piedra);
                            intent.putExtra("hierro", hierro);
                            intent.putExtra("oro", oro);
                            startActivity(intent);
                        } else {
                            showCustomToast(R.drawable.logo, "No se encontraron datos de la partida");
                        }
                    })
                    .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al cargar la partida: " + e));
        }
    }

    // Función para borrar una partida existente
    @SuppressLint("SetTextI18n")
    private void borrarPartida() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de que quiere borrar la partida?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (partidaId != null) {
                        firestore.collection("partidas").document(partidaId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    showCustomToast(R.drawable.logo, "Partida borrada con éxito");
                                    partidaCreada = false;
                                    partidaId = null;

                                    // Actualización del valor de los TextView a 0
                                    textViewMadera.setText("x0");
                                    textViewPiedra.setText("x0");
                                    textViewHierro.setText("x0");
                                    textViewOro.setText("x0");
                                })
                                .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al borrar la partida: " + e));
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
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