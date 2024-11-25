package servicios;

import modelos.Usuario;
import repositorios.UsuarioRepositorio;

import java.sql.SQLException;

public class UsuarioServicio {

    private final UsuarioRepositorio repo;

    public UsuarioServicio(UsuarioRepositorio repo) {
        this.repo = repo;
    }

    public void guardarUsuario(Usuario usuario) throws SQLException {
        repo.guardarUsuario(usuario);
    }

    public Usuario porId(long id, int tipo) throws SQLException {
        return repo.porId(id, tipo);
    }

    public Usuario porNombre(String nombre, int tipo) throws SQLException {
        return repo.porNombre(nombre, tipo);
    }



}
