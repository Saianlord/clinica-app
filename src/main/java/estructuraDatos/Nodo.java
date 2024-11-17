package estructuraDatos;

import modelos.Cita;

public class Nodo {
    private Cita elemento;
    private Nodo siguiente;

    public Nodo(Cita elemento, Nodo siguiente) {
        this.elemento = elemento;
        this.siguiente = siguiente;
    }

    public Cita getElemento() {
        return elemento;
    }

    public void setElemento(Cita elemento) {
        this.elemento = elemento;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}

