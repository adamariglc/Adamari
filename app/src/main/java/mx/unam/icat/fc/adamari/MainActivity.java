package mx.unam.icat.fc.adamari;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.unam.icat.fc.adamari.model.SessionManager;
import mx.unam.icat.fc.adamari.model.Session;
import mx.unam.icat.fc.adamari.view.PreferencesActivity;
import mx.unam.icat.fc.adamari.view.SessionHistoryActivity;

/**
 * Actividad principal que gestiona el ciclo de vida del temporizador Pomodoro.
 * Esta clase coordina la interfaz de usuario, los estados de la sesión y la
 * lógica de temporización utilizando CountDownTimer.
 * @author <a href="adamariglc@ciencias.unam.mx" > Lopez Cortes Adamari Gianina </a> - @adamariglc
 * @version 1.3, mar 2026 (esqueleto para alumnos)
 */
public class MainActivity extends AppCompatActivity {

    /** Estados posibles del temporizador. */
    enum TimerState { IDLE, RUNNING, PAUSED }

    /** Modos de sesión según la técnica Pomodoro. */
    enum SessionMode { FOCUS, BREAK, REST}

    // Constantes de configuración.
    private static final long FOCUS_DURATION_MS   = 1 * 60 * 1000L;
    private static final long BREAK_DURATION_MS   =  5 * 60 * 1000L;
    private static final long REST_DURATION_MS    = 15 * 60 * 1000L;
    private static final int SESSIONS_BEFORE_REST = 4;

    // Elementos de la IU.
    private Toolbar toolbar;
    private TextView tvAppTittle;
    private ImageButton btnStats, btnSettings;
    private ChipGroup chipGroupMode;
    private Chip chipFocus, chipBreak, chipRest;
    // TODO: delcarar el texto que indica el estado de la sesion.
    private TextView tvTimerDisplay;
    // TODO: delcarar el texto que indica cuantas sesiones han sido completadas.
    private MaterialButton btnStartStop;
    // TODO: delcarar los botones de reinicio y salto de una sesion.
    private LinearLayout sessionDotsContainer;
    // TODO: declarar el texto para la frase motivadora.
    // Elementos para el funcionamiento del temporizador.
    private CountDownTimer countDownTimer;
    private TimerState timerState = TimerState.IDLE;
    private SessionMode currentMode = SessionMode.FOCUS;
    private long timeLeftMillis = FOCUS_DURATION_MS;
    private int focusSessionsCompleted = 0;
    private ImageButton btnReset;
    private ImageButton btnSkip;
    private SessionManager sessionManager;
    private TextView tvSessionState;
    // Elementos para el registro de una sesión.
    private Session newSession;
    private boolean currentSessionIsCompleted = true;

    /**
     * TODO: Documentar.
     * @param savedInstanceState ...
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Inflamos nuestra vista.
        setContentView(R.layout.activity_main);

        // Inicializamos los elementos de la IU.
        bindViews();
        // Llamamos al controlador.
        sessionManager = new SessionManager(this);
        // Habilitamos nuestra barra de herramientas.
        setSupportActionBar(toolbar);
        // Asignamos los escuchas.
        setupClickListeners();
        // Actualizamos la IU.
        updateTimerDisplay(timeLeftMillis);
    }

    /**
     * Inicializa el menú de opciones superior (Overflow menu).
     * @param menu Objeto menú donde se inflarán las opciones.
     * @return true para que el menú sea visible.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Maneja la selección de ítems en el menú de la Toolbar.
     * @param item Ítem del menú seleccionado.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history) {
            startActivity(new Intent(this, SessionHistoryActivity.class));
        }

        if (id == R.id.action_preferences) {
            startActivity(new Intent(this, PreferencesActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Gestiona el comportamiento de pantalla completa inmersiva.
     * Se activa cada vez que la aplicación vuelve al primer plano.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    /**
     * TODO: Documentar.
     * TODO: cancelar el temporizador para evitar desbordes de memoria.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        cancelTimer();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardamos el tiempo restante y el modo para que,
        // si el sistema recrea la actividad, no se pierda el progreso.
        outState.putLong("timeLeft", timeLeftMillis);
        outState.putString("mode", currentMode.name());
        outState.putInt("sessions", focusSessionsCompleted);
    }


    /**
     * TODO: Documentar.
     * TODO: inicializar el texto que indica el estado de la sesion.
     * TODO: inicializar el texto que indica cuantas sesiones han sido completadas.
     * TODO: inicializar los botones de reinicio y salto de una sesion.
     * TODO: inicializar el texto para la frase motivadora.
     */
    private void bindViews() {
        toolbar = findViewById(R.id.tbMenu);
        btnStats = findViewById(R.id.btnStats);
        btnSettings = findViewById(R.id.btnSettings);
        chipGroupMode = findViewById(R.id.chipGroupMode);
        chipFocus = findViewById(R.id.chipFocus);
        chipBreak = findViewById(R.id.chipBreak);
        chipRest = findViewById(R.id.chipRest);
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        btnStartStop = findViewById(R.id.btnStartStop);
        sessionDotsContainer = findViewById(R.id.sessionDotsContainer);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        tvSessionState = findViewById(R.id.tvSessionState);
    }

