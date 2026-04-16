package mx.unam.icat.fc.adamari.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

/**
 * Gestiona el ciclo de vida de las tareas sugeridas dentro de la aplicación.
 * Implementa las operaciones básicas de persistencia en memoria (CRUD).
 * @author <a href="mailto:monmm@ciencias.unam.mx" > Mónica Miranda Mijangos </a> - @monmm
 * @version 1.3, feb 2026
 */
public class SessionManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Adamari.db";
    private static final int DATABASE_VERSION = 1;

    // Definición de los NOMBRES de las columnas de la tabla.
    public static final String TABLE_SESSIONS = "sessions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START = "startTime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_COMPLETED = "completed";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * TODO: Documentar.
     * @param context ...
     */
    public SessionManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * TODO: Documentar.
     * @param db ...
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SESSIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_START + " TEXT, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_COMPLETED + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * TODO: Documentar.
     * @param session ...
     */
    public void saveSession(Session session) {
        // Ejecutamos en el hilo de fondo
        executor.execute(() -> {
            try (SQLiteDatabase db = this.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TYPE, session.getType());
                values.put(COLUMN_DATE, session.getDate());
                values.put(COLUMN_START, session.getStartTime());
                values.put(COLUMN_DURATION, session.getDuration());
                values.put(COLUMN_COMPLETED, session.isCompleted() ? 1 : 0);

                db.insert(TABLE_SESSIONS, null, values);
                Log.d("SQLite", "Sesión guardada en segundo plano correctamente.");
            } catch (Exception e) {
                Log.e("SQLite", "Error al guardar sesión: " + e.getMessage());
            }
        });
    }

    /**
     * TODO: Documentar.
     * @return ...
     */
    public List<Session> getAllSessions() {
        List<Session> sessionList = new ArrayList<>();

        // Consultamos toda la tabla, ordenando por ID descendente (dejando la sesión más reciente primero)
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_SESSIONS, null, null, null, null, null, COLUMN_ID + " DESC")) {

            while (cursor.moveToNext()) {
                sessionList.add(cursorToSession(cursor));
            }
        }

        return sessionList;
    }

    // TODO: realizar metodo(s) para filtrar las sesiones:
    //  + del dia de hoy.
    //  + de esta semana.

    /**
     * Metodo para filtrar por dia.
     * IMPORTANTE: Asegúrate de que al guardar la sesión (saveSession),
     * el formato de session.getDate() coincida con este.
     */
    public List<Session> getTodaySessions() {
        List<Session> sessionList = new ArrayList<>();

        // Usamos el formato amigable que definiste para mostrar en la UI
        String todayDate = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                .format(new Date());

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(TABLE_SESSIONS, null, COLUMN_DATE + "=?",
                     new String[]{todayDate},
                     null, null, COLUMN_ID + " DESC")) {

            while (cursor.moveToNext()) {
                sessionList.add(cursorToSession(cursor));
            }
        }
        return sessionList;
    }

    /**
     * Metodo para filtrar por semana.
     * Para que esto funcione con texto, calculamos los días de la semana
     * y buscamos coincidencias exactas (IN clause) o cambiamos a formato ISO.
     */
    public List<Session> getThisWeekSessions() {
        List<Session> sessionList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Buscaremos las sesiones de los últimos 7 días
        // para evitar el error del formato dd/MM/yyyy en SQLite
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        List<String> daysOfCurrentWeek = new ArrayList<>();

        // Retrocedemos al inicio de la semana (Lunes)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        // Obtenemos los 7 strings de la semana actual
        for (int i = 0; i < 7; i++) {
            daysOfCurrentWeek.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Construimos la consulta con la cláusula IN (?,?,?,?,?,?,?)
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < daysOfCurrentWeek.size(); i++) {
            placeholders.append("?");
            if (i < daysOfCurrentWeek.size() - 1) placeholders.append(",");
        }

        String query = "SELECT * FROM " + TABLE_SESSIONS +
                " WHERE " + COLUMN_DATE + " IN (" + placeholders + ")" +
                " ORDER BY " + COLUMN_ID + " DESC";

        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.rawQuery(query, daysOfCurrentWeek.toArray(new String[0]))) {

            while (cursor.moveToNext()) {
                sessionList.add(cursorToSession(cursor));
            }
        } catch (Exception e) {
            Log.e("SQLite", "Error en filtrado semanal: " + e.getMessage());
        }

        return sessionList;
    }
        /**
         * Método auxiliar para evitar repetir código de mapeo de Cursor a Session.
         */
        private Session cursorToSession(Cursor cursor) {
                Session session = new Session();
                session.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                session.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                session.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START)));
                session.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                session.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                return session;
        }

    /**
     * TODO: Documentar.
     * @param db ...
     * @param oldVersion ...
     * @param newVersion ...
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 1. Eliminamos la tabla si ya existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);

        // 2. Volvemos a crearla llamando al metodo onCreate
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.enableWriteAheadLogging();
    }
}