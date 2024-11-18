package estructuraDatos;

import modelos.Cita;

public class Nodo {
    private Cita cita;
    private Nodo siguiente;

    public Nodo(Cita cita) {
        this.cita = cita;
        this.siguiente = null;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}