    /**
     * TODO: Documentar.
     */
    private void setupClickListeners() {
        // Asignamos un escucha al boton que controla nuestro temporizador.
        btnStartStop.setOnClickListener(v -> {
            // Se ha seleccionado la opcion para comenzar/pausar el temporizador.
            // Llamamos a los metodos correspondientes segun el estado del temporizador.
            if (timerState == TimerState.RUNNING) pauseTimer();
            else startTimer();
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToNextSession();
            }
        });

        btnStats.setOnClickListener(null);
        btnSettings.setOnClickListener(null);
    }

    /**
     * TODO: Documentar.
     */
    private void startTimer() {
        // Mantiene la pantalla encendida.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Actualizamos el estado del temporizador.
        timerState = TimerState.RUNNING;
        // Asignamos una texto mas adecuado al boton que controla nuestro temporizador.
        btnStartStop.setText("Pausar");
        Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show();

        // PRUEBA
        // addDot();

        // Registramos una nueva sesión.
        newSession = new Session();
        newSession.setType(currentMode.name());
        newSession.setCompleted(false); // Por defecto inicia incompleta
        newSession.setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        newSession.setStartTime(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));

        if (currentMode == SessionMode.FOCUS) {
            newSession.setType(getString(R.string.mode_focus));
            newSession.setDuration(25);
        } else if (currentMode == SessionMode.BREAK) {
            newSession.setType(getString(R.string.mode_break));
            newSession.setDuration(5);
        } else {
            newSession.setType(getString(R.string.mode_long_break));
            newSession.setDuration(15);
        }

        newSession.setDate(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date()));
        newSession.setStartTime(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));;

        // Creamos e inicializamos un contador.
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            /**
             * TODO: Documentar.
             * @param millisUntilFinished ...
             */
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerDisplay(millisUntilFinished);
            }

            /**
             * TODO: Documentar.
             */
            @Override
            public void onFinish() {

                onSessionFinished();
            }
        }.start();
    }

    /**
     * TODO: Documentar.
     */
    private void pauseTimer() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Detenemos nuestro contador.
        if (countDownTimer != null) countDownTimer.cancel();
        // Actualizamos el estado de nuestro temporizador.
        timerState = TimerState.PAUSED;
        // Actualizamos el texto del boton que controla el temporizador.
        btnStartStop.setText("Reanudar");
    }

    /**
     * TODO: Documentar.
     * TODO: reiniciar el contenedor de puntos o agregar un nuevo punto en el layout.
     * TODO: actualizar el texto que indica el numero de sesiones de enfoque completadas.
     */

    private void onSessionFinished() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cancelTimer();
        // Actualizamos el estado de nuestro temporizador.
        timerState = TimerState.IDLE;

        // Actualizar el estado de la sesión actual en el objeto
        if (newSession != null) {
            newSession.setCompleted(true);
            // Persistir el cambio inmediatamente en SQLite
            sessionManager.saveSession(newSession);
        }

        // Lógica de transición de la técnica Pomodoro.
        if (currentMode == SessionMode.FOCUS) {
            focusSessionsCompleted++;
            addDot();
            if (focusSessionsCompleted >= SESSIONS_BEFORE_REST) {
                focusSessionsCompleted = 0;
                currentMode = SessionMode.REST;
            } else {
                currentMode = SessionMode.BREAK;
            }
        } else {
            currentMode = SessionMode.FOCUS;
        }

        // TODO: Verificar los casos en que la sesión puede marcarse como incompleta.

        Toast.makeText(this, "Sesión guardada en el historial", Toast.LENGTH_SHORT).show();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        resetModeTime();
        btnStartStop.setText("Comenzar");
    }

    /**
     * TODO: Documentar.
     */
    private void addDot() {
        View dot = new View(this);

        int dotSize = (int) (10 * getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));

        dot.setLayoutParams(params);
        dot.setBackground(getDrawable(R.drawable.dot_session_completed));

        sessionDotsContainer.addView(dot);
    }

    /**
     * TODO: Documentar.
     */
    private void resetModeTime() {
        // Reasignamos la duracion de la sesion segun el estado actual.
        switch (currentMode) {
            case FOCUS: timeLeftMillis = FOCUS_DURATION_MS; break;
            case BREAK: timeLeftMillis = BREAK_DURATION_MS; break;
            case REST:  timeLeftMillis = REST_DURATION_MS; break;
        }
        // Actualizamos la IU.
        updateTimerDisplay(timeLeftMillis);
    }

    /**
     * TODO: Documentar.
     */
    private void cancelTimer() {
        // Si el temporizador esta activo:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Si el temporizador esta activo:
        if (countDownTimer != null) {
            // Detemos el tiempo.
            countDownTimer.cancel();
            // Anulamos el temporizador.
            countDownTimer = null;
        }
    }

    /**
     * TODO: Documentar.
     * TODO: Detener el tiempo, resetear el tiempo del estado actual y actualizar la IU.
     */
    private void resetTimer() {
        cancelTimer();
        timerState = TimerState.IDLE;

        resetModeTime();

        btnStartStop.setText("Comenzar");
    }

    /**
     * TODO: Documentar.
     * TODO: Cancelar el tiempo y forzar el fin de la sesion para saltar el estado actual.
     */
    private void skipToNextSession() {
        cancelTimer();
        onSessionFinished();
    }

    /**
     * TODO: Documentar.
     * TODO: actualizar el texto que indica el estado de la sesion actual.
     * @param millis ...
     */
    private void updateTimerDisplay(long millis) {
        // Resaltamos el chip correspondiente al estado actual del temporizador.
        selectChipForMode(currentMode);
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        // Actualizamos el texto del temporizador.
        tvTimerDisplay.setText(String.format("%02d:%02d", minutes, seconds));
        tvSessionState.setText(currentMode.name());
    }

    /**
     * TODO: Documentar.
     * @param mode ...
     */
    private void selectChipForMode(SessionMode mode) {
        // El identificador del chip a seleccionar.
        int chipId;
        switch (mode) {
            case BREAK:
                // Asignamos el elemento en el layout (el chip).
                chipId = R.id.chipBreak;
                // Resaltamos el chip seleccionado.
                highlightChip(chipBreak);
                break;
            case REST:
                chipId = R.id.chipRest;
                highlightChip(chipRest);
                break;
            default:
                chipId = R.id.chipFocus;
                highlightChip(chipFocus);
                break;
        }
        chipGroupMode.check(chipId);
        // La agrupacion sabe que chip hemos seleccionado.
    }

    /**
     * TODO: Documentar.
     * @param activeChip ...
     */
    private void highlightChip(Chip activeChip) {
        // Obtenemos la densidad de pantalla necesaria para construir el borde de nuestros chips.
        float density = getResources().getDisplayMetrics().density;
        // Enlistamos los chips disponibles para manipularlos facilmente.
        Chip[] allChips = {chipFocus, chipBreak, chipRest};

        // Quitamos el borde de todos los chips.
        for (Chip chip : allChips) {
            chip.setChipStrokeWidth(0);
        }

        // Resaltamos el chip activo modificando el grosor del borde.
        activeChip.setChipStrokeWidth(2 * density);
        // Recuperamos el color para resaltar el borde del chip de los recursos de nuestra app.
        int colorAccent = ContextCompat.getColor(this, R.color.color_border_accent);
        // Asignamos el color del borde para resaltar al chip activo.
        activeChip.setChipStrokeColor(ColorStateList.valueOf(colorAccent));
    }
}
