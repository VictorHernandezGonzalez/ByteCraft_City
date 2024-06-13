package com.example.bytecraft_city;

// RegistroActivity.java
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class RegistroActivity extends AppCompatActivity {

    // Declaración de variables
    private FirebaseAuth auth;

    private EditText editTextEmail;
    private EditText editTextNombre;
    private EditText editTextContrasena;
    private EditText editTextRepetirContrasena;
    private MediaPlayer mp;

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Asignación de las variables con sus respectivos ID del layout
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextNombre = findViewById(R.id.editTextNombreRegistro);
        editTextContrasena = findViewById(R.id.editTextContrasenaRegistro);
        editTextRepetirContrasena = findViewById(R.id.editTextRepetirContrasenaRegistro);
        Button buttonCancelar = findViewById(R.id.buttonCancelar);
        Button buttonConfirmar = findViewById(R.id.buttonConfirmar);
        ImageButton imageButtonOjoConContrasena = findViewById(R.id.imageButtonOjoConContrasena);
        ImageButton imageButtonOjoConRepeticionContrasena = findViewById(R.id.imageButtonOjoConRepeticionContrasena);

        // Ocultar la barra de estado
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Inicializar MediaPlayer
        mp = MediaPlayer.create(this, R.raw.musicafondo1);

        // Restaurar la posición en la que se quedo la canción al pasar de Activity
        final int[] milisegundos = {getIntent().getIntExtra("mediaplayerSegundos", 0)};
        boolean mpPlaying = getIntent().getBooleanExtra("mpPlaying", false);

        // Verificación de si la música se esta reproduciendo
        mp.seekTo(milisegundos[0]);
        if (mpPlaying) {
            mp.start();
        }

        // Botón para confirmar el registro del usuario
        buttonConfirmar.setOnClickListener(v -> {
            String usuario = editTextNombre.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextContrasena.getText().toString().trim();
            String repeatedPassword = editTextRepetirContrasena.getText().toString().trim();

            // Verificación de que todos los campos no estén vacíos
            if (usuario.isEmpty() || password.isEmpty() || email.isEmpty() || repeatedPassword.isEmpty()) {
                showCustomToast(R.drawable.logo, "Todos los campos son obligatorios");
            // Verificación de que el E-mail tenga el formato correcto
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showCustomToast(R.drawable.logo, "Por favor, introduce un correo electrónico válido");
            // Verificación de que la contraseña cumpla con los requisitos necesarios
            } else if (password.length() < 8 || !validarFormatoContrasena(password)) {
                showCustomToast(R.drawable.logo, "La contraseña debe tener al menos 8 caracteres y contener al menos una letra, un número, una mayúscula y un símbolo");
            // Verificación de que las contraseñas coincidan
            } else if (!password.equals(repeatedPassword)) {
                showCustomToast(R.drawable.logo, "Las contraseñas no coinciden, intentelo de nuevo");
            } else {
                // Registro del usuario en Firebase
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String currentUserUid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                showCustomToast(R.drawable.logo, "Registro exitoso");
                                // Inicio de sesión automático después del registro
                                auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(signInTask -> {
                                            if (signInTask.isSuccessful()) {
                                                Intent intent1 = new Intent(RegistroActivity.this, SplashScreenSeleccion.class);
                                                intent1.putExtra("currentUserUid", currentUserUid);
                                                startActivity(intent1);
                                            } else {
                                                showCustomToast(R.drawable.logo, "Error al iniciar sesión después del registro: " + Objects.requireNonNull(signInTask.getException()).getMessage());
                                            }
                                        });
                            } else {
                                // Si el registro del usuario falla, aparece un mensaje de error
                                showCustomToast(R.drawable.logo, "Error al registrar usuario: " + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });
            }
        });

        // Botón para cancelar el registro del usuario y volver a la Activity de inicio
        buttonCancelar.setOnClickListener(v -> {
            Intent intent1 = new Intent(RegistroActivity.this, InicioActivity.class);
            // Guardado del estado de reproducción de la música
            milisegundos[0] = mp.getCurrentPosition();
            intent1.putExtra("mediaplayerSegundos", milisegundos[0]);
            // Detiene la música al volver a la Activity inicial
            mp.stop();
            startActivity(intent1);
        });

        // Imagen clicable para mostrar la contraseña del campo de la contraseña
        imageButtonOjoConContrasena.setOnClickListener(v -> mostrarContrasenaTemporalmente(editTextContrasena));

        // Imagen clicable para mostrar la contraseña del campo de la repetición de la contraseña
        imageButtonOjoConRepeticionContrasena.setOnClickListener(v -> mostrarContrasenaTemporalmente(editTextRepetirContrasena));
    }

    // Función para mostrar la contraseña temporalmente
    private void mostrarContrasenaTemporalmente(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // Mueve el cursor al final del texto
            editText.setSelection(editText.getText().length());
        }, 2000);
    }

    // Método para activar la música y asignar al temporizador la hora actual al iniciar la Activity
    @Override
    protected void onStart() {
        super.onStart();
        mp.start();
    }

    // Método para pausar la música y asignar al temporizador la hora actual al iniciar la Activity
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

    // Función para la validación del formato de la contraseña
    private boolean validarFormatoContrasena(String password) {
        boolean tieneAlMenosUnDigito = password.chars().anyMatch(Character::isDigit);
        boolean tieneAlMenosUnaMinuscula = password.chars().anyMatch(Character::isLowerCase);
        boolean tieneAlMenosUnaMayuscula = password.chars().anyMatch(Character::isUpperCase);
        boolean tieneAlMenosUnCaracterEspecial = password.chars().anyMatch(ch -> "º\\ª|!@·#~$%€¬&/()=?'¡¿<>`[^]+*´{¨},;.:-_".indexOf(ch) >= 0);
        boolean tieneAlMenosOchoCaracteres = password.length() >= 8;

        return tieneAlMenosUnDigito &&
                tieneAlMenosUnaMinuscula &&
                tieneAlMenosUnaMayuscula &&
                tieneAlMenosUnCaracterEspecial &&
                tieneAlMenosOchoCaracteres;
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