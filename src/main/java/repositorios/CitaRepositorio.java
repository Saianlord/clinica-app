package repositorios;

import Util.SQLiteConnection;
import modelos.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CitaRepositorio {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final Connection conn;


    public CitaRepositorio(Connection conn) {
        this.conn = conn;
    }


    public void guardarCita(Cita cita) throws SQLException {
        String sql = cita.getId() > 0 ? "UPDATE citas SET odontologo_id = ?, motivo = ?, estado = ?, fecha_atencion = ?, hora_inicio = ?, hora_final = ?, observaciones = ? WHERE id = ?" :
                "INSERT INTO citas (odontologo_id, motivo, estado, fecha_atencion, hora_inicio, fecha_registro, paciente_id, recepcionista_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, cita.getOdontologo().getId());
            preparedStatement.setString(2, cita.getMotivo());
            preparedStatement.setInt(3, convertirEstadoAInt(cita.getEstado()));
            preparedStatement.setString(4, cita.getFechaAtencion().format(dateFormatter));
            preparedStatement.setString(5, cita.getHoraInicial().format(timeFormatter));
            if (cita.getId() > 0) {
                preparedStatement.setString(6, cita.getHoraFinal().format(timeFormatter));
                preparedStatement.setString(7, cita.getObservaciones());
                preparedStatement.setLong(8, cita.getId());
            } else {
                preparedStatement.setString(6, cita.getFechaRegistro().format(dateFormatter));
                preparedStatement.setLong(7, cita.getPaciente().getId());
                preparedStatement.setLong(8, cita.getRecepcionista().getId());
            }
            preparedStatement.executeUpdate();
        }
    }

    public Cita porId(long id) throws SQLException {
        String sql = "SELECT " +
                "c.*, " +
                "o.nombre AS odontologo_nombre, " +
                "p.nombre AS paciente_nombre, " +
                "r.nombre AS recepcionista_nombre, " +
                "e.estado AS estado_nombre " +
                "FROM " +
                "citas AS c " +
                "INNER JOIN " +
                "usuarios AS o ON c.odontologo_id = o.id " +
                "INNER JOIN " +
                "usuarios AS p ON c.paciente_id = p.id " +
                "INNER JOIN " +
                "usuarios AS r ON c.recepcionista_id = r.id " +
                "INNER JOIN " +
                "estados_cita AS e ON c.estado = e.id " +
                "WHERE " +
                "c.id=?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearCita(rs);
            }

        }
    }

    public List<Cita> porFecha(LocalDate fecha) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT " +
                "c.*, " +
                "o.nombre AS odontologo_nombre, " +
                "o.apellido AS odontologo_apellido, " +
                "p.nombre AS paciente_nombre, " +
                "p.apellido AS paciente_apellido, " +
                "r.nombre AS recepcionista_nombre, " +
                "r.apellido AS recepcionista_apellido, " +
                "e.estado AS estado_nombre " +
                "FROM " +
                "citas AS c " +
                "INNER JOIN " +
                "usuarios AS o ON c.odontologo_id = o.id " +
                "INNER JOIN " +
                "usuarios AS p ON c.paciente_id = p.id " +
                "INNER JOIN " +
                "usuarios AS r ON c.recepcionista_id = r.id " +
                "INNER JOIN " +
                "estados_cita AS e ON c.estado = e.id " +
                "WHERE " +
                "c.fecha_atencion=? AND c.estado = 2";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, fecha.format(dateFormatter));

            try (ResultSet rs = preparedStatement.executeQuery()) {

                while (rs.next()){
                    citas.add(crearCita(rs));
                }
            }
        }

        return citas;
    }

    public Cita porPaciente(String nombre) throws SQLException {
        String sql = "SELECT " +
                "c.*, " +
                "o.nombre AS odontologo_nombre, " +
                "o.apellido AS odontologo_apellido, " +
                "p.nombre AS paciente_nombre, " +
                "p.apellido AS paciente_apellido, " +
                "r.nombre AS recepcionista_nombre, " +
                "r.apellido AS recepcionista_apellido, " +
                "e.estado AS estado_nombre " +
                "FROM " +
                "citas AS c " +
                "INNER JOIN " +
                "usuarios AS o ON c.odontologo_id = o.id " +
                "INNER JOIN " +
                "usuarios AS p ON c.paciente_id = p.id " +
                "INNER JOIN " +
                "usuarios AS r ON c.recepcionista_id = r.id " +
                "INNER JOIN " +
                "estados_cita AS e ON c.estado_id = e.id " +
                "WHERE " +
                "p.nombre=?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, nombre);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearCita(rs);
            }
        }

    }

    public void eliminarCita(Long id) throws SQLException {
        String sql = "DELETE FROM citas WHERE id = ?";

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        }

    }

    private Cita crearCita(ResultSet rs) throws SQLException {

        if(rs.getString("paciente_nombre") == null){
            return null;
        }

        Usuario odontologo = new Usuario();
        odontologo.setId(rs.getLong("odontologo_id"));
        odontologo.setNombre(rs.getString("odontologo_nombre"));
        odontologo.setApellido(rs.getString("odontologo_apellido"));
        Usuario paciente = new Usuario();
        paciente.setId(rs.getLong("paciente_id"));
        paciente.setNombre(rs.getString("paciente_nombre"));
        paciente.setApellido(rs.getString("paciente_apellido"));
        Usuario recepcionista = new Usuario();
        recepcionista.setId(rs.getLong("recepcionista_id"));
        recepcionista.setNombre(rs.getString("recepcionista_nombre"));
        recepcionista.setApellido(rs.getString("recepcionista_apellido"));

        Cita cita = new Cita();
        cita.setId(rs.getLong("id"));
        cita.setOdontologo(odontologo);
        cita.setPaciente(paciente);
        cita.setRecepcionista(recepcionista);
        cita.setMotivo(rs.getString("motivo"));
        cita.setEstado(EstadoCita.valueOf(rs.getString("estado_nombre")));
        cita.setFechaAtencion(LocalDate.parse(rs.getString("fecha_atencion"), dateFormatter));
        cita.setFechaRegistro(LocalDate.parse(rs.getString("fecha_registro"), dateFormatter));
        cita.setHoraInicial(LocalTime.parse(rs.getString("hora_inicio"), timeFormatter));
        cita.setObservaciones(rs.getString("observaciones"));
        if (rs.getString("hora_final") != null) {
            cita.setHoraFinal(LocalTime.parse(rs.getString("hora_final"), timeFormatter));
        }

        return cita;

    }

    private int convertirEstadoAInt(EstadoCita estado) {
        return switch (estado) {
            case COMPLETADA -> 1;
            case PENDIENTE -> 2;
            case CANCELADA -> 3;
        };
    }

}







