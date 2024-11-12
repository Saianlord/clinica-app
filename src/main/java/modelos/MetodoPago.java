package modelos;

public enum MetodoPago {
    EFECTIVO("Efectivo"),
    CREDITO("Tarjeta de Crédito"),
    DEBITO("Tarjeta de Débito"),
    SINPE("Sinpe Móvil");

    private final String nombre;

    MetodoPago(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
