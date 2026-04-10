package mx.unam.icat.fc.adamari.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el ciclo de vida de las tareas sugeridas dentro de la aplicación.
 * Implementa las operaciones básicas de persistencia en memoria (CRUD).
 * @author <a href="mailto:monmm@ciencias.unam.mx" > Mónica Miranda Mijangos </a> - @monmm
 * @version 1.0, feb 2026
 */
public class SessionManager {
    private List<Session> sessionHistory;

    public SessionManager() {
        this.sessionHistory = new ArrayList<>();
    }

    /**
     *ADD
     * @param session
     */
    public void addSession(Session session) {
        if (session != null) {
            sessionHistory.add(0, session); // Insertamos al inicio para ver lo más reciente
        }
    }

    /**
     *READ
     * @return
     */
    public List<Session> getHistory() {
        return new ArrayList<>(sessionHistory);
    }

    // TODO: completar operaciones CRUD.
    //  + metodo para obtener las sesiones del dia de hoy.
    //  + metodo para las sesiones de esta semana.

    /**
     *
     * Update
     * @param session
     */
    public void updateSession(int index, Session session) {
        if (index >= 0 && index < sessionHistory.size()) {
            sessionHistory.set(index, session);
        }
    }

    /**
     * DELETE
     * @param index
     */
    public void deleteSession(int index) {
        if (index >= 0 && index < sessionHistory.size()) {
            sessionHistory.remove(index);
        }
    }

    /**
     * Sesiones de hoy comparando Strings
     * @param todayDate
     * @return
     */
    public ArrayList<Session> getTodaySessions(String todayDate) {
        ArrayList<Session> result = new ArrayList<>();

        for (int i = 0; i < sessionHistory.size(); i++) {
            Session s = sessionHistory.get(i);

            if (s.getDate().equals(todayDate)) {
                result.add(s);
            }
        }

        return result;
    }

    /**
     * Sesiones por semana usando el arreglo de fechas
     * @param weekDates
     * @return
     */
    public ArrayList<Session> getWeekSessions(String[] weekDates) {
        ArrayList<Session> result = new ArrayList<>();

        for (int i = 0; i < sessionHistory.size(); i++) {
            Session s = sessionHistory.get(i);

            for (int j = 0; j < weekDates.length; j++) {
                if (s.getDate().equals(weekDates[j])) {
                    result.add(s);
                    break; // evita duplicados
                }
            }
        }

        return result;
    }
}