package repositorios;

import modelos.Item;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRepositorio {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final Connection conn;

    public ItemRepositorio (Connection conn) {
        this.conn = conn;
    }

    public void guardarItem(Item item) throws SQLException {
        String sql = item.getId() > 0 ? "UPDATE items SET activo = ?, tipo = ?, nombre = ?, descripcion = ?, precio = ? WHERE id = ?" :
                "INSERT INTO items (activo, tipo, nombre, descripcion, precio, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.getActivo());
            preparedStatement.setInt(2, item.getTipo());
            preparedStatement.setString(3, item.getNombre());
            preparedStatement.setString(4, item.getDescripcion());
            preparedStatement.setFloat(5, item.getPrecio());
            if(item.getId() > 0){
                preparedStatement.setLong(6, item.getId());

            }else{
                preparedStatement.setString(6, item.getFechaRegistro().format(dateFormatter));
            }

            preparedStatement.executeUpdate();
        }
    }

    public List<Item> listarItemsActivos() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items";

        try (Statement statement = conn.createStatement()) {

            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    items.add(crearItem(rs));
                }
            }
        }

        return items;
    }

    public Item porId(long id) throws SQLException {
        String sql = "SELECT * FROM items WHERE id=?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearItem(rs);
            }

        }

    }

    public Item porNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM items WHERE nombre=?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, nombre);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearItem(rs);
            }

        }

    }

    public void cambiarEstadoItem(Item item) throws SQLException {
        String sql = "UPDATE items SET activo = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            if(item.getActivo() == 1) {
                preparedStatement.setInt(1, 1);
            }else {
                preparedStatement.setInt(1, 0);
            }
            preparedStatement.setLong(2, item.getId());

            preparedStatement.executeUpdate();

        }
    }

    public void generarItemsFactura(long facturaId, Map<Long,String> items) throws SQLException {
        String sql = "INSERT INTO facturas_items (factura_id, item_id, cantidad) VALUES(?,?,?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            items.forEach((key, value) -> {
                try {
                    int cantidad = Integer.parseInt(value.split(",")[0]);
                    preparedStatement.setLong(1, facturaId);
                    preparedStatement.setLong(2, key);
                    preparedStatement.setInt(3, cantidad);
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            preparedStatement.executeBatch();
        }
    }

    public void eliminarItemsFactura(long facturaId) throws SQLException {
        String sql = "DELETE FROM facturas_items WHERE factura_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setLong(1, facturaId);
            preparedStatement.executeUpdate();
        }
    }

    public Map<Item, Integer> listarItemsFactura(long facturaId) throws SQLException {
        Map<Item, Integer> itemsCantidad = new HashMap<>();

        String sql = "SELECT i.*, fi.cantidad as cantidad FROM items as i INNER JOIN facturas_items as fi ON i.id = fi.item_id WHERE fi.factura_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, facturaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Item item = crearItem(rs);
                    if (item != null) {
                        itemsCantidad.put(item, rs.getInt("cantidad"));
                    }
                }
            }
        }

        return itemsCantidad;
    }

    private Item crearItem(ResultSet rs) throws SQLException {
        Item item = new Item();

        item.setId(rs.getLong("id"));
        item.setActivo(rs.getInt("activo"));
        item.setTipo(rs.getInt("tipo"));
        item.setNombre(rs.getString("nombre"));
        item.setDescripcion(rs.getString("descripcion"));
        item.setPrecio(rs.getFloat("precio"));
        item.setFechaRegistro(LocalDate.parse(rs.getString("fecha_registro"), dateFormatter));

        return item;
    }



}
