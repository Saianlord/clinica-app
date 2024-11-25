package modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita implements Comparable<Cita> {
    private long id;
    private Usuario odontologo;
    private Usuario paciente;
    private Usuario recepcionista;
    private String motivo;
    private EstadoCita estado; 
    private LocalDate fechaAtencion;
    private LocalDate fechaRegistro;
    private LocalTime horaInicial;
    private LocalTime horaFinal;
    private String observaciones;

    public Cita(Usuario odontologo, Usuario paciente, Usuario recepcionista, String motivo, LocalDate fechaAtencion, LocalTime horaInicial) {
        this.odontologo = odontologo;
        this.paciente = paciente;
        this.recepcionista = recepcionista;
        this.motivo = motivo;
        this.estado = EstadoCita.PENDIENTE;
        this.fechaAtencion = fechaAtencion;
        this.horaInicial = horaInicial;
        this.fechaRegistro = LocalDate.now();
    }

    public Cita() {
        this.estado = EstadoCita.PENDIENTE;
        this.fechaRegistro = LocalDate.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Usuario getOdontologo() {
        return odontologo;
    }

    public void setOdontologo(Usuario odontologo) {
        this.odontologo = odontologo;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalTime getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(LocalTime horaInicial) {
        this.horaInicial = horaInicial;
    }

    public LocalTime getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(LocalTime horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }


    @Override
    public int compareTo(Cita otraCita) {
        return this.horaInicial.compareTo(otraCita.getHoraInicial());
    }

    @Override
    public String toString() {
        return String.format("%-5d %-20s %-20s %-12s %-8s",
                id,
                paciente.getNombre(),
                odontologo.getNombre(),
                fechaAtencion,
                horaInicial);
    }
}