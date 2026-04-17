package mx.unam.icat.fc.adamari.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.unam.icat.fc.adamari.R;
import mx.unam.icat.fc.adamari.model.Session;
import mx.unam.icat.fc.adamari.model.SessionManager;

/**
 * Actividad que visualiza el historial cronológico de las sesiones de enfoque y descanso.
 * Se utiliza como práctica para el manejo de RecyclerView, adaptadores y filtrado de datos.
 * @author <a href="mailto:monmm@ciencias.unam.mx" > Mónica Miranda Mijangos </a> - @monmm
 * @version 1.2, mar 2026 (esqueleto para alumnos)
 */
public class SessionHistoryActivity extends AppCompatActivity {

    // Componentes de la Interfaz de Usuario.
    private Toolbar toolbar;
    private TextView tvResultCount;
    private ConstraintLayout layoutEmpty;
    private RecyclerView recyclerView;

    // TODO: Declarar los componentes de filtrado (ChipGroup y Chips individuales).

    // Lógica y Datos.
    private SessionHistoryAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_session);

        // 1. Vincular vistas primero (incluyendo la inicialización del manager)
        bindViews();

        // 2. Configurar la Toolbar (UI estática)
        setupToolbar();

        // 3. Obtener los datos (Lógica de negocio)
        List<Session> sessions = sessionManager.getAllSessions();

        // 4. Protección: Si la lista es null, inicializarla vacía para evitar crash
        if (sessions == null) {
            sessions = new java.util.ArrayList<>();
        }

        // 5. Configurar el RecyclerView con la lista (ya sea llena o vacía)
        setupRecyclerView(sessions);

        // 6. Configurar clics y filtros (Interacción)
        setupFilterLogic();

        // 7. Refrescar la pantalla (Contadores y Empty State)
        updateHistoryDisplay(sessions);
    }

    /**
     * Vincula las variables con los componentes del XML.
     */
    private void bindViews() {
        toolbar = findViewById(R.id.history_toolbar);
        tvResultCount = findViewById(R.id.tvResultCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.recyclerViewHistory);
        sessionManager = new SessionManager(this);
        // TODO: Vincular Chips mediante findViewById y asignar IDs correspondientes.

        // Puedes descomentar estas líneas para probar el diseño:
        /*
        sessionManager.addSession(new Session("Enfoque", "18 mar 2026", "15:00", 25, true));
        sessionManager.addSession(new Session("Descanso", "18 mar 2026", "15:25", 5, true));
        sessionManager.addSession(new Session("Enfoque", "18 mar 2026", "17:25", 3, false));
        sessionManager.addSession(new Session("Descanso", "18 mar 2026", "18:30", 15, true));
        */
    }

    /**
     * Configuración del sistema de filtrado por temporalidad.
     */
    private void setupFilterLogic() {
        // TODO (Opcional): Implementar el funcionamiento del ChipGroup (filtrado).

    }

    /**
     * Configura la Toolbar como ActionBar de la actividad.
     * Habilita el botón de retroceso (Up Navigation) y asigna el título
     * desde los recursos de cadena para soporte multi-idioma.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_history);
        }
    }

    /**
     * Inicializa el RecyclerView con su LayoutManager y Adaptador.
     * Vincula la lista de sesiones obtenida del SessionManager con la
     * interfaz visual mediante el SessionHistoryAdapter.
     */
    private void setupRecyclerView(List<Session> sessions) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador.
        adapter = new SessionHistoryAdapter(sessions, getResources());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Gestiona la visibilidad de la UI y actualiza el contador.
     */
    /**
     * Gestiona la visibilidad de la UI y actualiza el contador.
     */
    private void updateHistoryDisplay(List<Session> sessions) {
        // 1. Calculamos el total y verificamos si la lista está vacía
        int total = (sessions != null ? sessions.size() : 0);
        boolean isEmpty = (total == 0);

        // 2. Controlamos la visibilidad de los componentes
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        // 3. ACTUALIZACIÓN DEL CONTADOR
        // Usamos el plural para que diga "1 sesión" o "X sesiones" correctamente
        String countText = getResources().getQuantityString(R.plurals.session_count_plural, total, total);
        tvResultCount.setText(countText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void filterByToday() {
        List<Session> todaySessions = sessionManager.getTodaySessions();
        // Creamos un nuevo adaptador con la lista filtrada
        adapter = new SessionHistoryAdapter(todaySessions, getResources());
        recyclerView.setAdapter(adapter);
        updateHistoryDisplay(todaySessions); // Para que el contador se actualice
    }
}