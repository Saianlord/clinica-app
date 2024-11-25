package servicios;

import modelos.*;
import repositorios.CitaRepositorio;

import java.sql.SQLException;
import java.time.LocalDate;

import java.util.List;

public class CitaServicio {
   private final CitaRepositorio repo;

    public CitaServicio(CitaRepositorio repo) {
        this.repo = repo;
    }

    public void  guardarCita(Cita cita) throws SQLException {
        repo.guardarCita(cita);
    }

    public Cita porId(long id) throws SQLException {
        return repo.porId(id);
    }

    public List<Cita> porFecha(LocalDate fecha) throws SQLException {
        List<Cita> citas = repo.porFecha(fecha);

        citas.sort(null);

        return citas;
    }

    public Cita porPaciente(String nombre) throws SQLException {
        return repo.porPaciente(nombre);
    }

    public void eliminarCita(Long id) throws SQLException {
        repo.eliminarCita(id);
    }

}
