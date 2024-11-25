package servicios;

import modelos.Factura;
import modelos.FacturaResultado;
import modelos.Item;
import repositorios.FacturaRepositorio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacturaServicio {

    private final FacturaRepositorio repo;
    private final ItemServicio iServicio;
    private final Connection conn;

    public FacturaServicio(FacturaRepositorio repo, ItemServicio iServicio, Connection conn) {
        this.repo = repo;
        this.iServicio = iServicio;
        this.conn = conn;
    }

    public FacturaResultado guardarFactura(Factura factura, Map<Long, String> items) throws SQLException {
        FacturaResultado facturaResultado = null;
        try {
            conn.setAutoCommit(false);

             facturaResultado = repo.guardarFactura(factura);

            if (!items.isEmpty()) {
                iServicio.generarItemsFactura(facturaResultado.getId(), items);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }

        return facturaResultado;
    }


    public void anularFactura(String numeroFactura) throws SQLException {
        try {
            conn.setAutoCommit(false);

            Factura factura = repo.porNumeroFactura(numeroFactura);

            iServicio.eliminarItemsFactura(factura.getId());
            repo.anularFactura(factura.getId());

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    public Map<Factura, Map<Item, Integer>> porNumeroFactura(String numeroFactura) throws SQLException{
        Map<Factura, Map<Item, Integer>> facturaItems = new HashMap<>();

        Factura factura = repo.porNumeroFactura(numeroFactura);
        Map<Item, Integer> itemsCantidad = iServicio.listarItemsFactura(factura.getId());

        facturaItems.put(factura, itemsCantidad);

        return facturaItems;
    }


}
