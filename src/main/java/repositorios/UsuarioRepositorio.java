package repositorios;

import static Util.SQLiteConnection.getConnection;

import modelos.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UsuarioRepositorio {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final Connection conn;

    public UsuarioRepositorio(Connection conn) {
        this.conn = conn;
    }

    public void guardarUsuario(Usuario usuario) throws SQLException {
        String sql = usuario.getId() > 0 ? "UPDATE usuarios SET tipo = ?, nombre = ?, apellido = ?, telefono = ?, fecha_nacimiento = ?, activo = ? WHERE id = ?" :
                "INSERT INTO usuarios (tipo, nombre, apellido, telefono, fecha_nacimiento, activo, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
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

    public Usuario porId(long id, int tipo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=? AND tipo=?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, tipo);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                return crearUsuario(rs);
            }

        }
    }

    public Usuario porNombre(String nombre, int tipo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nombre=? AND tipo=?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, nombre);
            preparedStatement.setInt(2, tipo);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearUsuario(rs);
            }

        }
    }

    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        if(rs.getString("nombre") == null){
            return null;
        }
        usuario.setId(rs.getLong("id"));
        usuario.setTipo(rs.getInt("tipo"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setFechaNacimiento(LocalDate.parse(rs.getString("fecha_nacimiento"), dateFormatter));
        usuario.setFechaRegistro(LocalDate.parse(rs.getString("fecha_registro"), dateFormatter));
        usuario.setActivo(rs.getInt("activo"));

        return usuario;

    }
}
