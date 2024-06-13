package com.example.bytecraft_city;

// LoginActivity.java
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Declaración de variables
    private FirebaseAuth auth;
    private GoogleApiClient googleApiClient;

    private EditText editTextEmail;
    private EditText editTextContrasenaRegistro;

    private MediaPlayer mp; // Declaración de un reproductor de música

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Asignación de las variables con sus respectivos ID del layout
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContrasenaRegistro = findViewById(R.id.editTextContrasenaRegistro);
        Button buttonCancelarI = findViewById(R.id.buttonCancelar);
        Button buttonInicioSesion = findViewById(R.id.buttonInicioSesion);
        SignInButton signInButtonGoogle = findViewById(R.id.signInButtonGoogle);
        ImageButton imageButtonOjoConContrasenaLogin = findViewById(R.id.imageButtonOjoConContrasenaLogin);

        // Ocultar la barra de estado
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Inicializar MediaPlayer
        mp = MediaPlayer.create(this, R.raw.musicafondo1);

        // Restaurar la posición en la que se quedo la canción al pasar de Activity
        int milisegundos = getIntent().getIntExtra("mediaplayerSegundos", 0);
        boolean mpPlaying = getIntent().getBooleanExtra("mpPlaying", false);

        // Verificación de si la música se esta reproduciendo
        mp.seekTo(milisegundos);
        if (mpPlaying) {
            mp.start();
        }

        // Botón para iniciar sesión con correo electrónico y contraseña
        buttonInicioSesion.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextContrasenaRegistro.getText().toString().trim();

            // Verificación de que todos los campos no estén vacíos
            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                showCustomToast(R.drawable.logo, "Todos los campos son obligatorios");
            // Verificación de que el E-mail tenga el formato correcto
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showCustomToast(R.drawable.logo, "Formato de correo electrónico inválido");
            //  Verificación de que la contraseña tenga los caracteres necesarios
            } else if (password.length() < 8 || !validarFormatoContrasena(password)) {
                showCustomToast(R.drawable.logo, "Contraseña incorrecta");
            } else {
                // Inicio de sesión del usuario en Firebase
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                showCustomToast(R.drawable.logo, "Inicio de sesión exitoso");
                                Intent intent = new Intent(LoginActivity.this, SplashScreenSeleccion.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showCustomToast(R.drawable.logo, "Error al iniciar sesión");
                            }
                        });
            }
        });

        // Botón para cancelar el inicio de sesión y volver a la Activity de inicio
        buttonCancelarI.setOnClickListener(v -> {
            Intent intent1 = new Intent(LoginActivity.this, InicioActivity.class);
            // Guardado del estado de reproducción de la música
            int milisegundos1 = mp.getCurrentPosition();
            intent1.putExtra("mediaplayerSegundos", milisegundos1);
            intent1.putExtra("mpPlaying", mp.isPlaying());
            // Detiene la música al volver a la Activity inicial
            mp.stop();
            startActivity(intent1);
        });

        // Configuración del ID de cliente web de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Configuración de la API para iniciar sesión con Google
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Botón para iniciar sesión con Google
        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        // Imagen clicable para mostrar la contraseña del Login
        imageButtonOjoConContrasenaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarContrasenaTemporalmente(editTextContrasenaRegistro);
            }
        });
    }

    // Función para mostrar la contraseña del Login temporalmente
    private void mostrarContrasenaTemporalmente(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setSelection(editText.getText().length()); // Mover el cursor al final del campo del EditText
        }, 2000);
    }

    // Método para activar la música al iniciar la Activity
    @Override
    protected void onStart() {
        super.onStart();
        mp.start();
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

    // Manejo de resultados de inicio de sesión con Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (data != null) { // Verificar que data no sea nulo
                GoogleSignInAccount account = Objects.requireNonNull(Auth.GoogleSignInApi.getSignInResultFromIntent(data)).getSignInAccount();
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    showCustomToast(R.drawable.logo, "Error al obtener la cuenta de Google");
                }
            } else {
                showCustomToast(R.drawable.logo, "El intent de inicio de sesión es nulo");
            }
        }
    }

    // Autenticación con Firebase usando las credenciales de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showCustomToast(R.drawable.logo, "Inicio de sesión con Google exitoso");
                        Intent intent = new Intent(LoginActivity.this, SplashScreenSeleccion.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showCustomToast(R.drawable.logo, "Error al iniciar sesión con Google: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    // Manejo de conexión fallida con la API de Google
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showCustomToast(R.drawable.logo, "Error de conexión con Google: " + connectionResult.getErrorMessage());
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

    // Código de solicitud para iniciar sesión con Google
    private static final int RC_SIGN_IN = 9001;
}