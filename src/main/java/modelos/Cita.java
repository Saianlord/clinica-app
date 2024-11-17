package modelos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita implements Comparable<Cita>{
    private long id;
    private Odontologo odontologo;
    private Paciente paciente;
    private Recepcionista recepcionista;
    private String motivo;
    private EstadoCita estado; // Se tiene que definir el enum de Estado cita
    private LocalDate fechaAtencion;
    private LocalDate fechaRegistro;
    private LocalTime horaInicial;
    private LocalTime horaFinal;
    private String observaciones;

    public Cita(Odontologo odontologo, Paciente paciente, Recepcionista recepcionista, String motivo, LocalDate fechaAtencion, LocalTime horaInicial) {
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
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Odontologo getOdontologo() {
        return odontologo;
    }

    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Recepcionista getRecepcionista() {
        return recepcionista;
    }

    public void setRecepcionista(Recepcionista recepcionista) {
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
}
