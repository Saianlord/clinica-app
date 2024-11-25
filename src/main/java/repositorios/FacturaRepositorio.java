package repositorios;

import modelos.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FacturaRepositorio {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final Connection conn;

    public FacturaRepositorio(Connection conn) {
        this.conn = conn;
    }

    public Factura porNumeroFactura(String numeroFactura) throws SQLException {
        String sql = "SELECT " +
                "f.*, " +
                "p.id AS paciente_id, p.nombre AS paciente_nombre, p.apellido AS paciente_apellido, " +
                "r.id AS recepcionista_id, r.nombre AS recepcionista_nombre, r.apellido AS recepcionista_apellido " +
                "FROM " +
                "facturas AS f " +
                "INNER JOIN " +
                "usuarios AS p ON f.paciente_id = p.id " +
                "INNER JOIN " +
                "usuarios AS r ON f.recepcionista_id = r.id " +
                "WHERE " +
                "f.numero_factura = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, numeroFactura);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return crearFactura(rs);
                }
            }
        }
        return null;
    }



    public FacturaResultado guardarFactura(Factura factura) throws SQLException {
        String sql = "INSERT INTO facturas (paciente_id, cita_id, recepcionista_id, metodo_pago_id, numero_factura, total, fecha_emision) "
                + "VALUES (?, ?, ?, ?, NULL, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, factura.getPaciente().getId());
            preparedStatement.setLong(2, factura.getCita().getId());
            preparedStatement.setLong(3, factura.getRecepcionista().getId());
            preparedStatement.setInt(4, factura.getMetodoPago().getId());
            preparedStatement.setFloat(5, factura.getTotalPago());
            preparedStatement.setString(6, factura.getFechaEmision().format(dateFormatter));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long facturaId = generatedKeys.getLong(1);

                        String numeroFactura = "FAC-" + LocalDate.now().toString().replace("-", "") + facturaId;

                        String updateSql = "UPDATE facturas SET numero_factura = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, numeroFactura);
                            updateStmt.setLong(2, facturaId);
                            updateStmt.executeUpdate();
                        }

                        return new FacturaResultado(facturaId, numeroFactura);
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado para la factura.");
                    }
                }
            } else {
                throw new SQLException("No se insertó ninguna factura.");
            }
        }
    }




    public void anularFactura(long id) throws SQLException {
        String sql = "DELETE FROM facturas WHERE id = ?";

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        }
    }

    public long ultimaFactura() throws SQLException {
        String sql = "SELECT id FROM facturas ORDER BY id DESC LIMIT 1";
        long facturaId = 0;

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                facturaId = rs.getLong("id");
            }
        }

        return facturaId;
    }



    private Factura crearFactura(ResultSet rs) throws SQLException {
        Usuario paciente = new Usuario();
        paciente.setId(rs.getLong("paciente_id"));
        paciente.setNombre(rs.getString("paciente_nombre"));
        paciente.setApellido(rs.getString("paciente_apellido"));

        Usuario recepcionista = new Usuario();
        recepcionista.setId(rs.getLong("recepcionista_id"));
        recepcionista.setNombre(rs.getString("recepcionista_nombre"));
        recepcionista.setApellido(rs.getString("recepcionista_apellido"));

        Factura factura = new Factura();
        factura.setId(rs.getLong("id"));
        factura.setNumeroFactura(rs.getString("numero_factura"));
        factura.setTotalPago(rs.getInt("total"));
        factura.setFechaEmision(LocalDate.parse(rs.getString("fecha_emision"), dateFormatter));
        int metodoPagoId = rs.getInt("metodo_pago_id");
        factura.setMetodoPago(MetodoPago.fromId(metodoPagoId));
        factura.setPaciente(paciente);
        factura.setRecepcionista(recepcionista);

        return factura;
    }



}
