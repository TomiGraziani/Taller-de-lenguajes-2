package dao;

import java.util.List;
import modelo.Usuario;

public interface UsuarioDAO {

    void insertar(String nombreUsuario, String email, String contrasenia, int idDP);

    List<Usuario> listarTodos();

    Usuario buscarPorCredenciales(String usuario, String contrasenia);

    Usuario buscarPorEmail(String email);

    Usuario buscarPorNombreUsuario(String nombreUsuario);
}
