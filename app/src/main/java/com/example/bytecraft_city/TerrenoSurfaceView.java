package com.example.bytecraft_city;

// TerrenoSurfaceView.java
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class TerrenoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // Declaración de variables
    private int tamanoCelda = 0; // Tamaño de cada celda en la cuadrícula
    private final int tamanoCuadricula = 20; // Tamaño de la cuadrícula en número de cuadrados
    private final CuadriculaCelda[][] cuadricula = new CuadriculaCelda[tamanoCuadricula][tamanoCuadricula]; // Matriz de celdas en la cuadrícula
    private final List<Edificio> edificios = new ArrayList<>(); // Lista de edificios en el terreno
    private final List<Carretera> carreteras = new ArrayList<>(); // Lista de carreteras en el terreno
    private boolean seleccionEdificio = false; // Indicador de selección de edificio
    private boolean seleccionCarretera = false; // Indicador de selección de carretera
    private boolean seleccionDemolicion = false; // Indicador de selección de demolición

    // Inicialización de Firestore y obtención del usuario actual
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final String currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    // Contadores de recursos y población
    public int maderaContador = 0;
    public int piedraContador = 0;
    public int hierroContador = 0;
    public int oroContador = 0;
    public int poblacionContador = 0;

    // Constructor del TerrenoSurfaceView
    public TerrenoSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this); // Agregar un Callback al SurfaceView
        cargarEdificios(); // Cargar los edificios desde Firestore
        cargarCarreteras(); // Cargar las carreteras desde Firestore
        inicializarCuadricula(); // Inicializar la cuadrícula del terreno
    }

    // Función para guardar la partida en Firestore
    void guardarPartida() {
        // Creación de un mapa de datos con los recursos y la población
        Map<String, Object> datosRecursos = new HashMap<>();
        datosRecursos.put("madera", maderaContador);
        datosRecursos.put("piedra", piedraContador);
        datosRecursos.put("hierro", hierroContador);
        datosRecursos.put("oro", oroContador);
        datosRecursos.put("poblacion", poblacionContador);

        // Creación de dos listas para almacenar la información de los edificios y de las carreteras
        List<Map<String, Object>> listaEdificios = new ArrayList<>();
        List<Map<String, Object>> listaCarreteras = new ArrayList<>();

        // Forma de recorrer la cuadrícula y agregar la información de los edificios y las carreteras a sus listas correspondientes
        for (int i = 0; i < tamanoCuadricula; i++) {
            for (int j = 0; j < tamanoCuadricula; j++) {
                CuadriculaCelda celda = cuadricula[i][j];
                if (celda.getEdificio() != null) {
                    Map<String, Object> edificioData = new HashMap<>();
                    edificioData.put("x", celda.getX());
                    edificioData.put("y", celda.getY());
                    edificioData.put("tipo", edificios.indexOf(celda.getEdificio()));
                    listaEdificios.add(edificioData);
                }
                if (celda.getCarretera() != null) {
                    Map<String, Object> carreteraData = new HashMap<>();
                    carreteraData.put("x", celda.getX());
                    carreteraData.put("y", celda.getY());
                    carreteraData.put("tipo", carreteras.indexOf(celda.getCarretera()));
                    listaCarreteras.add(carreteraData);
                }
            }
        }

        // Crear un mapa de datos con los recursos, los edificios y las carreteras
        Map<String, Object> datosJuego = new HashMap<>();
        datosJuego.put("recursos", datosRecursos);
        datosJuego.put("edificios", listaEdificios);
        datosJuego.put("carreteras", listaCarreteras);

        // Guardar los datos de la partida en Firestore
        firestore.collection("partidas").document(currentUserUid)
                .set(datosJuego)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Guardado de los datos en la instancia de la clase PartidaDatos
                        PartidaDatos.setDatosJuego(datosJuego);
                    }
                })
                .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al obtener los datos de la partida: " + e));
    }

    // Función para cargar los edificios disponibles
    private void cargarEdificios() {
        edificios.add(new Edificio(
                BitmapFactory.decodeResource(getResources(), R.drawable.buildingtiles_012),
                Edificio.crearMapaRecursos(2, 9, 2, 5),
                10
        ));
        edificios.add(new Edificio(
                BitmapFactory.decodeResource(getResources(), R.drawable.buildingtiles_021),
                Edificio.crearMapaRecursos(6, 8, 7, 5),
                2
        ));
        edificios.add(new Edificio(
                BitmapFactory.decodeResource(getResources(), R.drawable.buildingtiles_026),
                Edificio.crearMapaRecursos(10, 1, 9, 7),
                8
        ));
        edificios.add(new Edificio(
                BitmapFactory.decodeResource(getResources(), R.drawable.buildingtiles_029),
                Edificio.crearMapaRecursos(3, 2, 8, 2),
                9
        ));
        edificios.add(new Edificio(
                BitmapFactory.decodeResource(getResources(), R.drawable.buildingtiles_042),
                Edificio.crearMapaRecursos(3, 2, 9, 6),
                4
        ));
    }

    // Función para cargar las carreteras disponibles
    private void cargarCarreteras() {
        carreteras.add(new Carretera(
                BitmapFactory.decodeResource(getResources(), R.drawable.carreterasuperiorderecha),
                Carretera.crearMapaRecursos(3, 4, 1, 6)
        ));
        carreteras.add(new Carretera(
                BitmapFactory.decodeResource(getResources(), R.drawable.carreterasuperiorizquierda),
                Carretera.crearMapaRecursos(10, 2, 6, 8)
        ));
        carreteras.add(new Carretera(
                BitmapFactory.decodeResource(getResources(), R.drawable.carreteravertical),
                Carretera.crearMapaRecursos(8, 1, 9, 2)
        ));
        carreteras.add(new Carretera(
                BitmapFactory.decodeResource(getResources(), R.drawable.carreterahorizontal),
                Carretera.crearMapaRecursos(3, 4, 5, 7)
        ));
        carreteras.add(new Carretera(
                BitmapFactory.decodeResource(getResources(), R.drawable.carreterainferiorderecha),
                Carretera.crearMapaRecursos(4, 1, 8, 2)
        ));
        carreteras.add(new Carretera(
                BitmapFactory.decodeResource(getResources(), R.drawable.carreterainferiorizquierda),
                Carretera.crearMapaRecursos(5, 10, 3, 9)
        ));
    }

    // Función para inicializar la cuadrícula de celdas y cargar los datos obtenidos de Firestore
    private void inicializarCuadricula() {
        for (int i = 0; i < tamanoCuadricula; i++) {
            for (int j = 0; j < tamanoCuadricula; j++) {
                cuadricula[i][j] = new CuadriculaCelda(j, i);
            }
        }

        // Cargar datos de la partida de Firestore
        firestore.collection("partidas").document(currentUserUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document != null && document.exists()) {
                            // Recuperación de los recursos y la población que se habían guardado anteriormente
                            Map<String, Long> datosRecursos = (Map<String, Long>) document.get("recursos");
                            if (datosRecursos != null) {
                                maderaContador = datosRecursos.get("madera") != null ? Objects.requireNonNull(datosRecursos.get("madera")).intValue() : 0;
                                piedraContador = datosRecursos.get("piedra") != null ? Objects.requireNonNull(datosRecursos.get("piedra")).intValue() : 0;
                                hierroContador = datosRecursos.get("hierro") != null ? Objects.requireNonNull(datosRecursos.get("hierro")).intValue() : 0;
                                oroContador = datosRecursos.get("oro") != null ? Objects.requireNonNull(datosRecursos.get("oro")).intValue() : 0;
                                poblacionContador = datosRecursos.get("poblacion") != null ? Objects.requireNonNull(datosRecursos.get("poblacion")).intValue() : 0;
                            }

                            // Recuperación de los edificios y de las carreteras que se habían guardado anteriormente
                            List<Map<String, Object>> listaEdificios = (List<Map<String, Object>>) document.get("edificios");
                            if (listaEdificios != null) {
                                for (Map<String, Object> edificioData : listaEdificios) {
                                    int x = edificioData.get("x") != null ? ((Long) Objects.requireNonNull(edificioData.get("x"))).intValue() : 0;
                                    int y = edificioData.get("y") != null ? ((Long) Objects.requireNonNull(edificioData.get("y"))).intValue() : 0;
                                    int tipo = edificioData.get("tipo") != null ? ((Long) Objects.requireNonNull(edificioData.get("tipo"))).intValue() : 0;
                                    if (tipo >= 0 && tipo < edificios.size()) {
                                        cuadricula[y][x].setEdificio(edificios.get(tipo));
                                    } else {
                                        cuadricula[y][x].setEdificio(null);
                                    }
                                }
                            }

                            List<Map<String, Object>> listaCarreteras = (List<Map<String, Object>>) document.get("carreteras");
                            if (listaCarreteras != null) {
                                for (Map<String, Object> carreteraData : listaCarreteras) {
                                    int x = carreteraData.get("x") != null ? ((Long) Objects.requireNonNull(carreteraData.get("x"))).intValue() : 0;
                                    int y = carreteraData.get("y") != null ? ((Long) Objects.requireNonNull(carreteraData.get("y"))).intValue() : 0;
                                    int tipo = carreteraData.get("tipo") != null ? ((Long) Objects.requireNonNull(carreteraData.get("tipo"))).intValue() : 0;
                                    if (tipo >= 0 && tipo < carreteras.size()) {
                                        cuadricula[y][x].setCarretera(carreteras.get(tipo));
                                    } else {
                                        cuadricula[y][x].setCarretera(null);
                                    }
                                }
                            }

                            draw(); // Forma para dibujar la cuadrícula con los datos cargados
                        }
                    }
                })
                .addOnFailureListener(e -> showCustomToast(R.drawable.logo, "Error al obtener los datos de la partida: " + e));
    }

    // Método de la interfaz SurfaceHolder.Callback para el evento de creación del terreno
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        draw(); // Llamada al método para dibujar cuando se crea la superficie
    }

    // Método de la interfaz SurfaceHolder.Callback para el evento de cambio del terreno
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // No es necesario hacer nada aquí
    }

    // Método de la interfaz SurfaceHolder.Callback para el evento de destrucción del terreno
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // No es necesario hacer nada aquí
    }

    // Función para manejar eventos táctiles de la cuadrícula del terreno
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Obtención de las coordenadas del evento táctil
            float x = event.getX();
            float y = event.getY();

            // Calculo de los límites de la cuadrícula dentro del terreno
            int cuadriculaIzquierda = (getWidth() - tamanoCuadricula * tamanoCelda) / 2;
            int cuadriculaArriba = (getHeight() - tamanoCuadricula * tamanoCelda) / 2;

            // Calculo de la columna y fila de la cuadrícula donde ocurrió el evento táctil
            int columna = (int) ((x - cuadriculaIzquierda) / tamanoCelda);
            int fila = (int) ((y - cuadriculaArriba) / tamanoCelda);

            // Verificación de si el toque está dentro de los límites de la cuadrícula
            if (columna >= 0 && columna < tamanoCuadricula && fila >= 0 && fila < tamanoCuadricula) {
                CuadriculaCelda celda = cuadricula[fila][columna];
                if (seleccionDemolicion) {
                    // Demolición del edificio o la carretera si existen en la celda
                    if (celda.getEdificio() != null) {
                        // Disminución de la población del edificio demolido
                        if (celda.getEdificio() != null) {
                            poblacionContador -= celda.getEdificio().getPoblacion();
                        }
                        celda.setEdificio(null);
                        draw();
                    }
                    if (celda.getCarretera() != null) {
                        celda.setCarretera(null);
                        draw();
                    }
                } else if (seleccionEdificio) {
                    // Colocación de un edificio aleatorio en la celda si no hay edificio ni carretera en ella
                    if (celda.getEdificio() == null && celda.getCarretera() == null) {
                        Random random = new Random();
                        Edificio edificioSeleccionado = edificios.get(random.nextInt(edificios.size()));
                        if (edificioSeleccionado != null) {
                            // Verificación de si hay suficientes recursos para construir el edificio
                            if (tienesSuficientesRecursos(edificioSeleccionado.getRecursos())) {
                                celda.setEdificio(edificioSeleccionado);
                                deducirRecursos(edificioSeleccionado.getRecursos());
                                poblacionContador += edificioSeleccionado.getPoblacion();
                                draw();
                            } else {
                                showCustomToast(R.drawable.logo, "No hay suficientes recursos para construir el edificio seleccionado");
                            }
                        }
                    }
                } else if (seleccionCarretera) {
                    // Colocación de una carretera aleatoria en la celda si no hay carretera ni edificio en ella
                    if (celda.getCarretera() == null && celda.getEdificio() == null) {
                        Random random = new Random();
                        Carretera carreteraSeleccionada = carreteras.get(random.nextInt(carreteras.size()));
                        if (carreteraSeleccionada != null) {
                            // Verificación de si hay suficientes recursos para construir la carretera
                            if (tienesSuficientesRecursos(carreteraSeleccionada.getRecursos())) {
                                celda.setCarretera(carreteraSeleccionada);
                                deducirRecursos(carreteraSeleccionada.getRecursos());
                                draw();
                            } else {
                                showCustomToast(R.drawable.logo, "No hay suficientes recursos para construir la carretera seleccionada");
                            }
                        }
                    }
                }
            }
        }
        return true; // Indicatorio de que el evento táctil se ha realizado correctamente
    }

    // Verificación de si se tienen suficientes recursos para construir un edificio o una carretera
    private boolean tienesSuficientesRecursos(Map<String, Integer> resources) {
        Integer madera = resources.getOrDefault("madera", 0);
        Integer piedra = resources.getOrDefault("piedra", 0);
        Integer hierro = resources.getOrDefault("hierro", 0);
        Integer oro = resources.getOrDefault("oro", 0);

        // Verificación de si alguno de los valores de recursos es nulo
        if (madera == null || piedra == null || hierro == null || oro == null) {
            return false; // Esto indica que no hay suficientes recursos
        }

        return madera <= maderaContador &&
                piedra <= piedraContador &&
                hierro <= hierroContador &&
                oro <= oroContador;
    }

    // Deducción de si se tienen los recursos necesarios para construir un edificio o una carretera
    private void deducirRecursos(Map<String, Integer> resources) {
        Integer madera = resources.getOrDefault("madera", 0);
        Integer piedra = resources.getOrDefault("piedra", 0);
        Integer hierro = resources.getOrDefault("hierro", 0);
        Integer oro = resources.getOrDefault("oro", 0);

        // Verificación de si alguno de los valores de recursos es nulo
        if (madera != null && piedra != null && hierro != null && oro != null) {
            maderaContador -= madera;
            piedraContador -= piedra;
            hierroContador -= hierro;
            oroContador -= oro;
        }
    }

    // Método para dibujar la superficie del juego
    private void draw() {
        Canvas canvas = null; // Inicializar Canvas para el terreno
        try {
            canvas = getHolder().lockCanvas(); // Obtener la instancia de Canvas para dibujar en la superficie
            if (canvas != null) {
                drawTerreno(canvas); // Dibujar el terreno de juego
                drawCuadricula(canvas); // Dibujar la cuadrícula
                drawEdificios(canvas); // Dibujar los edificios
                drawCarreteras(canvas); // Dibujar las carreteras
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas); // Liberar la instancia de Canvas después de dibujar
            }
        }
    }

    // Método para dibujar el terreno del juego con texturas y efectos de profundidad
    private void drawTerreno(Canvas canvas) {
        // Definición del color de fondo
        canvas.drawColor(Color.parseColor("#70a800"));

        // Definición del suelo texturizado con sombras y luces para simular profundidad
        LinearGradient degradado = new LinearGradient(
                0f, (float) getHeight(), (float) getWidth(), 0f,
                Color.parseColor("#92c800"), Color.parseColor("#508a00"), Shader.TileMode.CLAMP
        );
        Paint textura = new Paint();
        textura.setShader(degradado);
        canvas.drawRect(0f, 0f, (float) getWidth(), (float) getHeight(), textura);

        // Definición del efecto de paralelaje a la hierba, para darle sombreado
        LinearGradient sombraHierba = new LinearGradient(
                0f, (float) getHeight(), (float) getWidth(), 0f,
                Color.parseColor("#4c7a03"), Color.parseColor("#2d4602"), Shader.TileMode.CLAMP
        );
        Paint pinturaHierba = new Paint();
        pinturaHierba.setShader(sombraHierba);
        pinturaHierba.setStyle(Paint.Style.FILL);

        int alturaHierba = 60;
        int anchuraHierba = 80;
        for (int y = 0; y < getHeight(); y += alturaHierba) {
            for (int x = 0; x < getWidth(); x += anchuraHierba) {
                int offsetY = (y / alturaHierba % 2 == 0) ? 0 : -alturaHierba / 2;
                canvas.drawRect((float) x, (float) (y + offsetY), (float) (x + anchuraHierba), (float) (y + alturaHierba), pinturaHierba);
            }
        }
    }

    // Método para dibujar la cuadrícula del juego en el Canvas especificado
    private void drawCuadricula(Canvas canvas) {
        // Calculo del tamaño de cada celda
        tamanoCelda = 78;

        // Calculo del espacio vacío en la parte superior e inferior para centrar la cuadrícula
        int alturaCuadricula = tamanoCelda * tamanoCuadricula;
        int anchuraCuadricula = tamanoCelda * tamanoCuadricula;
        int startY = (canvas.getHeight() - alturaCuadricula) / 2;
        int startX = (canvas.getWidth() - anchuraCuadricula) / 2;

        // Configuración del estilo y el color del borde de la cuadrícula
        Paint gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(Color.WHITE);
        gridPaint.setStrokeWidth(1f);

        // Definición de las líneas verticales de la cuadrícula
        for (int i = 0; i <= tamanoCuadricula; i++) {
            canvas.drawLine(
                    startX + i * tamanoCelda,
                    startY, // y inicial
                    startX + i * tamanoCelda,
                    startY + alturaCuadricula,
                    gridPaint
            );
        }

        // Definición de las líneas horizontales de la cuadrícula
        for (int i = 0; i <= tamanoCuadricula; i++) {
            canvas.drawLine(
                    startX, // x inicial
                    startY + i * tamanoCelda,
                    startX + anchuraCuadricula,
                    startY + i * tamanoCelda,
                    gridPaint
            );
        }
    }

    // Método para dibujar los edificios en la cuadrícula del juego en el Canvas especificado
    private void drawEdificios(Canvas canvas) {
        // Obtención de las coordenadas para centrar la cuadrícula en el Canvas
        int cuadriculaIzquierda = (getWidth() - tamanoCuadricula * tamanoCelda) / 2;
        int cuadriculaArriba = (getHeight() - tamanoCuadricula * tamanoCelda) / 2;

        // Iteración sobre cada celda en la cuadrícula
        for (CuadriculaCelda[] fila : cuadricula) {
            for (CuadriculaCelda celda : fila) {
                // Verificación de si la celda contiene un edificio o no
                if (celda.getEdificio() != null) {
                    Edificio edificio = celda.getEdificio();
                    // Escalado de la imagen del edificio al tamaño de la celda
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(edificio.getImagen(), tamanoCelda, tamanoCelda, false);
                    // Calculo de las coordenadas de dibujo en el Canvas para esta celda
                    int x = celda.getX() * tamanoCelda + cuadriculaIzquierda;
                    int y = celda.getY() * tamanoCelda + cuadriculaArriba;
                    // Definición de la imagen del edificio en el Canvas en las coordenadas calculadas
                    canvas.drawBitmap(scaledBitmap, (float) x, (float) y, null);
                }
            }
        }
    }

    // Método para dibujar las carreteras en la cuadrícula del juego en el Canvas especificado
    private void drawCarreteras(Canvas canvas) {
        // Obtención de las coordenadas para centrar la cuadrícula en el Canvas
        int cuadriculaIzquierda = (getWidth() - tamanoCuadricula * tamanoCelda) / 2;
        int cuadriculaArriba = (getHeight() - tamanoCuadricula * tamanoCelda) / 2;

        // Iteración sobre cada celda en la cuadrícula
        for (CuadriculaCelda[] fila : cuadricula) {
            for (CuadriculaCelda celda : fila) {
                // Verificación de si la celda contiene una carretera
                if (celda.getCarretera() != null) {
                    Carretera carretera = celda.getCarretera();
                    // Escalado de la imagen de la carretera al tamaño de la celda
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(carretera.getImagen(), tamanoCelda, tamanoCelda, false);
                    // Calculo de las coordenadas de dibujo en el Canvas para esta celda
                    int x = celda.getX() * tamanoCelda + cuadriculaIzquierda;
                    int y = celda.getY() * tamanoCelda + cuadriculaArriba;
                    // Definición de la imagen de la carretera en el Canvas en las coordenadas calculadas
                    canvas.drawBitmap(scaledBitmap, (float) x, (float) y, null);
                }
            }
        }
    }

    // Método para que se seleccione una carretera aleatoria y que se coloque en la cudrícula clicable
    public void seleccionarEdificioAleatorio() {
        if (!seleccionEdificio) {
            Random random = new Random();
            // Forma de seleccionar el edificio para que se construya
            Edificio edificioSeleccionado = edificios.get(random.nextInt(edificios.size()));
            seleccionEdificio = true;
            seleccionDemolicion = false; // Desactivación de demolición cuando se selecciona un edificio
            seleccionCarretera = false; // Desactivación de selección de carreteras cuando se selecciona un edificio
        }
    }

    // Método para que se seleccione un edificio aleatorio y que se coloque en la cudrícula clicable
    public void seleccionarCarreteraAleatoria() {
        if (!seleccionCarretera) {
            Random random = new Random();
            // Forma de seleccionar la carretera para que se construya
            Carretera carreteraSeleccionada = carreteras.get(random.nextInt(carreteras.size()));
            seleccionCarretera = true;
            seleccionDemolicion = false; // Desactivación de demolición cuando se selecciona una carretera
            seleccionEdificio = false; // Desactivación de selección de edificios cuando se selecciona una carretera
        }
    }

    // Método para activar el modo de demolición, que lo que permite es que se puedan demoler los edificios o las carreteras que haya construidas
    public void activarModoDemolicion() {
        seleccionDemolicion = true;
        seleccionEdificio = false; // Desactivación de selección de edificios cuando se activa la demolición
        seleccionCarretera = false; // Desactivación de selección de carreteras cuando se activa la demolición
    }

    // Función para mostrar mensajes personalizados
    @SuppressLint("InflateParams")
    public void showCustomToast(int iconResId, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        View layout = layoutInflater.inflate(R.layout.activity_custom_toast_layout, null);

        ImageView imageView = layout.findViewById(R.id.imageView);
        imageView.setImageResource(iconResId);

        TextView textView = layout.findViewById(R.id.textView);
        textView.setText(message);

        Toast toast = new Toast(this.getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}