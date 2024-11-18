package repositorios;

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

import static Util.SQLiteConnection.getConnection;

public class CitaRepositorio {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


    public CitaRepositorio() {

    }

    //Este método actualiza, guarda, y cancela citas dependiendo de si el id de usuario se seteo o no.
    // Si el id es mayor a 0 actualiza. Si se cambia estado a CANCELADO, la cita se cancela

    public void guardarCita(Cita cita) throws SQLException {
        String sql = cita.getId() > 0 ? "UPDATE citas SET odontologo_id = ?, motivo = ?, estado = ?, fecha_atencion = ?, hora_inicio = ?, hora_final = ?, observaciones = ? WHERE id = ?" :
                "INSERT INTO citas (odontologo_id, motivo, estado, fecha_atencion, hora_inicio, fecha_registro, paciente_id, recepcionista_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, cita.getOdontologo().getId());
            preparedStatement.setString(2, cita.getMotivo());
            preparedStatement.setString(3, cita.getEstado().toString());
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

    //retorna un usuario encontrado por id
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

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
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
                "c.fecha_atencion=?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, fecha.format(dateFormatter));

            try (ResultSet rs = preparedStatement.executeQuery()) {

                while (rs.next()){
                    citas.add(crearCita(rs));
                }
            }
        }

        citas.sort(null);

        return citas;
    }

    private Cita crearCita(ResultSet rs) throws SQLException {

        Odontologo odontologo = new Odontologo();
        odontologo.setId(rs.getLong("odontologo_id"));
        odontologo.setNombre(rs.getString("odontologo_nombre"));
        Paciente paciente = new Paciente();
        paciente.setId(rs.getLong("paciente_id"));
        paciente.setNombre(rs.getString("paciente_nombre"));
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setId(rs.getLong("recepcionista_id"));
        recepcionista.setNombre(rs.getString("recepcionista_nombre"));

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

}







