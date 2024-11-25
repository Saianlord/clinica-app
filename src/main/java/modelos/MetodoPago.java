package modelos;

public enum MetodoPago {
    EFECTIVO("Efectivo", 1),
    CREDITO("Tarjeta de Crédito", 2),
    DEBITO("Tarjeta de Débito", 3),
    SINPE("Sinpe Móvil", 4);

    private final String nombre;
    private final int id;

    MetodoPago(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public static MetodoPago fromId(int id) {
        for (MetodoPago metodo : values()) {
            if (metodo.id == id) {
                return metodo;
            }
        }
        throw new IllegalArgumentException("No se encontró un Método de Pago para el ID: " + id);
    }
}
