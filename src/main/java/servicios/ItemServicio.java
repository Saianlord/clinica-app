package servicios;

import modelos.Item;
import repositorios.ItemRepositorio;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ItemServicio {

    private final ItemRepositorio repo;

    public ItemServicio(ItemRepositorio repo) {
        this.repo = repo;
    }

    public void guardarItem(Item item) throws SQLException {
        repo.guardarItem(item);
    }

    public List<Item> listarItemsActivos() throws SQLException {
        return repo.listarItemsActivos();
    }

    public Item porId(long id) throws SQLException {
        return repo.porId(id);
    }

    public Item porNombre(String nombre) throws SQLException {
       return repo.porNombre(nombre);
    }

    public void cambiarEstadoItem(Item item) throws SQLException {
        repo.cambiarEstadoItem(item);
    }


    public void generarItemsFactura(long facturaId, Map<Long, String> items) throws SQLException {
        repo.generarItemsFactura(facturaId, items);
    }

    public void eliminarItemsFactura(long facturaId) throws SQLException {
        repo.eliminarItemsFactura(facturaId);
    }

    public Map<Item, Integer> listarItemsFactura(long facturaId) throws SQLException{
        return repo.listarItemsFactura(facturaId);
    }
}
