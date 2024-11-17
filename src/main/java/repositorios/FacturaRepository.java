package repositorios;

import modelos.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static Util.SQLiteConnection.getConnection;

public class FacturaRepository {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    public FacturaRepository() {

    }

    public void guardarFactura(Factura factura) throws SQLException {
        String sql = "INSERT INTO usuarios (paciente_id, cita_id, recepcionista_id, metodo_pago_id, total, fecha_emision) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, factura.getPaciente().getId());
            preparedStatement.setLong(2, factura.getCita().getId());
            preparedStatement.setLong(3, factura.getRecepcionista().getId());
            preparedStatement.setInt(4, factura.getMetodoPago().getId());
            preparedStatement.setFloat(5, factura.getTotalPago());
            preparedStatement.setString(6, factura.getFechaEmision().format(dateFormatter));

            preparedStatement.executeUpdate();
        }

        sql = "UPDATE facturas SET numero_factura = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            long id = generarNumeroFactura(factura);
            preparedStatement.setString(1, factura.getNumeroFactura());
            preparedStatement.setLong(2,id);
        }
    }

    public void anularFactura(String numeroFactura) throws SQLException {
        String sql = "DELETE FROM facturas WHERE numero_factura = ?";

        try(Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1, numeroFactura);

            preparedStatement.executeUpdate();
        }
    }

    private long generarNumeroFactura(Factura factura) throws SQLException {
        String numeroFactura;
        long facturaId;
        String sql = "SELECT * FROM facturas ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection(); Statement statement = conn.createStatement()){
            try(ResultSet rs = statement.executeQuery(sql)) {
                facturaId = rs.getLong("id");
                numeroFactura = LocalDate.parse(rs.getString("fecha_emision")).toString()
                        .concat("-").concat(String.valueOf(facturaId));
                factura.setNumeroFactura(numeroFactura);
            }
        }
        return facturaId;
    }


}
