package modelos;

import java.time.LocalDate;

public class Factura {
    private int id;
    private Paciente paciente;
    private Cita cita;
    private Recepcionista recepcionista;
    private MetodoPago metodoPago;
    private String numeroFactura;
    private boolean pago;
    private float totalPago;
    private LocalDate fechaEmision;

    public Factura(Paciente paciente, Cita cita, Recepcionista recepcionista, MetodoPago metodoPago, String numeroFactura, boolean pago, float totalPago) {
        this.paciente = paciente;
        this.cita = cita;
        this.recepcionista = recepcionista;
        this.metodoPago = metodoPago;
        this.numeroFactura = numeroFactura;
        this.pago = pago;
        this.totalPago = totalPago;
        this.fechaEmision = LocalDate.now();
    }

    public int getId() {
        return id;
    }


    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Recepcionista getRecepcionista() {
        return recepcionista;
    }

    public void setRecepcionista(Recepcionista recepcionista) {
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

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
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
}
