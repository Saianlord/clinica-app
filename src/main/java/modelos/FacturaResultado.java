package modelos;

public class FacturaResultado {
    private long id;
    private String numeroFactura;

    public FacturaResultado(long id, String numeroFactura) {
        this.id = id;
        this.numeroFactura = numeroFactura;
    }

    public long getId() {
        return id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }
}
