package Util;
import View.*;
import estructuraDatos.Cola;
import repositorios.CitaRepositorio;
import repositorios.FacturaRepositorio;
import repositorios.ItemRepositorio;
import repositorios.UsuarioRepositorio;
import servicios.CitaServicio;
import servicios.FacturaServicio;
import servicios.ItemServicio;
import servicios.UsuarioServicio;

import java.sql.Connection;

import static Util.SQLiteConnection.getConnection;

public class ConfigApp {
    private final Connection conn = getConnection();
    private final UsuarioRepositorio uRepo = new UsuarioRepositorio(conn);
    private final FacturaRepositorio fRepo = new FacturaRepositorio(conn);
    private final ItemRepositorio iRepo = new ItemRepositorio(conn);
    private final CitaRepositorio cRepo = new CitaRepositorio(conn);
    private final UsuarioServicio uServicio = new UsuarioServicio(uRepo);
    private final ItemServicio iServicio = new ItemServicio(iRepo);
    private final CitaServicio cServicio = new CitaServicio(cRepo);
    private final FacturaServicio fServicio = new FacturaServicio(fRepo, iServicio, conn);
    private final Cola cola = new Cola(cServicio, fServicio, iServicio);
    private final MenuUsuarios menuUsuarios = new MenuUsuarios(uServicio);
    private final MenuCitas menuCitas = new MenuCitas(cServicio, uServicio, cola );
    private final MenuItems menuItems = new MenuItems(iServicio);
    private final MenuFactura menuFactura = new MenuFactura(fServicio);
    private final MenuPrincipal menuPrincipal = new MenuPrincipal(menuCitas, menuUsuarios, menuItems, menuFactura);

    public Cola getCola() {
        return cola;
    }

    public MenuPrincipal getMenuPrincipal() {
        return menuPrincipal;
    }
}

