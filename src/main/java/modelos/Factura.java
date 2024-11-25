package modelos;

import java.time.LocalDate;

public class Factura {
    private Long id;
    private Usuario paciente;
    private Cita cita;
    private Usuario recepcionista;
    private MetodoPago metodoPago;
    private String numeroFactura;
    private float totalPago;
    private LocalDate fechaEmision;

    public Factura(Usuario paciente, Cita cita, Usuario recepcionista, MetodoPago metodoPago, String numeroFactura, float totalPago) {
        this.paciente = paciente;
        this.cita = cita;
        this.recepcionista = recepcionista;
        this.metodoPago = metodoPago;
        this.numeroFactura = numeroFactura;
        this.totalPago = totalPago;
        this.fechaEmision = LocalDate.now();
    }

    public Factura() {
        this.fechaEmision = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public Usuario getRecepcionista() {
        return recepcionista;
    }

    public void setRecepcionista(Usuario recepcionista) {
        this.recepcionista = recepcionista;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public float getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(float totalPago) {
        this.totalPago = totalPago;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
