package repositorios;

import static Util.SQLiteConnection.getConnection;

import modelos.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UsuarioRepositorio {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    public UsuarioRepositorio() {

    }

    //Este método actualiza, guarda, y desactiva usuarios usuarios dependiendo de si el id de usuario se seteo o no.
    // Si el id es mayor a 0 actualiza. Si se cambia activo de 1 a 0, el usuario se desactiva
    public void guardarUsuario(Usuario usuario) throws SQLException {
        String sql = usuario.getId() > 0 ? "UPDATE usuarios SET tipo = ?, nombre = ?, apellido = ?, telefono = ?, fecha_nacimiento = ?, activo = ? WHERE id = ?" :
                "INSERT INTO usuarios (tipo, nombre, apellido, telefono, fecha_nacimiento, activo, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, usuario.getTipo());
            preparedStatement.setString(2, usuario.getNombre());
            preparedStatement.setString(3, usuario.getApellido());
            preparedStatement.setString(4, usuario.getTelefono());
            preparedStatement.setString(5, usuario.getFechaNacimiento().format(dateFormatter));
            preparedStatement.setInt(6, usuario.getActivo());

            if (usuario.getId() > 0) {
                preparedStatement.setLong(7, usuario.getId());
            } else {
                preparedStatement.setString(7, usuario.getFechaRegistro().format(dateFormatter));
            }

            preparedStatement.executeUpdate();
        }
    }

    //retorna un usuario encontrado por id
    public Usuario porId(long id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearUsuario(rs);
            }

        }
    }

    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        int tipo = rs.getInt("tipo");

        Usuario usuario = determinarTipo(tipo);

        if (usuario != null) {
            usuario.setId(rs.getLong("id"));
            usuario.setTipo(tipo);
            usuario.setNombre(rs.getString("nombre"));
            usuario.setApellido(rs.getString("apellido"));
            usuario.setTelefono(rs.getString("telefono"));
            usuario.setFechaNacimiento(LocalDate.parse(rs.getString("fecha_nacimiento"), dateFormatter));
            usuario.setFechaRegistro(LocalDate.parse(rs.getString("fecha_registro"), dateFormatter));
            usuario.setActivo(rs.getInt("activo"));
        }

        return usuario;

    }

    private static Usuario determinarTipo(int tipo) {

        switch (tipo) {
            case 1 -> {
                return new Odontologo();
            }
            case 2 -> {
                return new Paciente();
            }
            case 3 -> {
                return new Recepcionista();
            }
            default -> {
                return null;
            }
        }

    }
}
