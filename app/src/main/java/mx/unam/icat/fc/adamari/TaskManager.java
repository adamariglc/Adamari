package mx.unam.icat.fc.adamari;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para realizar CRUD
 */
public class TaskManager {

    private List<Task> taskList;
    private int idCounter = 1;

    public TaskManager() {
        this.taskList = new ArrayList<>();
    }

    /**
     * CREATE: Agrega una nueva tarea a la lista.
     */
    public void agregarTask(String descripcion){
        Task newTask = new Task(idCounter++, descripcion);
        taskList.add(newTask);
    }

    /**
     * READ: Muestra la lista de tareas
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskList);
    }

    /**
     * UPDATE: Cambia el estado de una tarea a completada.
     */
    public void marcarCompletada(int id) {
        for (Task t : taskList) {
            if (t.getId() == id) {
                t.setTerminada(true);
                break;
            }
        }
    }

    /**
     * DELETE: Elimina una tarea por su ID.
     */
    public void deleteTask(int id) {
        taskList.removeIf(t -> t.getId() == id);
    }

    public static class Task {
        private int id;
        private String descripcion;
        private boolean taskTerminada;

        public Task(int id, String descripcion) {
            this.id = id;
            this.descripcion = descripcion;
            this.taskTerminada = false;
        }

        // Getters
        public int getId() {
            return id;
        }
        public String getDescripcion() {
            return descripcion;
        }
        public boolean isTerminada() {
            return taskTerminada;
        }

        public void setTerminada(boolean done) {
            taskTerminada = done;
        }

        @Override
        public String toString() {
            return "ID: " + id + " " + descripcion + (taskTerminada ? " Completada" : " Pendiente");
        }
    }
}