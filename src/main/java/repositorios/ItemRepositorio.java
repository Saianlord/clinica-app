package repositorios;

import modelos.Item;
import modelos.Producto;
import modelos.Servicio;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Util.SQLiteConnection.getConnection;

public class ItemRepositorio {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ItemRepositorio() {

    }

    //Este método actualiza y guarda items dependiendo de si el id de usuario se seteo o no.
    public void guardarItem(Item item) throws SQLException {
        String sql = item.getId() > 0 ? "UPDATE items SET tipo = ?, nombre = ?, descripcion = ?, precio = ? WHERE id = ?" :
                "INSERT INTO items (tipo, nombre, descripcion, precio, fecha_registro) VALUES (?, ?, ?, ?, ?)";
        int tipo = item.getClass() == Servicio.class ? 0 : 1;
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, tipo);
            preparedStatement.setString(2, item.getNombre());
            preparedStatement.setString(3, item.getDescripcion());
            preparedStatement.setFloat(4, item.getPrecio());
            if(item.getId() > 0){
                preparedStatement.setLong(5, item.getId());

            }else{
                preparedStatement.setString(5, item.getFechaRegistro().format(dateFormatter));
            }

            preparedStatement.executeUpdate();
        }
    }

    public List<Item> listarItems() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items";

        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {

            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    items.add(crearItem(rs));
                }
            }
        }

        return items;
    }

    //retorna un item encontrado por id
    public Item porId(long id) throws SQLException {
        String sql = "SELECT * FROM items WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                return crearItem(rs);
            }

        }
    }

    public void eliminarItem(long id) throws SQLException {
        String sql = "DELETE * FROM items WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();

        }
    }

    public void generarItemsFactura(long facturaId, Map<Long,String> items) throws SQLException {
        String sql = "INSERT INTO facturas_items (factura_id, item_id, cantidad) VALUES(?,?,?)";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

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

    private static Item crearItem(ResultSet rs) throws SQLException {

        int tipo = rs.getInt("tipo");

        Item item;

        if(tipo == 0){
            item = new Servicio();
        }else {
            item = new Producto();
        }
        item.setId(rs.getLong("id"));
        item.setNombre(rs.getString("nombre"));
        item.setDescripcion(rs.getString("descripcion"));
        item.setPrecio(rs.getFloat("precio"));
        item.setFechaRegistro(LocalDate.parse(rs.getString("fecha_registro"), dateFormatter));

        return item;

    }


}
